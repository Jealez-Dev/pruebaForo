package com.example.foro.dto;

import java.time.LocalDateTime;

public record CommentResponseDTO(
        Long id,
        Long hiloId,
        Long comentarioPadreId,
        String contenido,
        String autor,
        LocalDateTime fechaCreacion
) {
}
