package com.example.foro.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentRequestDTO(
        @NotBlank String contenido,
        @NotBlank String autor,
        Long comentarioPadreId
) {
}
