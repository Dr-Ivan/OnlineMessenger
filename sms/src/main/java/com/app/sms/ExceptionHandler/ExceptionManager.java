package com.app.sms.ExceptionHandler;

import com.app.sms.DTO.ErrorResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionManager {
    private final Logger log = LoggerFactory.getLogger(ExceptionManager.class);

    @ExceptionHandler(exception = Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleBasicException(Exception e) {
        log.error("Exception {} occurred at {}", e.toString(), LocalDateTime.now());
        ErrorResponseDTO err = new ErrorResponseDTO(
                "Unexpected error",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }

    @ExceptionHandler(exception = EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityNotFoundException(Exception e) {
        log.error("EntityNotFoundException {} occurred at {}", e.toString(), LocalDateTime.now());
        ErrorResponseDTO err = new ErrorResponseDTO(
                "Requested entity was not found",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler(exception = {
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(Exception e) {
        log.error("IllegalArgumentException {} occurred at {}", e.toString(), LocalDateTime.now());
        ErrorResponseDTO err = new ErrorResponseDTO(
                "Arguments are unacceptable",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(err);
    }

    @ExceptionHandler(exception = ValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(Exception e) {
        log.error("ValidationException {} occurred at {}", e.toString(), LocalDateTime.now());
        ErrorResponseDTO err = new ErrorResponseDTO(
                "Arguments did not pass validation",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

}
