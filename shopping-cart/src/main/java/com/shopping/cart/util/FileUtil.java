package com.shopping.cart.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;

public class FileUtil {

    public static File convert(MultipartFile file) throws Exception {
        File convFile;

        try {
            convFile = new File(file.getOriginalFilename());

            if (convFile.getCanonicalFile().createNewFile()) {
                try (FileOutputStream fos = new FileOutputStream(convFile.getCanonicalFile())) {
                    fos.write(file.getBytes());
                }
            }
        } catch (Exception e) {
            throw new Exception("Error while processing the file conversion");
        }
        return convFile;
    }
}
