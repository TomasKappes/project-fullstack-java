package com.tomas.backend.service.productos;
import com.tomas.backend.entity.Categoria;
import com.tomas.backend.entity.Producto;
import com.tomas.backend.repository.CategoriaRepository;
import com.tomas.backend.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;


    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public Producto crearProducto(Producto producto) {
        if (producto.getCategoria() == null || producto.getPrecio() == null || producto.getNombre() == null) {
            throw new RuntimeException("Error al crear producto, faltan campos obligatorios a rellenar");
        }
        if (producto.getPrecio().compareTo(BigDecimal.ZERO)<=0){
            throw new RuntimeException("Error al crear producto, el precio debe ser mayor que 0");
        }
        return productoRepository.save(producto);
    }

    public Producto obtenerProducto(Long idProducto){
        Producto optProducto= productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("No existe el producto con el id: "+idProducto));

        if (!optProducto.isActivo()) {
            throw new RuntimeException("El producto se encuentra desactivado");
        }

        return optProducto;
    }

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Producto actualizarProducto(Producto producto, Long idProducto) {

        Producto optProducto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (producto.getCategoria() == null || producto.getPrecio() == null || producto.getNombre() == null) {
            throw new RuntimeException("Error al actualizar producto,faltan campos obligatorios a rellenar");
        }

        if (producto.getPrecio().compareTo(BigDecimal.ZERO)<=0){
            throw new RuntimeException("Error al actualizar producto, el precio debe ser mayor que 0");
        }

            optProducto.setNombre(producto.getNombre());
            optProducto.setDescripcion(producto.getDescripcion());
            optProducto.setPrecio(producto.getPrecio());
            optProducto.setStock(producto.getStock());
            optProducto.setCategoria(producto.getCategoria());
           return productoRepository.save(optProducto);

    }


   public List<Producto> productosPorCategoria( Long idCategoria) {
        Categoria optCategoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
        return productoRepository.findByCategoria(optCategoria);
   }

   public Producto desactivarProducto(Long idProducto) {
        Producto optProducto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        optProducto.setActivo(false);
        return productoRepository.save(optProducto);
   }

    public Producto activarProducto(Long idProducto) {
        Producto optProducto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        optProducto.setActivo(true);
        return productoRepository.save(optProducto);
    }

    public Producto aumentarStock(Long idProducto, Integer aumentoStock) {
        Producto optProducto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (!optProducto.isActivo()) {
            throw new RuntimeException("El producto se encuentra desactivado");
        }

        if (aumentoStock <= 0) {
            throw new RuntimeException("Error al aumentar stock, el valor de aumento debe ser mayor que 0");
        }
        optProducto.setStock(optProducto.getStock() + aumentoStock);
        return productoRepository.save(optProducto);
    }

    public Producto disminuirStock(Long idProducto, Integer disminuirStock) {
        Producto optProducto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (!optProducto.isActivo()) {
            throw new RuntimeException("El producto se encuentra desactivado");
        }

        if (disminuirStock <= 0) {
            throw new RuntimeException("Error al resta stock, el valor de resta debe ser mayor que 0");
        }

        if (disminuirStock > optProducto.getStock()) {
            throw new RuntimeException("No se puede realizar la accion, el numero a restar es mayor que el stock");
        }

        optProducto.setStock(optProducto.getStock() - disminuirStock);
        return productoRepository.save(optProducto);
    }

    public boolean estaActivo(Long idProducto) {
        Producto optProducto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return optProducto.isActivo();
    }

}
