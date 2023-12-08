package com.example.test.Controller;

import com.example.test.entities.Weather;
import com.example.test.models.AverageParameters;
import com.example.test.repositories.WeatherRepository;
import com.example.test.service.Impl.WeatherServiceImpl;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
public class WeatherController {

    private final static Logger LOGGER = LoggerFactory.getLogger(Logger.class);

    @Autowired
    WeatherServiceImpl weatherService;

    @Autowired
    WeatherRepository weatherRepository;

    Weather weather;

    AverageParameters averageParameters;

    @GetMapping("/weather/actual")
    public ResponseEntity<?> getActualInfo(){
        LOGGER.info("Вызод get запроса для получения актуальной информации о погоде");

        weather = weatherRepository.findTopByOrderByIdDesc();

        LOGGER.info("Возращение актуальной информации о погоде");
        return weather != null
                ? new ResponseEntity<>(weather, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/weather")
    public ResponseEntity<?> getAvgInfo(@RequestParam("dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                                        @RequestParam("dateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo){

        LOGGER.info("Вызод get запроса для получения информации о погоде за указанный период");

        averageParameters = weatherService.getAvgParametersByDates(dateFrom, dateTo);

        LOGGER.info("Возращение информации о погоде за указанный период");
        return averageParameters != null
                ? new ResponseEntity<>(averageParameters, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
