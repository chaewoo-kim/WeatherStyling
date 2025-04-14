package com.example.weatherstyle.service;

import com.example.weatherstyle.api.WeatherApiClient;
import com.example.weatherstyle.entity.WeatherEntity;
import com.example.weatherstyle.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Service
public class WeatherService {
    @Autowired
    private WeatherRepository weatherRepository;

    @Autowired
    private WeatherApiClient weatherApiClient; // API 호출을 담당하는 클래스

    public void saveWeatherData(String tm, String stn) {
        String response = weatherApiClient.fetchWeatherData(tm, stn); // API 호출

        if (response == null || response.isEmpty()) {
            System.out.println("API 응답 없음!");
            return;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.readTree(response);

            double temperature = json.get("temperature").asDouble();
            double windSpeed = json.get("wind_speed").asDouble();
            double humidity = json.get("humidity").asDouble();
            double precipitation = json.get("precipitation").asDouble();

            WeatherEntity weather = new WeatherEntity();
            weather.setTemperature(temperature);
            weather.setWindSpeed(windSpeed);
            weather.setHumidity(humidity);
            weather.setPrecipitation(precipitation);
            weather.setRecordedAt(LocalDateTime.now());

            weatherRepository.save(weather);
            System.out.println("날씨 데이터 저장 완료!");
        } catch (Exception e) {
            System.out.println("데이터 저장 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
