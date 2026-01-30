package com.tomas.backend.controller;

import com.tomas.backend.entity.Pedido;
import com.tomas.backend.entity.PedidoDetalle;
import com.tomas.backend.service.pedidos.PedidoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
public class PedidosController {
    private final PedidoService pedidoService;

    public PedidosController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/id/{idPedido}")
    public Pedido getPedidoById(@PathVariable Long idPedido) {
        return pedidoService.obtenerPedido(idPedido);
    }

    @PostMapping("/crear")
    public Pedido crear(@RequestBody Pedido pedido) {
        return pedidoService.crear(pedido);
    }

    @PutMapping("/agregar-producto/{idPedido}")
    public Pedido editarPedido(@PathVariable Long idPedido,@RequestBody PedidoDetalle detalle) {
        return pedidoService.agregarProducto(idPedido, detalle);
    }

    @PutMapping("/presupuestar/{idPedido}")
    public Pedido presupuestar(@PathVariable Long idPedido) {
        return pedidoService.calcularPresupuesto(idPedido);
    }

    @PutMapping("/confirmar")
    public Pedido confirmar(@RequestBody Pedido pedido) {
        return pedidoService.confirmarPedido(pedido);
    }
}
