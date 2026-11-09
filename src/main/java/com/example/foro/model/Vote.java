package com.example.foro.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "votos", uniqueConstraints = {
        @jakarta.persistence.UniqueConstraint(name = "uk_voto_usuario_hilo", columnNames = {
                "usuario_id", "hilo_id"
        }),
        @jakarta.persistence.UniqueConstraint(name = "uk_voto_usuario_comentario", columnNames = {
                "usuario_id", "comentario_id"
        })
})
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hilo_id")
    private Hilo hilo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comentario_id")
    private Comment comentario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private VoteState estado;

    public Vote() {
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Hilo getHilo() {
        return hilo;
    }

    public void setHilo(Hilo hilo) {
        this.hilo = hilo;
    }

    public Comment getComentario() {
        return comentario;
    }

    public void setComentario(Comment comentario) {
        this.comentario = comentario;
    }

    public VoteState getEstado() {
        return estado;
    }

    public void setEstado(VoteState estado) {
        this.estado = estado;
    }
}
