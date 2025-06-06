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

    public String temperature; //기온
    public String sky; //하늘상태
    public String prep; //강수 유무
    public String st; //강수 확률

}
