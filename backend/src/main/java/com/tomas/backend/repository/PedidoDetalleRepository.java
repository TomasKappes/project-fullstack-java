package com.tomas.backend.repository;
import com.tomas.backend.entity.PedidoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle,Long> {

}
