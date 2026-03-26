package com.tomas.backend.DTOs.pedidos;

import com.tomas.backend.DTOs.pedidos_detalles.PedidoDetallesCreateDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class PedidosCreateDTO {

    private List<PedidoDetallesCreateDTO> pedidosDetalle;
    @NotNull
    private Long usuarioId;
    public PedidosCreateDTO() {
    }

    public List<PedidoDetallesCreateDTO> getPedidosDetalle() {
        return pedidosDetalle;
    }

    public void setPedidosDetalle(List<PedidoDetallesCreateDTO> pedidosDetalle) {
        this.pedidosDetalle = pedidosDetalle;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}
