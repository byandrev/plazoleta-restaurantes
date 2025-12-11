package com.pragma.powerup.infrastructure.out.jpa.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "empleados_restaurantes")
public class EmployeeRestaurantEntity {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "restaurantId", column = @Column(name = "id_restaurante")),
            @AttributeOverride(name = "userId", column = @Column(name = "id_user"))
    })
    private EmployeeRestaurantId id;

    @ManyToOne
    @MapsId("restaurantId")
    @JoinColumn(name = "id_restaurante", insertable = false, updatable = false)
    private RestaurantEntity restaurant;

}
