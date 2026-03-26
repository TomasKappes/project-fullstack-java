package com.tomas.backend.mappers;
import com.tomas.backend.DTOs.productos.ProductoCreateDTO;
import com.tomas.backend.DTOs.productos.ProductoResponseDTO;
import com.tomas.backend.DTOs.productos.ProductoUpdateDTO;
import com.tomas.backend.entity.Categoria;
import com.tomas.backend.entity.Producto;
import com.tomas.backend.excetions.custom.ResourceNotFoundException;
import com.tomas.backend.repository.CategoriaRepository;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {
    private final CategoriaRepository categoriaRepository;

    public  ProductoMapper(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public Producto toEntity(ProductoCreateDTO productoCreateDTO,Categoria categoria) {
        Producto producto = new Producto(productoCreateDTO.getNombre(), categoria);
        producto.setNombre(productoCreateDTO.getNombre());
        producto.setDescripcion(productoCreateDTO.getDescripcion());
        producto.setPrecio(productoCreateDTO.getPrecio());
        producto.setActivo(true);
        producto.setStock(productoCreateDTO.getStock());

        return producto;
    }

    public void toUpdateEntity(ProductoUpdateDTO productoUpdateDTO, Producto producto, Categoria categoria) {
        if(productoUpdateDTO.getNombre() != null) {
            producto.setNombre(productoUpdateDTO.getNombre());
        }
        if(productoUpdateDTO.getDescripcion() != null) {
            producto.setDescripcion(productoUpdateDTO.getDescripcion());
        }
        if (productoUpdateDTO.getStock() != null) {
            producto.setStock(productoUpdateDTO.getStock());
        }
        if (productoUpdateDTO.getPrecio() != null) {
            producto.setPrecio(productoUpdateDTO.getPrecio());
        }
        if(categoria != null) {
            producto.setCategoria(categoria);
        }

        }


    public ProductoResponseDTO toResponseDTO(Producto producto) {
        ProductoResponseDTO productoResponseDTO = new ProductoResponseDTO();
        productoResponseDTO.setNombre(producto.getNombre());
        productoResponseDTO.setDescripcion(producto.getDescripcion());
        productoResponseDTO.setPrecio(producto.getPrecio());
        productoResponseDTO.setCategoria(producto.getCategoria().getId());


        return productoResponseDTO;
    }

}
