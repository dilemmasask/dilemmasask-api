package pl.edu.agh.tai.dilemmasask.api.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.tai.dilemmasask.api.model.User;

import java.security.Principal;

@RestController
public class UserController {

    @GetMapping(value = "/user") //just for test
    public Principal getLoggedUser(Principal user) {
        return user;
    }

}
