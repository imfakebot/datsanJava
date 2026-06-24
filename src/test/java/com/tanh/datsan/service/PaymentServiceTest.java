package com.tanh.datsan.service;

import com.tanh.datsan.config.VNPayConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private VNPayConfig vnPayConfig;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        // We use lenient() or just standard when() for these since they are needed
        org.mockito.Mockito.lenient().when(vnPayConfig.getTmnCode()).thenReturn("TEST_CODE");
        org.mockito.Mockito.lenient().when(vnPayConfig.getSecretKey()).thenReturn("TEST_SECRET_KEY_MUST_BE_LONG_ENOUGH");
        org.mockito.Mockito.lenient().when(vnPayConfig.getUrl()).thenReturn("http://localhost/vnpay");
        org.mockito.Mockito.lenient().when(vnPayConfig.getReturnUrl()).thenReturn("http://localhost/return");
    }

    @Test
    void testCreateVnPayUrl() {
        double amount = 500000.0;
        String orderId = "12345";
        String ipAddr = "127.0.0.1";

        String url = paymentService.createVnPayUrl(amount, orderId, ipAddr);

        assertNotNull(url);
        assertTrue(url.startsWith("http://localhost/vnpay?"));
        assertTrue(url.contains("vnp_Amount=50000000")); // Amount * 100
        assertTrue(url.contains("vnp_TxnRef=12345"));
        assertTrue(url.contains("vnp_SecureHash="));
    }

    @Test
    void testVerifyReturnUrl_Failure_Empty() {
        Map<String, String> params = new HashMap<>();
        boolean isValid = paymentService.verifyReturnUrl(params);
        assertFalse(isValid);
    }
}
