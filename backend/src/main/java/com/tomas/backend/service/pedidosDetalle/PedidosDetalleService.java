package com.tomas.backend.service.pedidosDetalle;
import com.tomas.backend.entity.PedidoDetalle;
import com.tomas.backend.repository.PedidoDetalleRepository;
import org.springframework.stereotype.Service;



@Service
public class PedidosDetalleService {
    private final PedidoDetalleRepository pedidoDetalleRepository;

    public PedidosDetalleService(PedidoDetalleRepository pedidoDetalleRepository) {
        this.pedidoDetalleRepository = pedidoDetalleRepository;
    }

    public PedidoDetalle crear(PedidoDetalle pedidoDetalle){
        return pedidoDetalleRepository.save(pedidoDetalle);
    }

    public PedidoDetalle obtenerPedidoDetalle(Long idPedidoDetalle){
        PedidoDetalle optPedidoDetalle = pedidoDetalleRepository.findById(idPedidoDetalle)
                .orElseThrow(()->new RuntimeException("Pedido detalle no encontrado"));
        return optPedidoDetalle;
    }


    public PedidoDetalle actualizar(PedidoDetalle pedidoDetalle,Long idPedidoDetalle){
        PedidoDetalle optPedidoDetalle = pedidoDetalleRepository.findById(idPedidoDetalle)
                .orElseThrow(()->new RuntimeException("Pedido detalle no encontrado"));


        optPedidoDetalle.setPrecioUnitario(pedidoDetalle.getPrecioUnitario());
        optPedidoDetalle.setCantidad(pedidoDetalle.getCantidad());
        optPedidoDetalle.setSubtotal(pedidoDetalle.getSubtotal());

        return pedidoDetalleRepository.save(optPedidoDetalle);
    }

    public void eliminarPedidoDetalle(Long idPedidoDetalle){
        PedidoDetalle optPedidoDetalle = pedidoDetalleRepository.findById(idPedidoDetalle)
                .orElseThrow(()->new RuntimeException("Pedido detalle no encontrados"));
        pedidoDetalleRepository.deleteById(idPedidoDetalle);
    }



}
