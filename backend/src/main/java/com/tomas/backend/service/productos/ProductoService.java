package com.tomas.backend.service.productos;
import com.tomas.backend.entity.Producto;
import com.tomas.backend.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public Producto crearProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public Optional<Producto> obtenerProducto(Long idProducto){
        return productoRepository.findById(idProducto);
    }

    public List<Producto> listaProductos() {
        return productoRepository.findAll();
    }
}
