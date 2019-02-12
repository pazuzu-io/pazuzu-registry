package io.pazuzu.registry.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.ProblemHandling;

@ControllerAdvice
public class GlobalExceptionHandler implements ProblemHandling {
    // enables zalando problem library

    @ExceptionHandler
    public ResponseEntity<Problem> handleAccessDenied(
            final AccessDeniedException exception,
            final NativeWebRequest request) {
        return create(Status.FORBIDDEN, exception, request);
    }
}
