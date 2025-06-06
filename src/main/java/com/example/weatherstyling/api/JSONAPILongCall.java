package com.example.weatherstyling.api;

import com.example.weatherstyling.model.LongWeather;
import com.example.weatherstyling.model.Weather;
import com.example.weatherstyling.repository.LongWeatherRepository;
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
public class JSONAPILongCall {

    private final LongWeatherRepository longWeatherRepository;
    private final WeatherRepository weatherRepository;

    private String common_reg;

    public Map<String, String> callAPI(String info, String tempURL, String reg) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        //여기다가 온도 API까지 같이 해 버리자.

        //얘는 URL 만들 때 발표 시각은 현재, 발효 시각은 원할 때로 해야 함
        //https://apihub.kma.go.kr/api/typ01/url/fct_afs_wl.php?tmfc1=2025052406&tmfc2=2025052418&tmef1=2025052806&tmef2=2025052818&disp=0&help=0&authKey=oqq5zcJtQkSquc3CbWJEyA

        Optional<LongWeather> longWeatherOptional = longWeatherRepository.findByUrl(info);
        if (longWeatherOptional.isPresent()) {

            LongWeather longWeather = longWeatherOptional.get();

            Map<String, String> map = new HashMap<>();

            map.put("ST", longWeather.getSt());
            map.put("SKY", longWeather.getSky());
            map.put("PREP", longWeather.getPrep());
            map.put("TA", longWeather.getTemperature());

            Weather weather = weatherRepository.findById(1L).get();
            weather.setTemperature(longWeather.getTemperature());
            weather.setSky(longWeather.getSky());
            weather.setSt(longWeather.getSt());
            weather.setPrep(longWeather.getPrep());
            weatherRepository.save(weather);

            System.out.println("값 DB에 존재함");
            System.out.println(map);

            return map;
        } else {
            try {

                // API URL을 만듭니다.
                URL url = new URL(info);
                URL tempUrl = new URL(tempURL);

                // HttpURLConnection 객체를 만들어 API를 호출합니다.
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                HttpURLConnection tempCon = (HttpURLConnection) tempUrl.openConnection();

                // 요청 방식을 GET으로 설정합니다.
                con.setRequestMethod("GET");
                tempCon.setRequestMethod("GET");

                // 요청 헤더를 설정합니다. 여기서는 Content-Type을 application/json으로 설정합니다.
                con.setRequestProperty("Content-Type", "application/json; charset=EUC-KR");
                tempCon.setRequestProperty("Content-Type", "application/json; charset=EUC-KR");

                // 에러 메시지 확인용
                InputStream stream;
                if (con.getResponseCode() >= 200 && con.getResponseCode() < 300) {
                    stream = con.getInputStream();
                } else {
                    stream = con.getErrorStream();
                }

                InputStream tempStream;
                if (tempCon.getResponseCode() >= 200 && tempCon.getResponseCode() < 300) {
                    tempStream = tempCon.getInputStream();
                } else {
                    tempStream = tempCon.getErrorStream();
                }


                // API의 응답을 읽기 위한 BufferedReader를 생성합니다.
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(stream, "EUC-KR"));
                String inputLine;
                StringBuffer response = new StringBuffer();

                BufferedReader tempIn = new BufferedReader(
                        new InputStreamReader(tempStream, "EUC-KR"));
                String tempInputLine;
                StringBuffer tempResponse = new StringBuffer();

                // 응답을 한 줄씩 읽어들이면서 StringBuffer에 추가합니다.
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                while ((tempInputLine = tempIn.readLine()) != null) {
                    tempResponse.append(tempInputLine);
                }

                // BufferedReader를 닫습니다.
                in.close();
                tempIn.close();

                String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
                String tempJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tempResponse);
                System.out.println("prettyJson: " + prettyJson);
                System.out.println("tempJson: " + tempJson);
                StringParser stringParser = new StringParser(prettyJson, longWeatherRepository, info, tempJson, weatherRepository);
                stringParser.stringParser();

