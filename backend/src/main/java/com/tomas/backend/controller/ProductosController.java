package com.tomas.backend.controller;
import com.tomas.backend.entity.Producto;
import com.tomas.backend.service.productos.ProductoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductosController {
    private final ProductoService productoService;

    public ProductosController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<Producto> getProductos() {
        return productoService.listarProductos();
    }

    @GetMapping("/id/{idProducto}")
    public Producto getProducto(@PathVariable Long idProducto) {
        return productoService.obtenerProducto(idProducto);
    }

    @GetMapping("/categoria/{idCategoria}")
    public List<Producto> getProductosPorCategoria(@PathVariable Long idCategoria) {
        return productoService.productosPorCategoria(idCategoria);
    }

    @GetMapping("/activo/{idProducto}")
    public boolean activo(@PathVariable Long idProducto) {
        return productoService.estaActivo(idProducto);
    }

    @PostMapping("/crear")
    public Producto crearProducto(@RequestBody Producto producto) {
        return productoService.crearProducto(producto);
    }

    @PostMapping("/actualizar/{idProducto}")
    public Producto actualizarProducto(@RequestBody Producto producto,@PathVariable Long idProducto) {
        return productoService.actualizarProducto(producto,idProducto);
    }

    @PostMapping("/activar/{idProducto}")
    public Producto activarProducto(@PathVariable Long idProducto) {
        return productoService.activarProducto(idProducto);
    }

    @PostMapping("/desactivar/{idProducto}")
    public Producto desactivarProducto(@PathVariable Long idProducto) {
        return productoService.desactivarProducto(idProducto);
    }

    @PostMapping("/Stock/aumento/{idProducto}/{cantidad}")
    public Producto aumentarStockProducto(@PathVariable Long idProducto,@PathVariable Integer cantidad) {
        return productoService.aumentarStock(idProducto,cantidad);
    }

    @PostMapping("/Stock/disminuir/{idProducto}/{cantidad}")
    public Producto disminuirStockProducto(@PathVariable Long idProducto,@PathVariable Integer cantidad) {
        return productoService.disminuirStock(idProducto,cantidad);
    }

}
