package com.example.weatherstyling.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "shortWeather")
@Getter
@Setter
@NoArgsConstructor
public class ShortWeather {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(unique = true)
    public String url;

    public String temperature;
    public String st; //강수 확률
    public String sky; //하늘 상태
    public String prep; //강수 유무

}
