package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.CategoriaModel;

public interface ICategoriaPersistencePort {

    CategoriaModel save(CategoriaModel categoriaModel);

    CategoriaModel getByNombre(String nombre);

}
