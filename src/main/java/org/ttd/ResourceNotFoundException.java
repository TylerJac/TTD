package org.ttd;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

// This annotation marks the exception with an HTTP status code.
// Whenever this exception is thrown, the client will receive a 404 Not Found response.
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    // Serial version UID is used for ensuring the consistency of a serialized object across different JVMs.
    // This is needed because exceptions are serializable in Java.
    @Serial
    private static final long serialVersionUID = 1L;

    // Constructor that accepts only a message.
    // This message is passed to the superclass (RuntimeException), and it's typically used to describe the cause of the exception.
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Constructor that accepts both a message and a cause (another throwable).
    // This is useful when you want to pass a root cause along with the exception message.
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

