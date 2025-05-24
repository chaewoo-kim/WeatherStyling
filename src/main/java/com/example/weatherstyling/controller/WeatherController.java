package com.example.weatherstyling.controller;

import com.example.weatherstyling.WeatherStylingApplication;
import com.example.weatherstyling.api.*;
import com.example.weatherstyling.repository.LongWeatherRepository;
import com.example.weatherstyling.repository.ShortWeatherRepository;
import com.example.weatherstyling.service.WeatherService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.cglib.core.Local;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.time.LocalDate;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;
    private final JSONAPIShortCall jsonapiShortCall;
    private final JSONAPILongCall jsonapiLongCall;

    private final ShortWeatherRepository shortWeatherRepository;
    private final LongWeatherRepository longWeatherRepository;

    LocalDate today = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    String formattedToday = today.format(formatter);




    @PostMapping("/getApiInfo")
    public Map<String, String> getApiInfo(@RequestBody WeatherRequest request) {

        if (formattedToday.substring(6,8).equals("01")) {
            shortWeatherRepository.deleteAll();
            longWeatherRepository.deleteAll();
        }


        request.setStart_tm(request.getYear() + request.getMonth() + request.getDay() + request.getStart_hour());
        request.setEnd_tm(request.getYear() + request.getMonth() + request.getDay() + request.getEnd_hour());

        if (Integer.parseInt(formattedToday) + 3 > Integer.parseInt(request.getYear() + request.getMonth() + request.getDay())) {
            //단기 예보
            System.out.println(formattedToday);
            System.out.println(request.getYear() + request.getMonth() + request.getDay());
            request.setUrl_main(request.getUrl_body() + "fct_afs_dl.php?" + "stn=" + request.getPlaceNumber() + "&tmfc1=" + request.getStart_tm() + "&tmfc2=" + request.getEnd_tm() + "&disp=0&" + request.getHelp() + "&authKey=" + request.getAuthKey());
            System.out.println("url: "+request.getUrl_main());
            return weatherService.getShortWeatherData(request.getUrl_main());
        } else {
            //중기 예보
            request.setUrl_main(request.getUrl_body() + "fct_afs_wl.php?" + "stn=" + request.getPlaceNumber() + "&tmfc1=" + formattedToday + "06" + "&tmfc2=" + formattedToday + "18&tmef1=" + request.getStart_tm() + "&tmef2=" + request.getEnd_tm() + "&disp=0&" + request.getHelp() + "&authKey=" + request.getAuthKey());
            System.out.println("url : " + request.getUrl_main());
            return weatherService.getLongWeatherData(request.getUrl_main());
        }

    }


    @PostMapping("/outfit")
    public Map<String, String> getOutfit(@RequestBody CustomerRequest request) {

        return weatherService.getOutfitData(request.getStyle());
    }

}
