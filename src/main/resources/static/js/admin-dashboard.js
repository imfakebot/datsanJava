// Admin Dashboard JavaScript

let charts = {};
let currentEditingPitchId = null;
let allPitchesData = []; // Store all pitches for filtering

// Initialize Dashboard
document.addEventListener('DOMContentLoaded', function() {
    setDefaultDateRange();
    loadRevenueData();
    loadPitches();
});

// Set Default Date Range (Last 90 days for better chart data)
function setDefaultDateRange() {
    const endDate = new Date();
    const startDate = new Date(endDate.getTime() - 90 * 24 * 60 * 60 * 1000);

    document.getElementById('startDate').valueAsDate = startDate;
    document.getElementById('endDate').valueAsDate = endDate;
}

// Load Revenue Data
async function loadRevenueData() {
    try {
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;

        if (!startDate || !endDate) {
            return;
        }

        // Fetch revenue summary
        const summaryResponse = await fetch(`/api/admin/revenue/summary?startDate=${startDate}&endDate=${endDate}`);
        if (!summaryResponse.ok) {
            console.error('Revenue summary API error:', summaryResponse.status, summaryResponse.statusText);
            if (summaryResponse.status === 403 || summaryResponse.status === 401) {
                showToast('Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.', 'danger');
            }
            return;
        }
        const summary = await summaryResponse.json();

        // Fetch daily revenue
        const dailyResponse = await fetch(`/api/admin/revenue/daily?startDate=${startDate}&endDate=${endDate}`);
        const dailyData = dailyResponse.ok ? await dailyResponse.json() : [];

        updateRevenueCards(summary);
        updateRevenueCharts(summary, dailyData);
    } catch (error) {
        console.error('Error loading revenue data:', error);
    }
}

// Update Revenue Summary Cards
function updateRevenueCards(summary) {
    document.getElementById('totalRevenue').textContent = formatCurrency(summary.totalRevenue || 0);
    document.getElementById('totalBookings').textContent = summary.totalBookings || 0;
    document.getElementById('avgPerBooking').textContent = formatCurrency(summary.averagePerBooking || 0);

    // Calculate today's revenue
    const today = new Date().toISOString().split('T')[0];
    const todayRevenue = (summary.dailyRevenue && summary.dailyRevenue[today]) || 0;
    document.getElementById('todayRevenue').textContent = formatCurrency(todayRevenue);
}

// Update Revenue Charts
function updateRevenueCharts(summary, dailyData) {
    updateDailyRevenueChart(dailyData);

    if (summary.monthlyRevenue) {
        updateMonthlyRevenueChart(summary.monthlyRevenue);
    }
    if (summary.yearlyRevenue) {
        updateYearlyRevenueChart(summary.yearlyRevenue);
    }
    if (summary.pitchRevenue) {
        updatePitchRevenueChart(summary.pitchRevenue);
    }
}

// Daily Revenue Chart
function updateDailyRevenueChart(dailyData) {
    const ctx = document.getElementById('dailyRevenueChart').getContext('2d');

    if (charts.daily) {
        charts.daily.destroy();
    }

    if (!dailyData || dailyData.length === 0) {
        charts.daily = new Chart(ctx, {
            type: 'line',
            data: { labels: ['Không có dữ liệu'], datasets: [{ label: 'Doanh Thu (VND)', data: [0] }] },
            options: { responsive: true }
        });
        return;
    }

    const dates = dailyData.map(d => new Date(d.date).toLocaleDateString('vi-VN'));
    const revenues = dailyData.map(d => d.dailyTotal);

    charts.daily = new Chart(ctx, {
        type: 'line',
        data: {
            labels: dates,
            datasets: [{
                label: 'Doanh Thu (VND)',
                data: revenues,
                borderColor: '#10b981',
                backgroundColor: 'rgba(16, 185, 129, 0.1)',
                borderWidth: 3,
                fill: true,
                tension: 0.4,
                pointBackgroundColor: '#10b981',
                pointBorderColor: '#fff',
                pointBorderWidth: 2,
                pointRadius: 6,
                pointHoverRadius: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                    labels: {
                        font: { size: 12, weight: 'bold' },
                        padding: 15
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return formatCurrency(value, true);
                        }
                    }
                }
            }
        }
    });
}

