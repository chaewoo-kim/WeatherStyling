package com.example.weatherstyling.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "test")
@Getter @Setter
@NoArgsConstructor
public class Test {

    @Id
    private Integer id;


}