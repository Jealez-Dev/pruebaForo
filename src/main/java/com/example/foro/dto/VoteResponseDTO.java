package com.example.foro.dto;

import com.example.foro.model.VoteState;

public record VoteResponseDTO(
        long upvotes,
        long downvotes,
        VoteState estado
) {
}
