package com.main.Util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public class CloudinarySetup {
    private static final String CLOUD_NAME = "dbdclnnrj";
    private static final String API_KEY = "782657435867687";
    private static final String API_SECRET = "l4EejH7LGdVb-m8BMhwv9tKI_3c";

    public static Cloudinary getInstance() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", CLOUD_NAME,
                "api_key", API_KEY,
                "api_secret", API_SECRET
        ));
    }
}
