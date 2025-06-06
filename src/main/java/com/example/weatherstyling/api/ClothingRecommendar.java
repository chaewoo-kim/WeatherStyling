package com.example.weatherstyling.api;

import com.example.weatherstyling.model.Weather;
import com.example.weatherstyling.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.server.endpoint.interceptor.DelegatingSmartEndpointInterceptor;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.Classifier;
import weka.core.*;
import weka.classifiers.Classifier;

import java.time.LocalDate;
import java.util.*;


public class ClothingRecommendar {

    private final WeatherRepository weatherRepository;

    private String style = "";
    private String gender = "";

    LocalDate today = LocalDate.now();
    int month = today.getMonthValue();
    String season = "";

    String path = "/images";

    Weather weather = new Weather();
    ArrayList<Attribute> attributes = new ArrayList<>();

    Map<String, String> returnMap = new HashMap<>();

    public ClothingRecommendar(WeatherRepository weatherRepository, String style, String gender) {

        this.weatherRepository = weatherRepository;
        this.style = style;
        this.gender = gender;

        if (month <= 2 || month == 12) {
            this.season = "겨울";
        } else if (month >= 3 && month <= 5) {
            this.season = "봄";
        } else if (month >= 6 && month <= 8) {
            this.season = "여름";
        } else {
            this.season = "가을";
        }


        System.out.println("path: "+path);
        //path 설정 잘 하고 학습 데이터 바꿔야함
        //chatgpt한테 아예 내 폴더 구조에 있는 데이터셋을 알려주고 그걸 조합하라고 하는게 나을듯?
        //한 60장 정도로 중복 있이 해서 돌리는 것도 나쁘지 않을듯?

        Optional<Weather> weatherOptional = weatherRepository.findById(1L);
        this.weather = weatherOptional.get();

        attributes.add(new Attribute("temperature"));
        attributes.add(new Attribute("rainProbability"));
        attributes.add(new Attribute("sky", List.of("맑음", "구름많음", "흐림")));
        attributes.add(new Attribute("precipitation", List.of("0", "1", "2", "3", "4")));
        attributes.add(new Attribute("style", List.of("casual", "minimal", "formal")));
        attributes.add(new Attribute("season", List.of("봄", "여름", "가을", "겨울")));
        attributes.add(new Attribute("gender", List.of("남", "여")));
    }

    public void getTopRecommend() throws Exception {

        attributes.add(new Attribute("top", List.of("화이트셔츠", "연그린셔츠","그레이니트","베이지블라우스","라이트블루블라우스","네이비셔츠","라이트옐로우셔츠","다크그레이니트","핑크블라우스","화이트탑","블루반팔티","네이비탱크탑","네이비폴로셔츠","화이트민소매","블랙탱크탑","그레이셔츠","레드반팔티","라이트블루탑","블랙폴로셔츠","옐로우민소매","화이트탱크탑","카키셔츠","그린블라우스","그레이셔츠2","크림블라우스","카멜탑","핑크니트","화이트니트","아이보리니트","라이트그레이니트","옐로우티셔츠","오렌지블라우스","레몬셔츠","베이비블루탑","코랄니트","민트블라우스","그레이탱크탑","라이트핑크셔츠","올리브니트","머스타드탑","민트셔츠","화이트폴로","블루니트","레드탱크탑","베이지폴로","라이트그린셔츠","블랙니트","브라운탱크탑","핑크셔츠","라이트블루셔츠","레드셔츠","그레이셔츠3","블루셔츠","라임셔츠"))); // class attribute

        // 데이터셋 생성
        Instances top_data = new Instances("ClothingData", attributes, 0);
        top_data.setClassIndex(top_data.numAttributes() - 1); // 마지막 속성이 예측 대상(top)

        // 인스턴스 생성
        DenseInstance top_instance = new DenseInstance(top_data.numAttributes());
        top_instance.setValue(attributes.get(0), Integer.parseInt(weather.getTemperature())); // temperature
        top_instance.setValue(attributes.get(1), Integer.parseInt(weather.getSt())); // rainProbability

        if (weather.getSky().equals("DB01")) {
            top_instance.setValue(attributes.get(2), "맑음");
        } else if (weather.getSky().equals("DB02")) {
            top_instance.setValue(attributes.get(2), "구름조금");
        } else if (weather.getSky().equals("DB03")) {
            top_instance.setValue(attributes.get(2), "구름많음");
        } else {
            top_instance.setValue(attributes.get(2), "흐림");
        }

        top_instance.setValue(attributes.get(3), weather.getPrep());

        switch (style) {
            case "casual":
                System.out.println(style);
                top_instance.setValue(attributes.get(4), "casual");
                break;
            case "formal":
                top_instance.setValue(attributes.get(4), "formal");
                break;
            case "minimal":
                top_instance.setValue(attributes.get(4), "minimal");
                break;
        }

        top_instance.setValue(attributes.get(5), season);

        if (gender.equals("남성")) {
            top_instance.setValue(attributes.get(6), "남");
        } else {
            top_instance.setValue(attributes.get(6), "여");
        }

        // 마지막(top)은 예측 대상이므로 값 지정 안 함

        top_data.add(top_instance);

        // 모델 로드 및 예측
        Classifier model = (Classifier) weka.core.SerializationHelper.read("/Users/gimchaeu/Desktop/학교 자료/2025-1 과제/산학캡스톤프로젝트/WeatherStyling/WeatherStyling/src/main/java/com/example/weatherstyling/models/top.model");
        double predictionIndex = model.classifyInstance(top_data.instance(0));
        String predictedTop = top_data.classAttribute().value((int) predictionIndex);

        System.out.println("추천 상의: " + predictedTop);

        String top_path = path + "/top/" + predictedTop + ".jpeg";
        returnMap.put("top", top_path);

        attributes.removeIf(attr -> attr.name().equals("top"));

    }

