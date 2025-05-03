package com.example.weatherstyling.api;

import com.example.weatherstyling.model.Weather;
import jdk.jshell.spi.SPIResolutionException;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

// java.net.*과 java.io.* 패키지를 임포트합니다.
import java.net.*;
import java.io.*;

// JSONAPICall이라는 클래스를 생성합니다.
@Component
public class JSONAPICall {

    public String callAPI(String info) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();



        //url = url_body + tm + "&" + "stn=" + stn + "&" + help + "&" + "authKey=" + authKey

        //https://apihub.kma.go.kr/api/typ01/url/kma_sfctm2.php?tm=202211300900&stn=0&help=0&authKey=CkaIt4xtSU6GiLeMbalOLA

        // API URL을 만듭니다.
        URL url = new URL(info);
        // HttpURLConnection 객체를 만들어 API를 호출합니다.
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        // 요청 방식을 GET으로 설정합니다.
        con.setRequestMethod("GET");
        // 요청 헤더를 설정합니다. 여기서는 Content-Type을 application/json으로 설정합니다.
        con.setRequestProperty("Content-Type", "application/json");

        // API의 응답을 읽기 위한 BufferedReader를 생성합니다.
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        // 응답을 한 줄씩 읽어들이면서 StringBuffer에 추가합니다.
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        // BufferedReader를 닫습니다.
        in.close();

        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
        System.out.println(prettyJson);

        //기상청 API로 받은 string 편집
        StringParser stringParser = new StringParser(prettyJson);
        stringParser.stringParser();

        return prettyJson;
    }

}

