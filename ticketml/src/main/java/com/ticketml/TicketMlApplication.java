package com.ticketml;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class TicketMlApplication {

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Saigon"));
	}

	public static void main(String[] args) {
		SpringApplication.run(TicketMlApplication.class, args);
	}

}
