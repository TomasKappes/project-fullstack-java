package com.tomas.backend.controller;

import com.tomas.backend.DTOs.pedidos.PedidosCreateDTO;
import com.tomas.backend.DTOs.pedidos.PedidosResponseDTO;
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
    public PedidosResponseDTO getPedidoById(@PathVariable Long idPedido) {
        return pedidoService.obtenerPedido(idPedido);
    }

    @PostMapping("/crear")
    public PedidosResponseDTO crear(@RequestBody PedidosCreateDTO pedido) {
        return pedidoService.crear(pedido);
    }

    @PutMapping("/agregar-producto/{idPedido}")
    public PedidosResponseDTO editarPedido(@PathVariable Long idPedido,@RequestBody PedidosCreateDTO pedidosCreateDTO) {
        return pedidoService.reCrearPedido(idPedido,pedidosCreateDTO);
    }

    @PutMapping("/confirmar/{idPedido}")
    public PedidosResponseDTO confirmar(@PathVariable Long idPedido) {
        return pedidoService.confirmarPedido(idPedido);
    }
}
