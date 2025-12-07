package com.pragma.powerup.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class PedidoModel {

    private Long id;

    private LocalDate fecha;

    private PedidoEstado estado;

    private Long idCliente;

    private UserModel cliente;

    private Long idChef;

    private UserModel chef;

    private Long idRestaurante;

    private RestaurantModel restaurante;

    private Set<PedidoItemModel> items;

    public void addItem(PedidoItemModel item){
        this.items.add(item);
    }

}
