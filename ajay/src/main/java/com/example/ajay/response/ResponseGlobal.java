package com.example.ajay.response;

public class ResponseGlobal<T> {

    private boolean success;
    private String message;
    private T data;

    public ResponseGlobal() {
    }

    public ResponseGlobal(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Success response
    public static <T> ResponseGlobal<T> onSuccess(
            String message,
            T data
    ) {
        return new ResponseGlobal<>(
                true,
                message,
                data
        );
    }

    // Failure response
    public static <T> ResponseGlobal<T> onFailure(
            String message
    ) {
        return new ResponseGlobal<>(
                false,
                message,
                null
        );
    }

    // Error response
    public static <T> ResponseGlobal<T> onError(
            String message
    ) {
        return new ResponseGlobal<>(
                false,
                message,
                null
        );
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}