package llc.devlab.app.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("An unexpected error occurred", ex);
        ErrorResponse errorResponse = createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred");
        return ResponseEntity.status(500).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation error occurred", ex);
        ErrorResponse errorResponse = createErrorResponse(HttpStatus.BAD_REQUEST.value(),
                ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage());
        return ResponseEntity.status(400).body(errorResponse);
    }

    private ErrorResponse createErrorResponse(int statusCode, String message) {
        return new ErrorResponse(
                statusCode,
                message
        );
    }
}
