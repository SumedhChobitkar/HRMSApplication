package com.example.HRMS.Application.Exception;

public class HelpDeskNotFoundException extends RuntimeException {
    public HelpDeskNotFoundException(String message) {
        super(message);
    }
}