package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IPlatoServicePort;
import com.pragma.powerup.domain.exception.ResourceNotFound;
import com.pragma.powerup.domain.exception.UnauthorizedUserException;
import com.pragma.powerup.domain.model.CategoriaModel;
import com.pragma.powerup.domain.model.PlatoModel;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.ICategoriaPersistencePort;
import com.pragma.powerup.domain.spi.IPlatoPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlatoUseCase implements IPlatoServicePort {

    private final IPlatoPersistencePort platoPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final ICategoriaPersistencePort categoriaPersistencePort;

    @Override
    public PlatoModel save(Long userId, PlatoModel plato) {
        RestaurantModel restaurantModel = restaurantPersistencePort.getById(plato.getIdRestaurante());

        if (!Objects.equals(restaurantModel.getIdPropietario(), userId)) {
            throw new UnauthorizedUserException("No eres propietario del restaurante");
        }

        try {
            CategoriaModel categoria = categoriaPersistencePort.getByNombre(plato.getCategoria().getNombre());
            plato.setCategoria(categoria);
        } catch (ResourceNotFound ex) {
            CategoriaModel categoriaCreated = categoriaPersistencePort.save(plato.getCategoria());
            plato.setCategoria(categoriaCreated);
        }

        plato.setActivo(true);
        plato.setRestaurante(restaurantModel);

        return platoPersistencePort.save(plato);
    }

    @Override
    public Page<PlatoModel> getAll(String categoria, Long restauranteId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("nombre").ascending());

        if (categoria != null && !categoria.trim().isEmpty()) {
            return platoPersistencePort.getAllByCategoria(categoria.toUpperCase(), restauranteId, pageRequest);
        }

        return  platoPersistencePort.getAll(restauranteId, pageRequest);
    }

    @Override
    public PlatoModel getById(Long id) {
        return platoPersistencePort.getById(id);
    }

    @Override
    public PlatoModel update(Long userId, Long id, PlatoModel plato) {
        PlatoModel updatedPlato = platoPersistencePort.getById(id);
        RestaurantModel restaurantModel = restaurantPersistencePort.getById(updatedPlato.getIdRestaurante());

        if (!Objects.equals(restaurantModel.getIdPropietario(), userId)) {
            throw new UnauthorizedUserException("No eres propietario del restaurante");
        }

        Optional.ofNullable(plato.getPrecio()).ifPresent(updatedPlato::setPrecio);
        Optional.ofNullable(plato.getDescripcion()).ifPresent(updatedPlato::setDescripcion);
        Optional.ofNullable(plato.getActivo()).ifPresent(updatedPlato::setActivo);

        return platoPersistencePort.save(updatedPlato);
    }

}
