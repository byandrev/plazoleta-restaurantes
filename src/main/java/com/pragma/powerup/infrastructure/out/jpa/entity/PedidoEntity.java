package com.pragma.powerup.infrastructure.out.jpa.entity;

import com.pragma.powerup.domain.model.PedidoEstado;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
public class PedidoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PedidoEstado estado;

    @Column(name = "id_chef", nullable = false)
    private Long idChef;

    @ManyToOne
    @JoinColumn(name = "id_restaurante",nullable = false)
    private RestaurantEntity restaurante;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PedidoItemEntity> items;

}
