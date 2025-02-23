package com.main.Util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class CloudinaryUploader {
        public static String uploadImage(String filePath) {
            Cloudinary cloudinary = CloudinarySetup.getInstance();
            try {
                File file = new File(filePath);
                Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
                return uploadResult.get("secure_url").toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
}
