package com.app.sms.Mappers;

import com.app.sms.DTO.UserDTO;
import com.app.sms.Entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(UserDTO dto) {
        return new UserEntity(
                dto.getName(),
                dto.getPasswordHash()
        );
    }

    public UserDTO toDTO(UserEntity entity) {
        return new UserDTO(
                entity.getName(),
                entity.getPasswordHash()
        );
    }
}
