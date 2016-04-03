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
    private Long taken;
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
        return new RestResponseBuilder<>();
    }

    public static <E> RestResponseBuilder<E> of(E data){
        return RestResponse.<E>builder().data(data);
    }

    public static <E> RestResponseBuilder<E> of(Consumer<RestResponseBuilder<E>> func){
        return RestResponse.<E>builder().of(func);
    }

    public static <E> RestResponseBuilder<E> of(Supplier<E> func){
        return RestResponse.<E>builder().of(func);
    }

    public static <E> RestResponseBuilder<E> async(UnhandledSupplier<E,Exception> func) {
        return RestResponse.<E>builder().async(func);
    }

    public static <E> RestResponseBuilder<E> async(UnhandledConsumer<RestResponseBuilder<E>, Exception> func){
        return RestResponse.<E>builder().async(func);
    }

    public static <E> RestResponseBuilder<E> verbose(boolean verbose) {
        return RestResponse.<E>builder().verbose(verbose);
    }

    public String getException() {
        return exception;
    }

    public void setThrowable(Throwable exception) {
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

    public Long getTaken() {
        return taken;
    }

    public void setTaken(Long taken) {
        this.taken = taken;
    }

    public static class RestResponseBuilder<E>{
        RestResponse<E> current;
        HttpStatus status = HttpStatus.OK;
        boolean verbose;

        public RestResponseBuilder(){
            current = new RestResponse<>();
        }

        public RestResponseBuilder<E> success(boolean success){
            this.current.setSuccess(success);
            return this;
        }

        public RestResponseBuilder<E> message(String message){
            this.current.setMessage(message);
            return this;
        }

        public RestResponseBuilder<E> data(E data){
            this.current.setData(data);
            if(data!=null) this.current.setType(data.getClass());
            return this;
        }

        public RestResponseBuilder<E> status(HttpStatus status){
            this.status = status;
            return this;
        }
        public RestResponseBuilder<E> exception(Throwable exception){
            this.current.setThrowable(exception);
            this.current.setType(exception.getClass());
            return this;
        }

        public RestResponseBuilder<E> type(Class<?> clazz){
            this.current.setType(clazz);
            return this;
        }

        public RestResponseBuilder<E> spend(Long spendMillis){
            return take(spendMillis);
        }

        public RestResponseBuilder<E> take(Long spendMillis){
            this.current.setTaken(spendMillis);
            return this;
        }

        public RestResponseBuilder<E> verbose(boolean verbose){
            this.verbose = verbose;
            return this;
        }

        public RestResponseBuilder<E> of(Consumer<RestResponseBuilder<E>> func){
            StopWatch stopWatch = new StopWatch();
            try {
                stopWatch.start();
                this.success(true).status(HttpStatus.OK);
                func.accept(this);
            } catch (Exception ex){
                this.success(false).data(null).status(HttpStatus.INTERNAL_SERVER_ERROR).message(ex.getMessage());
            } finally {
                stopWatch.stop();
                this.spend(stopWatch.getTime());
            }
            return this;
        }

        public RestResponseBuilder<E>  of(Supplier<E> func){
            StopWatch stopWatch = new StopWatch();
            try {
                stopWatch.start();
                this.success(true).status(HttpStatus.OK).data(func.get());
            } catch (Throwable ex){
                this.success(false).status(HttpStatus.INTERNAL_SERVER_ERROR).message(ex.getMessage());
            } finally {
                stopWatch.stop();
                this.spend(stopWatch.getTime());
            }
            return this;
        }

        public RestResponseBuilder<E> async(UnhandledSupplier<E,Exception> func) {
            CompletableFuture future = CompletableFuture.runAsync(() -> {
                try {
                    this.status(HttpStatus.OK).success(true).data(func.get());
                } catch (Throwable e) {
                    this.success(false).status(HttpStatus.INTERNAL_SERVER_ERROR).message(e.getMessage()).exception(e);
                }
            });
            runAsync(future);
            return this;
        }

        private void runAsync(CompletableFuture future){
            StopWatch stopWatch = new StopWatch();
            try {
                stopWatch.start();
                future.get();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                future.cancel(true);
                this.success(false).status(HttpStatus.INTERNAL_SERVER_ERROR).message(ex.getMessage()).exception(ex);
            } catch (Throwable ex) {
                this.success(false).status(HttpStatus.INTERNAL_SERVER_ERROR).message(ex.getMessage()).exception(ex);
            } finally {
                stopWatch.stop();
                this.spend(stopWatch.getTime());
            }
        }

        public RestResponseBuilder<E> async(UnhandledConsumer<RestResponseBuilder<E>, Exception> func){
            CompletableFuture future = CompletableFuture.runAsync(() -> {
                try {
                    func.accept(this);
                    this.success(true);
                } catch (Throwable ex) {
                    this.success(false).status(HttpStatus.INTERNAL_SERVER_ERROR).message(ex.getMessage()).exception(ex);
                }
            });
            runAsync(future);
            return this;
        }

        public RestResponseBuilder<E> then(UnhandledConsumer<RestResponseBuilder<E>, Exception> func){
            try {
                func.accept(this);
            } catch (Throwable ex) {
                this.success(false).status(HttpStatus.INTERNAL_SERVER_ERROR).message(ex.getMessage()).exception(ex);
            }
            return this;
        }

        public ResponseEntity<RestResponse<E>> result(){
            this.current.setPath(BaseController.getCurrentRequest().getRequestURI());
            this.current.setTimestamp(System.currentTimeMillis());
            if(!this.verbose){
                this.current.setException(null);
                this.current.setPath(null);
            }
            return new ResponseEntity<>(this.current, this.status);
        }

        public RestResponseBuilder<E> status(boolean success, HttpStatus status) {
            this.success(success);
            this.status(status);
            return this;
        }
    }
}