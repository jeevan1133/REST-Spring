package payroll;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
class EmployeeNotFoundAdvice {

	@ResponseBody
	@ExceptionHandler(EmployeeNotFoundException.class)	
	ResponseEntity<?> employeeNotFoundHandler(EmployeeNotFoundException ex) {
		ErrorResponse error = new ErrorResponse();
		error.setErrorCode(HttpStatus.BAD_REQUEST.value());
		error.setMessage(ex.getMessage());	  
		return new ResponseEntity<ErrorResponse>(error, HttpStatus.NOT_FOUND);
	}
}