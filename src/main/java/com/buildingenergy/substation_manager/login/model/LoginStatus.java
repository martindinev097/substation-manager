package com.buildingenergy.substation_manager.login.model;

import lombok.Getter;

@Getter
public enum LoginStatus {

    SUCCEEDED("Succeeded"),
    FAILED("Failed");

    private final String displayName;

    LoginStatus(String displayName) {
        this.displayName = displayName;
    }
}
