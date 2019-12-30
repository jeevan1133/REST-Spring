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
public class OrderModelAssembler implements RepresentationModelAssembler<Order, RepresentationModel<EntityModel<Order>>> {

	@Override
	public EntityModel<Order> toModel(Order order) {
		// TODO Auto-generated method stub
		log.info("Called Order Model Assembler toModel()");
		EntityModel<Order> orderResource = new EntityModel<>(order, 
				linkTo(methodOn(OrderController.class).one(order.getId())).withSelfRel(),
				linkTo(methodOn(OrderController.class).all()).withRel("orders"));

		if (order.getStatus() == Status.IN_PROGRESS) {
			orderResource.add(
					linkTo(methodOn(OrderController.class)
							.cancel(order.getId())).withRel("cancel"));
			orderResource.add(
					linkTo(methodOn(OrderController.class)
							.complete(order.getId())).withRel("complete"));
		}
		return orderResource;
	}
}
