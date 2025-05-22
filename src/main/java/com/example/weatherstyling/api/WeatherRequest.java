package com.example.weatherstyling.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class WeatherRequest {
    private String url_body = "https://apihub.kma.go.kr/api/typ01/url/fct_afs_dl.php?";
    private String url_main = "";

    private String gender ="";

    private String start_tm = "";
    private String end_tm = "";
    private String year = "0";
    private String month = "0";
    private String day = "0";
    private String start_hour = "06";
    private String end_hour = "18";
    private String minute = "0";

    private String placeNumber = "0";

    private String help = "help=0";

    private String authKey = "oqq5zcJtQkSquc3CbWJEyA";
}
