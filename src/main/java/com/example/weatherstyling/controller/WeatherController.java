package com.example.weatherstyling.controller;

import com.example.weatherstyling.WeatherStylingApplication;
import com.example.weatherstyling.api.*;
import com.example.weatherstyling.service.WeatherService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;
    private final JSONAPIShortCall jsonapiShortCall;

    @PostMapping("/getApiInfo")
    public Map<String, String> getApiInfo(@RequestBody WeatherRequest request) {

        request.setStart_tm(request.getYear() + request.getMonth() + request.getDay() + request.getStart_hour());
        request.setEnd_tm(request.getYear() + request.getMonth() + request.getDay() + request.getEnd_hour());
        request.setUrl_main(request.getUrl_body() + "stn=" + request.getPlaceNumber() + "&tmfc1=" + request.getStart_tm() + "&tmfc2=" + request.getEnd_tm() + "&disp=0&" + request.getHelp() + "&authKey=" + request.getAuthKey());
        System.out.println("url: "+request.getUrl_main());
        return weatherService.getShortWeatherData(request.getUrl_main());
    }


    @PostMapping("/outfit")
    public Map<String, String> getOutfit(@RequestBody CustomerRequest request) {

        return weatherService.getOutfitData(request.getStyle());
    }

}
