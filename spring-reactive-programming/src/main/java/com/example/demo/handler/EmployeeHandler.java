package com.example.demo.handler;

import java.time.Duration;
import java.util.Date;
import java.util.stream.Stream;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.demo.model.Employee;
import com.example.demo.model.EmployeeEvent;
import com.example.demo.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Component
@RequiredArgsConstructor
public class EmployeeHandler {

	private final EmployeeRepository employeeRepository;

	public Mono<ServerResponse> findAll(ServerRequest serverRequest) {
		return ServerResponse.ok().body(employeeRepository.findAll(), Employee.class);
	}

	public Mono<ServerResponse> findById(ServerRequest serverRequest) {
		return ServerResponse.ok().body(employeeRepository.findById(serverRequest.pathVariable("id")), Employee.class)
				.switchIfEmpty(ServerResponse.notFound().build());
	}

	public Mono<ServerResponse> findByIdEvents(ServerRequest serverRequest) {
		return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM)
				.body(employeeRepository.findById(serverRequest.pathVariable("id")).flatMapMany(empl -> {
					Flux<Long> iterationFlux = Flux.interval(Duration.ofSeconds(2L));
					Flux<EmployeeEvent> employeeEventFlux = Flux.fromStream(
							Stream.generate(() -> EmployeeEvent.builder().employee(empl).date(new Date()).build()));

					return Flux.zip(iterationFlux, employeeEventFlux).map(Tuple2::getT2);
				}), EmployeeEvent.class).switchIfEmpty(ServerResponse.notFound().build());
	}

	public Mono<ServerResponse> post(ServerRequest serverRequest) {
		Mono<Employee> employee = serverRequest.bodyToMono(Employee.class);

		return ServerResponse.ok()
				.body(BodyInserters.fromPublisher(
						employee.map(em -> Employee.builder().name(em.getName()).salary(em.getSalary()).build())
								.flatMap(employeeRepository::save),
						Employee.class));
	}

	public Mono<ServerResponse> put(ServerRequest serverRequest) {
		Mono<Employee> employee = serverRequest.bodyToMono(Employee.class);
		Mono<Employee> employeeDb = employeeRepository.findById(serverRequest.pathVariable("id"));

		return employeeDb.flatMap(old -> ServerResponse.ok()
				.body(BodyInserters.fromPublisher(employee
						.map(em -> Employee.builder().id(old.getId()).name(em.getName()).salary(em.getSalary()).build())
						.flatMap(employeeRepository::save), Employee.class)));
	}

	public Mono<ServerResponse> delete(ServerRequest serverRequest) {
		Mono<Employee> employeeDb = employeeRepository.findById(serverRequest.pathVariable("id"));

		return employeeDb.flatMap(em -> ServerResponse.noContent().build(employeeRepository.delete(em)))
				.switchIfEmpty(ServerResponse.notFound().build());
	}
}
