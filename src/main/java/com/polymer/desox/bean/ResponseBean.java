package com.polymer.desox.bean;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ResponseBean<T> implements Serializable {

    private int status;

    private String message;

    private T data;

}
