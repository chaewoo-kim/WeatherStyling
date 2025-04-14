package com.example.weatherstyle.controller;

import com.example.weatherstyle.entity.WeatherEntity;
import com.example.weatherstyle.service.WeatherService;
import com.example.weatherstyle.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private WeatherRepository weatherRepository;

    // 기상청 API에서 데이터를 받아와 DB에 저장
    @PostMapping("/fetch")
    public String fetchAndSaveWeather(@RequestParam(required = false) String tm, @RequestParam(required = false) String stn) {
        if (tm != null && stn != null) {
            weatherService.saveWeatherData(tm, stn);
            return " 날씨 데이터가 저장되었습니다!";
        }
        return "파라미터가 누락되었습니다.";
    }

    @GetMapping("/weather_form")
    public String showForm() {
        return "weather_form"; // templates/weather_form.html 파일을 반환
    }

    // 저장된 날씨 데이터 조회 (전체 리스트)
    @GetMapping("/all")
    public List<WeatherEntity> getAllWeather() {
        return weatherRepository.findAll();
    }
}
