package com.tomas.backend.repository;

import com.tomas.backend.entity.Categoria;
import com.tomas.backend.enums.TipoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria,Long> {
        Boolean existsByNombreCategoria(TipoCategoria tipo);
        TipoCategoria findByNombreCategoria(String nombreCategoria);
}


