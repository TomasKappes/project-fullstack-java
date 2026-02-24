package com.tomas.backend.DTOs.pedidos;

import com.tomas.backend.entity.PedidoDetalle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class PedidosCreate {
    @NotBlank
    private List<PedidoDetalle> pedidosDetalle;
    @NotNull
    private Long usuarioId;
    public PedidosCreate() {
    }

    public List<PedidoDetalle> getPedidosDetalle() {
        return pedidosDetalle;
    }

    public void setPedidosDetalle(List<PedidoDetalle> pedidosDetalle) {
        this.pedidosDetalle = pedidosDetalle;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}
