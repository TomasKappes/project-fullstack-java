package com.tomas.backend.DTOs.pedidos;

import com.tomas.backend.DTOs.pedidos_detalles.PedidoDetallesCreateDTO;
import com.tomas.backend.DTOs.pedidos_detalles.PedidoDetallesResponseDTO;
import com.tomas.backend.entity.PedidoDetalle;
import com.tomas.backend.enums.EstadoPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidosResponseDTO {
    private String nombreUsuario;
    private LocalDateTime fechaPedido;
    private EstadoPedido estadoPedido;
    private List<PedidoDetallesResponseDTO> pedidosDetalle;
    private BigDecimal valorTotal;
    private Long idPedido;

    public PedidosResponseDTO() {}

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public LocalDateTime getFechaPedido(LocalDateTime fecha) {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public EstadoPedido getEstadoPedido() {
        return estadoPedido;
    }

    public void setEstadoPedido(EstadoPedido estadoPedido) {
        this.estadoPedido = estadoPedido;
    }

    public List<PedidoDetallesResponseDTO> getPedidosDetalle() {
        return pedidosDetalle;
    }

    public void setPedidosDetalle(List<PedidoDetallesResponseDTO> pedidosDetalle) {
        this.pedidosDetalle = pedidosDetalle;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }
}
