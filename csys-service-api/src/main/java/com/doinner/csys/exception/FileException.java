package com.doinner.csys.exception;


public class FileException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String message;

    /**
     * 空构造方法，避免反序列化问题
     */
    public FileException() {
    }

    public FileException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public FileException setMessage(String message) {
        this.message = message;
        return this;
    }
}
