package com.eyangless.Back.Configuration;

import lombok.Data;

@Data
public class OtpVerificationRequest {
    private String email;
    private String otp;
}
