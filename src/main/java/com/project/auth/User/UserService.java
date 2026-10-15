package com.project.auth.User;

import com.project.auth.CommonDTOs.ApiResponse;
import com.project.auth.CommonDTOs.ApiResponse2;
import com.project.auth.User.DTO.Requests.*;
import com.project.auth.User.DTO.Responses.ResponseRegister;
import com.project.auth.User.DTO.Responses.ResponseRegister2;
import com.project.auth.User.DTO.Responses.ResponseUser;
import com.project.auth.User.DTO.Responses.ResponseUserNameCheck;
import com.project.auth.Util.EmailService;
import com.project.auth.Security.JwtUtil;
import com.project.auth.Util.OtpUtil;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    @Value("${app.email.enabled}")
    private boolean emailEnabled;

    public UserService(UserRepository repo, PasswordEncoder passwordEncoder, EmailService emailService, JwtUtil jwtUtil) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    //    ---------------------- Register the User ----------------------
    public ResponseRegister registerUser(RequestRegister request) {
        UserEntity newUser = new UserEntity();
        boolean alreadyExists = repo.findByEmail(request.getEmail()).isPresent();
        if (alreadyExists) {
            throw new RuntimeException("User Already Exists");
        }
        newUser.setFirstname(request.getFirstname());
        newUser.setLastname(request.getLastname());
        newUser.setEmail(request.getEmail());
        newUser.setPhone(request.getPhone());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        String otp = OtpUtil.generateOtp();
        newUser.setOtp(otp);
        newUser.setOtpExpirationsAt(LocalDateTime.now().plusMinutes(3));
        newUser.setEmailVerified(false);
        repo.save(newUser);
        String message = "Your OTP is: " + otp + "\n\nDo not share this OTP with anyone.";

        if (emailEnabled) {
            emailService.sendEmail(newUser.getEmail(), "Verify Your Email", message);
            return new ResponseRegister(
                    "One more step to get Registration successful. Please verify your email.",
                    newUser.getEmail()
            );
        } else {
            // demo mode: mail illa, OTP ah response la anuppurom
            return new ResponseRegister(
                    "Demo mode: Your OTP is " + otp,
                    newUser.getEmail()
            );
        }
    }

    //    ---------------------- Email Verify ----------------------
    public ResponseRegister2 emailVerify(RequestOtpVerify request) {
        Optional<UserEntity> userOptional =
                repo.findByEmail(request.email());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User Not Found");
        }
        UserEntity verify = userOptional.get();
        if (LocalDateTime.now().isAfter(verify.getOtpExpirationsAt())) {
            repo.deleteById(userOptional.get().getId());
            throw new RuntimeException("OTP Expired. Re-register for new OTP");
        }
        if (!verify.getOtp().equals(request.OTP())) {
            repo.deleteById(userOptional.get().getId());
            throw new RuntimeException("Invalid OTP. Try Again with re-register.");
        }
        verify.setEmailVerified(true);
        verify.setOtp(null);
        verify.setOtpExpirationsAt(null);
        repo.save(verify);
        String token = jwtUtil.generateToken(verify);
        return new ResponseRegister2(
                "Email verified successfully",
                token
        );
    }

    //    ---------------------- Login User ----------------------
    public ApiResponse2 loginUser(@Valid RequestLogin request) {
        UserEntity user;
        if(request.userName().contains("@")){
            user = repo.findByEmail(request.userName()).orElseThrow(()->new RuntimeException("User Not Found"));
        } else {
            user = repo.findByPhone(request.userName()).orElseThrow(()->new RuntimeException("User Not Found"));
        }
        // Check BCrypt password
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user);
        return new ApiResponse2(
                "Login Successfull",
                token
        );
    }

    //    ---------------------- User Check for fg-password ----------------------
    public ResponseUserNameCheck userCheck(UserNameCheck request) {
        UserEntity user;
        if(request.userName().contains("@")){
            user=repo.findByEmail(request.userName()).orElseThrow(()->new RuntimeException("User not found"));
        }
        else{
            user=repo.findByPhone(request.userName()).orElseThrow(()->new RuntimeException("User not found"));
        }

        return new ResponseUserNameCheck(
                user.getEmail()
        );
    }

    //    ---------------------- Forgot password ----------------------
    public ApiResponse forgotPassword(RequestForgotPass request) {
        UserEntity user = repo.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (request.newPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        repo.save(user);

        return new ApiResponse("Password successfully updated. Now try login.");
    }

    //    ---------------------- Get User ----------------------
    public ResponseUser getUser(String authHeader) {
        String token = authHeader.substring(7);
        Claims claims = jwtUtil.validateToken(token);
        String email = claims.getSubject();
        UserEntity user = repo.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
        ResponseUser responseUser = new ResponseUser(
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getPhone(),
                user.isEmailVerified(),
                user.getCreatedAt()
        );
        return responseUser;
    }

    public ApiResponse editUser(RequestEditUser request, String authHeader) {

        // 1. Validate Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }

        // 2. Extract JWT token
        String token = authHeader.substring(7);

        // 3. Extract email from JWT claims
        Claims claims = jwtUtil.validateToken(token);
        String email = claims.getSubject();

        // 4. Fetch user using email
        UserEntity user = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 5. Update only the allowed fields
        user.setFirstname(request.firstName());
        user.setLastname(request.lastName());
        user.setPhone(request.phone());

        // 6. Save updated user
        repo.save(user);

        // 7. Return response
        return new ApiResponse("User details updated successfully");
    }

}
