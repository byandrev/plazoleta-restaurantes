package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.domain.api.IPlatoServicePort;
import com.pragma.powerup.domain.api.IRestaurantServicePort;
import com.pragma.powerup.domain.spi.ICategoriaPersistencePort;
import com.pragma.powerup.domain.spi.IPlatoPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.usecase.PlatoUseCase;
import com.pragma.powerup.domain.usecase.RestaurantUseCase;
import com.pragma.powerup.infrastructure.out.feign.adapter.UserExternalAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.CategoriaJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.PlatoJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.adapter.RestaurantJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.mapper.ICategoriaEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IPlatoEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.ICategoriaRepository;
import com.pragma.powerup.infrastructure.out.jpa.repository.IPlatoRepository;
import com.pragma.powerup.infrastructure.out.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final IRestaurantRepository  restaurantRepository;
    private final IPlatoRepository platoRepository;
    private final ICategoriaRepository  categoriaRepository;

    private final IRestaurantEntityMapper restaurantEntityMapper;
    private final IPlatoEntityMapper platoEntityMapper;
    private final ICategoriaEntityMapper  categoriaEntityMapper;

    private final UserExternalAdapter userExternalAdapter;

    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort() {
        return new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    @Bean
    public IRestaurantServicePort restaurantServicePort() {
        return new RestaurantUseCase(restaurantPersistencePort(), userExternalAdapter);
    }

    @Bean
    public IPlatoPersistencePort platoPersistencePort() {
        return new PlatoJpaAdapter(platoRepository, platoEntityMapper);
    }

    @Bean
    public ICategoriaPersistencePort categoriaPersistencePort() {
        return new CategoriaJpaAdapter(categoriaRepository, categoriaEntityMapper);
    }

    @Bean
    public IPlatoServicePort platoServicePort() {
        return new PlatoUseCase(platoPersistencePort(), restaurantPersistencePort(), categoriaPersistencePort());
    }

}