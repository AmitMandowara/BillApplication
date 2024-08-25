package billapplication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;


@ControllerAdvice
public class CentralizedExceptionClass {
	
	@ExceptionHandler(value=CustomerNotFoundException.class)
	public ResponseStatusException customerNotFoundException(CustomerNotFoundException customerNotFoundException){
		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, customerNotFoundException.getMessage(), customerNotFoundException);
	}
}
