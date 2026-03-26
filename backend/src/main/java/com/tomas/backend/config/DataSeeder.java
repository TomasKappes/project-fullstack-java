package com.tomas.backend.config;

import com.tomas.backend.entity.Categoria;
import com.tomas.backend.enums.TipoCategoria;
import com.tomas.backend.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoriaRepository categoryRepository;

    @Override
    public void run(String... args) {

        for (TipoCategoria tipo : TipoCategoria.values()) {

            if (!categoryRepository.existsByNombreCategoria(tipo)){
                Categoria categoria= new Categoria();
                categoria.setNombreCategoria(tipo);
                categoria.setNombre(tipo.toString());
                categoria.setActivo(true);

                categoryRepository.save(categoria);
            }
        }

        System.out.println("Seed de categorías sincronizado con enum");
    }
}

