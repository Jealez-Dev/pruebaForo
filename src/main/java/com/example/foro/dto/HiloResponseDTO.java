package com.example.foro.dto;

import com.example.foro.model.Categoria;

import java.time.LocalDateTime;

public record HiloResponseDTO(
        Long id,
        String titulo,
        String contenido,
        String autor,
        Categoria categoria,
        LocalDateTime fechaCreacion
) {
}
