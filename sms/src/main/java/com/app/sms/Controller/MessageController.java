package com.app.sms.Controller;


import com.app.sms.DTO.MessageDTO;
import com.app.sms.Service.MessageService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/messages")
public class MessageController {
    private static final Logger log = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }


    @PostMapping
    public ResponseEntity<MessageDTO> sendMessage (@RequestBody @Valid MessageDTO msg){
        MessageDTO res = messageService.addMessage(msg);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{name1}/chat/{name2}")
    public ResponseEntity<List<MessageDTO>> getChatMessages(
            @PathVariable("name1") String id1,
            @PathVariable("name2") String id2
    ){
        return ResponseEntity.ok(messageService.getMessagesBetweenUsers(id1, id2));
    }

    @PutMapping("/edit")
    public ResponseEntity<MessageDTO> editMessage(@RequestBody @Valid MessageDTO newMessage){
        // обновить в БД
        newMessage = messageService.updateMessage(newMessage);
        return ResponseEntity.ok(newMessage);
    }

    @DeleteMapping("/{user}/{id}")
    public void deleteMessage(
            @PathVariable("id") Long messageId,
            @PathVariable("user") String userName
    ){
        messageService.deleteMessage(userName, messageId);
    }
}
