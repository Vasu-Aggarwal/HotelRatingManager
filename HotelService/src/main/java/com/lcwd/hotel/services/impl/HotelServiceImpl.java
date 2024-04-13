package com.lcwd.hotel.services.impl;

import com.lcwd.hotel.entities.Hotel;
import com.lcwd.hotel.entities.Rating;
import com.lcwd.hotel.exceptions.ResourceNotFoundException;
import com.lcwd.hotel.repositories.HotelRepository;
import com.lcwd.hotel.services.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HotelServiceImpl implements HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Hotel createHotel(Hotel hotel) {
        String hotelId = UUID.randomUUID().toString();
        hotel.setHotelId(hotelId);
        return this.hotelRepository.save(hotel);
    }

    @Override
    public List<Hotel> getAllHotels() {
        List<Hotel> hotels = this.hotelRepository.findAll();
        List<Hotel> hotelList = hotels.stream().map(hotel -> {
            return getHotelById(hotel.getHotelId());
        }).collect(Collectors.toList());

        return hotelList;
    }

    @Override
    public Hotel getHotelById(String hotelId) {
        Hotel hotel = this.hotelRepository.findById(hotelId).orElseThrow(()->new ResourceNotFoundException("Resource with given hotelId not found"));

        //fetch all the ratings found on the hotel
        Rating[] ratings = this.restTemplate.getForObject("http://RATING-SERVICE/ratings/hotels/"+hotelId, Rating[].class);

        List<Rating> ratingList = Arrays.stream(ratings).toList();
        hotel.setRatings(ratingList);

        return hotel;
    }
}
