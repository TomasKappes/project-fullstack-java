package com.tomas.backend.DTOs.pedidos_detalles;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PedidoDetallesCreateDTO {
    @NotNull
    private Long productoId;
    @NotNull
    private Integer cantidad;

    public PedidoDetallesCreateDTO() {}


    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
