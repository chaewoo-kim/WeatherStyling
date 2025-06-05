package com.example.weatherstyling.service;

import com.example.weatherstyling.api.*;

import com.example.weatherstyling.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeatherService {

    private final JSONAPIShortCall jsonapiShortCall;
    public final JSONAPILongCall jsonapiLongCall;

    private final WeatherRepository weatherRepository;

    public WeatherService(JSONAPIShortCall jsonapiShortCall, JSONAPILongCall jsonapiLongCall, WeatherRepository weatherRepository) {
        this.jsonapiShortCall = jsonapiShortCall;
        this.jsonapiLongCall = jsonapiLongCall;
        this.weatherRepository = weatherRepository;
    }



    public Map<String, String> getShortWeatherData(String info) {
        try {
            return jsonapiShortCall.callAPI(info);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, String> getLongWeatherData(String info, String tempURL, String reg) {
        try {
            return jsonapiLongCall.callAPI(info, tempURL, reg);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, String> getOutfitData(String style, String gender) { //imageurl(파일 경로)을 return해야 함

        ClothingRecommendar clothingRecommendar = new ClothingRecommendar(weatherRepository, style, gender);
        try {
            clothingRecommendar.getTopRecommend();
            clothingRecommendar.getBottomRecommend();
            clothingRecommendar.getOuterRecommend();
            clothingRecommendar.getShoesRecommend();
            clothingRecommendar.getAccessoryRecommend();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> map = new HashMap<>();
        map.put("top", "/images/season/autumn/female/casual/top/top.jpeg");
        map.put("bottom", "/images/season/autumn/female/casual/bottom/bottom.jpeg");
        map.put("jacket", "/images/season/autumn/female/casual/outerwear/outerwear.jpeg");
        map.put("shoes", "/images/season/autumn/female/casual/shoes/shoes.jpeg");

        return map;
    }
}
