package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.UserModel;

public interface IUserExternalServicePort {

    UserModel getUserById(Long id);

}
