package com.senai.mikajuca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.senai.mikajuca.model.Curriculo;
import com.senai.mikajuca.model.User;

public interface CurriculoRepository extends JpaRepository<Curriculo, Long> {
    List<Curriculo> findByUser(User user);
}
