package com.example.foro.controller;

import com.example.foro.dto.HiloRequestDTO;
import com.example.foro.dto.HiloResponseDTO;
import com.example.foro.model.Categoria;
import com.example.foro.service.HiloService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hilos")
public class HiloController {

    private final HiloService hiloService;

    public HiloController(HiloService hiloService) {
        this.hiloService = hiloService;
    }

    @PostMapping
    public ResponseEntity<HiloResponseDTO> crear(@Valid @RequestBody HiloRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hiloService.crear(request));
    }

    @GetMapping
    public List<HiloResponseDTO> listar() {
        return hiloService.listar();
    }

    @GetMapping("/buscar")
    public List<HiloResponseDTO> buscar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Categoria categoria) {
        return hiloService.buscar(q, categoria);
    }

    @GetMapping("/{id}")
    public HiloResponseDTO obtenerPorId(@PathVariable Long id) {
        return hiloService.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public HiloResponseDTO actualizar(
            @PathVariable Long id,
            @Valid @RequestBody HiloRequestDTO request) {
        return hiloService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        hiloService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
