package com.example.test.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "weather")
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String temp;
    private String windSpeed;
    private String pressure;
    private String humidity;
    private String conditionText;
    private String location;
    private Timestamp date;
}
