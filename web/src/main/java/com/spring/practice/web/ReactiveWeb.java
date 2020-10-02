package com.spring.practice.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * Following along Josh Long his GOTO 2019 conference
 * Checking how reactive spring web works
 */
@SpringBootApplication
public class ReactiveWeb {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveWeb.class, args);
	}

}

@Component
@RequiredArgsConstructor
@Log4j2
class SampleDataInitializer {

	private final ReservationRepository reservationRepository;

	@EventListener(ApplicationReadyEvent.class)
	public void initialize(){
		var savedNames = Flux
				.just("person1", "person2", "person3", "person4")
				.map(name-> new Reservation(null, name))
				.flatMap(this.reservationRepository::save);

		reservationRepository
				.deleteAll()
				.thenMany(savedNames)
				.thenMany(this.reservationRepository.findAll())
				.subscribe(log::info);
	}
}
interface ReservationRepository extends ReactiveCrudRepository<Reservation, String> {}

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
class Reservation {
	@Id
	private String id;

	private String name;
}