package com.eyangless.Back.DTO;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String email;
    private String otp;
}
