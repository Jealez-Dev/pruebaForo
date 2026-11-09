package com.example.foro.repository;

import com.example.foro.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByHiloIdOrderByFechaCreacionAsc(Long hiloId);

    List<Comment> findByComentarioPadreIdOrderByFechaCreacionAsc(Long comentarioPadreId);
}
