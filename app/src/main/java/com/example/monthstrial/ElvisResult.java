package com.example.monthstrial;

import java.net.CookieManager;

public abstract class ElvisResult<T> {

    private ElvisResult(){}

    public static final class Success<T> extends ElvisResult<T>{
        public T elvisData;
        public String AuthToken;
        public Success(T data){
            this.elvisData = data;
        }
    }
    public static final class Error<T> extends ElvisResult<T>{
        public Exception exception;
        public Error(Exception ex){
            this.exception = ex;
        }
    }
}
