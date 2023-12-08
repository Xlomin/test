package com.example.test.repositories;

import com.example.test.entities.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface WeatherRepository extends JpaRepository<Weather, Long> {

    Weather findTopByOrderByIdDesc();

    List<Weather> findAllByDateBetween(Timestamp dateFrom, Timestamp dateTo);
    //findFirstByOrderByCreatedAtDesc();
}
