package com.eyangless.Back.Service;

import com.eyangless.Back.DTO.ForgetPasswordRequest;
import com.eyangless.Back.DTO.ResetPasswordRequest;
import com.eyangless.Back.DTO.VerifyOtpRequest;

import java.util.Map;

public interface PasswordResetService {
    Map<String, Object> forgotPassword(ForgetPasswordRequest request);
    Map<String, Object> verifyOtp(VerifyOtpRequest request);
    Map<String, Object> resetPassword(ResetPasswordRequest request);
}
