package com.tomas.backend.service.productos;
import com.tomas.backend.DTOs.categoria.CategoriaRequestDTO;
import com.tomas.backend.DTOs.productos.ProductoCreateDTO;
import com.tomas.backend.DTOs.productos.ProductoResponseDTO;
import com.tomas.backend.DTOs.productos.ProductoUpdateDTO;
import com.tomas.backend.entity.Categoria;
import com.tomas.backend.entity.Producto;
import com.tomas.backend.mappers.ProductoMapper;
import com.tomas.backend.repository.CategoriaRepository;
import com.tomas.backend.repository.ProductoRepository;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;


    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository, ProductoMapper productoMapper) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.productoMapper = productoMapper;
    }

    public ProductoResponseDTO crearProducto(ProductoCreateDTO productoCreateDTO) {

        Categoria optCategoria  = categoriaRepository.findById(productoCreateDTO.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));

         Producto producto =  productoMapper.toEntity(productoCreateDTO);
         producto.setCategoria(optCategoria);
         Producto productoGuardado = productoRepository.save(producto);
         return productoMapper.toResponseDTO(productoGuardado);
    }

    public ProductoResponseDTO obtenerProducto(Long idProducto){
        Producto optProducto= productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("No existe el producto con el id: "+idProducto));

        if (!optProducto.isActivo()) {
            throw new RuntimeException("El producto se encuentra desactivado");
        }

        return productoMapper.toResponseDTO(optProducto);
    }

    public List<ProductoResponseDTO> listarProductos() {
        List<ProductoResponseDTO> productosDTO = new ArrayList<>();

        for (Producto producto : productoRepository.findAll()) {
            productosDTO.add(productoMapper.toResponseDTO(producto));
        }
        return productosDTO;
    }

    public ProductoResponseDTO actualizarProducto(ProductoUpdateDTO productoUpdateDTO, Long idProducto) {

        Producto optProducto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("No existe el producto con el id: " + idProducto));

        Categoria categoria = null;

        if (productoUpdateDTO.getCategoriaId() != null) {
            categoria = categoriaRepository.findById(productoUpdateDTO.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("La categoria que se le dio a este producto no existe"));

        }


        productoMapper.toUpdateEntity(productoUpdateDTO,optProducto,categoria);
        productoRepository.save(optProducto);
        return productoMapper.toResponseDTO(optProducto);

    }


   public List<ProductoResponseDTO> productosPorCategoria( Long idCategoria) {
        Categoria optCategoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));

        List<ProductoResponseDTO> productosDTO = new ArrayList<>();

        for (Producto producto : productoRepository.findByCategoria(optCategoria)) {
            productosDTO.add(productoMapper.toResponseDTO(producto));
        }

        return productosDTO;
   }

   public ProductoResponseDTO desactivarProducto(Long idProducto) {
        Producto optProducto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        optProducto.setActivo(false);
        productoRepository.save(optProducto);
        return productoMapper.toResponseDTO(optProducto);
   }

    public ProductoResponseDTO activarProducto(Long idProducto) {
        Producto optProducto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        optProducto.setActivo(true);
       productoRepository.save(optProducto);
      return  productoMapper.toResponseDTO(optProducto);
    }

    public void aumentarStock(Long idProducto, Integer aumentoStock) {
        Producto optProducto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (!optProducto.isActivo()) {
            throw new RuntimeException("El producto se encuentra desactivado");
        }

        if (aumentoStock <= 0) {
            throw new RuntimeException("Error al aumentar stock, el valor de aumento debe ser mayor que 0");
        }
        optProducto.setStock(optProducto.getStock() + aumentoStock);
        productoRepository.save(optProducto);
    }

    public void disminuirStock(Long idProducto, Integer disminuirStock) {
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
        productoRepository.save(optProducto);
    }

    public boolean estaActivo(Long idProducto) {
        Producto optProducto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return optProducto.isActivo();
    }

}
