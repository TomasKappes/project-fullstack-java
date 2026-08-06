package com.tomas.backend.mappers;
import com.tomas.backend.DTOs.categoria.CategoriaCreateDTO;
import com.tomas.backend.DTOs.categoria.CategoriaRequestDTO;
import com.tomas.backend.DTOs.categoria.CategoriaResponseDTO;
import com.tomas.backend.entity.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    public CategoriaMapper() {}

    public CategoriaResponseDTO toResponseDTO(Categoria categoria) {
        CategoriaResponseDTO categoriaResponseDTO = new CategoriaResponseDTO();
        categoriaResponseDTO.setCategoriaId(categoria.getId());
        categoriaResponseDTO.setNombre(categoria.getNombre());
        return categoriaResponseDTO;
    }

    public Categoria toEntity(CategoriaCreateDTO categoriaCreateDTO) {
        Categoria categoria = new Categoria();
        categoria.setNombre(categoriaCreateDTO.getNombre());
        categoria.setActivo(true);
        return categoria;
    }

    public void toUpdateEntity(CategoriaRequestDTO categoriaRequestDTO, Categoria categoria) {
        categoria.setNombre(categoriaRequestDTO.getNombre());
    }
}