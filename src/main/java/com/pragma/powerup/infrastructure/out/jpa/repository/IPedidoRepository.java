package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.domain.model.PedidoEstado;
import com.pragma.powerup.infrastructure.out.jpa.entity.PedidoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface IPedidoRepository extends JpaRepository<PedidoEntity, Long> {
    Boolean existsByIdClienteAndEstadoIn(Long idCliente, Collection<PedidoEstado> estados);

    Page<PedidoEntity> findByRestaurante_IdAndEstado(Long restauranteId, PedidoEstado estado, Pageable pageable);

    Page<PedidoEntity> findAllByRestaurante_Id(Long restauranteId, Pageable pageable);
}
