package com.example.foro.service;

import com.example.foro.dto.VoteRequestDTO;
import com.example.foro.dto.VoteResponseDTO;
import com.example.foro.model.Comment;
import com.example.foro.model.Hilo;
import com.example.foro.model.Usuario;
import com.example.foro.model.Vote;
import com.example.foro.model.VoteState;
import com.example.foro.repository.CommentRepository;
import com.example.foro.repository.HiloRepository;
import com.example.foro.repository.UsuarioRepository;
import com.example.foro.repository.VoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class VoteService {

    private final VoteRepository voteRepository;
    private final UsuarioRepository usuarioRepository;
    private final HiloRepository hiloRepository;
    private final CommentRepository commentRepository;

    public VoteService(
            VoteRepository voteRepository,
            UsuarioRepository usuarioRepository,
            HiloRepository hiloRepository,
            CommentRepository commentRepository) {
        this.voteRepository = voteRepository;
        this.usuarioRepository = usuarioRepository;
        this.hiloRepository = hiloRepository;
        this.commentRepository = commentRepository;
    }

    public VoteResponseDTO votarHilo(Long hiloId, VoteRequestDTO request) {
        Hilo hilo = hiloRepository.findById(hiloId)
                .orElseThrow(() -> noEncontrado("hilo", hiloId));
        Usuario usuario = obtenerUsuario(request.usuarioId());
        Vote voto = voteRepository.findByUsuarioAndHilo(usuario, hilo).orElse(null);
        actualizarVoto(voto, usuario, hilo, null, request.estado());
        return resumenHilo(hilo.getId(), request.usuarioId());
    }

    public VoteResponseDTO votarComentario(Long comentarioId, VoteRequestDTO request) {
        Comment comentario = commentRepository.findById(comentarioId)
                .orElseThrow(() -> noEncontrado("comentario", comentarioId));
        Usuario usuario = obtenerUsuario(request.usuarioId());
        Vote voto = voteRepository.findByUsuarioAndComentario(usuario, comentario).orElse(null);
        actualizarVoto(voto, usuario, null, comentario, request.estado());
        return resumenComentario(comentario.getId(), request.usuarioId());
    }

    @Transactional(readOnly = true)
    public VoteResponseDTO resumenHilo(Long hiloId, Long usuarioId) {
        Hilo hilo = hiloRepository.findById(hiloId)
                .orElseThrow(() -> noEncontrado("hilo", hiloId));
        Usuario usuario = obtenerUsuario(usuarioId);
        VoteState estado = voteRepository.findByUsuarioAndHilo(usuario, hilo)
                .map(Vote::getEstado)
                .orElse(VoteState.NONE);
        return new VoteResponseDTO(
                voteRepository.countByHiloAndEstado(hilo, VoteState.UPVOTE),
                voteRepository.countByHiloAndEstado(hilo, VoteState.DOWNVOTE),
                estado);
    }

    @Transactional(readOnly = true)
    public VoteResponseDTO resumenComentario(Long comentarioId, Long usuarioId) {
        Comment comentario = commentRepository.findById(comentarioId)
                .orElseThrow(() -> noEncontrado("comentario", comentarioId));
        Usuario usuario = obtenerUsuario(usuarioId);
        VoteState estado = voteRepository.findByUsuarioAndComentario(usuario, comentario)
                .map(Vote::getEstado)
                .orElse(VoteState.NONE);
        return new VoteResponseDTO(
                voteRepository.countByComentarioAndEstado(comentario, VoteState.UPVOTE),
                voteRepository.countByComentarioAndEstado(comentario, VoteState.DOWNVOTE),
                estado);
    }

    private void actualizarVoto(
            Vote voto,
            Usuario usuario,
            Hilo hilo,
            Comment comentario,
            VoteState estado) {
        if (estado == VoteState.NONE) {
            if (voto != null) {
                voteRepository.delete(voto);
            }
            return;
        }
        if (voto == null) {
            voto = new Vote();
            voto.setUsuario(usuario);
            voto.setHilo(hilo);
            voto.setComentario(comentario);
        }
        voto.setEstado(estado);
        voteRepository.save(voto);
    }

    private Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> noEncontrado("usuario", id));
    }

    private ResponseStatusException noEncontrado(String tipo, Long id) {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No existe el " + tipo + " con id " + id);
    }
}