    public void getBottomRecommend() throws Exception {

        attributes.add(new Attribute("bottom", List.of("연청바지","슬랙스","블랙진","화이트슬랙스","베이지팬츠","블랙스커트","데님스커트","그레이와이드팬츠","라이트슬랙스","네이비슬랙스","아이보리스커트","코튼팬츠","브라운팬츠","화이트숏츠","연베이지팬츠","블랙숏츠","크림진","차콜슬랙스","플레어스커트","카고팬츠","네이비스커트","그린슬랙스","스트레이트진","슬림핏슬랙스","연보라스커트","그레이조거팬츠","버건디팬츠","라이트그린스커트","인디고진","화이트팬츠","민트조거팬츠","베이지와이드팬츠","크롭팬츠","스카이블루팬츠","딥블루진","연노랑스커트","카멜슬랙스","스웨트팬츠","네이비팬츠","레드플레어스커트","오트밀팬츠","코튼쇼츠",""))); // class attribute

        // 데이터셋 생성
        Instances bottom_data = new Instances("ClothingData", attributes, 0);
        bottom_data.setClassIndex(bottom_data.numAttributes() - 1); // 마지막 속성이 예측 대상(top)

        // 인스턴스 생성
        DenseInstance bottom_instance = new DenseInstance(bottom_data.numAttributes());
        bottom_instance.setValue(attributes.get(0), Integer.parseInt(weather.getTemperature())); // temperature
        bottom_instance.setValue(attributes.get(1), Integer.parseInt(weather.getSt())); // rainProbability

        if (weather.getSky().equals("DB01")) {
            bottom_instance.setValue(attributes.get(2), "맑음");
        } else if (weather.getSky().equals("DB02")) {
            bottom_instance.setValue(attributes.get(2), "구름조금");
        } else if (weather.getSky().equals("DB03")) {
            bottom_instance.setValue(attributes.get(2), "구름많음");
        } else {
            bottom_instance.setValue(attributes.get(2), "흐림");
        }

        bottom_instance.setValue(attributes.get(3), weather.getPrep());

        switch (style) {
            case "casual":
                bottom_instance.setValue(attributes.get(4), "casual");
                break;
            case "formal":
                bottom_instance.setValue(attributes.get(4), "formal");
                break;
            case "minimal":
                bottom_instance.setValue(attributes.get(4), "minimal");
                break;
        }

        bottom_instance.setValue(attributes.get(5), season);

        if (gender.equals("남성")) {
            bottom_instance.setValue(attributes.get(6), "남");
        } else {
            bottom_instance.setValue(attributes.get(6), "여");
        }

        // 마지막(bottom)은 예측 대상이므로 값 지정 안 함

        bottom_data.add(bottom_instance);

        // 모델 로드 및 예측
        Classifier bottom_model = (Classifier) weka.core.SerializationHelper.read("/Users/gimchaeu/Desktop/학교 자료/2025-1 과제/산학캡스톤프로젝트/WeatherStyling/WeatherStyling/src/main/java/com/example/weatherstyling/models/bottom.model");
        double bottom_predictionIndex = bottom_model.classifyInstance(bottom_data.instance(0));
        String predictedBot = bottom_data.classAttribute().value((int) bottom_predictionIndex);

        System.out.println("추천 하의: " + predictedBot);

        String bot_path = path + "/bottom/" + predictedBot + ".jpeg";
        returnMap.put("bottom", bot_path);

        attributes.removeIf(attr -> attr.name().equals("bottom"));

    }

