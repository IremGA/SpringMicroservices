package org.eaetirk.demo.elastic.query.service.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class ElasticSearchQueryException extends RuntimeException{

    private String message;
    private String reason;
    private HttpStatus status;

    public ElasticSearchQueryException(String message, String reason, HttpStatus status) {
        this.message = message;
        this.reason = reason;
        this.status = status;
    }

    public ElasticSearchQueryException(String message, String message1, String reason, HttpStatus status) {
        super(message);
        this.message = message1;
        this.reason = reason;
        this.status = status;
    }

    public ElasticSearchQueryException(String message, Throwable cause, String message1, String reason, HttpStatus status) {
        super(message, cause);
        this.message = message1;
        this.reason = reason;
        this.status = status;
    }

    public ElasticSearchQueryException(Throwable cause, String message, String reason, HttpStatus status) {
        super(cause);
        this.message = message;
        this.reason = reason;
        this.status = status;
    }

    public ElasticSearchQueryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String message1, String reason, HttpStatus status) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.message = message1;
        this.reason = reason;
        this.status = status;
    }
}
