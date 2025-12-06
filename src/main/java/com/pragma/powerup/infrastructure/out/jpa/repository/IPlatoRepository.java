package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.entity.PlatoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPlatoRepository extends JpaRepository<PlatoEntity, Long> {

    Page<PlatoEntity> findAllByRestaurante_Id(Long restauranteId, PageRequest pageRequest);

    Page<PlatoEntity> findByRestaurante_IdAndCategoria_Nombre(Long restauranteId, String categoriaNombre, Pageable pageable);

}
