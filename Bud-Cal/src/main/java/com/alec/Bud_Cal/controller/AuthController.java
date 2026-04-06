package com.alec.Bud_Cal.controller;

import com.alec.Bud_Cal.model.LoginForm;
import com.alec.Bud_Cal.model.SignupForm;
import com.alec.Bud_Cal.model.User;
import com.alec.Bud_Cal.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private static final String SESSION_USER_EMAIL = "userEmail";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model, HttpSession session) {
        if (session.getAttribute(SESSION_USER_EMAIL) != null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("pageTitle", "Login");
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        return "login";
    }

    @GetMapping("/signup")
    public String showSignupPage(Model model, HttpSession session) {
        if (session.getAttribute(SESSION_USER_EMAIL) != null) {
            return "redirect:/dashboard";
        }

        model.addAttribute("pageTitle", "Sign Up");
        if (!model.containsAttribute("signupForm")) {
            model.addAttribute("signupForm", new SignupForm());
        }
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(
            @Valid @ModelAttribute("signupForm") SignupForm signupForm,
            BindingResult bindingResult,
            Model model,
            HttpSession session) {
        model.addAttribute("pageTitle", "Sign Up");

        if (!bindingResult.hasFieldErrors("email") && authService.findByEmail(signupForm.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "duplicate", "An account with this email already exists.");
        }

        if (bindingResult.hasErrors()) {
            return "signup";
        }

        System.out.println("Attempting to register user: " + signupForm.getEmail());
        Optional<User> user = authService.register(signupForm);
        if (user.isEmpty()) {
            bindingResult.rejectValue("email", "duplicate", "An account with this email already exists.");
            return "signup";
        }

        System.out.println("Registration successful for: " + signupForm.getEmail());
        session.setAttribute(SESSION_USER_EMAIL, user.get().getEmail());
        return "redirect:/dashboard";
    }

    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute("loginForm") LoginForm loginForm,
            BindingResult bindingResult,
            Model model,
            HttpSession session) {
        model.addAttribute("pageTitle", "Login");

        if (bindingResult.hasErrors()) {
            return "login";
        }

        System.out.println("Attempting to authenticate user: " + loginForm.getEmail());
        Optional<User> user = authService.authenticate(loginForm);
        if (user.isEmpty()) {
            System.out.println("Authentication failed for: " + loginForm.getEmail());
            model.addAttribute("authError", "Invalid email or password.");
            return "login";
        }

        System.out.println("Authentication successful for: " + loginForm.getEmail());
        session.setAttribute(SESSION_USER_EMAIL, user.get().getEmail());
        return "redirect:/dashboard";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addAttribute("logout", "true");
        return "redirect:/login";
    }
}
