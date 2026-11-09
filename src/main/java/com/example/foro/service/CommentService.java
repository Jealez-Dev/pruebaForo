package com.example.foro.service;

import com.example.foro.dto.CommentRequestDTO;
import com.example.foro.dto.CommentResponseDTO;
import com.example.foro.model.Comment;
import com.example.foro.model.Hilo;
import com.example.foro.repository.CommentRepository;
import com.example.foro.repository.HiloRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final HiloRepository hiloRepository;

    public CommentService(CommentRepository commentRepository, HiloRepository hiloRepository) {
        this.commentRepository = commentRepository;
        this.hiloRepository = hiloRepository;
    }

    public CommentResponseDTO crear(Long hiloId, CommentRequestDTO request) {
        Hilo hilo = obtenerHilo(hiloId);
        Comment padre = request.comentarioPadreId() == null
                ? null
                : obtenerComentarioDelHilo(request.comentarioPadreId(), hilo);

        Comment comentario = new Comment();
        comentario.setContenido(request.contenido());
        comentario.setAutor(request.autor());
        comentario.setHilo(hilo);
        comentario.setComentarioPadre(padre);
        return toResponse(commentRepository.save(comentario));
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDTO> listar(Long hiloId) {
        obtenerHilo(hiloId);
        return commentRepository.findByHiloIdOrderByFechaCreacionAsc(hiloId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CommentResponseDTO actualizar(Long id, CommentRequestDTO request) {
        Comment comentario = obtenerComentario(id);
        if (request.comentarioPadreId() != null
                && !request.comentarioPadreId().equals(id)) {
            comentario.setComentarioPadre(obtenerComentarioDelHilo(
                    request.comentarioPadreId(), comentario.getHilo()));
        }
        comentario.setContenido(request.contenido());
        comentario.setAutor(request.autor());
        return toResponse(commentRepository.save(comentario));
    }

    public void eliminar(Long id) {
        commentRepository.delete(obtenerComentario(id));
    }

    private Hilo obtenerHilo(Long id) {
        return hiloRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe el hilo con id " + id));
    }

    private Comment obtenerComentario(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe el comentario con id " + id));
    }

    private Comment obtenerComentarioDelHilo(Long id, Hilo hilo) {
        Comment comentario = obtenerComentario(id);
        if (!comentario.getHilo().getId().equals(hilo.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El comentario padre pertenece a otro hilo");
        }
        return comentario;
    }

    private CommentResponseDTO toResponse(Comment comentario) {
        return new CommentResponseDTO(
                comentario.getId(),
                comentario.getHilo().getId(),
                comentario.getComentarioPadre() == null
                        ? null
                        : comentario.getComentarioPadre().getId(),
                comentario.getContenido(),
                comentario.getAutor(),
                comentario.getFechaCreacion());
    }
}
