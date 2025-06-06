package com.example.weatherstyling.repository;

import com.example.weatherstyling.model.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface WeatherRepository extends JpaRepository<Weather, Long> {




}
