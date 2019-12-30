package payroll;

import lombok.Data;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
@Slf4j
class Employee {

	private @Id @GeneratedValue Long id;
	private String name;
	private String role;
	private int age;

	Employee() {
		log.info("Employee object created");
	}

	Employee(String name, String role) {
		this.setName(name);
		this.setRole(role);
	}
	
	Employee(String name, String role, int age) {
		this.setName(name);
		this.setRole(role);
		this.setAge(age);
	}
}