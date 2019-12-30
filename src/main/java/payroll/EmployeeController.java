package payroll;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
class EmployeeController {

	@Autowired
	private final EmployeeRepository repository;
	
	@Autowired
	private final EmployeeModelAssembler assembler;

	EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {
		log.info("Employee Controller called with repository" + repository);
		this.repository = repository;
		this.assembler = assembler;
	}

	@GetMapping("/employees")
	CollectionModel<EntityModel<Employee>> all() {
		/*List<EntityModel<Employee>> employees = repository.findAll()
												.stream()
												.map(employee -> new EntityModel<>(employee,
															 linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
														     linkTo(methodOn(EmployeeController.class).all()).withRel("employees")))
												.collect(Collectors.toList());
		 */
		log.info("Getting employees from the database");
		List<EntityModel<Employee>> employees = repository.findAll()
				.stream()
				//.map(employee -> getEmployeeLinks(employee, employee.getId()))
				.map(assembler::toModel)
				.collect(Collectors.toList());
		log.info("have list of employees");
		return new CollectionModel<>(employees, 
				linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
	}

	@PostMapping("/employees")
	ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee) throws URISyntaxException {

		EntityModel<Employee> resource = assembler.toModel(repository.save(newEmployee));
		Link link = linkTo(EmployeeController.class).slash(newEmployee.getId()).withSelfRel();// withRel("employees");		  
		return ResponseEntity.created(link.toUri()).body(resource);			
	}

	// Single item
	@GetMapping("/employees/{id}")
	ResponseEntity<EntityModel<Employee>> one(@PathVariable Long id) throws EmployeeNotFoundException {
		log.info("Returning an employee with id: " + id);
		Employee employee = repository
				.findById(id)
				.orElseThrow(() -> new EmployeeNotFoundException(id));
		EntityModel<Employee> resource  = assembler.toModel(employee);
		Link link = linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel();
		log.info("Link: "+ link.toString());
		return ResponseEntity.created(link.toUri()).body(resource);		
	}

	@PutMapping("/employees/{id}")
	ResponseEntity<?> replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) throws URISyntaxException {
		log.info("Returning an updated employee with id: " + id);

		Employee updatedEmployee = repository.findById(id)
				.map(employee -> {
					employee.setName(newEmployee.getName());
					employee.setRole(newEmployee.getRole());
					log.info("updating an employee");
					return repository.save(employee);
				})						
				.orElseGet(() -> {
					newEmployee.setId(id);
					log.info("saving as a new employee");
					return repository.save(newEmployee);
				});

		log.info("Updated an employee");
		EntityModel<Employee> resource = assembler.toModel(updatedEmployee);
		Link link = linkTo(methodOn(EmployeeController.class).one(updatedEmployee.getId())).withSelfRel();
		log.info("Link: "+ link.toString());
		return ResponseEntity.created(link.toUri()).body(resource);		
	}

	@DeleteMapping("/employees/{id}")
	ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
		log.info("Deleting an employee with id: " + id);
		repository.findById(id)
		.orElseThrow(() -> new EmployeeNotFoundException(id));
		repository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}