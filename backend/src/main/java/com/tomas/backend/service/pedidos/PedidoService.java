package com.tomas.backend.service.pedidos;
import com.tomas.backend.entity.Pedido;
import com.tomas.backend.entity.PedidoDetalle;
import com.tomas.backend.entity.Producto;
import com.tomas.backend.entity.Usuario;
import com.tomas.backend.enums.EstadoPedido;
import com.tomas.backend.repository.PedidoRepository;
import com.tomas.backend.repository.ProductoRepository;
import com.tomas.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public PedidoService(PedidoRepository pedidoRepository, UsuarioRepository usuarioRepository, ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;

    }
    //Metodo para crear pedido en la BD
    @Transactional
    public Pedido crear(Pedido pedido) {


        Long idUsuario = pedido.getUsuario().getIdUsuario();
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        pedido.setUsuario(usuario);

        BigDecimal total = BigDecimal.ZERO;


        for (PedidoDetalle detalle : pedido.getPedidoDetalles()) {

            Long idProducto = detalle.getProducto().getIdProducto();
            Producto producto = productoRepository.findById(idProducto)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            detalle.setProducto(producto);
            detalle.setPedido(pedido);

            BigDecimal subtotal =
                    producto.getPrecio().multiply(detalle.getCantidad());

            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(subtotal);

            total = total.add(subtotal);
        }

        pedido.setTotal(total);
        pedido.setEstado(EstadoPedido.BORRADOR);

        return pedidoRepository.save(pedido);
    }


    //Metodo para obtener pedido por id
    public Pedido obtenerPedido(Long idPedido) {
        return pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

    }

    //Metodo para calcular presupuesto
    public Pedido calcularPresupuesto(Long idPedido) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));


        if (pedido.getEstado() != EstadoPedido.BORRADOR) {
            throw new RuntimeException("El pedido no está en estado borrador");
        }

        if (pedido.getPedidoDetalles()==null||pedido.getPedidoDetalles().isEmpty()) {
            throw new RuntimeException("Este pedido esta vacio, no se puede calcular presupuesto");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (PedidoDetalle detalle: pedido.getPedidoDetalles()) {
            Producto producto = detalle.getProducto();
            BigDecimal precio = producto.getPrecio();
            detalle.setPrecioUnitario(precio);
            detalle.setSubtotal(precio.multiply(detalle.getCantidad()));

            total = total.add(detalle.getSubtotal());
        }

        pedido.setTotal(total);
        pedido.setEstado(EstadoPedido.PRESUPUESTADO);
        System.out.println("TOTAL PEDIDO = " + pedido.getTotal());
        return pedidoRepository.save(pedido);
    }

    //Metodo para confirmar pedido
    public Pedido confirmarPedido(Pedido pedido) {

        if (pedido.getEstado()==null) {
            throw new RuntimeException("Estado de pedido invalido para realizar la operacion");
        }

        if (pedido.getEstado() == EstadoPedido.CONFIRMADO) {
            throw new RuntimeException("Este pedido ya fue confirmado");
        }
        if(pedido.getEstado() == EstadoPedido.BORRADOR){
            throw new RuntimeException("Este pedido aun no fue presupuestado aun no se puede confirmar");
        }


        pedido.setEstado(EstadoPedido.CONFIRMADO);
        return pedidoRepository.save(pedido);

    }




    //Metodo para agregar producto
    @Transactional
    public Pedido agregarProducto(Long idPedido, PedidoDetalle detalle) {

        Pedido optPedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (optPedido.getEstado() == EstadoPedido.CONFIRMADO) {
            throw new RuntimeException("Este pedido no puede modifcarse debido a que ya fue confirmado");
        }

        if (detalle.getProducto() == null || detalle.getProducto().getIdProducto() == null) {
            throw new RuntimeException("Debe indicar el producto");
        }

        Long idProducto = detalle.getProducto().getIdProducto();

        Producto producto = productoRepository.findById(idProducto)
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        detalle.setProducto(producto);

        detalle.setPedido(optPedido);
        optPedido.getPedidoDetalles().add(detalle);
        recalcularPedido(optPedido);

        return pedidoRepository.save(optPedido);
    }

    //Metodo para recalcular presupuesto si se agrega un producto
    public void recalcularPedido (Pedido pedidoModificado) {
        BigDecimal total = BigDecimal.ZERO;

        for (PedidoDetalle detalle: pedidoModificado.getPedidoDetalles()) {
            Producto producto = detalle.getProducto();
            BigDecimal precio = producto.getPrecio();
            BigDecimal subtotal = precio.multiply(detalle.getCantidad());

            detalle.setPrecioUnitario(precio);
            detalle.setSubtotal(subtotal);
            total = total.add(detalle.getSubtotal());
        }
        pedidoModificado.setTotal(total);
    }



}
