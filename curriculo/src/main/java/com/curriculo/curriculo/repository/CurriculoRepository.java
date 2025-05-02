package com.curriculo.curriculo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.curriculo.curriculo.model.Curriculo;

public interface CurriculoRepository extends JpaRepository<Curriculo, Long> {
}

