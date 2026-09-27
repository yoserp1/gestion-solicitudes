package com.yoserp1.prueba.solicitudes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SolicitudesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SolicitudesApplication.class, args);
	}

}
