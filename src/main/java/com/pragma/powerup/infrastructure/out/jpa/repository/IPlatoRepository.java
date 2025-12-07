package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.entity.PlatoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface IPlatoRepository extends JpaRepository<PlatoEntity, Long> {

    Page<PlatoEntity> findAllByRestaurante_Id(Long restauranteId, PageRequest pageRequest);

    Page<PlatoEntity> findByRestaurante_IdAndCategoria_Nombre(Long restauranteId, String categoriaNombre, Pageable pageable);

    @Query("SELECT p.id FROM PlatoEntity p WHERE p.id IN :ids")
    List<Long> findAllIdsByIds(@Param("ids") Set<Long> ids);

}
