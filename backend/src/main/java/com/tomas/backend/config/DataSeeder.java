package com.tomas.backend.config;

import com.tomas.backend.entity.Categoria;
import com.tomas.backend.entity.Producto;
import com.tomas.backend.enums.TipoCategoria;
import com.tomas.backend.excetions.custom.ResourceNotFoundException;
import com.tomas.backend.repository.CategoriaRepository;
import com.tomas.backend.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoriaRepository categoryRepository;
    private final ProductoRepository productoRepository;

    @Override
    public void run(String... args) {

        for (TipoCategoria tipo : TipoCategoria.values()) {

            if (!categoryRepository.existsByNombreCategoria(tipo)){
                Categoria categoria= new Categoria();
                categoria.setNombreCategoria(tipo);
                categoria.setNombre(tipo.name());
                categoria.setActivo(true);

                categoryRepository.save(categoria);
            }
        }

        System.out.println("Seed de categorías sincronizado con enum");

        if(productoRepository.count()>0){
            return;
        }

        List<Categoria> categorias = categoryRepository.findAll();
        Map<String, Categoria> mapaCategorias = categorias.stream()
                .collect(Collectors.toMap(Categoria::getNombre, categoria -> categoria));

        Categoria cpuIntel = mapaCategorias.get("CPU_INTEL");
        Categoria cpuAmd = mapaCategorias.get("CPU_AMD");
        Categoria gpu = mapaCategorias.get("GPU");
        Categoria ram = mapaCategorias.get("RAM");
        Categoria almacenamiento = mapaCategorias.get("ALMACENAMIENTO");
        Categoria motherboardIntel = mapaCategorias.get("MOTHERBOARD_INTEL");
        Categoria motherboardAmd = mapaCategorias.get("MOTHERBOARD_AMD");



        List<Producto> productos = List.of(

                // GPUs
                new Producto("RTX 5070 Ti",gpu),
                new Producto("RX 7600 XT", gpu),
                new Producto("RX 7700 XT", gpu),
                new Producto("RTX 3080 Ti", gpu),
                new Producto("RX 580", gpu),
                new Producto("RX 6800 XT", gpu),
                new Producto("GTX 1080", gpu),
                new Producto("GTX 1060", gpu),

                // CPUs AMD
                new Producto("Ryzen 5 8600G", cpuAmd),
                new Producto("Ryzen 5 5500X3D", cpuAmd),
                new Producto("Ryzen 7 7700X", cpuAmd),
                new Producto("Ryzen 7 8700G", cpuAmd),

                // CPUs Intel
                new Producto("i5-12400F", cpuIntel),
                new Producto("i5-14600K", cpuIntel),
                new Producto("i7-13700F", cpuIntel),
                new Producto("i7-14700F", cpuIntel),

                // Motherboards
                new Producto("ASUS ROG Strix B650E-F", motherboardAmd),
                new Producto("MSI Tomahawk Z790 WiFi", motherboardIntel),
                new Producto("Gigabyte B550 Aorus Elite V2", motherboardAmd),
                new Producto("ASRock B650M RipTide", motherboardAmd),
                new Producto("ASUS Prime Z690-P", motherboardIntel),
                new Producto("Gigabyte X670 Aorus Elite AX", motherboardAmd),
                new Producto("MSI B550M Pro-VDH WiFi", motherboardAmd),
                new Producto("ASRock Z790 Steel Legend", motherboardIntel),

                // RAM
                new Producto("Corsair Vengeance 32GB DDR5", ram),
                new Producto("Corsair Vengeance 16GB DDR5", ram),
                new Producto("Corsair Vengeance 8GB DDR5", ram),
                new Producto("Kingston 32GB DDR4", ram),
                new Producto("Kingston 16GB DDR4", ram),
                new Producto("Kingston 8GB DDR4", ram),
                new Producto("Patriot 16GB DDR4", ram),
                new Producto("TeamGroup 8GB DDR4", ram),

                // Storage
                new Producto("Kingston 256GB SSD", almacenamiento),
                new Producto("Kingston 512GB SSD", almacenamiento),
                new Producto("Seagate 2TB HDD", almacenamiento),
                new Producto("Toshiba 1TB HDD", almacenamiento),
                new Producto("NVMe 1TB SSD", almacenamiento),
                new Producto("NVMe 512GB SSD", almacenamiento),
                new Producto("Kingston 256GB NVMe", almacenamiento),
                new Producto("Kingston 1TB NVMe", almacenamiento)
        );

        if (cpuIntel == null || cpuAmd == null || gpu == null || motherboardIntel == null || ram == null || motherboardAmd==null) {
            throw new ResourceNotFoundException("Error en seed: categorías faltantes");
        }

        productoRepository.saveAll(productos);

    }
}

