package com.buildingenergy.substation_manager.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReadingListWrapper {

    private List<ReadingRequest> readings;

}
