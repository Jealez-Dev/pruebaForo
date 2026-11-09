package com.example.foro.controller;

import com.example.foro.dto.VoteRequestDTO;
import com.example.foro.dto.VoteResponseDTO;
import com.example.foro.service.VoteService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping("/hilos/{hiloId}/voto")
    public VoteResponseDTO votarHilo(
            @PathVariable Long hiloId,
            @Valid @RequestBody VoteRequestDTO request) {
        return voteService.votarHilo(hiloId, request);
    }

    @GetMapping("/hilos/{hiloId}/voto")
    public VoteResponseDTO resumenHilo(
            @PathVariable Long hiloId,
            @RequestParam Long usuarioId) {
        return voteService.resumenHilo(hiloId, usuarioId);
    }

    @PostMapping("/comentarios/{comentarioId}/voto")
    public VoteResponseDTO votarComentario(
            @PathVariable Long comentarioId,
            @Valid @RequestBody VoteRequestDTO request) {
        return voteService.votarComentario(comentarioId, request);
    }

    @GetMapping("/comentarios/{comentarioId}/voto")
    public VoteResponseDTO resumenComentario(
            @PathVariable Long comentarioId,
            @RequestParam Long usuarioId) {
        return voteService.resumenComentario(comentarioId, usuarioId);
    }
}