// Monthly Revenue Chart
function updateMonthlyRevenueChart(monthlyData) {
    const ctx = document.getElementById('monthlyRevenueChart').getContext('2d');

    if (charts.monthly) {
        charts.monthly.destroy();
    }

    const months = Object.keys(monthlyData).sort();
    const revenues = months.map(m => monthlyData[m]);

    charts.monthly = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: months,
            datasets: [{
                label: 'Doanh Thu Tháng (VND)',
                data: revenues,
                backgroundColor: [
                    '#0d6efd',
                    '#0dcaf0',
                    '#198754',
                    '#ffc107',
                    '#fd7e14',
                    '#dc3545',
                    '#6f42c1',
                    '#e83e8c',
                    '#0d6efd',
                    '#0dcaf0',
                    '#198754',
                    '#ffc107'
                ],
                borderRadius: 8,
                hoverBackgroundColor: '#0a58ca'
            }]
        },
        options: {
            indexAxis: 'x',
            responsive: true,
            plugins: {
                legend: {
                    display: true
                }
            },
            scales: {
                y: {
                    ticks: {
                        callback: function(value) {
                            return formatCurrency(value, true);
                        }
                    }
                }
            }
        }
    });
}

// Yearly Revenue Chart
function updateYearlyRevenueChart(yearlyData) {
    const ctx = document.getElementById('yearlyRevenueChart').getContext('2d');

    if (charts.yearly) {
        charts.yearly.destroy();
    }

    const years = Object.keys(yearlyData).sort();
    const revenues = years.map(y => yearlyData[y]);

    charts.yearly = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: years,
            datasets: [{
                data: revenues,
                backgroundColor: [
                    '#0d6efd',
                    '#0dcaf0',
                    '#198754',
                    '#ffc107',
                    '#fd7e14'
                ],
                borderColor: '#fff',
                borderWidth: 3
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'bottom'
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return formatCurrency(context.parsed);
                        }
                    }
                }
            }
        }
    });
}

// Pitch Revenue Chart
function updatePitchRevenueChart(pitchRevenue) {
    const ctx = document.getElementById('pitchRevenueChart').getContext('2d');

    if (charts.pitch) {
        charts.pitch.destroy();
    }

    const pitches = Object.keys(pitchRevenue);
    const bookings = pitches.map(p => pitchRevenue[p]);

    charts.pitch = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: pitches,
            datasets: [{
                label: 'Số Lượt Đặt',
                data: bookings,
                backgroundColor: '#0d6efd',
                borderColor: '#0a58ca',
                borderWidth: 2,
                borderRadius: 8
            }]
        },
        options: {
            indexAxis: 'x',
            responsive: true,
            plugins: {
                legend: {
                    display: true
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1
                    }
                }
            }
        }
    });
}

// ============================================
// Pitch Management
// ============================================

async function loadPitches() {
    try {
        const response = await fetch('/api/admin/pitches');
        if (!response.ok) {
            console.error('Pitches API error:', response.status);
            return;
        }
        allPitchesData = await response.json();
        displayPitches(allPitchesData);
    } catch (error) {
        console.error('Error loading pitches:', error);
        document.getElementById('pitchTableBody').innerHTML =
            '<tr><td colspan="6" class="text-center text-danger">Lỗi khi tải danh sách sân</td></tr>';
    }
}

function displayPitches(pitches) {
    const tbody = document.getElementById('pitchTableBody');
    tbody.innerHTML = '';

    if (!pitches || pitches.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">Chưa có sân nào</td></tr>';
        return;
    }

    pitches.forEach((pitch, index) => {
        let statusBadge = '';
        let statusText = '';
        if (pitch.status === 'ACTIVE') {
            statusBadge = 'bg-success';
            statusText = 'Hoạt Động';
        } else if (pitch.status === 'INACTIVE') {
            statusBadge = 'bg-warning text-dark';
            statusText = 'Tạm Dừng';
        } else if (pitch.status === 'DELETED') {
            statusBadge = 'bg-danger';
            statusText = 'Đã Xóa';
        } else {
            statusBadge = 'bg-secondary';
            statusText = pitch.status;
        }

        let pitchTypeText = pitch.pitchType;
        if (pitchTypeText === '5-A-SIDE') pitchTypeText = 'Sân 5 người';
        else if (pitchTypeText === '7-A-SIDE') pitchTypeText = 'Sân 7 người';
        else if (pitchTypeText === '11-A-SIDE') pitchTypeText = 'Sân 11 người';
        else if (pitchTypeText === 'FUTSAL') pitchTypeText = 'Futsal';

        const row = `
            <tr>
                <td>${index + 1}</td>
                <td><strong>${pitch.name}</strong></td>
                <td>${pitch.location || 'N/A'}</td>
                <td>${pitchTypeText}</td>
                <td>
                    <span class="badge ${statusBadge}">${statusText}</span>
                </td>
                <td class="action-buttons">
                    <button class="btn btn-sm btn-primary me-1" onclick="editPitch(${pitch.id})">
                        <i class="bi bi-pencil"></i> Sửa
                    </button>
                    ${pitch.status === 'ACTIVE'
                        ? `<button class="btn btn-sm btn-warning me-1" onclick="deactivatePitch(${pitch.id})"><i class="bi bi-pause-circle"></i> Tạm Dừng</button>`
                        : `<button class="btn btn-sm btn-success me-1" onclick="activatePitch(${pitch.id})"><i class="bi bi-play-circle"></i> Kích Hoạt</button>`
                    }
                    <button class="btn btn-sm btn-danger" onclick="deletePitch(${pitch.id})">
                        <i class="bi bi-trash"></i> Xóa
                    </button>
                </td>
            </tr>
        `;
        tbody.innerHTML += row;
    });
}

