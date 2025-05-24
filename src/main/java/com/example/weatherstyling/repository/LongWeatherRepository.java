package com.example.weatherstyling.repository;

import com.example.weatherstyling.model.LongWeather;
import com.example.weatherstyling.model.ShortWeather;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LongWeatherRepository extends JpaRepository<LongWeather, Long> {

    Optional<LongWeather> findByUrl(String url);

}
