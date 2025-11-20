package com.buildingenergy.substation_manager.exception;

import lombok.Getter;

@Getter
public class CannotExportEmptyCompanyHistory extends RuntimeException {

    private final int month;

    public CannotExportEmptyCompanyHistory(String message, int month) {
        super(message);
        this.month = month;
    }

}
