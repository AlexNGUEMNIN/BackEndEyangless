package com.eyangless.Back.API;

import com.eyangless.Back.DTO.ForgetPasswordRequest;
import com.eyangless.Back.DTO.ResetPasswordRequest;
import com.eyangless.Back.DTO.VerifyOtpRequest;
import com.eyangless.Back.Service.PasswordResetService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/auth/password")
public class PasswordResetAPI {
    private PasswordResetService passwordResetService;

    @PostMapping("/forgot")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody ForgetPasswordRequest request) {
        Map<String, Object> response = passwordResetService.forgotPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody VerifyOtpRequest request) {
        Map<String, Object> response = passwordResetService.verifyOtp(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody ResetPasswordRequest request) {
        Map<String, Object> response = passwordResetService.resetPassword(request);
        return ResponseEntity.ok(response);
    }
}
