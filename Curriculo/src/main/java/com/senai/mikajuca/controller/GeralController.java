package com.senai.mikajuca.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.senai.mikajuca.model.Curriculo;
import com.senai.mikajuca.model.User;
import com.senai.mikajuca.repository.CurriculoRepository;
import com.senai.mikajuca.repository.UserRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Controller
public class GeralController {

    @Autowired
    private CurriculoRepository curriculoRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/main")
    public String getCurriculosUsuarioLogado(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                             .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        List<Curriculo> curriculos = curriculoRepository.findByUser(user);
        model.addAttribute("curriculos", curriculos); // Passa os currículos para a view
        
        return "index"; // Nome da view para renderizar (index.html)
    }

}
