package com.example.demo.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.demo.handler.EmployeeHandler;

@Configuration
public class EmployeeRouter {

	@Bean
	RouterFunction<ServerResponse> employeeRoute(EmployeeHandler employeeHandler) {
		return RouterFunctions.route(RequestPredicates.GET("/reactive/employee/all"), employeeHandler::findAll)
				.andRoute(RequestPredicates.GET("/reactive/employee/{id}"), employeeHandler::findById)
				.andRoute(RequestPredicates.GET("/reactive/employee/{id}/events"), employeeHandler::findByIdEvents)
				.andRoute(RequestPredicates.POST("/reactive/employee"), employeeHandler::post)
				.andRoute(RequestPredicates.PUT("/reactive/employee/{id}"), employeeHandler::put)
				.andRoute(RequestPredicates.DELETE("/reactive/employee/{id}"), employeeHandler::delete);
	}

}
