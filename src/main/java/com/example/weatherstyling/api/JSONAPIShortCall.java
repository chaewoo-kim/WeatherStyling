package com.example.weatherstyling.api;

import com.example.weatherstyling.model.ShortWeather;
import com.example.weatherstyling.model.Weather;
import com.example.weatherstyling.repository.ShortWeatherRepository;
import com.example.weatherstyling.repository.WeatherRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JSONAPIShortCall {

    private final ShortWeatherRepository shortWeatherRepository;
    private final WeatherRepository weatherRepository;

    public Map<String, String> callAPI(String info) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        // API URL을 만듭니다.
        //URL 생성할 때 날짜의 형식은 YYYYMMDD06 ~ YYYYMMDD18로 해야함
        URL url = new URL(info);

        Optional<ShortWeather> shortWeatherOptional = shortWeatherRepository.findByUrl(info); //info가 있으면 weather Entity, 없으면 null 반환
        if (shortWeatherOptional.isPresent()) {
            //저장된 게 있으면 있던 값들 Map 자료형으로 만들어서 보내줘야 함

            ShortWeather shortWeather = shortWeatherOptional.get();
            Map<String, String> map = new HashMap<>();

            map.put("ST", shortWeather.getSt()); //강수 확률
            map.put("TA", shortWeather.getTemperature()); //기온
            map.put("SKY", shortWeather.getSky()); //하늘 상태
            map.put("PREP", shortWeather.getPrep()); //강수 유무

            System.out.println("값 DB에 존재함");
            System.out.println(map);

            Weather weather = weatherRepository.findById(1L).get();
            weather.setTemperature(shortWeather.getTemperature());
            weather.setSky(shortWeather.getSky());
            weather.setSt(shortWeather.getSt());
            weather.setPrep(shortWeather.getPrep());
            weatherRepository.save(weather);

            return map;

        } else {

            // HttpURLConnection 객체를 만들어 API를 호출합니다.
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // 요청 방식을 GET으로 설정합니다.
            con.setRequestMethod("GET");
            // 요청 헤더를 설정합니다. 여기서는 Content-Type을 application/json으로 설정합니다.
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // 에러 메시지 확인용
            InputStream stream;
            if (con.getResponseCode() >= 200 && con.getResponseCode() < 300) {
                stream = con.getInputStream();
            } else {
                stream = con.getErrorStream();
            }

            // API의 응답을 읽기 위한 BufferedReader를 생성합니다.
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(stream,"EUC-KR"));
            String inputLine;
            StringBuffer response = new StringBuffer();

            // 응답을 한 줄씩 읽어들이면서 StringBuffer에 추가합니다.
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            // BufferedReader를 닫습니다.
            in.close();

            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);

            // 응답을 출력합니다.
            System.out.println(prettyJson);

            StringParser stringParser = new StringParser(prettyJson, shortWeatherRepository, info, weatherRepository);
            stringParser.stringParser();

            System.out.println("값 DB에 존재하지 않음");

            return stringParser.makeMap();
        }


    }


}
