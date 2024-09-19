package com.shubham.UserServer.responseEntity;

import jakarta.persistence.Column;

public class RoomResponse {
    private Integer roomNo;
    private String location;
    private Integer extraChargePerMonth;
    private Integer availableSeats;
}
