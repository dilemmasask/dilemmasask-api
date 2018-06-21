package pl.edu.agh.tai.dilemmasask.api.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.tai.dilemmasask.api.model.User;

@RestController
public class UserController {

    @GetMapping(value = {"/login", "/user"}) //just for test
    public User getLoggedUser(@AuthenticationPrincipal User user) {
        return user;
    }

}
