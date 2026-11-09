package com.example.foro.dto;

import com.example.foro.model.VoteState;
import jakarta.validation.constraints.NotNull;

public record VoteRequestDTO(
        @NotNull Long usuarioId,
        @NotNull VoteState estado
) {
}
