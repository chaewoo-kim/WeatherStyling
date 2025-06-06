package com.example.weatherstyling.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "longWeather")
@Getter
@Setter
@NoArgsConstructor
public class LongWeather {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(unique = true)
    public String url;

    public String temperature;
    public String sky; //하늘상태
    public String prep; //강수 유무
    public String st; //강수 확률
}
