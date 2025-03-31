package com.example.Exception;

public class FinanceException extends RuntimeException {
    private final boolean showToUser;

    public FinanceException(String message, boolean showToUser) {
        super(message);
        this.showToUser = showToUser;
    }

    public boolean isShowToUser() {
        return showToUser;
    }
}

