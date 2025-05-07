package com.example.weatherstyling.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class WeatherRequest {
    private String url_body = "https://apihub.kma.go.kr/api/typ01/url/kma_sfctm2.php?tm=";
    private String url_main = "";

    private String gender ="";

    private String tm = "";
    private String year = "0";
    private String month = "0";
    private String day = "0";
    private String hour = "0";
    private String minute = "0";

    private String placeNumber = "0";

    private String help = "help=0";

    private String authKey = "oqq5zcJtQkSquc3CbWJEyA";
}