// Filter pitches by name search AND status dropdown
function filterPitches() {
    const searchText = document.getElementById('searchPitch').value.toLowerCase().trim();
    const statusFilter = document.getElementById('statusFilter').value;

    let filtered = allPitchesData;

    // Filter by search text (name or location)
    if (searchText) {
        filtered = filtered.filter(pitch =>
            (pitch.name && pitch.name.toLowerCase().includes(searchText)) ||
            (pitch.location && pitch.location.toLowerCase().includes(searchText))
        );
    }

    // Filter by status
    if (statusFilter) {
        filtered = filtered.filter(pitch => pitch.status === statusFilter);
    }

    displayPitches(filtered);
}

function showAddPitchModal() {
    currentEditingPitchId = null;
    document.getElementById('pitchForm').reset();
    document.getElementById('pitchModalTitle').textContent = 'Thêm Sân Mới';
    new bootstrap.Modal(document.getElementById('pitchModal')).show();
}

async function editPitch(pitchId) {
    try {
        const response = await fetch(`/api/admin/pitches/${pitchId}`);
        if (!response.ok) throw new Error('Không thể tải thông tin sân');
        const pitch = await response.json();

        currentEditingPitchId = pitchId;
        document.getElementById('pitchName').value = pitch.name;
        document.getElementById('pitchLocation').value = pitch.location;
        document.getElementById('pitchLatitude').value = pitch.latitude || '';
        document.getElementById('pitchLongitude').value = pitch.longitude || '';
        document.getElementById('pitchType').value = pitch.pitchType;

        document.getElementById('pitchModalTitle').textContent = 'Chỉnh Sửa Sân';
        new bootstrap.Modal(document.getElementById('pitchModal')).show();
    } catch (error) {
        console.error('Error loading pitch:', error);
        showToast('Lỗi khi tải thông tin sân', 'danger');
    }
}

