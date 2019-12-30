package payroll;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	OrderNotFoundException(Long id) {
	    super("Could not find order: " + id);
	    log.warn("order not found exception");
	}
}
