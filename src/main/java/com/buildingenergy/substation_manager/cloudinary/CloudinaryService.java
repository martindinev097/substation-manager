package com.buildingenergy.substation_manager.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadExcel(byte[] bytes, String fileName) {
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    bytes,
                    ObjectUtils.asMap(
                            "resource_type", "raw",
                            "public_id", "excel_exports/" + fileName,
                            "format", "xlsx",
                            "overwrite", true
                    )
            );

            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload Excel to Cloudinary", e);
        }
    }
}
