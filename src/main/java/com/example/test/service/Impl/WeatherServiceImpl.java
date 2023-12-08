package com.example.test.service.Impl;

import com.example.test.entities.Weather;
import com.example.test.models.AverageParameters;
import com.example.test.repositories.WeatherRepository;
import com.example.test.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.sql.Timestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WeatherServiceImpl implements WeatherService {

    private final static Logger LOGGER = LoggerFactory.getLogger(Logger.class);

    @Autowired
    private WeatherRepository weatherRepository;

    String pattern = "yyyy-MM-dd HH:mm";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    @Scheduled(fixedDelay = 15, timeUnit = TimeUnit.MINUTES)
    @Override
    public void getData() throws IOException, InterruptedException {

        LOGGER.info("Получение данных от Weather API с переодичностью 15мин");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://weatherapi-com.p.rapidapi.com/current.json?q=Minsk"))
                .header("X-RapidAPI-Key", "50a9572bebmsh0b1b0b5c236de1bp1710c7jsn1fef1cc8255a")
                .header("X-RapidAPI-Host", "weatherapi-com.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());


        setData(response);
    }

    public void setData(HttpResponse<String> response){
        Weather weather = new Weather();

        weather.setTemp(getValueByParameter(response.body(), "temp_c"));
        weather.setWindSpeed(getValueByParameter(response.body(), "wind_kph"));
        weather.setPressure(getValueByParameter(response.body(), "pressure_mb"));
        weather.setHumidity(getValueByParameter(response.body(), "humidity"));
        weather.setConditionText(getValueByParameter(response.body(), "text").replaceAll("^.|.$", ""));
        weather.setLocation(getValueByParameter(response.body(), "name").replaceAll("^.|.$", ""));

        LocalDateTime localDateTime = LocalDateTime.from(formatter
                .parse(getValueByParameter(response.body(), "last_updated")
                        .replaceAll("^.|.$", "")));

        weather.setDate(Timestamp.valueOf(localDateTime));

        LOGGER.info("Сохранение полученных данных в БД");

        weatherRepository.save(weather);
    }

    @Override
    public String getValueByParameter(String info, String paramName) {
        LOGGER.debug("Поиск необходимых данных в полученной строке от Weather API с помощью регулярного выражения");

        Pattern p = Pattern.compile(paramName + ".:([^,]*)(,|$)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(info);

        if (!m.find()) return null;

        try {
            return URLDecoder.decode(m.group(1), "UTF-8");
        } catch (UnsupportedEncodingException unsencex) {
            throw new RuntimeException(unsencex);
        }
    }

    @Override
    public AverageParameters getAvgParametersByDates(LocalDate dateFrom, LocalDate dateTo){

        float avgTemp = 0;
        float avgWindSpeed = 0;
        float avgPressure = 0;
        float avgHumidity = 0;

        LOGGER.debug("Поиск экземпляров класса Weather, которые подходят по установленным временных рамок");

        List<Weather> weatherList = weatherRepository.findAllByDateBetween(
                Timestamp.valueOf(dateFrom.atTime(LocalTime.MIN)),
                Timestamp.valueOf(dateTo.atTime(LocalTime.MAX)));

        for (Weather weather : weatherList) {
            avgTemp += Float.parseFloat(weather.getTemp());
            avgWindSpeed += Float.parseFloat(weather.getWindSpeed());
            avgPressure += Float.parseFloat(weather.getPressure());
            avgHumidity += Float.parseFloat(weather.getHumidity());
        }

        avgTemp = avgTemp/weatherList.size();
        avgWindSpeed = avgWindSpeed/weatherList.size();
        avgPressure = avgPressure/weatherList.size();
        avgHumidity = avgHumidity/weatherList.size();


        LOGGER.info("Возращение среднесуточных показателей");

        return new AverageParameters(
                String.valueOf(avgTemp),
                String.valueOf(avgWindSpeed),
                String.valueOf(avgPressure),
                String.valueOf(avgHumidity));
    }
}
