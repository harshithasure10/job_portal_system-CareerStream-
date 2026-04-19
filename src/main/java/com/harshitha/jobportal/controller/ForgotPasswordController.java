package com.harshitha.jobportal.controller;

import com.harshitha.jobportal.entity.PasswordResetToken;
import com.harshitha.jobportal.entity.Users;
import com.harshitha.jobportal.repository.PasswordResetTokenRepository;
import com.harshitha.jobportal.services.EmailService;
import com.harshitha.jobportal.services.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.UUID;

@Controller
public class ForgotPasswordController {

    private final UsersService usersService;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public ForgotPasswordController(UsersService usersService, PasswordResetTokenRepository tokenRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.usersService = usersService;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, HttpServletRequest request, Model model) {
        Optional<Users> userOptional = usersService.getUserByEmail(email);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            String token = UUID.randomUUID().toString();
            
            // Delete old tokens for this user if any
            tokenRepository.deleteByUser(user);
            
            PasswordResetToken resetToken = new PasswordResetToken(token, user);
            tokenRepository.save(resetToken);
            
            String resetLink = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/reset-password?token=" + token;
            emailService.sendSimpleMessage(email, "Password Reset Request", "To reset your password, click the link below:\n" + resetLink);
            
            model.addAttribute("message", "We have sent a reset password link to your email.");
        } else {
            model.addAttribute("error", "No user found with that email.");
        }
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByToken(token);
        if (resetToken.isPresent() && resetToken.get().getExpiryDate().after(new java.util.Date())) {
            model.addAttribute("token", token);
            return "reset-password";
        } else {
            model.addAttribute("error", "Invalid or expired token.");
            return "forgot-password";
        }
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token, @RequestParam("password") String password, Model model) {
        Optional<PasswordResetToken> resetTokenOptional = tokenRepository.findByToken(token);
        if (resetTokenOptional.isPresent()) {
            PasswordResetToken resetToken = resetTokenOptional.get();
            Users user = resetToken.getUser();
            user.setPassword(passwordEncoder.encode(password));
            usersService.save(user);
            
            tokenRepository.delete(resetToken);
            
            model.addAttribute("message", "You have successfully changed your password.");
            return "login";
        } else {
            model.addAttribute("error", "Invalid token.");
            return "forgot-password";
        }
    }
}