async function savePitch() {
    const name = document.getElementById('pitchName').value.trim();
    const location = document.getElementById('pitchLocation').value.trim();
    const latitude = document.getElementById('pitchLatitude').value;
    const longitude = document.getElementById('pitchLongitude').value;
    const pitchType = document.getElementById('pitchType').value;

    if (!name || !location || !pitchType) {
        showToast('Vui lòng điền đầy đủ thông tin bắt buộc', 'warning');
        return;
    }

    const data = {
        name,
        location,
        latitude: latitude ? parseFloat(latitude) : null,
        longitude: longitude ? parseFloat(longitude) : null,
        pitchType
    };

    try {
        let response;
        if (currentEditingPitchId) {
            response = await fetch(`/api/admin/pitches/${currentEditingPitchId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        } else {
            response = await fetch('/api/admin/pitches', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        }

        if (response.ok) {
            bootstrap.Modal.getInstance(document.getElementById('pitchModal')).hide();
            loadPitches();
            showToast(currentEditingPitchId ? 'Cập nhật sân thành công!' : 'Thêm sân mới thành công!', 'success');
        } else {
            const errorText = await response.text();
            showToast('Lỗi: ' + (errorText || 'Không thể lưu dữ liệu'), 'danger');
        }
    } catch (error) {
        console.error('Error saving pitch:', error);
        showToast('Lỗi khi lưu dữ liệu', 'danger');
    }
}

async function deletePitch(pitchId) {
    if (!confirm('Bạn chắc chắn muốn xóa sân này?')) {
        return;
    }

    try {
        const response = await fetch(`/api/admin/pitches/${pitchId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            loadPitches();
            showToast('Xóa sân thành công!', 'success');
        } else {
            showToast('Lỗi khi xóa sân', 'danger');
        }
    } catch (error) {
        console.error('Error deleting pitch:', error);
        showToast('Lỗi khi xóa sân', 'danger');
    }
}

async function activatePitch(pitchId) {
    if (!confirm('Kích hoạt lại sân này?')) return;
    try {
        const response = await fetch(`/api/admin/pitches/${pitchId}/activate`, { method: 'PUT' });
        if (response.ok) {
            loadPitches();
            showToast('Kích hoạt sân thành công!', 'success');
        } else {
            showToast('Lỗi khi kích hoạt sân', 'danger');
        }
    } catch (error) {
        console.error('Error activating pitch:', error);
    }
}

async function deactivatePitch(pitchId) {
    if (!confirm('Tạm dừng hoạt động sân này?')) return;
    try {
        const response = await fetch(`/api/admin/pitches/${pitchId}/deactivate`, { method: 'PUT' });
        if (response.ok) {
            loadPitches();
            showToast('Tạm dừng sân thành công!', 'success');
        } else {
            showToast('Lỗi khi tạm dừng sân', 'danger');
        }
    } catch (error) {
        console.error('Error deactivating pitch:', error);
    }
}

// ============================================
// Section Navigation
// ============================================

function showSection(sectionName) {
    document.querySelectorAll('.section-content').forEach(el => {
        el.classList.remove('active');
    });
    document.getElementById(`${sectionName}-section`).classList.add('active');

    document.querySelectorAll('.sidebar .nav-link').forEach(el => {
        el.classList.remove('active');
    });
    event.target.closest('.nav-link').classList.add('active');
}

// ============================================
// Utility Functions
// ============================================

function formatCurrency(value, short = false) {
    if (!value && value !== 0) value = 0;
    const formatter = new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND',
        maximumFractionDigits: 0
    });

    if (short && value >= 1000000) {
        return (value / 1000000).toFixed(1) + 'M VND';
    }

    return formatter.format(value);
}

// Toast notification helper
function showToast(message, type = 'info') {
    // Create a simple toast notification
    const toastContainer = document.getElementById('toastContainer') || createToastContainer();
    const toastId = 'toast-' + Date.now();
    const toastHtml = `
        <div id="${toastId}" class="toast align-items-center text-bg-${type} border-0 show" role="alert">
            <div class="d-flex">
                <div class="toast-body">${message}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" onclick="this.closest('.toast').remove()"></button>
            </div>
        </div>
    `;
    toastContainer.insertAdjacentHTML('beforeend', toastHtml);

    // Auto remove after 4 seconds
    setTimeout(() => {
        const el = document.getElementById(toastId);
        if (el) el.remove();
    }, 4000);
}

function createToastContainer() {
    const container = document.createElement('div');
    container.id = 'toastContainer';
    container.className = 'toast-container position-fixed top-0 end-0 p-3';
    container.style.zIndex = '9999';
    document.body.appendChild(container);
    return container;
}

async function exportRevenueData() {
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    if (!startDate || !endDate) {
        showToast('Vui lòng chọn khoảng thời gian', 'warning');
        return;
    }

    try {
        const response = await fetch(`/api/admin/revenue/daily?startDate=${startDate}&endDate=${endDate}`);
        if (!response.ok) {
            showToast('Lỗi khi tải dữ liệu xuất', 'danger');
            return;
        }
        const dailyData = await response.json();

        let csvContent = "data:text/csv;charset=utf-8,\uFEFF";
        csvContent += "Ngày,Tổng Doanh Thu (VND),Số Lượt Đặt\r\n";

        dailyData.forEach(function(rowArray) {
            let row = `${rowArray.date},${rowArray.dailyTotal},${rowArray.completedBookings}`;
            csvContent += row + "\r\n";
        });

        const encodedUri = encodeURI(csvContent);
        const link = document.createElement("a");
        link.setAttribute("href", encodedUri);
        link.setAttribute("download", `doanh_thu_${startDate}_den_${endDate}.csv`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        showToast('Xuất dữ liệu thành công!', 'success');
    } catch (error) {
        console.error('Error exporting data:', error);
        showToast('Lỗi khi xuất dữ liệu', 'danger');
    }
}
