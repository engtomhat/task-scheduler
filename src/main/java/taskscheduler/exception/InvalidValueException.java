package taskscheduler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidValueException extends RuntimeException {

	private static final long serialVersionUID = 497296913059336380L;

	public InvalidValueException(String message) {
		super(message);
	}

}
