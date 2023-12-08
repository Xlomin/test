package com.example.test.service;

import com.example.test.models.AverageParameters;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;

public interface WeatherService {

    void getData() throws IOException, InterruptedException;

    void setData(HttpResponse<String> response);

    String getValueByParameter(String info, String paramName);

    AverageParameters getAvgParametersByDates(LocalDate dateFrom, LocalDate dateTo);
}
