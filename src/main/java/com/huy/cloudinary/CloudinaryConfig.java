package com.huy.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@ConfigurationProperties(prefix = "application.cloudinary")
@Data
public class CloudinaryConfig {
    @Value("${application.cloudinary.cloud-name}")
    private String cloudName;
    @Value("${application.cloudinary.api-key}")
    private String apiKey;
    @Value("${application.cloudinary.api-secret}")
    private String apiSecret;


}
