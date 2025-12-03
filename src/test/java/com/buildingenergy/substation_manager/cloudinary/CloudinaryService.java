package com.buildingenergy.substation_manager.cloudinary;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test")
public class CloudinaryService {

    public String uploadExcel(byte[] bytes, String fileName) {
        return "https://fake-cloudinary-url.test/" + fileName;
    }

}
