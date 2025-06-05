package com.example.weatherstyling.api;

import com.example.weatherstyling.model.LongWeather;
import com.example.weatherstyling.model.ShortWeather;
import com.example.weatherstyling.model.Weather;
import com.example.weatherstyling.repository.LongWeatherRepository;
import com.example.weatherstyling.repository.ShortWeatherRepository;
import com.example.weatherstyling.repository.WeatherRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@RequiredArgsConstructor
public class StringParser {

    //해당 클래스 사용 방법
    //1. 생성자로 info 전달
    //1.1 info : 기상청 API로 받은 문자열
    //2. stringParser 호출 -> 문자열 잘라서 저장
    //3. makeMap 호출 ->> Map 객체 생성 및 JSON에 형식에 맞게 저장 및 출력
    //끝

    //동작 알고리즘
    //info로 string 형태의 날씨 정보를 받음
    //열의 개수를 파악해 하나의 변수에 넣어야 함 -> 데이터를 자를 기준으로 사용하기 위해
    //열 : YYMMDDHHMI STN WD WS GST GST GST PA PS PT PR TA TD HM PV RN RN RN RN SD SD SD WC WP WW CA CA CH CT CT CT CT VS SS SI ST TS TE TE TE TE ST WH BF IR IX
    //뽑아낼 데이터 : 풍속(WS), 기온(TA), 상대습도(HM), 강수량(RN), 국내식 일기코드(WW), 지면상태 코드(ST_GD)
    //1. string 분할
    //2. 분할한 데이터로 필요한 데이터 추출
    //3. json 객체로 구성 후 return

    //JSONAPICall에서 사용하는 변수들
    String info; //기상청 API로부터 받은 정리되지 않은 기상 정보 문자열
    String tempInfo; //기상청 중기 기온 예특보 API로부터 받은 값
    String url;
    String column;
    String temperature;

    String[] columns;
    int columnLength; //열 종류의 수
    String numData = ""; //문자열로 저장된 숫자 데이터
    String [] numbers = new String[columnLength]; //숫자를 string으로 순서대로 저장

    private final WeatherRepository weatherRepository;
    private final ShortWeatherRepository shortWeatherRepository;
    private final LongWeatherRepository longWeatherRepository;

    public StringParser(String info, WeatherRepository weatherRepository, String url) {
        this.info = info;
        this.weatherRepository = weatherRepository;
        this.shortWeatherRepository = null;
        this.longWeatherRepository = null;
        this.url = url;
        this.column = "YYMMDDHHMI STN WD WS GST_WD GST_WS GST_TM PA PS PT PR TA TD HM PV RN RN_DAY RN_JUN RN_INT SD_HR3 SD_DAY SD_TOT WC WP WW CA_TOT CA_MID CH_MIN CT CT_TOP CT_MID CT_LOW VS SS SI ST_GD TS TE_005 TE_01 TE_02 TE_03 ST_SEA WH BF IR IX";
        columns = column.split(" ");
        columnLength = column.trim().split("\\s+").length;
    }

    public StringParser(String info, ShortWeatherRepository shortWeatherRepository, String url, WeatherRepository weatherRepository) {
        this.info = info;
        this.weatherRepository = weatherRepository;
        this.shortWeatherRepository = shortWeatherRepository;
        this.longWeatherRepository = null;
        this.url = url;
        this.column = "REG_ID TM_FC TM_EF MOD NE STN C MAN_ID MAN_FC W1 T W2 TA ST SKY PREP WF";
        columns = column.split(" ");
        columnLength = column.trim().split("\\s+").length;
    }

    public StringParser(String info, LongWeatherRepository longWeatherRepository, String url, String tempInfo, WeatherRepository weatherRepository) {
        this.info = info;
        this.tempInfo = tempInfo;
        this.weatherRepository = weatherRepository;
        this.longWeatherRepository = longWeatherRepository;
        this.shortWeatherRepository = null;
        this.url = url;
        this.column = "REG_ID TM_FC TM_EF MOD STN C SKY PRE CONF WF RN_ST";
        columns = column.split(" ");
        columnLength = column.trim().split("\\s+").length;
    }



