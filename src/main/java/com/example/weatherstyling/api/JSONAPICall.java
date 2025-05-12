package com.example.weatherstyling.api;

import com.example.weatherstyling.model.Weather;
import com.example.weatherstyling.repository.WeatherRepository;
import jdk.jshell.spi.SPIResolutionException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

// java.net.*과 java.io.* 패키지를 임포트합니다.
import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// JSONAPICall이라는 클래스를 생성합니다.
@Component
@RequiredArgsConstructor
public class JSONAPICall {

    private final WeatherRepository weatherRepository;

    public Map<String, String> callAPI(String info) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        //url = url_body + tm + "&" + "stn=" + stn + "&" + help + "&" + "authKey=" + authKey

        //https://apihub.kma.go.kr/api/typ01/url/kma_sfctm2.php?tm=202211300900&stn=108&help=0&authKey=oqq5zcJtQkSquc3CbWJEyA

        // API URL을 만듭니다.
        URL url = new URL("https://apihub.kma.go.kr/api/typ01/url/kma_sfctm2.php?tm=202211300900&stn=108&help=0&authKey=oqq5zcJtQkSquc3CbWJEyA");

        // url이 기존 DB에 저장된 게 있는지 확인
        Optional<Weather> weatherOptional = weatherRepository.findByUrl(info); //info가 있으면 weather Entity, 없으면 null 반환
        if (weatherOptional.isPresent()) {
            //저장된 게 있으면 있던 값들 Map 자료형으로 만들어서 보내줘야 함

            Weather weather = weatherOptional.get();
            Map<String, String> map = new HashMap<>();

            map.put("HM", weather.getHumidity());
            map.put("TA", weather.getTemperature());
            map.put("WS", weather.getWind_speed());
            map.put("RN", weather.getRainfall());

            System.out.println("값 DB에 존재함");

            return map;

        } else { //url이 처음이면 저장하고 이후 진행

            // HttpURLConnection 객체를 만들어 API를 호출합니다.
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // 요청 방식을 GET으로 설정합니다.
            con.setRequestMethod("GET");
            // 요청 헤더를 설정합니다. 여기서는 Content-Type을 application/json으로 설정합니다.
            con.setRequestProperty("Content-Type", "application/json");

            // 에러 메시지 확인용
            InputStream stream;
            if (con.getResponseCode() >= 200 && con.getResponseCode() < 300) {
                stream = con.getInputStream();
            } else {
                stream = con.getErrorStream();
            }

            // API의 응답을 읽기 위한 BufferedReader를 생성합니다.
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(stream));
            String inputLine;
            StringBuffer response = new StringBuffer();

            // 응답을 한 줄씩 읽어들이면서 StringBuffer에 추가합니다.
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            // BufferedReader를 닫습니다.
            in.close();

            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);

            //기상청 API로 받은 string 편집
            StringParser stringParser = new StringParser(prettyJson, weatherRepository, info);
            stringParser.stringParser();

            System.out.println("값 DB에 존재하지 않음");

            return stringParser.makeMap();
        }


    }

}

