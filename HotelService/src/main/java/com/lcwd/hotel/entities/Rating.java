package com.lcwd.hotel.entities;

import lombok.Data;

@Data
public class Rating {

    private String ratingId;
    private String uuid;
    private String hotelId;
    private int rating;
    private String feedback;
}
