package com.example.weatherstyle.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather")
@Getter
@Setter
public class WeatherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double temperature;   // 온도
    private double windSpeed;     // 풍속
    private double humidity;      // 습도
    private double precipitation; // 강수량

    @Column(name = "recorded_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime recordedAt;
}