    public void getOuterRecommend() throws Exception {

        attributes.add(new Attribute("outer", List.of("트렌치코트","데님자켓","가디건","야상자켓","바람막이","블레이저","코튼자켓","레더자켓","니트가디건","롱코트","숏패딩","롱패딩","무스탕","더플코트","울코트","체크자켓","싱글코트","경량패딩","후드집업",""))); // class attribute

        // 데이터셋 생성
        Instances outer_data = new Instances("ClothingData", attributes, 0);
        outer_data.setClassIndex(outer_data.numAttributes() - 1); // 마지막 속성이 예측 대상(top)

        // 인스턴스 생성
        DenseInstance outer_instance = new DenseInstance(outer_data.numAttributes());
        outer_instance.setValue(attributes.get(0), Integer.parseInt(weather.getTemperature())); // temperature
        outer_instance.setValue(attributes.get(1), Integer.parseInt(weather.getSt())); // rainProbability

        if (weather.getSky().equals("DB01")) {
            outer_instance.setValue(attributes.get(2), "맑음");
        } else if (weather.getSky().equals("DB02")) {
            outer_instance.setValue(attributes.get(2), "구름조금");
        } else if (weather.getSky().equals("DB03")) {
            outer_instance.setValue(attributes.get(2), "구름많음");
        } else {
            outer_instance.setValue(attributes.get(2), "흐림");
        }

        outer_instance.setValue(attributes.get(3), weather.getPrep());

        switch (style) {
            case "casual":
                outer_instance.setValue(attributes.get(4), "casual");
                break;
            case "formal":
                outer_instance.setValue(attributes.get(4), "formal");
                break;
            case "minimal":
                outer_instance.setValue(attributes.get(4), "minimal");
                break;
        }

        outer_instance.setValue(attributes.get(5), season);

        if (gender.equals("남성")) {
            outer_instance.setValue(attributes.get(6), "남");
        } else {
            outer_instance.setValue(attributes.get(6), "여");
        }

        // 마지막(outer)은 예측 대상이므로 값 지정 안 함

        outer_data.add(outer_instance);

        // 모델 로드 및 예측
        Classifier outer_model = (Classifier) weka.core.SerializationHelper.read("/Users/gimchaeu/Desktop/학교 자료/2025-1 과제/산학캡스톤프로젝트/WeatherStyling/WeatherStyling/src/main/java/com/example/weatherstyling/models/outer.model");
        double outer_predictionIndex = outer_model.classifyInstance(outer_data.instance(0));
        String predictedOuter = outer_data.classAttribute().value((int) outer_predictionIndex);

        System.out.println("추천 외투: " + predictedOuter);

        String outer_path = path + "/outer/" + predictedOuter + ".jpeg";
        returnMap.put("jacket", outer_path);
        System.out.println(outer_path);

        attributes.removeIf(attr -> attr.name().equals("outer"));

    }

