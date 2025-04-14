package com.example.weatherstyle.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiRequestDto {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private String placeNumber;
    private String gender;
}
