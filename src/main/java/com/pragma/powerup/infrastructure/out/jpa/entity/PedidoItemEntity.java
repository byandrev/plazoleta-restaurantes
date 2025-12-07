package com.pragma.powerup.infrastructure.out.jpa.entity;

import lombok.Data;

import javax.persistence.*;


@Entity
@Table(name = "pedidos_platos")
@Data
public class PedidoItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_pedido")
    private PedidoEntity pedido;

    @ManyToOne
    @JoinColumn(name = "id_plato")
    private PlatoEntity plato;

    private Integer cantidad;

}
