package kr.adapterz.springboot.image.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ImageUploadResponse {
    private String imageUrl;
    private String markdown;
}
