package com.tomas.backend.service.categorias;

import com.tomas.backend.entity.Categoria;
import com.tomas.backend.excetions.custom.BadRequestException;
import com.tomas.backend.excetions.custom.ConflictException;
import com.tomas.backend.excetions.custom.ResourceNotFoundException;
import com.tomas.backend.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    public Categoria obtenerCategoria(Long idCategoria) {

        Categoria optCategoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una categoria con el id: "+idCategoria));

        if(!optCategoria.isActivo()){
            throw new ConflictException("La categoria se encuentra desactivada");
        }

        return optCategoria;

    }

    public Categoria crearCategoria(Categoria categoria) {
        if (categoria.getNombre()==null) {
            throw new BadRequestException("La categoria debe tener un nombre");
        }

        return categoriaRepository.save(categoria);
    }

    public Categoria actualizarCategoria(Categoria categoria, Long idCategoria) {
        Categoria optCategoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la categoria con el id: "+idCategoria));

        if (!optCategoria.isActivo()) {
            throw new ConflictException("La categoria se encuentra desactivada");
        }

        if (categoria.getNombre()==null) {
            throw new BadRequestException("La categoria debe tener un nombre");
        }

        optCategoria.setNombre(categoria.getNombre());
        return categoriaRepository.save(optCategoria);
    }

    public Categoria desactivarCategoria(Long idCategoria) {
        Categoria optCategoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la categoria con el id: "+idCategoria));

        optCategoria.setActivo(false);
        return categoriaRepository.save(optCategoria);
    }

    public Categoria activarCategoria(Long idCategoria) {
        Categoria optCategoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la categoria con el id: "+idCategoria));

        optCategoria.setActivo(true);
        return categoriaRepository.save(optCategoria);
    }
}


