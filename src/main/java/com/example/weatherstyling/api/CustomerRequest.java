package com.example.weatherstyling.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class CustomerRequest {
    private String style; //casual, formal 둘로 구분
}
