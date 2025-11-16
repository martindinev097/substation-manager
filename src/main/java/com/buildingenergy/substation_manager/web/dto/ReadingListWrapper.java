package com.buildingenergy.substation_manager.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ReadingListWrapper {

    private List<ReadingRequest> readings;

}
