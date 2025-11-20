package com.buildingenergy.substation_manager.exception;

import lombok.Getter;

@Getter
public class CannotExportEmptyMetersHistory extends RuntimeException {

    private final int month;

    public CannotExportEmptyMetersHistory(String message, int month) {
        super(message);
        this.month = month;
    }

}
