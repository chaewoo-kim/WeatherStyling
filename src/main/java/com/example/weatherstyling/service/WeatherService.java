package com.example.weatherstyling.service;

import com.example.weatherstyling.api.*;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeatherService {

    private final JSONAPICall jsonAPICall;
    private final JSONAPIShortCall jsonapiShortCall;
    public final JSONAPILongCall jsonapiLongCall;

    public WeatherService(JSONAPICall jsonAPICall, JSONAPIShortCall jsonapiShortCall, JSONAPILongCall jsonapiLongCall) {
        this.jsonAPICall = jsonAPICall;
        this.jsonapiShortCall = jsonapiShortCall;
        this.jsonapiLongCall = jsonapiLongCall;
    }

    public Map<String, String> getWeatherData(String info) {
        try {
            return jsonAPICall.callAPI(info);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, String> getShortWeatherData(String info) {
        try {
            return jsonapiShortCall.callAPI(info);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, String> getLongWeatherData(String info, String tempURL) {
        try {
            return jsonapiLongCall.callAPI(info, tempURL);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, String> getOutfitData(String style) { //imageurl(파일 경로)을 return해야 함

        //여기서 어떤 옷을 보낼지에 대한 로직이 있어야 함

        StyleList styleList = new StyleList();
        styleList.setTop("/images/season/autumn/female/casual/top/top.jpeg");
        styleList.setTop("/images/season/autumn/female/casual/bottom/bottom.jpeg");
        styleList.setTop("/images/season/autumn/female/casual/outwear/jacket.jpeg");
        styleList.setTop("/images/season/autumn/female/casual/shoes/shoes.jpeg");

        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("top", styleList.getTop());
//        jsonObject.put("bottom", styleList.getBottom());
//        jsonObject.put("jacket", styleList.getOuterwear());
//        jsonObject.put("shoes", styleList.getShoes());

        Map<String, String> map = new HashMap<>();
        map.put("top", "/images/season/autumn/female/casual/top/top.jpeg");
        map.put("bottom", "/images/season/autumn/female/casual/bottom/bottom.jpeg");
        map.put("jacket", "/images/season/autumn/female/casual/outerwear/outerwear.jpeg");
        map.put("shoes", "/images/season/autumn/female/casual/shoes/shoes.jpeg");

        jsonObject.put("top", "/images/season/autumn/female/casual/top/top.jpeg");
        jsonObject.put("bottom", "/images/season/autumn/female/casual/bottom/bottom.jpeg");
        jsonObject.put("jacket", "/images/season/autumn/female/casual/outwear/jacket.jpeg");
        jsonObject.put("shoes", "/images/season/autumn/female/casual/shoes/shoes.jpeg");

        System.out.println(jsonObject);
        return map;
    }
}
