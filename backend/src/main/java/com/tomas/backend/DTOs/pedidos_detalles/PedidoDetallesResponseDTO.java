package com.tomas.backend.DTOs.pedidos_detalles;

import java.math.BigDecimal;

public class PedidoDetallesResponseDTO {

    private Long pedidoDetalleId;
    private String nombreProducto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;




    public PedidoDetallesResponseDTO() {}

    public Long getPedidoDetalleId() {
        return pedidoDetalleId;
    }
    public void setPedidoDetalleId(Long pedidoDetalleId) {
        this.pedidoDetalleId = pedidoDetalleId;
    }
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }


    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
}
