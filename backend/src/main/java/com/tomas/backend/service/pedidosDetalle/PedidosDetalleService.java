package com.tomas.backend.service.pedidosDetalle;
import com.tomas.backend.DTOs.pedidos_detalles.PedidoDetallesCreateDTO;
import com.tomas.backend.DTOs.pedidos_detalles.PedidoDetallesResponseDTO;
import com.tomas.backend.entity.PedidoDetalle;
import com.tomas.backend.excetions.custom.ResourceNotFoundException;
import com.tomas.backend.mappers.PedidoDetalleMapper;
import com.tomas.backend.repository.PedidoDetalleRepository;
import com.tomas.backend.repository.PedidoRepository;
import com.tomas.backend.repository.ProductoRepository;
import org.springframework.stereotype.Service;

@Service
public class PedidosDetalleService {
    private final PedidoDetalleRepository pedidoDetalleRepository;
    private final ProductoRepository productoRepository;
    private final PedidoDetalleMapper pedidoDetalleMapper;

    public PedidosDetalleService(PedidoDetalleRepository pedidoDetalleRepository, ProductoRepository productoRepository, PedidoDetalleMapper pedidoDetalleMapper) {
        this.pedidoDetalleRepository = pedidoDetalleRepository;
        this.productoRepository = productoRepository;
        this.pedidoDetalleMapper = pedidoDetalleMapper;
    }


    public PedidoDetallesResponseDTO obtenerPedidoDetalle(Long idPedidoDetalle){
        PedidoDetalle optPedidoDetalle = pedidoDetalleRepository.findById(idPedidoDetalle)
                .orElseThrow(()->new ResourceNotFoundException("Pedido detalle no encontrado"));
        return pedidoDetalleMapper.toResponseDTO(optPedidoDetalle);
    }





}
