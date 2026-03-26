package com.tomas.backend.controller;
import com.tomas.backend.entity.Categoria;
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
    public List<Categoria> getCategorias(){
        return categoriaService.listarCategorias();
    }

    @GetMapping("id/{idCategoria}")
    public Categoria getCategoria(@PathVariable Long idCategoria){
        return categoriaService.obtenerCategoria(idCategoria);
    }

    @PostMapping("actualizar/{idCategoria}")
    public Categoria actualizarCategoria(@Valid @RequestBody Categoria categoria, @PathVariable Long idCategoria){
        return categoriaService.actualizarCategoria(categoria,idCategoria);
    }

    @PostMapping("activar/{idCategoria}")
    public Categoria activarCategoria(@PathVariable Long idCategoria){
        return categoriaService.activarCategoria(idCategoria);
    }

    @PostMapping("desactivar/{idCategoria}")
    public Categoria desactivarCategoria(@PathVariable Long idCategoria){
        return categoriaService.desactivarCategoria(idCategoria);
    }

    @PostMapping("crear")
    public Categoria crearCategoria(@Valid @RequestBody Categoria categoria){
        return categoriaService.crearCategoria(categoria);
    }
}
