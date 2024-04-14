package com.lcwd.user.service.controllers;

import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.services.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/")
    public ResponseEntity<User> createUser(@RequestBody User user){
        User newUser = this.userService.saveUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @GetMapping("/")
    @CircuitBreaker(name="allUserRating")
    public ResponseEntity<List<User>> getAllUsers(){
        return new ResponseEntity<>(this.userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{uuid}")
    @CircuitBreaker(name="singleUserRating", fallbackMethod = "singleUserRatingFallback")
    public ResponseEntity<User> getUserById(@PathVariable String uuid){
        return new ResponseEntity<>(this.userService.getUser(uuid), HttpStatus.OK);
    }

    //Creating fallback method for singleUserRating
    public ResponseEntity<User> singleUserRatingFallback(String uuid, Exception exception){
        logger.info("Fallback is executed because service is down : {}", exception.getMessage());
        User user = User.builder()
                .email("Dummy@gmail.com")
                .name("Dummy")
                .about("This user is created because service is down !!")
                .build();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
