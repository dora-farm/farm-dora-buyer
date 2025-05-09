package com.farmdora.farmdorabuyer.common.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ncp.image")
public class NcpImageProperties {
    private ImageInfo product;
    private ImageInfo banner;
    private ImageInfo event;
    private ImageInfo review;

    @Getter
    @Setter
    public static class ImageInfo {
        private String path;
        private String type;

        public String createImageUrl(String imageName) {
            return String.format("%s%s%s", path, imageName, type);
        }
    }
}
