package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.PaginationInfo;
import com.pragma.powerup.domain.model.PaginationResult;
import com.pragma.powerup.domain.model.PedidoEstado;
import com.pragma.powerup.domain.model.PedidoModel;
import com.pragma.powerup.domain.spi.IPedidoPersistencePort;
import com.pragma.powerup.infrastructure.exception.ResourceNotFound;
import com.pragma.powerup.infrastructure.out.jpa.entity.PedidoEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IPaginationMapper;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IPedidoEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IPedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PedidoJpaAdapter implements IPedidoPersistencePort {

    private final IPedidoRepository pedidoRepository;

    private final IPedidoEntityMapper pedidoEntityMapper;

    private final IPaginationMapper paginationMapper;

    @Override
    public PedidoModel save(PedidoModel pedidoModel) {
        PedidoEntity pedidoEntity = pedidoEntityMapper.toEntity(pedidoModel);
        return pedidoEntityMapper.toModel(pedidoRepository.save(pedidoEntity));
    }

    @Override
    public PedidoModel getById(Long id) {
        PedidoEntity pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("El pedido no existe"));
        return pedidoEntityMapper.toModel(pedido);
    }

    @Override
    public Boolean existsByClienteIdAndEstadoIn(Long clientId) {
        List<PedidoEstado> estadosEnProceso = List.of(
                PedidoEstado.PENDIENTE,
                PedidoEstado.EN_PREPARACION,
                PedidoEstado.LISTO
        );

        return pedidoRepository.existsByIdClienteAndEstadoIn(clientId, estadosEnProceso);
    }

    @Override
    public PaginationResult<PedidoModel> getAll(Long restaurantId, PaginationInfo pagination) {
        Page<PedidoEntity> pageResult = pedidoRepository.findAllByRestaurante_Id(restaurantId, paginationMapper.toPageable(pagination));
        return paginationMapper.toModel(pageResult.map(pedidoEntityMapper::toModel));
    }

    @Override
    public PaginationResult<PedidoModel> getAllByEstado(Long restaurantId, PedidoEstado estado,PaginationInfo pagination) {
        Page<PedidoEntity> pageResult = pedidoRepository.findByRestaurante_IdAndEstado(restaurantId, estado, paginationMapper.toPageable(pagination));
        return paginationMapper.toModel(pageResult.map(pedidoEntityMapper::toModel));
    }

}