                System.out.println("값 DB에 존재하지 않음");

                return stringParser.makeMap();
            } catch (Exception e) {

                String url_reg_three = reg.substring(0,3);
                String url_reg_four = reg.substring(0,4);

                if (url_reg_three.equals("11B")) {
                    this.common_reg = "11B0000";
                }

                switch (url_reg_four) {
                    case "11C1":
                        this.common_reg = "11C1000";
                        break;
                    case "11C2":
                        this.common_reg = "11C2000";
                        break;
                    case "11D1":
                        this.common_reg = "11D1000";
                        break;
                    case "11D2":
                        this.common_reg = "11D2000";
                        break;
                    case "11F1":
                        this.common_reg = "11F1000";
                        break;
                    case "11F2":
                        this.common_reg = "11F2000";
                        break;
                    case "11G0":
                        this.common_reg = "11G0000";
                        break;
                    case "11H1":
                        this.common_reg = "11H1000";
                        break;
                    case "11H2":
                        this.common_reg = "11H2000";
                        break;
                    case "11I0":
                        this.common_reg = "11I0000";
                        break;
                    case "11J1":
                        this.common_reg = "11J1000";
                        break;
                    case "11J2":
                        this.common_reg = "11J2000";
                        break;
                    case "11K1":
                        this.common_reg = "11K1000";
                        break;
                    case "11K2":
                        this.common_reg = "11K2000";
                        break;
                }


                // API URL을 만듭니다.
                String target = "reg=";
                int index = info.indexOf(target);
                String front_url = info.substring(0, index + target.length());
                String back_url = info.substring(index + target.length());

                //url 만들면 됨
                String new_url = front_url + common_reg + back_url;

                System.out.println("new_url: " + new_url);

                URL url = new URL(new_url);
                URL tempUrl = new URL(tempURL);

                // HttpURLConnection 객체를 만들어 API를 호출합니다.
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                HttpURLConnection tempCon = (HttpURLConnection) tempUrl.openConnection();

                // 요청 방식을 GET으로 설정합니다.
                con.setRequestMethod("GET");
                tempCon.setRequestMethod("GET");

                // 요청 헤더를 설정합니다. 여기서는 Content-Type을 application/json으로 설정합니다.
                con.setRequestProperty("Content-Type", "application/json; charset=EUC-KR");
                tempCon.setRequestProperty("Content-Type", "application/json; charset=EUC-KR");

                // 에러 메시지 확인용
                InputStream stream;
                if (con.getResponseCode() >= 200 && con.getResponseCode() < 300) {
                    stream = con.getInputStream();
                } else {
                    stream = con.getErrorStream();
                }

                InputStream tempStream;
                if (tempCon.getResponseCode() >= 200 && tempCon.getResponseCode() < 300) {
                    tempStream = tempCon.getInputStream();
                } else {
                    tempStream = tempCon.getErrorStream();
                }


                // API의 응답을 읽기 위한 BufferedReader를 생성합니다.
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(stream, "EUC-KR"));
                String inputLine;
                StringBuffer response = new StringBuffer();

                BufferedReader tempIn = new BufferedReader(
                        new InputStreamReader(tempStream, "EUC-KR"));
                String tempInputLine;
                StringBuffer tempResponse = new StringBuffer();

                // 응답을 한 줄씩 읽어들이면서 StringBuffer에 추가합니다.
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                while ((tempInputLine = tempIn.readLine()) != null) {
                    tempResponse.append(tempInputLine);
                }

                // BufferedReader를 닫습니다.
                in.close();
                tempIn.close();

                String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
                String tempJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tempResponse);
                System.out.println("prettyJson: " + prettyJson);
                System.out.println("tempJson: " + tempJson);
                StringParser stringParser = new StringParser(prettyJson, longWeatherRepository, info, tempJson, weatherRepository);
                stringParser.stringParser();

                System.out.println("값 DB에 존재하지 않음");
                e.printStackTrace();

                return stringParser.makeMap();

            }
        }
    }
}

