package com.ebay.logstorm.server.controllers;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class RestResponse<T>{
    private boolean success = true;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static <E> RestResponseBuilder<E> builder(){
        return new RestResponseBuilder<E>();
    }

    public static <E> RestResponseBuilder<E> data(E data){
        return new RestResponseBuilder<E>().data(data);
    }

    public static class RestResponseBuilder<E>{
        RestResponse<E>  response;
        HttpStatus status = HttpStatus.OK;

        public RestResponseBuilder(){
            response = new RestResponse<E>();
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
        public ResponseEntity<RestResponse<E>> build(){
            return ResponseEntity.status(this.status).body(response);
        }
    }
}
