package com.example.weatherstyle.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Component;

@Component
public class WeatherApiClient {
    private final String BASE_API_URL = "https://apihub.kma.go.kr/api/typ01/url/kma_sfctm2.php";
    private final String AUTH_KEY = "2fPJEXvxS5ezyRF78VuXMA";

    public String fetchWeatherData(String tm, String stn) {
        String apiUrl = String.format("%s?tm=%s&stn=%s&disp=0&help=1&authKey=%s",
                BASE_API_URL, tm, stn, AUTH_KEY);

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString(); // API 응답을 반환
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
