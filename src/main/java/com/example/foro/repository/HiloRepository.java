package com.example.foro.repository;

import com.example.foro.model.Categoria;
import com.example.foro.model.Hilo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HiloRepository extends JpaRepository<Hilo, Long> {

    List<Hilo> findByCategoriaOrderByFechaCreacionDesc(Categoria categoria);

    List<Hilo> findAllByOrderByFechaCreacionDesc();
}
