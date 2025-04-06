package com.example.weatherstyling.service;

import com.example.weatherstyling.api.JSONAPICall;

import com.example.weatherstyling.api.StyleList;
import com.example.weatherstyling.api.WeatherRequest;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    private final JSONAPICall jsonAPICall;

    public WeatherService(JSONAPICall jsonAPICall) {
        this.jsonAPICall = jsonAPICall;
    }

    public String getWeatherData(String info) {
        try {
            return jsonAPICall.callAPI(info);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public StyleList getOutfitData(String style) { //imageurl(파일 경로)을 return해야 함

        //여기서 어떤 옷을 보낼지에 대한 로직이 있어야 함

        StyleList styleList = new StyleList();
        return styleList;
    }
}