    //string 분할
    public void stringParser() {

        if (shortWeatherRepository != null) {
            String target = "WF";
            int index = info.indexOf(target);
            String data = info.substring(index+1);
            System.out.println("data : "+data);

            String result = data.replaceAll("\\s+", " "); //많은 공백을 하나의 공백으로 변경

            for (int i = 0; i < columnLength; i++) {
                numbers = result.split("\\s+");
            }
        } else if (longWeatherRepository != null) {
            String target = "RN_ST";

            System.out.println("info: " + info);
            String data = info.replaceAll("[\\\\]", " ")
                    .replaceAll("\"", " ")
                    .replaceAll("RN_ST", "RN_ST ")
                    .replaceAll("\\s+", " ");

            String tempData = tempInfo.replaceAll("\\s+", " ");

            int index = data.indexOf(target);

            System.out.println("data: " + data);
            System.out.println("index: " + index);
            String output = data.substring(index+6);
            System.out.println("output : " + output);

            String [] parts = output.split("\\s+");
            String [] tempParts = tempData.split("\\s+");
            String rn_st = parts[10];
            temperature = String.valueOf((Integer.parseInt(tempParts[18]) + Integer.parseInt(tempParts[19])) / 2);
            parts[10] = rn_st.substring(0,2);

            for (int i = 0; i < columnLength; i++) {
                numbers = parts;
            }
        }
    }


    public Map<String, String> makeMap() {
        // Map 자료구조를 사용해 열 값에 맞는 값들을 넣을 것임
        // key : column, value : data
        Map<String, String> map = new HashMap<>();
        System.out.println("columnLength: " + columnLength);
        try {for (int i = 0; i < columnLength; i++) {
            map.put(columns[i], numbers[i]);
        }} catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("map : " + map);

        if (shortWeatherRepository != null) {
            ShortWeather shortWeather = new ShortWeather();
            shortWeather.setUrl(url);
            shortWeather.setTemperature(map.get("TA"));
            shortWeather.setSt(map.get("ST"));
            shortWeather.setSky(map.get("SKY"));
            shortWeather.setPrep(map.get("PREP"));

            shortWeatherRepository.save(shortWeather);

            Weather weather = weatherRepository.findById(1L).get();
            weather.setTemperature(shortWeather.getTemperature());
            weather.setSky(shortWeather.getSky());
            weather.setSt(shortWeather.getSt());
            weather.setPrep(shortWeather.getPrep());
            weatherRepository.save(weather);

            return map;
        } else if (longWeatherRepository != null) {
            LongWeather longWeather = new LongWeather();
            longWeather.setUrl(url);
            longWeather.setPrep(map.get("PRE"));
            longWeather.setSky(map.get("SKY"));
            longWeather.setSt(map.get("RN_ST"));
            longWeather.setTemperature(temperature);

            longWeatherRepository.save(longWeather);

            //map 자료형 다시 만들어서 정확한 이름으로 보내야함
            Map<String, String> newMap = new HashMap<>();
            newMap.put("TA", temperature);
            newMap.put("ST", map.get("RN_ST"));
            newMap.put("SKY", map.get("SKY"));
            newMap.put("PREP", map.get("PRE"));

            Weather weather = weatherRepository.findById(1L).get();
            weather.setTemperature(longWeather.getTemperature());
            weather.setSky(longWeather.getSky());
            weather.setSt(longWeather.getSt());
            weather.setPrep(longWeather.getPrep());
            weatherRepository.save(weather);

            return newMap;
        }

        return null;
    }

    public JSONObject returnJsonObject(Map map) {
        //지금은 안 쓰고 있는 함수

        //원하는 데이터 json 객체에 저장
        //뽑아낼 데이터 : 풍속(WS), 기온(TA), 상대습도(HM), 강수확률(강수확률은 API 새로 파야함)
        JSONObject json = new JSONObject();
        json.put("windSpeed", map.get("WS"));
        json.put("temperature", map.get("TA"));
        json.put("humidity", map.get("HM"));
        System.out.println("json : " + json);
        return json;
    }


}
