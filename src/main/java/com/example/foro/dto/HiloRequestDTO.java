package com.example.foro.dto;

import com.example.foro.model.Categoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record HiloRequestDTO(
        @NotBlank
        @Size(max = 100)
        String titulo,
        @NotBlank
        String contenido,
        @NotBlank
        String autor,
        @NotNull
        Categoria categoria
) {
}
