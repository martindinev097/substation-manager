package com.buildingenergy.substation_manager.exception;

public class ForbiddenAccessAfterRoleChange extends RuntimeException {

    public ForbiddenAccessAfterRoleChange(String message) {
        super(message);
    }

}
