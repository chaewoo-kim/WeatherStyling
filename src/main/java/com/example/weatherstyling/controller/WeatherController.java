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
import java.time.temporal.ChronoUnit;
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

        String providedDateStr = request.getYear() + request.getMonth() + request.getDay();

        request.setStart_tm(request.getYear() + request.getMonth() + request.getDay() + request.getStart_hour());
        request.setEnd_tm(request.getYear() + request.getMonth() + request.getDay() + request.getEnd_hour());

        LocalDate providedDate = LocalDate.parse(providedDateStr, formatter);
        LocalDate today = LocalDate.now();

        long daysBetween = ChronoUnit.DAYS.between(today, providedDate);
        System.out.println(providedDate);
        System.out.println(today);
        System.out.println(daysBetween);


        if (daysBetween < 4) {
            //단기 예보
            System.out.println(formattedToday);
            System.out.println(request.getYear() + request.getMonth() + request.getDay());
            request.setUrl_main(request.getUrl_body() + "fct_afs_dl.php?" + "&tmef1=" + request.getStart_tm() + "&tmef2=" + request.getEnd_tm() + "&disp=0&" + request.getHelp() + "&authKey=" + request.getAuthKey());
            System.out.println("url: "+request.getUrl_main());

            return weatherService.getShortWeatherData(request.getUrl_main());
        } else {
            //중기 예보
            request.setUrl_main(request.getUrl_body() + "fct_afs_wl.php?" + "&reg=" + request.getReg() + "&tmfc1=" + formattedToday + "06" + "&tmfc2=" + formattedToday + "18&tmef1=" + request.getStart_tm() + "&tmef2=" + request.getEnd_tm() + "&disp=0&" + request.getHelp() + "&authKey=" + request.getAuthKey());
            String tempURL = "";
            tempURL = request.getUrl_body() + "fct_afs_wc.php?" + "&reg=" + request.getReg() + "&tmfc1=" + formattedToday + "06" + "&tmfc2=" + formattedToday + "18&tmef1=" + request.getStart_tm() + "&tmef2=" + request.getEnd_tm() + "&disp=0&" + request.getHelp() + "&authKey=" + request.getAuthKey();
            System.out.println("url : " + request.getUrl_main());
            System.out.println("tmep URL : " + tempURL);

            return weatherService.getLongWeatherData(request.getUrl_main(), tempURL, request.getReg());
        }

    }


    @PostMapping("/outfit")
    public Map<String, String> getOutfit(@RequestBody CustomerRequest request) {

        return weatherService.getOutfitData(request.getStyle(), request.getGender());
    }

}
