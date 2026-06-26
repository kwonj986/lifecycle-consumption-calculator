package com.lifecycleincome.app.controller;

import com.lifecycleincome.app.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor

public class UserController {
    private final UserService userService;

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam("username") String username,
                         @RequestParam("password") String password) {
        try {
            userService.create(username, password);
            return "redirect:/login";
        } catch (Exception e) {
            return "redirect:/user/signup?error";
        }
    }

    @PostMapping("/delete")
    public String deleteUser(Principal principal, HttpServletRequest request) {
        if (principal != null) {
            userService.delete(principal.getName());

            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            SecurityContextHolder.clearContext();
        }
        return "redirect:/";
    }

    @GetMapping("/settings")
    public String settingsPage(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            model.addAttribute("username", username);
        }
        return "settings";
    }

    @PostMapping("/settings")
    public String updateProfile(@RequestParam("username") String username,
                                @RequestParam("password") String password) {
        try {
            userService.update(username, password);
            return "redirect:/login";
        } catch (Exception e) {
            return "redirect:/";
        }
    }
}
