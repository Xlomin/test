package com.example.test.models;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AverageParameters {

    private String avdTemp;
    private String avgWindSpeed;
    private String avgPressure;
    private String avgHumidity;
}
