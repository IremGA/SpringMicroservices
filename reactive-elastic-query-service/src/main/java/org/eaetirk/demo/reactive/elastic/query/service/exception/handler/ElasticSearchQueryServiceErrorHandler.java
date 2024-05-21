package org.eaetirk.demo.reactive.elastic.query.service.exception.handler;

import org.eaetirk.demo.reactive.elastic.query.service.exception.ElasticSearchQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class ElasticSearchQueryServiceErrorHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchQueryServiceErrorHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ElasticSearchError> handle(AccessDeniedException e){
        LOG.error("Access Denied Exception !!", e);
        ElasticSearchError elasticSearchError = ElasticSearchError
                .builder()
                .message("You are not authorized to access this resource")
                .reason(e.getMessage())
                .status(HttpStatus.FORBIDDEN.toString())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(elasticSearchError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ElasticSearchError> handle(IllegalArgumentException e){
        LOG.error("Illegal Argument Exception !!", e);
        ElasticSearchError elasticSearchError = ElasticSearchError
                .builder()
                .message("Invalid Request object found")
                .reason(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.toString())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(elasticSearchError);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ElasticSearchError> handle(RuntimeException e){
        LOG.error("RunTime Exception !!", e);
        ElasticSearchError elasticSearchError = ElasticSearchError
                .builder()
                .message("There is an error during processing Request")
                .reason(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.toString())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(elasticSearchError);
    }

    @ExceptionHandler(ElasticSearchQueryException.class)
    public ResponseEntity<ElasticSearchError> handle(ElasticSearchQueryException e){
        LOG.error("RunTime Exception !!", e);
        ElasticSearchError elasticSearchError = ElasticSearchError
                .builder()
                .message(e.getMessage())
                .reason(e.getReason())
                .status(e.getStatus().toString())
                .build();
        return ResponseEntity.status(e.getStatus()).body(elasticSearchError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ElasticSearchError> handle(Exception e){
        LOG.error("Internal Server Exception !!", e);
        ElasticSearchError elasticSearchError = ElasticSearchError
                .builder()
                .message(e.getMessage())
                .reason("Internal Sever Error !!")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(elasticSearchError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ElasticSearchError> handle(MethodArgumentNotValidException e){
        LOG.error("Method Argument Not Valid !!", e);
        ElasticSearchError elasticSearchError = ElasticSearchError
                .builder()
                .message(e.getMessage())
                .reason(e.getBody().getDetail())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(elasticSearchError);
    }


}
