package com.example.weatherstyling.api;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    String info; //기상청 API로부터 받은 정리되지 않은 기상 정보 문자열
    String column = "YYMMDDHHMI STN WD WS GST_WD GST_WS GST_TM PA PS PT PR TA TD HM PV RN RN_DAY RN_JUN RN_INT SD_HR3 SD_DAY SD_TOT WC WP WW CA_TOT CA_MID CH_MIN CT CT_TOP CT_MID CT_LOW VS SS SI ST_GD TS TE_005 TE_01 TE_02 TE_03 ST_SEA WH BF IR IX";
    String[] columns = column.split(" ");
    int columnLength = column.trim().split("\\s+").length; //열 종류의 수
    String numData = ""; //문자열로 저장된 숫자 데이터
    String [] numbers = new String[columnLength]; //숫자를 string으로 순서대로 저장
//    Integer [] numArr = new Integer[columnLength]; //string으로 되어있는 숫자를 Integer로 저장


    public StringParser(String info) {
        this.info = info;
    }


    //string 분할
    public void stringParser() {

        //YYMMDDHHMI부터 끝까지 string 분할
        //앞 의미 없는 부분 날림
        String target = "YYMMDDHHMI";
        int index = info.indexOf(target);
        String data = info.substring(index); //YYMMDDHHMI부터 끝까지 저장
        System.out.println(data);
        System.out.println(info);
        System.out.println(columnLength);

        //데이터 부분만 남기기
        Pattern pattern = Pattern.compile("\\d{12}");
        Matcher matcher = pattern.matcher(data);

        if (matcher.find()) {
            int startIndex = matcher.start();
            numData = data.substring(startIndex);
            numbers = numData.split("\\s+");
            System.out.println(Arrays.toString(numbers));
//            numbers[numbers.length-1] = numbers[numbers.length-1].replace("#", "");
            System.out.println(numbers.length);
        } else {
            System.out.println("12자리 숫자 찾을 수 없음");
        }

//        //필요한 열들 반복분에 쓰기 쉽도록 배열에 저장
//        for (int i = 0; i < numbers.length; i++) {
////            numArr[i] = Integer.parseInt(numbers[i]);
//        }
    }

    public Map<String, String> makeMap() {
        // Map 자료구조를 사용해 열 값에 맞는 값들을 넣을 것임
        // key : column, value : data
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < columnLength; i++) {
            map.put(columns[i], numbers[i]);
        }
        System.out.println("map : " + map);
        return map;
    }

    public JSONObject returnJsonObject(Map map) {
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
