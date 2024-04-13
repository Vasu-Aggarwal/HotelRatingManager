package com.lcwd.user.service.services.impl;

import com.lcwd.user.service.entities.Hotel;
import com.lcwd.user.service.entities.Rating;
import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.exceptions.ResourceNotFoundException;
import com.lcwd.user.service.repositories.UserRepository;
import com.lcwd.user.service.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User saveUser(User user) {
        String uuid = UUID.randomUUID().toString();
        user.setUuid(uuid);
        return this.userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = this.userRepository.findAll();

        return users.stream().map(user -> {
            return getUser(user.getUuid());
        }).collect(Collectors.toList());
    }

    @Override
    public User getUser(String uuid) {
        User user = this.userRepository.findById(uuid).orElseThrow(ResourceNotFoundException::new);

        //fetch rating of the above user from RATING-SERVICE
        //http://localhost:8083/ratings/users/{uuid}
        Rating[] ratingOfUser = this.restTemplate.getForObject("http://RATING-SERVICE/ratings/users/"+user.getUuid(), Rating[].class);
        logger.info("{} ", ratingOfUser);

        List<Rating> ratings = Arrays.stream(ratingOfUser).toList();

        List<Rating> ratingList = ratings.stream().map(rating -> {
           //api call to hotel service to get the hotel
            ResponseEntity<Hotel> forObject = this.restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/"+rating.getHotelId(), Hotel.class);
            Hotel hotel = forObject.getBody();
            rating.setHotel(hotel);
           return rating;
        }).collect(Collectors.toList());

        user.setRatings(ratingList);
        return user;
    }
}
