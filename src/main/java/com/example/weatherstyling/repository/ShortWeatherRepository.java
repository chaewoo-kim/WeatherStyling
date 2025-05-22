package com.example.weatherstyling.repository;

import com.example.weatherstyling.model.ShortWeather;
import com.example.weatherstyling.model.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortWeatherRepository extends JpaRepository<ShortWeather, Long> {
    Optional<ShortWeather> findByUrl(String url);
}