    public void getShoesRecommend() throws Exception {

        attributes.add(new Attribute("shoes", List.of("운동화","로퍼","샌들","구두","워커","부츠","슬립온"))); // class attribute

        // 데이터셋 생성
        Instances shoes_data = new Instances("ClothingData", attributes, 0);
        shoes_data.setClassIndex(shoes_data.numAttributes() - 1); // 마지막 속성이 예측 대상(top)

        // 인스턴스 생성
        DenseInstance shoes_instance = new DenseInstance(shoes_data.numAttributes());
        shoes_instance.setValue(attributes.get(0), Integer.parseInt(weather.getTemperature())); // temperature
        shoes_instance.setValue(attributes.get(1), Integer.parseInt(weather.getSt())); // rainProbability

        if (weather.getSky().equals("DB01")) {
            shoes_instance.setValue(attributes.get(2), "맑음");
        } else if (weather.getSky().equals("DB02")) {
            shoes_instance.setValue(attributes.get(2), "구름조금");
        } else if (weather.getSky().equals("DB03")) {
            shoes_instance.setValue(attributes.get(2), "구름많음");
        } else {
            shoes_instance.setValue(attributes.get(2), "흐림");
        }

        shoes_instance.setValue(attributes.get(3), weather.getPrep());

        switch (style) {
            case "casual":
                shoes_instance.setValue(attributes.get(4), "casual");
                break;
            case "formal":
                shoes_instance.setValue(attributes.get(4), "formal");
                break;
            case "minimal":
                shoes_instance.setValue(attributes.get(4), "minimal");
                break;
        }

        shoes_instance.setValue(attributes.get(5), season);

        if (gender.equals("남성")) {
            shoes_instance.setValue(attributes.get(6), "남");
        } else {
            shoes_instance.setValue(attributes.get(6), "여");
        }

        // 마지막(outer)은 예측 대상이므로 값 지정 안 함

        shoes_data.add(shoes_instance);

        // 모델 로드 및 예측
        Classifier shoes_model = (Classifier) weka.core.SerializationHelper.read("/Users/gimchaeu/Desktop/학교 자료/2025-1 과제/산학캡스톤프로젝트/WeatherStyling/WeatherStyling/src/main/java/com/example/weatherstyling/models/shoes.model");
        double shoes_predictionIndex = shoes_model.classifyInstance(shoes_data.instance(0));
        String predictedShoes= shoes_data.classAttribute().value((int) shoes_predictionIndex);

        System.out.println("추천 신발: " + predictedShoes);

        String shoes_path = path + "/shoes/" + predictedShoes + ".jpeg";
        returnMap.put("shoes", shoes_path);
        System.out.println(shoes_path);

        attributes.removeIf(attr -> attr.name().equals("shoes"));

    }

    public void getAccessoryRecommend() throws Exception {

        attributes.add(new Attribute("accessory", List.of("없음","시계","목도리","선글라스","모자","장갑","에코백","벨트","헤어밴드"))); // class attribute

        // 데이터셋 생성
        Instances accessory_data = new Instances("ClothingData", attributes, 0);
        accessory_data.setClassIndex(accessory_data.numAttributes() - 1); // 마지막 속성이 예측 대상(top)

        // 인스턴스 생성
        DenseInstance accessory_instance = new DenseInstance(accessory_data.numAttributes());
        accessory_instance.setValue(attributes.get(0), Integer.parseInt(weather.getTemperature())); // temperature
        accessory_instance.setValue(attributes.get(1), Integer.parseInt(weather.getSt())); // rainProbability

        if (weather.getSky().equals("DB01")) {
            accessory_instance.setValue(attributes.get(2), "맑음");
        } else if (weather.getSky().equals("DB02")) {
            accessory_instance.setValue(attributes.get(2), "구름조금");
        } else if (weather.getSky().equals("DB03")) {
            accessory_instance.setValue(attributes.get(2), "구름많음");
        } else {
            accessory_instance.setValue(attributes.get(2), "흐림");
        }

        accessory_instance.setValue(attributes.get(3), weather.getPrep());

        switch (style) {
            case "casual":
                accessory_instance.setValue(attributes.get(4), "casual");
                break;
            case "formal":
                accessory_instance.setValue(attributes.get(4), "formal");
                break;
            case "minimal":
                accessory_instance.setValue(attributes.get(4), "minimal");
                break;
        }

        accessory_instance.setValue(attributes.get(5), season);

        if (gender.equals("남성")) {
            accessory_instance.setValue(attributes.get(6), "남");
        } else {
            accessory_instance.setValue(attributes.get(6), "여");
        }

        // 마지막(outer)은 예측 대상이므로 값 지정 안 함

        accessory_data.add(accessory_instance);

        // 모델 로드 및 예측
        Classifier accessory_model = (Classifier) weka.core.SerializationHelper.read("/Users/gimchaeu/Desktop/학교 자료/2025-1 과제/산학캡스톤프로젝트/WeatherStyling/WeatherStyling/src/main/java/com/example/weatherstyling/models/bottom.model");
        double accessory_predictionIndex = accessory_model.classifyInstance(accessory_data.instance(0));
        String predictedAccessory = accessory_data.classAttribute().value((int) accessory_predictionIndex);

        System.out.println("추천 악세서리: " + predictedAccessory);

        String accessory_path = path + "/accessory/" + predictedAccessory + ".jpeg";
        returnMap.put("accessory", accessory_path);

        attributes.removeIf(attr -> attr.name().equals("accessory"));

    }

    public Map<String, String> returnMap() {
        return returnMap;
    }
}
