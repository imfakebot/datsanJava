SET NAMES utf8mb4;
USE datsanjava;
DELETE FROM pitches;

INSERT INTO pitches (name, location, latitude, longitude, pitch_type, status, image_url) VALUES 
('Sân bóng đá Chuyên Việt', 'Quận Hải Châu, Đà Nẵng', 16.0375, 108.2255, 'Sân 5', 'Available', 'https://images.unsplash.com/photo-1579952363873-27f3bade9f55?q=80&w=600&auto=format&fit=crop'),
('Sân bóng đá Tuyên Sơn', 'Quận Hải Châu, Đà Nẵng', 16.0321, 108.2273, 'Sân 7', 'Available', 'https://images.unsplash.com/photo-1518605368461-1ee7e5f32a76?q=80&w=600&auto=format&fit=crop'),
('Sân bóng Phước Mỹ', 'Quận Sơn Trà, Đà Nẵng', 16.0682, 108.2435, 'Sân 11', 'Available', 'https://images.unsplash.com/photo-1551280857-2b9ebf241ac7?q=80&w=600&auto=format&fit=crop'),
('Sân bóng Hòa Xuân', 'Quận Cẩm Lệ, Đà Nẵng', 15.9984, 108.2258, 'Sân 5', 'Maintenance', 'https://images.unsplash.com/photo-1459865264687-595d652de67e?q=80&w=600&auto=format&fit=crop');
