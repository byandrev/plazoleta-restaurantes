package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.entity.PedidoItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPedidoItemRepository extends JpaRepository<PedidoItemEntity, Long> {
}
