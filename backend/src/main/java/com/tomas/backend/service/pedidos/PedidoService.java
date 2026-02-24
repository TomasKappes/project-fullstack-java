package com.tomas.backend.service.pedidos;
import com.tomas.backend.DTOs.pedidos.PedidosCreateDTO;
import com.tomas.backend.DTOs.pedidos.PedidosResponseDTO;
import com.tomas.backend.DTOs.pedidos_detalles.PedidoDetallesCreateDTO;
import com.tomas.backend.entity.Pedido;
import com.tomas.backend.entity.PedidoDetalle;
import com.tomas.backend.entity.Producto;
import com.tomas.backend.entity.Usuario;
import com.tomas.backend.enums.EstadoPedido;
import com.tomas.backend.mappers.PedidoDetalleMapper;
import com.tomas.backend.mappers.PedidoMapper;
import com.tomas.backend.repository.PedidoRepository;
import com.tomas.backend.repository.ProductoRepository;
import com.tomas.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final PedidoMapper pedidoMapper;
    private final PedidoDetalleMapper pedidoDetalleMapper;

    public PedidoService(PedidoRepository pedidoRepository, UsuarioRepository usuarioRepository,
                         ProductoRepository productoRepository, PedidoMapper pedidoMapper, PedidoDetalleMapper pedidoDetalleMapper) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.pedidoMapper = pedidoMapper;
        this.pedidoDetalleMapper = pedidoDetalleMapper;

    }
    //Metodo para crear pedido en la BD
    @Transactional
    public PedidosResponseDTO crear(PedidosCreateDTO pedidoCreate) {


        Pedido pedido = new Pedido();

        Long idUsuario = pedidoCreate.getUsuarioId();
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no Valido"));

        BigDecimal total = BigDecimal.ZERO;


        for (PedidoDetallesCreateDTO detalleDTO : pedidoCreate.getPedidosDetalle()) {

            PedidoDetalle detalle = pedidoDetalleMapper.toEntity(detalleDTO);

            Long idProducto = detalleDTO.getProductoId();
            Producto producto = productoRepository.findById(idProducto)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));



            if (producto.getStock() == 0){
                throw new RuntimeException("Producto sin stock");

            }
            if (producto.getStock() < detalle.getCantidad()){
                throw new RuntimeException("Stock insuficiente para la cantidad seleccionada");
            }

            detalle.setProducto(producto);
            int cantidad = detalle.getCantidad();

            BigDecimal subtotal =
                    producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));

            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(subtotal);
            detalle.setPedido(pedido);
            pedido.getPedidoDetalles().add(detalle);

            total = total.add(subtotal);
        }


        pedido.setTotal(total);
        pedido.setUsuario(usuario);
        pedido.setEstado(EstadoPedido.PRESUPUESTADO);
        pedido.setFecha(LocalDateTime.now());

        pedidoRepository.save(pedido);


        return pedidoMapper.toResponseDTO(pedido);
    }


    //Metodo para obtener pedido por id
    public PedidosResponseDTO obtenerPedido(Long idPedido) {
        Pedido optPedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        return pedidoMapper.toResponseDTO(optPedido);

    }


    //Metodo para confirmar pedido
    @Transactional
    public PedidosResponseDTO confirmarPedido(Long  idPedido ) {

        Pedido optPedido = pedidoRepository.findById(idPedido).
                orElseThrow(() -> new RuntimeException("Pedido no encontrado"));


        if (optPedido.getEstado() == EstadoPedido.CONFIRMADO) {
            throw new RuntimeException("Este pedido ya fue confirmado");
        }

        optPedido.setEstado(EstadoPedido.CONFIRMADO);

        return pedidoMapper.toResponseDTO(optPedido);

    }


    //Metodo para recrear pedido
    @Transactional
    public PedidosResponseDTO reCrearPedido(Long idPedido, PedidosCreateDTO pedidoCreate) {

        Pedido optPedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (optPedido.getEstado() == EstadoPedido.CONFIRMADO) {
            throw new RuntimeException("Este pedido no puede modifcarse debido a que ya fue confirmado");
        }

        optPedido.setEstado(EstadoPedido.CANCELADO);

        return crear(pedidoCreate);


    }


}
