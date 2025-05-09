package com.example.weatherstyling.controller;

import com.example.weatherstyling.WeatherStylingApplication;
import com.example.weatherstyling.api.CustomerRequest;
import com.example.weatherstyling.api.JSONAPICall;
import com.example.weatherstyling.api.StyleList;
import com.example.weatherstyling.api.WeatherRequest;
import com.example.weatherstyling.service.WeatherService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @PostMapping("/getApiInfo")
    public String getApiInfo(@RequestBody WeatherRequest request) {

        request.setTm(request.getYear() + request.getMonth() + request.getDay() + request.getHour() + request.getMinute());
        request.setUrl_main(request.getUrl_body() + request.getTm() + "&stn=" + request.getPlaceNumber() + "&" + request.getHelp() + "&authKey=" + request.getAuthKey());

        return weatherService.getWeatherData(request.getUrl_main());
    }

    @GetMapping("/outfit")
    public StyleList getOutfit(@RequestBody CustomerRequest request) {
        return weatherService.getOutfitData(request.getStyle());
    }

}
