package com.ebay.logstorm.server.controllers;

import com.ebay.logstorm.server.entities.RestResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;
import java.util.function.Supplier;

public class RestResponse extends ResponseEntity<RestResponseEntity> {
    public RestResponse(RestResponseEntity body, HttpStatus statusCode) {
        super(body, statusCode);
    }

    public static <E> RestResponseBuilder<E> builder(){
        return new RestResponseBuilder<E>();
    }

    public static <E> RestResponseBuilder<E> data(E data){
        return new RestResponseBuilder<E>().data(data);
    }

    public static <E> RestResponse of(Function<RestResponseBuilder,E> func){
        RestResponseBuilder<E> builder = new RestResponseBuilder<E>();
        try {
            builder.success(true).status(HttpStatus.OK).data(func.apply(builder));
        } catch (Exception ex){
            builder.data(null).status(HttpStatus.BAD_REQUEST).message(ex.getMessage());
        }
        return builder.build();
    }

    public static <E> RestResponse of(Supplier<E> func){
        RestResponseBuilder<E> builder = new RestResponseBuilder<E>();
        try {
            builder.success(true).status(HttpStatus.OK).data(func.get());
        } catch (Exception ex){
            builder.data(null).status(HttpStatus.BAD_REQUEST).message(ex.getMessage());
        }
        return builder.build();
    }

    public static class RestResponseBuilder<E>{
        RestResponseEntity<E> response;
        HttpStatus status = HttpStatus.OK;

        public RestResponseBuilder(){
            response = new RestResponseEntity<E>();
        }

        public RestResponseBuilder<E> success(boolean success){
            this.response.setSuccess(success);
            return this;
        }
        public RestResponseBuilder<E> message(String message){
            this.response.setMessage(message);
            return this;
        }
        public RestResponseBuilder<E> data(E data){
            this.response.setData(data);
            return this;
        }
        public RestResponseBuilder<E> status(HttpStatus status){
            this.status = status;
            return this;
        }
        public RestResponse build(){
            return new RestResponse(this.response,this.status);
        }
    }
}