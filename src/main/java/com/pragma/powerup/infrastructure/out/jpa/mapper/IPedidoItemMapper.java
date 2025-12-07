package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.PedidoItemModel;
import com.pragma.powerup.infrastructure.out.jpa.entity.PedidoEntity;
import com.pragma.powerup.infrastructure.out.jpa.entity.PedidoItemEntity;
import com.pragma.powerup.infrastructure.out.jpa.entity.PlatoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface IPedidoItemMapper {

    @Mapping(target = "plato", source = "platoId", qualifiedByName = "longToPlato")
    @Mapping(target = "pedido", source = "pedidoId", qualifiedByName = "longToPedido")
    PedidoItemEntity toEntity(PedidoItemModel pedidoItemModel);

    PedidoItemModel toModel(PedidoItemEntity pedidoItemEntity);

    @Named("longToPlato")
    default PlatoEntity longToPlato(Long platoId) {
        if (platoId == null) {
            return null;
        }

        PlatoEntity platoEntity = new PlatoEntity();
        platoEntity.setId(platoId);

        return platoEntity;
    }

    @Named("longToPedido")
    default PedidoEntity longToPedido(Long pedidoId) {
        if (pedidoId == null) {
            return null;
        }

        PedidoEntity pedidoEntity = new PedidoEntity();
        pedidoEntity.setId(pedidoId);

        return pedidoEntity;
    }

}
