package com.example.foro.controller;

import com.example.foro.dto.CommentRequestDTO;
import com.example.foro.dto.CommentResponseDTO;
import com.example.foro.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/hilos/{hiloId}/comentarios")
    public ResponseEntity<CommentResponseDTO> crear(
            @PathVariable Long hiloId,
            @Valid @RequestBody CommentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.crear(hiloId, request));
    }

    @GetMapping("/hilos/{hiloId}/comentarios")
    public List<CommentResponseDTO> listar(@PathVariable Long hiloId) {
        return commentService.listar(hiloId);
    }

    @PutMapping("/comentarios/{id}")
    public CommentResponseDTO actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequestDTO request) {
        return commentService.actualizar(id, request);
    }

    @DeleteMapping("/comentarios/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        commentService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
