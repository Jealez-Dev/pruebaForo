package com.example.foro.repository;

import com.example.foro.model.Comment;
import com.example.foro.model.Hilo;
import com.example.foro.model.Usuario;
import com.example.foro.model.Vote;
import com.example.foro.model.VoteState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByUsuarioAndHilo(Usuario usuario, Hilo hilo);

    Optional<Vote> findByUsuarioAndComentario(Usuario usuario, Comment comentario);

    long countByHiloAndEstado(Hilo hilo, VoteState estado);

    long countByComentarioAndEstado(Comment comentario, VoteState estado);
}
