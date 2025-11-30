package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IPlatoServicePort;
import com.pragma.powerup.domain.model.CategoriaModel;
import com.pragma.powerup.domain.model.PlatoModel;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.ICategoriaPersistencePort;
import com.pragma.powerup.domain.spi.IPlatoPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.infrastructure.exception.NoDataFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlatoUseCase implements IPlatoServicePort {

    private final IPlatoPersistencePort platoPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final ICategoriaPersistencePort categoriaPersistencePort;

    @Override
    public PlatoModel save(PlatoModel plato) {
        RestaurantModel restaurantModel = restaurantPersistencePort.getById(plato.getIdRestaurante());

        try {
            CategoriaModel categoria = categoriaPersistencePort.getByNombre(plato.getCategoria().getNombre());
            plato.setCategoria(categoria);
        } catch (NoDataFoundException ex) {
            CategoriaModel categoriaCreated = categoriaPersistencePort.save(plato.getCategoria());
            plato.setCategoria(categoriaCreated);
        }

        plato.setActivo(true);

        return platoPersistencePort.save(plato);
    }

    @Override
    public PlatoModel getById(Long id) {
        return platoPersistencePort.getById(id);
    }

    @Override
    public PlatoModel update(Long id, PlatoModel plato) {
        return platoPersistencePort.update(id, plato);
    }

}
