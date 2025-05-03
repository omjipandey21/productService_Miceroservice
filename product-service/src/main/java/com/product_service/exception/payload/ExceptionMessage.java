package com.product_service.exception.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.product_service.constant.AppConstant;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Builder
@Data
@AllArgsConstructor
public class ExceptionMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonFormat(pattern = AppConstant.ZONED_DATE_TIME_FORMAT, shape = JsonFormat.Shape.STRING)
    private final ZonedDateTime timeStamp;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private final String errorMessage;

    private final HttpStatus httpStatus;
    private final String message;

    public ExceptionMessage(ZonedDateTime timeStamp, HttpStatus httpStatus, String message, Throwable throwable) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.timeStamp = timeStamp;
        this.errorMessage = throwable !=null ? throwable.getMessage() : null;
    }

}