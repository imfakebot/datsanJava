package com.tanh.datsan.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(500, "Lỗi hệ thống", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_NOT_FOUND(404, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    PITCH_NOT_FOUND(404, "Không tìm thấy sân", HttpStatus.NOT_FOUND),
    BOOKING_NOT_FOUND(404, "Không tìm thấy cuốc đặt sân", HttpStatus.NOT_FOUND),
    BOOKING_OVERLAP(400, "Sân đã được đặt trong khoảng thời gian này", HttpStatus.BAD_REQUEST),
    INVALID_KEY(400, "Lỗi dữ liệu đầu vào", HttpStatus.BAD_REQUEST),
    USER_EXISTED(400, "Người dùng đã tồn tại", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus statusCode;

    ErrorCode(int code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
