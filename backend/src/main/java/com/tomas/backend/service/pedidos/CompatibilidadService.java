package com.tomas.backend.service.pedidos;

import com.tomas.backend.DTOs.pedidos_detalles.PedidoDetallesCreateDTO;
import com.tomas.backend.entity.Producto;
import com.tomas.backend.enums.TipoCategoria;
import com.tomas.backend.excetions.custom.BadRequestException;
import com.tomas.backend.excetions.custom.ConflictException;
import com.tomas.backend.excetions.custom.ResourceNotFoundException;
import com.tomas.backend.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompatibilidadService {
    private final ProductoRepository productoRepository;

    public CompatibilidadService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }



    public void validarDetalle(List<PedidoDetallesCreateDTO> detalles) {
        Producto cpu = null;
        Producto motherBoard = null;

        for (PedidoDetallesCreateDTO pedidoDetalle : detalles) {
            Producto producto = productoRepository.findById(pedidoDetalle.getProductoId()).
                    orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

            TipoCategoria tipo = producto.getCategoria().getNombreCategoria();

            if (tipo == TipoCategoria.CPU_AMD || tipo == TipoCategoria.CPU_INTEL) {
                if (cpu != null) {
                    throw new ConflictException("Solo puede haber un cpu por pedido");
                }
                cpu = producto;
            }
            if (tipo == TipoCategoria.MOTHERBOARD_AMD || tipo == TipoCategoria.MOTHERBOARD_INTEL) {
                if (motherBoard != null) {
                    throw new ConflictException("Solo puede haber una motherBoard por pedido");
                }
                motherBoard = producto;
            }
        }
        validarComponentes(cpu, motherBoard);
    }

    public void validarComponentes(Producto cpu, Producto motherBoard) {
        if (cpu == null ||  motherBoard == null) {
            throw new BadRequestException("El pedido debe incluir si o si una motherBoard y un cpu");
        }
        if (cpu.getCategoria().getNombreCategoria() == TipoCategoria.CPU_AMD && motherBoard.getCategoria().getNombreCategoria() == TipoCategoria.MOTHERBOARD_INTEL) {
            throw new ConflictException("Error, Componentes incompatibles , cpu amd no es compatible con motherboard intel");
        }
        if (cpu.getCategoria().getNombreCategoria() == TipoCategoria.CPU_INTEL && motherBoard.getCategoria().getNombreCategoria() == TipoCategoria.MOTHERBOARD_AMD) {
            throw new ConflictException("Error, Componentes incompatibles, cpu intel no es compatible con motherboard amd");
        }

    }

}
