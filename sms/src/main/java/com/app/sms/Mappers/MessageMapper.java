package com.app.sms.Mappers;

import com.app.sms.DTO.MessageDTO;
import com.app.sms.Entity.MessageEntity;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageEntity toEntity(MessageDTO dto) {
        return new MessageEntity(
                dto.getFromUserName(),
                dto.getToUserName(),
                dto.getTime(),
                dto.getContent()
        );
    }

    public MessageDTO toDTO(MessageEntity entity) {
        return new MessageDTO(
                entity.getId(),
                entity.getFromUserName(),
                null, // избежание утечки хэша пароля в DTO
                entity.getToUserName(),
                entity.getTime(),
                entity.getContent()
        );
    }
}
