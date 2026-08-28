package com.pragma.powerup.infrastructure.configuration;

import com.pragma.powerup.application.handler.IPedidoHandler;
import com.pragma.powerup.application.handler.IPlatoHandler;
import com.pragma.powerup.application.handler.IRestaurantHandler;
import com.pragma.powerup.application.handler.impl.PedidoHandler;
import com.pragma.powerup.application.handler.impl.PlatoHandler;
import com.pragma.powerup.application.handler.impl.RestaurantHandler;
import com.pragma.powerup.application.mapper.*;
import com.pragma.powerup.domain.api.IPedidoServicePort;
import com.pragma.powerup.domain.api.IPlatoServicePort;
import com.pragma.powerup.domain.api.IRestaurantServicePort;
import com.pragma.powerup.domain.spi.*;
import com.pragma.powerup.domain.usecase.PedidoUseCase;
import com.pragma.powerup.domain.usecase.PlatoUseCase;
import com.pragma.powerup.domain.usecase.RestaurantUseCase;
import com.pragma.powerup.infrastructure.out.feign.IMessageFeignClient;
import com.pragma.powerup.infrastructure.out.feign.ITraceabilityFeignClient;
import com.pragma.powerup.infrastructure.out.feign.IUserFeignClient;
import com.pragma.powerup.infrastructure.out.feign.adapter.MessageExternalAdapter;
import com.pragma.powerup.infrastructure.out.feign.adapter.TraceabilityExternalAdapter;
import com.pragma.powerup.infrastructure.out.feign.adapter.UserExternalAdapter;
import com.pragma.powerup.infrastructure.out.feign.mapper.ITraceabilityFeignMapper;
import com.pragma.powerup.infrastructure.out.feign.mapper.IUserFeignMapper;
import com.pragma.powerup.infrastructure.out.jpa.adapter.*;
import com.pragma.powerup.infrastructure.out.jpa.mapper.*;
import com.pragma.powerup.infrastructure.out.jpa.repository.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    // --- Persistence adapters (SPI) ---

    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort(IRestaurantRepository repository,
                                                                IRestaurantEntityMapper mapper,
                                                                IPaginationMapper paginationMapper) {
        return new RestaurantJpaAdapter(repository, mapper, paginationMapper);
    }

    @Bean
    public IPlatoPersistencePort platoPersistencePort(IPlatoRepository repository,
                                                      IPlatoEntityMapper mapper,
                                                      IPaginationMapper paginationMapper) {
        return new PlatoJpaAdapter(repository, mapper, paginationMapper);
    }

    @Bean
    public ICategoriaPersistencePort categoriaPersistencePort(ICategoriaRepository repository,
                                                              ICategoriaEntityMapper mapper) {
        return new CategoriaJpaAdapter(repository, mapper);
    }

    @Bean
    public IEmployeePersistencePort employeePersistencePort(IEmployeeRestaurantRepository repository,
                                                            IEmployeeRestaurantEntityMapper mapper) {
        return new EmployeeJpaAdapter(repository, mapper);
    }

    @Bean
    public IPedidoPersistencePort pedidoPersistencePort(IPedidoRepository repository,
                                                        IPedidoEntityMapper mapper,
                                                        IPaginationMapper paginationMapper) {
        return new PedidoJpaAdapter(repository, mapper, paginationMapper);
    }

    @Bean
    public IPedidoItemPersistencePort pedidoItemPersistencePort(IPedidoItemRepository repository,
                                                                IPedidoItemMapper mapper) {
        return new PedidoItemJpaAdapter(repository, mapper);
    }

    // --- External service adapters (SPI) ---

    @Bean
    public IUserExternalServicePort userExternalServicePort(IUserFeignClient client, IUserFeignMapper mapper) {
        return new UserExternalAdapter(client, mapper);
    }

    @Bean
    public ITraceabilityExternalServicePort traceabilityExternalServicePort(ITraceabilityFeignClient client,
                                                                           ITraceabilityFeignMapper mapper,
                                                                           IPaginationMapper paginationMapper) {
        return new TraceabilityExternalAdapter(client, mapper, paginationMapper);
    }

    @Bean
    public IMessageExternalServicePort messageExternalServicePort(IMessageFeignClient client) {
        return new MessageExternalAdapter(client);
    }

    // --- Use cases (API) ---

    @Bean
    public IRestaurantServicePort restaurantServicePort(IRestaurantPersistencePort restaurantPersistencePort,
                                                        IUserExternalServicePort userExternalServicePort,
                                                        IEmployeePersistencePort employeePersistencePort) {
        return new RestaurantUseCase(restaurantPersistencePort, userExternalServicePort, employeePersistencePort);
    }

    @Bean
    public IPlatoServicePort platoServicePort(IPlatoPersistencePort platoPersistencePort,
                                              IRestaurantPersistencePort restaurantPersistencePort,
                                              ICategoriaPersistencePort categoriaPersistencePort) {
        return new PlatoUseCase(platoPersistencePort, restaurantPersistencePort, categoriaPersistencePort);
    }

    @Bean
    public IPedidoServicePort pedidoServicePort(IPedidoPersistencePort pedidoPersistencePort,
                                                IPlatoPersistencePort platoPersistencePort,
                                                IRestaurantPersistencePort restaurantPersistencePort,
                                                IUserExternalServicePort userExternalServicePort,
                                                ITraceabilityExternalServicePort traceabilityExternalServicePort,
                                                IEmployeePersistencePort employeePersistencePort,
                                                IMessageExternalServicePort messageExternalServicePort) {
        return new PedidoUseCase(pedidoPersistencePort, platoPersistencePort, restaurantPersistencePort,
                userExternalServicePort, traceabilityExternalServicePort, employeePersistencePort,
                messageExternalServicePort);
    }

    // --- Handlers ---

    @Bean
    public IRestaurantHandler restaurantHandler(IRestaurantServicePort restaurantServicePort,
                                                IRestaurantRequestMapper restaurantRequestMapper,
                                                IRestaurantResponseMapper restaurantResponseMapper,
                                                IEmployeeRequestDtoMapper employeeRequestDtoMapper,
                                                IPaginationResponseMapper paginationResponseMapper,
                                                IPaginationRequestMapper paginationRequestMapper) {
        return new RestaurantHandler(restaurantServicePort, restaurantRequestMapper, restaurantResponseMapper,
                employeeRequestDtoMapper, paginationResponseMapper, paginationRequestMapper);
    }

    @Bean
    public IPlatoHandler platoHandler(IPlatoServicePort platoServicePort,
                                      IPlatoRequestMapper platoRequestMapper,
                                      IPlatoResponseMapper platoResponseMapper,
                                      IPaginationResponseMapper paginationResponseMapper,
                                      IPaginationRequestMapper paginationRequestMapper) {
        return new PlatoHandler(platoServicePort, platoRequestMapper, platoResponseMapper,
                paginationResponseMapper, paginationRequestMapper);
    }

    @Bean
    public IPedidoHandler pedidoHandler(IPedidoServicePort pedidoServicePort,
                                        IPedidoRequestMapper pedidoRequestMapper,
                                        IPedidoUpdateMapper pedidoUpdateMapper,
                                        IPedidoResponseMapper pedidoResponseMapper,
                                        IPedidoTimeResponseMapper pedidoTimeResponseMapper,
                                        IEmpleadoTiempoResponseMapper empleadoTiempoResponseMapper,
                                        IPaginationResponseMapper paginationResponseMapper,
                                        IPaginationRequestMapper paginationRequestMapper,
                                        ITraceabilityResponseMapper traceabilityResponseMapper) {
        return new PedidoHandler(pedidoServicePort, pedidoRequestMapper, pedidoUpdateMapper, pedidoResponseMapper,
                pedidoTimeResponseMapper, empleadoTiempoResponseMapper, paginationResponseMapper,
                paginationRequestMapper, traceabilityResponseMapper);
    }
}
