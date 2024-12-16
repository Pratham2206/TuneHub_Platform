package com.tunehub.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tunehub.entities.Song;
import com.tunehub.entities.Users;
import com.tunehub.services.SongService;
import com.tunehub.services.UsersService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UsersController {
    @Autowired
    UsersService services;

    @Autowired
    SongService songService;

    @Autowired
    private JavaMailSender mailSender; // Mail sender to send emails

    @PostMapping("/register")
    public String addUsers(@ModelAttribute Users user) {
        boolean userStatus = services.emailExists(user.getEmail());
        if (!userStatus) {
            services.addUser(user);
            System.out.println("User added");
        } else {
            System.out.println("User already exists");
        }
        return "login";
    }

    @PostMapping("/validate")
    public String validate(@RequestParam("email") String email, @RequestParam("password") String password,
                           HttpSession session, Model model) {
        if (services.validateUser(email, password)) {
            String role = services.getRole(email);
            session.setAttribute("email", email);

            if (role.equals("admin")) {
                return "adminHome";
            } else {
                Users user = services.getUser(email);
                boolean userStatus = user.isPremium();
                List<Song> songsList;
                if (userStatus) {
                    songsList = songService.fetchAllSongs();
                } else {
                    songsList = songService.fetchLimitedSongs(2); // Fetch limited songs for non-premium users
                }
                model.addAttribute("isPremium", userStatus);
                model.addAttribute("songs", songsList);
                return "customerHome";
            }
        } else {
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "login";
    }

    @PostMapping("/sendResetLink")
    public String sendResetLink(@RequestParam("email") String email, Model model) {
        Users user = services.getUserByEmail(email);
        if (user == null) {
            model.addAttribute("error", "Email not found");
            return "forgot-password";
        }

        // Generate a random reset token
        String token = UUID.randomUUID().toString();
        services.createPasswordResetTokenForUser(user, token);

        // Send email
        String resetUrl = "http://localhost:8080/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the link below:\n" + resetUrl);

        mailSender.send(message);

        model.addAttribute("message", "A password reset link has been sent to your email.");
        return "login"; // Redirect to login page with success message
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam("token") String token, @RequestParam("password") String password, Model model) {
        Users user = services.getUserByPasswordResetToken(token);
        if (user == null) {
            model.addAttribute("error", "Invalid token");
            return "reset-password"; // return reset-password view
        }

        services.updatePassword(user, password);
        model.addAttribute("message", "Password has been reset successfully");
        return "login"; // Redirect to login after successful reset
    }


    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }
}