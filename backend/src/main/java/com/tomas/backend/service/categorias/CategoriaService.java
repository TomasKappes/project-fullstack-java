package com.tomas.backend.service.categorias;

import com.tomas.backend.DTOs.categoria.CategoriaCreateDTO;
import com.tomas.backend.DTOs.categoria.CategoriaRequestDTO;
import com.tomas.backend.DTOs.categoria.CategoriaResponseDTO;
import com.tomas.backend.entity.Categoria;
import com.tomas.backend.excetions.custom.BadRequestException;
import com.tomas.backend.excetions.custom.ConflictException;
import com.tomas.backend.excetions.custom.ResourceNotFoundException;
import com.tomas.backend.mappers.CategoriaMapper;
import com.tomas.backend.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaService(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    public List<CategoriaResponseDTO> listarCategorias() {
        List<CategoriaResponseDTO> categoriasDTO = new ArrayList<>();

        for (Categoria categoria : categoriaRepository.findAll()) {
            categoriasDTO.add(categoriaMapper.toResponseDTO(categoria));
        }
        return categoriasDTO;
    }

    public CategoriaResponseDTO obtenerCategoria(Long idCategoria) {

        Categoria optCategoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new ResourceNotFoundException("No existe una categoria con el id: "+idCategoria));

        if(!optCategoria.isActivo()){
            throw new ConflictException("La categoria se encuentra desactivada");
        }

        return categoriaMapper.toResponseDTO(optCategoria);

    }

    public CategoriaResponseDTO crearCategoria(CategoriaCreateDTO categoriaCreateDTO) {
        if (categoriaCreateDTO.getNombre()==null) {
            throw new BadRequestException("La categoria debe tener un nombre");
        }

        Categoria categoria = categoriaMapper.toEntity(categoriaCreateDTO);
        Categoria categoriaGuardada = categoriaRepository.save(categoria);
        return categoriaMapper.toResponseDTO(categoriaGuardada);
    }

    public CategoriaResponseDTO actualizarCategoria(CategoriaRequestDTO categoriaRequestDTO, Long idCategoria) {
        Categoria optCategoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la categoria con el id: "+idCategoria));

        if (!optCategoria.isActivo()) {
            throw new ConflictException("La categoria se encuentra desactivada");
        }

        if (categoriaRequestDTO.getNombre()==null) {
            throw new BadRequestException("La categoria debe tener un nombre");
        }

        categoriaMapper.toUpdateEntity(categoriaRequestDTO, optCategoria);
        Categoria categoriaGuardada = categoriaRepository.save(optCategoria);
        return categoriaMapper.toResponseDTO(categoriaGuardada);
    }

    public CategoriaResponseDTO desactivarCategoria(Long idCategoria) {
        Categoria optCategoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la categoria con el id: "+idCategoria));

        optCategoria.setActivo(false);
        Categoria categoriaGuardada = categoriaRepository.save(optCategoria);
        return categoriaMapper.toResponseDTO(categoriaGuardada);
    }

    public CategoriaResponseDTO activarCategoria(Long idCategoria) {
        Categoria optCategoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la categoria con el id: "+idCategoria));

        optCategoria.setActivo(true);
        Categoria categoriaGuardada = categoriaRepository.save(optCategoria);
        return categoriaMapper.toResponseDTO(categoriaGuardada);
    }
}