package kr.adapterz.springboot.image;

import kr.adapterz.springboot.global.ApiResponse;
import kr.adapterz.springboot.image.dto.ImageUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/images")
@RequiredArgsConstructor
@RestController
public class ImageController {

    private final ImageService imageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadImage(
            @RequestPart("file") MultipartFile file
    ) {
        ImageUploadResponse response = imageService.upload(file);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("image_uploaded", response));
    }
}
