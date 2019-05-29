package com.kriger.guidinglight.controller;

import com.kriger.guidinglight.model.User;
import com.kriger.guidinglight.service.EmailService;
import com.kriger.guidinglight.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/registration")
    public String registration(Model model){
        User user = new User();
        model.addAttribute("user", user);
        return "registration";
    }

    @PostMapping("/registration")
    public String saveRegistration(@ModelAttribute("user") @Valid User user) {
        boolean userSaved = userService.registerUser(user);
        if (!userSaved) {
            return "redirect:/login?error";
        }
        emailService.sendMessage(user);
        return "auth/login";
    }

    @GetMapping("/activation/{code}")
    public String activation(@PathVariable("code") String code) {
        boolean isActivation = userService.userActivation(code);
        if (!isActivation) {
            return "auth/login";
        }
        return "redirect:/login?activation";
    }

}