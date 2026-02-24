package com.tomas.backend.DTOs.pedidos_detalles;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PedidoDetallesCreate {
    @NotNull
    private Long productoId;
    @NotNull
    private BigDecimal cantidad;

    public PedidoDetallesCreate() {}


    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }
}
