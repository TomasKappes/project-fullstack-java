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

import java.math.BigDecimal;
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
                new Producto("RTX 5070 Ti", gpu, BigDecimal.valueOf(900), 20),
                new Producto("RX 7600 XT", gpu, BigDecimal.valueOf(400), 20),
                new Producto("RX 7700 XT", gpu, BigDecimal.valueOf(540), 20),
                new Producto("RTX 3080 Ti", gpu, BigDecimal.valueOf(720), 20),
                new Producto("RX 580", gpu, BigDecimal.valueOf(150), 20),
                new Producto("RX 6800 XT", gpu, BigDecimal.valueOf(600), 20),
                new Producto("GTX 1080", gpu, BigDecimal.valueOf(220), 20),
                new Producto("GTX 1060", gpu, BigDecimal.valueOf(120), 20),

                // CPUs AMD
                new Producto("Ryzen 5 8600G", cpuAmd, BigDecimal.valueOf(280), 20),
                new Producto("Ryzen 5 5500X3D", cpuAmd, BigDecimal.valueOf(240), 20),
                new Producto("Ryzen 7 7700X", cpuAmd, BigDecimal.valueOf(380), 20),
                new Producto("Ryzen 7 8700G", cpuAmd, BigDecimal.valueOf(400), 20),

                // CPUs Intel
                new Producto("i5-12400F", cpuIntel, BigDecimal.valueOf(190), 20),
                new Producto("i5-14600K", cpuIntel, BigDecimal.valueOf(360), 20),
                new Producto("i7-13700F", cpuIntel, BigDecimal.valueOf(420), 20),
                new Producto("i7-14700F", cpuIntel, BigDecimal.valueOf(460), 20),

                // Motherboards
                new Producto("ASUS ROG Strix B650E-F", motherboardAmd, BigDecimal.valueOf(340), 20),
                new Producto("MSI Tomahawk Z790 WiFi", motherboardIntel, BigDecimal.valueOf(360), 20),
                new Producto("Gigabyte B550 Aorus Elite V2", motherboardAmd, BigDecimal.valueOf(180), 20),
                new Producto("ASRock B650M RipTide", motherboardAmd, BigDecimal.valueOf(220), 20),
                new Producto("ASUS Prime Z690-P", motherboardIntel, BigDecimal.valueOf(200), 20),
                new Producto("Gigabyte X670 Aorus Elite AX", motherboardAmd, BigDecimal.valueOf(380), 20),
                new Producto("MSI B550M Pro-VDH WiFi", motherboardAmd, BigDecimal.valueOf(160), 20),
                new Producto("ASRock Z790 Steel Legend", motherboardIntel, BigDecimal.valueOf(350), 20),

                // RAM
                new Producto("Corsair Vengeance 32GB DDR5", ram, BigDecimal.valueOf(180), 20),
                new Producto("Corsair Vengeance 16GB DDR5", ram, BigDecimal.valueOf(110), 20),
                new Producto("Corsair Vengeance 8GB DDR5", ram, BigDecimal.valueOf(70), 20),
                new Producto("Kingston 32GB DDR4", ram, BigDecimal.valueOf(130), 20),
                new Producto("Kingston 16GB DDR4", ram, BigDecimal.valueOf(75), 20),
                new Producto("Kingston 8GB DDR4", ram, BigDecimal.valueOf(45), 20),
                new Producto("Patriot 16GB DDR4", ram, BigDecimal.valueOf(70), 20),
                new Producto("TeamGroup 8GB DDR4", ram, BigDecimal.valueOf(35), 20),

                // Storage
                new Producto("Kingston 256GB SSD", almacenamiento, BigDecimal.valueOf(30), 20),
                new Producto("Kingston 512GB SSD", almacenamiento, BigDecimal.valueOf(50), 20),
                new Producto("Seagate 2TB HDD", almacenamiento, BigDecimal.valueOf(75), 20),
                new Producto("Toshiba 1TB HDD", almacenamiento, BigDecimal.valueOf(55), 20),
                new Producto("NVMe 1TB SSD", almacenamiento, BigDecimal.valueOf(100), 20),
                new Producto("NVMe 512GB SSD", almacenamiento, BigDecimal.valueOf(65), 20),
                new Producto("Kingston 256GB NVMe", almacenamiento, BigDecimal.valueOf(35), 20),
                new Producto("Kingston 1TB NVMe", almacenamiento, BigDecimal.valueOf(105), 20)
        );

        if (cpuIntel == null || cpuAmd == null || gpu == null || motherboardIntel == null || ram == null || motherboardAmd==null) {
            throw new ResourceNotFoundException("Error en seed: categorías faltantes");
        }

        productoRepository.saveAll(productos);

    }
}

