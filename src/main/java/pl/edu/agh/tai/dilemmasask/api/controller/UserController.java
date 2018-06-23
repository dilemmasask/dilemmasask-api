package pl.edu.agh.tai.dilemmasask.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {

    @GetMapping(value = "/user") //just for test
    public Principal getLoggedUser(Principal user) {
        return user;
    }

}
