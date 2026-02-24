package com.tomas.backend.mappers;

import com.tomas.backend.DTOs.pedidos.PedidosResponseDTO;
import com.tomas.backend.DTOs.pedidos_detalles.PedidoDetallesResponseDTO;
import com.tomas.backend.entity.Pedido;
import com.tomas.backend.entity.PedidoDetalle;
import com.tomas.backend.entity.Usuario;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class PedidoMapper {
    private final PedidoDetalleMapper pedidoDetalleMapper;

    public PedidoMapper(PedidoDetalleMapper pedidoDetalleMapper) {
        this.pedidoDetalleMapper = pedidoDetalleMapper;
    }

    public Pedido toEntity(List<PedidoDetalle> pedidoDetalles, Usuario usuario, BigDecimal total) {
        Pedido pedido = new Pedido();
        pedido.setPedidoDetalles(pedidoDetalles);
        pedido.setUsuario(usuario);
        pedido.setTotal(total);
        pedido.setFecha(LocalDateTime.now());
        return pedido;
    }

    public PedidosResponseDTO toResponseDTO(Pedido pedido) {
        PedidosResponseDTO pedidoResponseDTO = new PedidosResponseDTO();
        pedidoResponseDTO.setNombreUsuario(pedido.getUsuario().getNombre());
        pedidoResponseDTO.setEstadoPedido(pedido.getEstado());
        pedidoResponseDTO.setFechaPedido(   pedido.getFecha());
        pedidoResponseDTO.setIdPedido(pedido.getIdPedido());
        pedidoResponseDTO.setValorTotal(pedido.getTotal());
        List<PedidoDetallesResponseDTO> pedidoDetalles = new ArrayList<>();
        for(PedidoDetalle detalle : pedido.getPedidoDetalles()) {
            PedidoDetallesResponseDTO detalleResponseDTO = pedidoDetalleMapper.toResponseDTO(detalle);
            pedidoDetalles.add(detalleResponseDTO);
        }
        pedidoResponseDTO.setPedidosDetalle(pedidoDetalles);
        return pedidoResponseDTO;
    }


}
