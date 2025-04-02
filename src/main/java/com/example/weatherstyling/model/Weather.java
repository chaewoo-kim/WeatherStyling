package com.example.weatherstyling.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "weather")
@Getter
@Setter
@NoArgsConstructor
public class Weather {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    public Date date;
    public String region;
    public Double temperature;
    public Double humidity;
    public Double wind_speed;
    public Double rainfall;
    public String weather_condition;
    public Integer cloud_amount;
}
