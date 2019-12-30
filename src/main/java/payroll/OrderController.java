package payroll;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.mediatype.vnderrors.VndErrors;
import org.springframework.http.HttpStatus;
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
public class OrderController {
	private final OrderRepository orderRepository;
	private final OrderModelAssembler assembler;

	public OrderController(OrderRepository orderRepository, OrderModelAssembler assembler) {
		// TODO Auto-generated constructor stub
		this.orderRepository = orderRepository;
		this.assembler = assembler;
	}

	@GetMapping("/orders")
	CollectionModel<EntityModel<Order>>  all() {
		log.info("Getting orders from the database");
		List<EntityModel<Order>> orders = orderRepository.findAll()
				.stream()
				.map(assembler::toModel)
				.collect(Collectors.toList());
		log.info("have list of orders");
		return new CollectionModel<>(orders,
				linkTo(methodOn(OrderController.class).all()).withRel("orderList"));
	}

	@GetMapping("/orders/{id}")
	EntityModel<Order> one(@PathVariable Long id) {
		return assembler.toModel(
				orderRepository.findById(id)
				.orElseThrow(() -> new OrderNotFoundException(id)));
	}

	@PostMapping("/orders")
	ResponseEntity<EntityModel<Order>> newOrder(@RequestBody Order order) {

		order.setStatus(Status.IN_PROGRESS);
		Order newOrder = orderRepository.save(order);

		return ResponseEntity
				.created(linkTo(methodOn(OrderController.class).one(newOrder.getId())).toUri())
				.body(assembler.toModel(newOrder));
	}

	@DeleteMapping("/orders/{id}/cancel")
	ResponseEntity<?> cancel(@PathVariable Long id) {		
		Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

		if (order.getStatus() == Status.IN_PROGRESS) {
			order.setStatus(Status.CANCELLED);
			return ResponseEntity.ok(assembler.toModel(orderRepository.save(order)));
		}

		return ResponseEntity
				.status(HttpStatus.METHOD_NOT_ALLOWED)
				.body(new VndErrors.VndError("Method not allowed", "You can't cancel an order that is in the " + order.getStatus() + " status"));
	}

	@PutMapping("/orders/{id}/complete")
	ResponseEntity<?> complete(@PathVariable Long id) {
		// TODO Auto-generated method stub
		Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
		System.out.println("Order is: " + order);
		if (order.getStatus() == Status.IN_PROGRESS) {
			order.setStatus(Status.COMPLETED);
			return ResponseEntity.ok(assembler.toModel(orderRepository.save(order)));
		}

		return ResponseEntity
				.status(HttpStatus.METHOD_NOT_ALLOWED)
				.body(new VndErrors.VndError("Method not allowed", "You can't complete an order that is in the " + order.getStatus() + " status"));
	}

	@DeleteMapping("/orders/{id}")
	ResponseEntity<?> inCompleteDelete(HttpServletRequest request, @PathVariable Long id) {
		log.warn("Incomplete request order");
		Enumeration<String> headerNames = request.getHeaderNames();
		while(headerNames.hasMoreElements())
		{
			String headerName = headerNames.nextElement();
			log.warn("Header: " + headerName + " ; Value: " + request.getHeader(headerName));
		}

		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(new VndErrors.VndError("Request not allowed", "You can't complete the request"));
	}
}
