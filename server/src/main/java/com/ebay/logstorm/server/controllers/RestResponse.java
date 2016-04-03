package com.ebay.logstorm.server.controllers;

import com.ebay.logstorm.core.LogStormConstants;
import com.ebay.logstorm.server.functions.UnhandledConsumer;
import com.ebay.logstorm.server.functions.UnhandledSupplier;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestResponse<T>{
    private String version = LogStormConstants.CURRENT_VERSION;
    private Long timestamp;
    private boolean success = false;
    private String message;
    private String exception;
    private T data;
    private Long spend;
    private String path;
    private Class<?> type;

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

    public static <E> RestResponseBuilder<E> of(E data){
        return new RestResponseBuilder<E>().data(data);
    }

    public static <E> ResponseEntity<RestResponse<E>> of(Consumer<RestResponseBuilder<E>> func){
        RestResponseBuilder<E> builder = new RestResponseBuilder<>();
        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
            builder.success(true).status(HttpStatus.OK);
            func.accept(builder);
        } catch (Exception ex){
            builder.success(false).data(null).status(HttpStatus.INTERNAL_SERVER_ERROR).message(ex.getMessage());
        } finally {
            stopWatch.stop();
            builder.spend(stopWatch.getTime());
        }
        return builder.build();
    }

    public static <E> ResponseEntity<RestResponse<E>> of(Supplier<E> func){
        RestResponseBuilder<E> builder = new RestResponseBuilder<>();
        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
            builder.success(true).status(HttpStatus.OK).data(func.get());
        } catch (Throwable ex){
            builder.success(false).status(HttpStatus.INTERNAL_SERVER_ERROR).message(ex.getMessage());
        } finally {
            stopWatch.stop();
            builder.spend(stopWatch.getTime());
        }
        return builder.build();
    }

    public static <E> ResponseEntity<RestResponse<E>> async(UnhandledSupplier<E,Exception> func) {
        final RestResponseBuilder<E> builder = new RestResponseBuilder<>();
        CompletableFuture future = CompletableFuture.runAsync(() -> {
            try {
                builder.data(func.get()).status(HttpStatus.OK).success(true);
            } catch (Throwable e) {
                builder.success(false).status(HttpStatus.INTERNAL_SERVER_ERROR).message(e.getMessage()).exception(e);
            }
        });
        return runAsync(future,builder);
    }

    private static <E> ResponseEntity<RestResponse<E>> runAsync(CompletableFuture future, RestResponseBuilder<E> builder){
        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
            future.get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            future.cancel(true);
            builder.success(false).status(HttpStatus.INTERNAL_SERVER_ERROR).message(ex.getMessage()).exception(ex);
        } catch (Throwable ex) {
            builder.success(false).status(HttpStatus.INTERNAL_SERVER_ERROR).message(ex.getMessage()).exception(ex);
        } finally {
            stopWatch.stop();
            builder.spend(stopWatch.getTime());
        }
        return builder.build();
    }

    public static <E> ResponseEntity<RestResponse<E>> async(UnhandledConsumer<RestResponseBuilder<E>, Exception> func){
        final RestResponseBuilder<E> builder = new RestResponseBuilder<>();
        CompletableFuture future = CompletableFuture.runAsync(() -> {
            try {
                func.accept(builder);
            } catch (Throwable ex) {
                builder.success(false).status(HttpStatus.INTERNAL_SERVER_ERROR).message(ex.getMessage()).exception(ex);
            }
        });
        return runAsync(future,builder);
    }

    public String getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.setException(ExceptionUtils.getStackTrace(exception));
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getSpend() {
        return spend;
    }

    public void setSpend(Long spend) {
        this.spend = spend;
    }


    public static class RestResponseBuilder<E>{
        RestResponse<E> response;
        HttpStatus status = HttpStatus.OK;

        public RestResponseBuilder(){
            response = new RestResponse<>();
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
            if(data!=null) this.response.setType(data.getClass());
            return this;
        }

        public RestResponseBuilder<E> status(HttpStatus status){
            this.status = status;
            return this;
        }
        public RestResponseBuilder<E> exception(Throwable exception){
            this.response.setException(exception);
            this.response.setType(exception.getClass());
            return this;
        }

        public RestResponseBuilder<E> type(Class<?> clazz){
            this.response.setType(clazz);
            return this;
        }

        public RestResponseBuilder<E> spend(Long spendMilis){
            this.response.setSpend(spendMilis);
            return this;
        }

        public ResponseEntity<RestResponse<E>> build(){
            this.response.setPath(BaseController.getCurrentRequest().getRequestURI());
            this.response.setTimestamp(System.currentTimeMillis());
            return new ResponseEntity<>(this.response, this.status);
        }
    }
}