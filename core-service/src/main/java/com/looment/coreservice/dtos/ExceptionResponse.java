package com.looment.coreservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class ExceptionResponse<T> implements Serializable {
    private String code;
    private T message;
}