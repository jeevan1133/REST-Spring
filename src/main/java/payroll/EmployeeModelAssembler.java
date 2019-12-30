package payroll;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
class EmployeeModelAssembler implements RepresentationModelAssembler<Employee, RepresentationModel<EntityModel<Employee>>> {
	
	public EmployeeModelAssembler() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public EntityModel<Employee> toModel(Employee entity) {
		// TODO Auto-generated method stub	
		log.info("Called Employee Model Assembler toModel()");
		return new EntityModel<>(entity, 
				linkTo(methodOn(EmployeeController.class).one(entity.getId())).withSelfRel(),
			      linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));		
	}
}