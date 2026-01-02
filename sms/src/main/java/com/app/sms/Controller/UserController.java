package com.app.sms.Controller;


import com.app.sms.DTO.UserDTO;
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
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final MessageService messageService;

    @Autowired
    public UserController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ResponseEntity<List<String>> getUsers() {
        return ResponseEntity.ok(messageService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO user) {
        UserDTO res = messageService.addUser(user);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/login/{name}/{pass}")
    public ResponseEntity<UserDTO> loginUser(
            @PathVariable("name") String name,
            @PathVariable("pass") String pass
    ) {
        UserDTO res = messageService.getUser(name, pass);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{name}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("name") String name) {
        UserDTO res = messageService.getUser(name);
        res.setPasswordHash(null);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{name}/contacts")
    public ResponseEntity<List<String>> getContacts (@PathVariable("name") String name){
        return ResponseEntity.ok(messageService.getContactsOfUser(name));
    }

    @DeleteMapping("/{name}/{password}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable("name") String name,
            @PathVariable("password") String password
    ){
        messageService.deleteUser(name, password);
        return ResponseEntity.ok().build();
    }
}
