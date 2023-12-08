package com.example.test.Controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import com.example.test.entities.Weather;
import com.example.test.models.AverageParameters;
import com.example.test.repositories.WeatherRepository;
import com.example.test.service.Impl.WeatherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class WeatherControllerTest {

    @Mock
    WeatherServiceImpl weatherService;

    @Mock
    WeatherRepository weatherRepository;

    @InjectMocks
    WeatherController weatherController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(weatherController).build();
    }

    @Test
    void getActualInfo() throws Exception {
        Weather weather = mock(Weather.class);
        when(weatherRepository.findTopByOrderByIdDesc()).thenReturn(weather);

        mockMvc.perform(get("/weather/actual")).andExpect(status().isOk());
        verify(weatherRepository, times(1)).findTopByOrderByIdDesc();
    }

    @Test
    void getAvgInfo() throws Exception {
        AverageParameters averageParameters = mock(AverageParameters.class);
        LocalDate dateFrom = mock(LocalDate.class);
        LocalDate dateTo = mock(LocalDate.class);
        when(weatherService.getAvgParametersByDates(dateFrom, dateTo)).thenReturn(averageParameters);

        mockMvc.perform(get("/weather")).andExpect(status().isOk());
        verify(weatherService, times(1)).getAvgParametersByDates(dateFrom, dateTo);
    }
}