package com.example.demo;

import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;

@SpringBootApplication
public class SpringReactiveProgrammingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringReactiveProgrammingApplication.class, args);
	}
	
	@Bean
	CommandLineRunner run(EmployeeRepository employeeRepository) {
		return args->{
			employeeRepository.deleteAll().subscribe(null, null,()->{
				Stream.of(Employee.builder().name("Abderrahim").salary(14000L).build(),
						Employee.builder().name("Farid").salary(24000L).build(),
						Employee.builder().name("chris").salary(21000L).build(),
						Employee.builder().name("Yves").salary(22000L).build())
				.forEach(em-> employeeRepository.save(em).subscribe(s-> System.out.println("s------------>"+s)));
			});
		};
	}

}
