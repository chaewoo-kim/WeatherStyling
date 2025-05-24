package com.example.weatherstyling.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
@RequiredArgsConstructor
public class JSONAPILongCallTemperature {

    public void callAPI(String info) throws Exception{

        ObjectMapper objectMapper = new ObjectMapper();

        //얘는 URL 만들 때 발표 시각은 현재, 발표 시각은 원할 때로 해야 함
        //https://apihub.kma.go.kr/api/typ01/url/fct_afs_wl.php?tmfc1=2025052406&tmfc2=2025052418&tmef1=2025052806&tmef2=2025052818&disp=0&help=0&authKey=oqq5zcJtQkSquc3CbWJEyA
        // API URL을 만듭니다.
        URL url = new URL(info);
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
                new InputStreamReader(stream, "EUC-KR"));
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

    }

}

