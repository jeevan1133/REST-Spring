package payroll;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
class LoadDatabase {

	@Bean
	CommandLineRunner initDatabase(EmployeeRepository repository, OrderRepository orderRepository) {
		log.info("Commandline runner load database called");
		return args -> {
			log.info("Preloading " + repository.save(new Employee("Bilbo Baggins", "burglar")));
			log.info("Preloading " + repository.save(new Employee("Frodo Baggins", "thief")));
			log.info("Preloading " + orderRepository.save(new Order("MacBook Pro", Status.COMPLETED)));
			log.info("Preloading " + orderRepository.save(new Order("iPhone", Status.IN_PROGRESS)));
		};
	}
}