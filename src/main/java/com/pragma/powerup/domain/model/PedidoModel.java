package com.pragma.powerup.domain.model;

import com.pragma.powerup.domain.exception.DomainException;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
public class PedidoModel {

    private Long id;
    private LocalDateTime fecha;
    private PedidoEstado estado;
    private Long idCliente;
    private UserModel cliente;
    private Long idChef;
    private UserModel chef;
    private Long idRestaurante;
    private RestaurantModel restaurante;
    private Set<PedidoItemModel> items;
    private String pin;

    public void addItem(PedidoItemModel item){
        this.items.add(item);
    }

    public void canBeCreated(boolean hasPendingOrder) {
        if (hasPendingOrder) {
            throw new DomainException("No puedes crear un pedido porque tienes uno pendiente.");
        }
    }

    public void prepare(Long chefId) {
        if (this.estado != PedidoEstado.PENDIENTE) {
            throw new DomainException("Solo se puede preparar un pedido en estado PENDIENTE. Estado actual: " + this.estado);
        }

        if (chefId == null) {
            throw new DomainException("Para prepara un pedido se necesita el chefId");
        }

        this.estado = PedidoEstado.EN_PREPARACION;
        this.idChef = chefId;
    }

    public void ready(Long chefId) {
        if (this.estado != PedidoEstado.EN_PREPARACION) {
            throw new DomainException("Para cambiar a estado LISTO el pedido debe estar EN_PREPARACION. Estado actual: " + this.estado);
        }
        this.estado = PedidoEstado.LISTO;
        this.idChef = chefId;
    }

    public void deliver(String pin) {
        if (this.estado != PedidoEstado.LISTO) {
            throw new DomainException("Solo se puede entregar un pedido en estado LISTO. Estado actual: " + this.estado);
        }

        if (pin == null) {
            throw new DomainException("El PIN del pedido no puede estar vacio");
        }

        if (!pin.equals(this.pin)) {
            throw new DomainException("El PIN no es el correcto");
        }

        this.estado = PedidoEstado.ENTREGADO;
    }

    public void cancel() {
        if (this.estado != PedidoEstado.PENDIENTE) {
            throw new DomainException("Lo sentimos, tu pedido ya está en preparación y no puede cancelarse");
        }

        this.estado = PedidoEstado.CANCELADO;
    }

}
