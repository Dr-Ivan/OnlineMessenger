package com.app.sms.Service;

import com.app.sms.DTO.MessageDTO;
import com.app.sms.DTO.PageFilter;
import com.app.sms.DTO.UserDTO;
import com.app.sms.Entity.MessageEntity;
import com.app.sms.Entity.UserEntity;
import com.app.sms.Repository.MessageRepository;
import com.app.sms.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Value("${pagination.defaultPageSize:10}")
    private int defaultPageSize;

    @Autowired
    public MessageService(UserRepository userRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    public UserDTO getUser(String name) throws IllegalArgumentException {
        Optional<UserEntity> user = userRepository.findByName(name);
        if (user.isPresent())
            return new UserDTO(
                    user.get().getName(),
                    user.get().getPasswordHash()
            );
        else
            throw new EntityNotFoundException("Нет имени " + name + " пользователя");
    }

    public UserDTO getUser(String name, String pass) throws IllegalArgumentException {
        if (userRepository.existsByNameAndPasswordHash(name, pass)) {
            Optional<UserEntity> user = userRepository.findByName(name);
            if (user.isEmpty()) throw new IllegalArgumentException("Нет такого имени пользователя");
            return new UserDTO(
                    user.get().getName(),
                    user.get().getPasswordHash()
            );
        } else
            throw new EntityNotFoundException("Нет имени " + name + " пользователя с указанным паролем");
    }

    public List<String> getAllUsers(PageFilter filter) {
        int pageSize = filter.pageSize() == null
                ? this.defaultPageSize : filter.pageSize();
        int pageNum = filter.pageNumber() == null
                ? 0 : filter.pageNumber();
        Pageable pageable = Pageable
                .ofSize(pageSize)
                .withPage(pageNum);
        return userRepository
                .getUserNamesPaged(pageable);
    }

    public List<MessageDTO> getMessagesOfUser(String name) {
        return messageRepository.findByFromUserName(name)
                .stream().map(a -> new MessageDTO(
                        a.getId(),
                        a.getFromUserName(),
                        null,
                        a.getToUserName(),
                        a.getTime(),
                        a.getContent()))
                .toList();
    }

    public List<MessageDTO> getMessagesBetweenUsers(String nameFrom, String nameTo) {
//        messages.stream()
//                .filter(a -> (
//                        a.getFromUserName().equals(nameFrom)
//                                && a.getToUserName().equals(nameTo))
//                        || (a.getFromUserName().equals(nameTo)
//                        && a.getToUserName().equals(nameFrom)))
//                .toList();
        return messageRepository.getDialogMessages(nameFrom, nameTo)
                .stream().map(a -> new MessageDTO(
                        a.getId(),
                        a.getFromUserName(),
                        null,
                        a.getToUserName(),
                        a.getTime(),
                        a.getContent()))
                .toList();
    }

    public List<String> getContactsOfUser(String name) {
//        messages.stream()
//                .filter(m -> m.getFromUserName().equals(name) || m.getToUserName().equals(name))
//                .map(m -> m.getFromUserName().equals(name) ? m.getToUserName() : m.getFromUserName())
//                .distinct().toList();
        return messageRepository.getContactsOfUser(name);
    }

    public MessageDTO addMessage(MessageDTO m) throws IllegalArgumentException, EntityNotFoundException {
        if (m == null)
            throw new IllegalArgumentException("Не может быть пустого сообщения ");

        if (m.getTime() == null) m.setTime(LocalDateTime.now());

        String fromUserName = m.getFromUserName();
        String toUserName = m.getToUserName();

        if (!userRepository.existsByName(fromUserName))
            throw new IllegalArgumentException("Несуществующий пользователь с именем " + fromUserName);

        if (!userRepository.existsByName(toUserName))
            throw new IllegalArgumentException("Несуществующий пользователь с именем " + toUserName);

        if (!userRepository.existsByNameAndPasswordHash(m.getFromUserName(), m.getFromUserPasswordHash()))
            throw new EntityNotFoundException("Неверное имя пользователя " + fromUserName + " или пароль");


        MessageEntity toSave = new MessageEntity(m.getFromUserName(),
                m.getToUserName(),
                m.getTime(),
                m.getContent()
        );

        messageRepository.save(toSave);
        m.setId(toSave.getId());
        return m;
    }

    public UserDTO addUser(UserDTO user) throws IllegalArgumentException {
        if (user == null || user.getName() == null || user.getPasswordHash() == null
                || user.getName().isEmpty() || user.getName().isBlank()
                || user.getPasswordHash().isEmpty() || user.getPasswordHash().isBlank())
            throw new IllegalArgumentException("Не может быть пользователя c неполным набором атрибутов");

        if (userRepository.existsByName(user.getName()))
            throw new IllegalArgumentException("Имя пользователя " + user.getName() + " уже занято");

        UserEntity toSave = new UserEntity(
                user.getName(),
                user.getPasswordHash()
        );
        userRepository.save(toSave);
        return user;
    }

    public void deleteUser(String name, String password) throws EntityNotFoundException {
        if (!userRepository.existsByNameAndPasswordHash(name, password))
            throw new EntityNotFoundException("Пользователя с именем " + name + " и указанным паролем не существует");
        userRepository.deleteById(name);
    }

    public MessageDTO updateMessage(MessageDTO newMessage) throws EntityNotFoundException, IllegalArgumentException {
        if (!userRepository.existsByNameAndPasswordHash(
                newMessage.getFromUserName(),
                newMessage.getFromUserPasswordHash()))
            throw new EntityNotFoundException("Неверное имя пользователя " + newMessage.getFromUserName() + " или пароль");

        if (!messageRepository.existsById(newMessage.getId()))
            throw new EntityNotFoundException("Не существует сообщения с id = " + newMessage.getId());

        MessageEntity old = messageRepository
                .findById(newMessage.getId())
                .orElseThrow(() -> new EntityNotFoundException("Не существует сообщения с id = " + newMessage.getId()));

        if (!old.getFromUserName().equals(newMessage.getFromUserName()))
            throw new IllegalArgumentException("Нельзя изменить чужое сообщение. Отправитель: " + old.getFromUserName());

        messageRepository.updateMessageText(newMessage.getId(), newMessage.getContent());

        newMessage.setFromUserPasswordHash(null);
        return newMessage;
    }

    public void deleteMessage(String userName, Long id) {
        MessageEntity old = messageRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Не существует сообщения с id = " + id));

        if (!old.getFromUserName().equals(userName))
            throw new IllegalArgumentException("Нельзя удалить чужое сообщение. Отправитель: " + old.getFromUserName());

        messageRepository.deleteById(id);
    }
}
