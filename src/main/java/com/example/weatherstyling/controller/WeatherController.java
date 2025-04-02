package com.example.weatherstyling.controller;

import com.example.weatherstyling.WeatherStylingApplication;
import com.example.weatherstyling.api.JSONAPICall;
import com.example.weatherstyling.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/get")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/weather")
    public String getWeather() {
        return weatherService.getWeatherData();
    }

}
