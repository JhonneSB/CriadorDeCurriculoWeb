package com.senai.mikajuca.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.senai.mikajuca.model.User;
import com.senai.mikajuca.service.UserService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Controller
public class LoginController {

    @Autowired
    private final UserService userService;

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("erro", "");
        return "login";
    }

    @GetMapping("/contrato")
    public String homePage() {
        return "index";
    }

    @PostMapping("/register")
    public String registerUser(@Validated @ModelAttribute("user") User user,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "login";
        }

        // Verifica se o email já está cadastrado
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("error", "Usuário já cadastrado.");
            return "login";
        }

        userService.save(user);

        model.addAttribute("mensagem", "Cadastro realizado com sucesso!");
        return "redirect:/login";
    }
}