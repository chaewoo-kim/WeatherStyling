package com.example.weatherstyling.service;

import com.example.weatherstyling.api.JSONAPICall;

import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private final JSONAPICall jsonAPICall;

    public WeatherService(JSONAPICall jsonAPICall) {
        this.jsonAPICall = jsonAPICall;
    }

    public String getWeatherData() {
        try {
            return jsonAPICall.callAPI();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
