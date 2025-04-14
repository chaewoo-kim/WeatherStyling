package com.example.weatherstyle.controller;

import com.example.weatherstyle.api.WeatherApiClient;
import com.example.weatherstyle.api.dto.ApiRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class WeatherApiController {

    private final WeatherApiClient weatherApiClient;

    @Autowired
    public WeatherApiController(WeatherApiClient weatherApiClient) {
        this.weatherApiClient = weatherApiClient;
    }

    /**
     * [POST] /api/getApiInfo
     * - 클라이언트가 보낸 날짜/시간/측정소 정보로 기상청 API 호출
     * - 응답 받은 날씨 데이터를 그대로 반환
     */
    @PostMapping("/getApiInfo")
    public String getWeatherInfo(@RequestBody ApiRequestDto request) {
        // 시간 문자열 생성 (예: 202504071430)
        String tm = String.format("%04d%02d%02d%02d%02d",
                request.getYear(), request.getMonth(), request.getDay(),
                request.getHour(), request.getMinute());

        String stn = request.getPlaceNumber();

        String result = weatherApiClient.fetchWeatherData(tm, stn);
        return result != null ? result : "기상청 API 호출에 실패했습니다.";
    }
}
