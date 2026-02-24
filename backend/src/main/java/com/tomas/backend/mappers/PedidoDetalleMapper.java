package com.tomas.backend.mappers;


import com.tomas.backend.DTOs.pedidos_detalles.PedidoDetallesCreateDTO;
import com.tomas.backend.DTOs.pedidos_detalles.PedidoDetallesResponseDTO;
import com.tomas.backend.entity.PedidoDetalle;
import org.springframework.stereotype.Component;

@Component
public class PedidoDetalleMapper {


    public PedidoDetalle toEntity(PedidoDetallesCreateDTO pedidoDetallesCreate) {
        PedidoDetalle pedidoDetalle = new PedidoDetalle();
        pedidoDetalle.setCantidad(pedidoDetallesCreate.getCantidad());
        return pedidoDetalle;
    }

    public PedidoDetallesResponseDTO toResponseDTO(PedidoDetalle pedidoDetalle) {
        PedidoDetallesResponseDTO pedidoDetallesResponseDTO = new PedidoDetallesResponseDTO();
        pedidoDetallesResponseDTO.setPedidoDetalleId(pedidoDetalle.getIdDetalle());
        pedidoDetallesResponseDTO.setNombreProducto(pedidoDetalle.getProducto().getNombre());
        pedidoDetallesResponseDTO.setSubtotal(pedidoDetalle.getSubtotal());
        pedidoDetallesResponseDTO.setCantidad(pedidoDetalle.getCantidad());
        pedidoDetallesResponseDTO.setPrecioUnitario(pedidoDetalle.getPrecioUnitario());

        return pedidoDetallesResponseDTO;
    }
}
