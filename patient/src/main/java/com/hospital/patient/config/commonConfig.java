package com.hospital.patient.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
public class commonConfig {

    @Bean
    public ModelMapper modelMapper() {
        // Make model mapper correctly map string to locale date
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(String.class, LocalDate.class)
                .setConverter(context ->
                        LocalDate.parse(context.getSource()));
        return modelMapper;
    }
}
