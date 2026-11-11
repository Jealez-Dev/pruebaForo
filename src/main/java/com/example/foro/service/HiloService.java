package com.example.foro.service;

import com.example.foro.dto.HiloRequestDTO;
import com.example.foro.dto.HiloResponseDTO;
import com.example.foro.model.Comment;
import com.example.foro.model.Hilo;
import com.example.foro.repository.CommentRepository;
import com.example.foro.repository.HiloRepository;
import com.example.foro.repository.VoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@Transactional
public class HiloService {

    private static final Pattern SEPARADOR_TEXTO = Pattern.compile("[^\\p{L}\\p{N}]+");

    private final HiloRepository hiloRepository;
    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;

    public HiloService(
            HiloRepository hiloRepository,
            CommentRepository commentRepository,
            VoteRepository voteRepository) {
        this.hiloRepository = hiloRepository;
        this.commentRepository = commentRepository;
        this.voteRepository = voteRepository;
    }

    public HiloResponseDTO crear(HiloRequestDTO request) {
        Hilo hilo = new Hilo();
        hilo.setTitulo(request.titulo());
        hilo.setContenido(request.contenido());
        hilo.setAutor(request.autor());
        hilo.setCategoria(request.categoria());
        return toResponse(hiloRepository.save(hilo));
    }

    @Transactional(readOnly = true)
    public List<HiloResponseDTO> listar() {
        return hiloRepository.findAllByOrderByFechaCreacionDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HiloResponseDTO> buscar(String q, com.example.foro.model.Categoria categoria) {
        List<Hilo> candidatos = categoria == null
                ? hiloRepository.findAllByOrderByFechaCreacionDesc()
                : hiloRepository.findByCategoriaOrderByFechaCreacionDesc(categoria);

        return candidatos.stream()
                .filter(hilo -> coincide(hilo, q))
                .map(this::toResponse)
                .toList();
    }

    private boolean coincide(Hilo hilo, String q) {
        if (q == null || q.isBlank()) {
            return true;
        }

        String consulta = normalizar(q);
        int distanciaMaxima = Math.max(1, consulta.length() / 4);
        return coincideConTexto(hilo.getTitulo(), consulta, distanciaMaxima)
                || coincideConTexto(hilo.getContenido(), consulta, distanciaMaxima);
    }

    private boolean coincideConTexto(String texto, String consulta, int distanciaMaxima) {
        String textoNormalizado = normalizar(texto);
        if (textoNormalizado.contains(consulta)) {
            return true;
        }

        return Arrays.stream(SEPARADOR_TEXTO.split(textoNormalizado))
                .filter(token -> !token.isBlank())
                .anyMatch(token -> distanciaLevenshtein(token, consulta) <= distanciaMaxima);
    }

    private String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }

    private int distanciaLevenshtein(String izquierda, String derecha) {
        if (izquierda.length() < derecha.length()) {
            return distanciaLevenshtein(derecha, izquierda);
        }

        int[] filaAnterior = new int[derecha.length() + 1];
        for (int indice = 0; indice <= derecha.length(); indice++) {
            filaAnterior[indice] = indice;
        }

        for (int fila = 1; fila <= izquierda.length(); fila++) {
            int[] filaActual = new int[derecha.length() + 1];
            filaActual[0] = fila;
            for (int columna = 1; columna <= derecha.length(); columna++) {
                int coste = izquierda.charAt(fila - 1) == derecha.charAt(columna - 1) ? 0 : 1;
                filaActual[columna] = Math.min(
                        Math.min(filaActual[columna - 1] + 1, filaAnterior[columna] + 1),
                        filaAnterior[columna - 1] + coste);
            }
            filaAnterior = filaActual;
        }
        return filaAnterior[derecha.length()];
    }

    @Transactional(readOnly = true)
    public HiloResponseDTO obtenerPorId(Long id) {
        return toResponse(obtenerEntidad(id));
    }

    public HiloResponseDTO actualizar(Long id, HiloRequestDTO request) {
        Hilo hilo = obtenerEntidad(id);
        hilo.setTitulo(request.titulo());
        hilo.setContenido(request.contenido());
        hilo.setCategoria(request.categoria());
        return toResponse(hiloRepository.save(hilo));
    }

    public void eliminar(Long id) {
        Hilo hilo = obtenerEntidad(id);

        List<Comment> comentarios = commentRepository.findByHiloIdOrderByFechaCreacionAsc(id);
        voteRepository.deleteByHilo(hilo);
        if (!comentarios.isEmpty()) {
            voteRepository.deleteByComentarios(comentarios);

            // Primero se eliminan las referencias entre respuestas para poder borrar todos los comentarios.
            comentarios.forEach(comentario -> comentario.setComentarioPadre(null));
            commentRepository.flush();
            commentRepository.deleteAll(comentarios);
            commentRepository.flush();
        }

        hiloRepository.delete(hilo);
    }

    private Hilo obtenerEntidad(Long id) {
        return hiloRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe el hilo con id " + id));
    }

    private HiloResponseDTO toResponse(Hilo hilo) {
        return new HiloResponseDTO(
                hilo.getId(),
                hilo.getTitulo(),
                hilo.getContenido(),
                hilo.getAutor(),
                hilo.getCategoria(),
                hilo.getFechaCreacion());
    }
}
