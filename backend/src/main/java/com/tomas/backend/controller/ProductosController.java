package com.tomas.backend.controller;
import com.tomas.backend.DTOs.productos.ProductoCreateDTO;
import com.tomas.backend.DTOs.productos.ProductoRequestDTO;
import com.tomas.backend.DTOs.productos.ProductoResponseDTO;
import com.tomas.backend.DTOs.productos.ProductoUpdateDTO;
import com.tomas.backend.entity.Producto;
import com.tomas.backend.service.productos.ProductoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductosController {
    private final ProductoService productoService;

    public ProductosController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<ProductoResponseDTO> getProductos() {
        return productoService.listarProductos();
    }

    @GetMapping("/id/{idProducto}")
    public ProductoResponseDTO getProducto(@PathVariable Long idProducto) {
        return productoService.obtenerProducto(idProducto);
    }

    @GetMapping("/categoria/{idCategoria}")
    public List<ProductoResponseDTO> getProductosPorCategoria(@PathVariable Long idCategoria) {
        return productoService.productosPorCategoria(idCategoria);
    }

    @GetMapping("/activo/{idProducto}")
    public boolean activo(@PathVariable Long idProducto) {
        return productoService.estaActivo(idProducto);
    }

    @PostMapping("/crear")
    public ProductoResponseDTO crearProducto(@Valid @RequestBody ProductoCreateDTO productoCreateDTO) {
        return productoService.crearProducto(productoCreateDTO);
    }

    @PostMapping("/actualizar/{idProducto}")
    public ProductoResponseDTO actualizarProducto(@Valid @RequestBody ProductoUpdateDTO productoUpdateDTO, @PathVariable Long idProducto) {
        return productoService.actualizarProducto(productoUpdateDTO,idProducto);
    }

    @PostMapping("/activar/{idProducto}")
    public ProductoResponseDTO activarProducto(@PathVariable Long idProducto) {
        return productoService.activarProducto(idProducto);
    }

    @PostMapping("/desactivar/{idProducto}")
    public ProductoResponseDTO desactivarProducto(@PathVariable Long idProducto) {
        return productoService.desactivarProducto(idProducto);
    }

    @PostMapping("/stock/aumento/{idProducto}/{cantidad}")
    public void aumentarStockProducto(@PathVariable Long idProducto,@PathVariable Integer cantidad) {
        productoService.aumentarStock(idProducto,cantidad);
    }

    @PostMapping("/stock/disminuir/{idProducto}/{cantidad}")
    public void disminuirStockProducto(@PathVariable Long idProducto,@PathVariable Integer cantidad) {
       productoService.disminuirStock(idProducto,cantidad);
    }

    @PutMapping("/actualizarPrecio/{idProducto}/{precio}")
    public ProductoResponseDTO actualizarPrecio(@PathVariable Long idProducto,@PathVariable BigDecimal precio) {
        return productoService.actualizarPrecio(idProducto,precio);
    }

}
