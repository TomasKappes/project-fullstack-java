package com.tomas.backend.controller;
import com.tomas.backend.DTOs.categoria.CategoriaCreateDTO;
import com.tomas.backend.DTOs.categoria.CategoriaRequestDTO;
import com.tomas.backend.DTOs.categoria.CategoriaResponseDTO;
import com.tomas.backend.service.categorias.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriasController {
    private final CategoriaService categoriaService;

    public CategoriasController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public List<CategoriaResponseDTO> getCategorias(){
        return categoriaService.listarCategorias();
    }

    @GetMapping("id/{idCategoria}")
    public CategoriaResponseDTO getCategoria(@PathVariable Long idCategoria){
        return categoriaService.obtenerCategoria(idCategoria);
    }

    @PostMapping("actualizar/{idCategoria}")
    public CategoriaResponseDTO actualizarCategoria(@Valid @RequestBody CategoriaRequestDTO categoriaRequestDTO, @PathVariable Long idCategoria){
        return categoriaService.actualizarCategoria(categoriaRequestDTO,idCategoria);
    }

    @PostMapping("activar/{idCategoria}")
    public CategoriaResponseDTO activarCategoria(@PathVariable Long idCategoria){
        return categoriaService.activarCategoria(idCategoria);
    }

    @PostMapping("desactivar/{idCategoria}")
    public CategoriaResponseDTO desactivarCategoria(@PathVariable Long idCategoria){
        return categoriaService.desactivarCategoria(idCategoria);
    }

    @PostMapping("crear")
    public CategoriaResponseDTO crearCategoria(@Valid @RequestBody CategoriaCreateDTO categoriaCreateDTO){
        return categoriaService.crearCategoria(categoriaCreateDTO);
    }
}