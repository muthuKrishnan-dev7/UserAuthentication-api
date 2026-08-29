package com.project.auth.Service;

import com.project.auth.DTO.*;
import com.project.auth.Entity.UserEntity;
import com.project.auth.Util.EmailService;
import com.project.auth.Util.JwtUtil;
import com.project.auth.Util.OtpUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.project.auth.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository repo, PasswordEncoder passwordEncoder, EmailService emailService, JwtUtil jwtUtil) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    //    --- Register the User ---
    public ResponseDTO2 registerUser(RegisterDTO request) {
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
        newUser.setExpirationsAt(LocalDateTime.now().plusMinutes(3));
        newUser.setEmailVerified(false);

        repo.save(newUser);
        emailService.sendEmail(
                newUser.getEmail(),
                "Verify Your Email",
                "Your OTP is: " + otp + "\n\nDo not share this OTP with anyone."
        );

        return new ResponseDTO2(
                "Registration successful. Please verify your email.",
                newUser.getEmail()
        );
    }

    //    --- Email Verify ---
    public ResponseDTO1 emailVerify(OtpRequestDTO request) {

        Optional<UserEntity> userOptional =
                repo.findByEmail(request.email());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User Not Found");
        }

        UserEntity verify = userOptional.get();

        if (LocalDateTime.now().isAfter(verify.getExpirationsAt())) {
            throw new RuntimeException("OTP Expired. Request for new OTP");
        }

        if (!verify.getOtp().equals(request.OTP())) {
            throw new RuntimeException("Invalid OTP. Try Again");
        }

        verify.setEmailVerified(true);
        verify.setOtp(null);
        verify.setExpirationsAt(null);

        repo.save(verify);
        String token = jwtUtil.generateToken(verify);

        return new ResponseDTO1(
                "Email verified successfully",
                token,
                verify.getEmail()
        );
    }

}
