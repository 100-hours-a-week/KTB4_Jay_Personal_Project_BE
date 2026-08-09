package kr.adapterz.springboot.image;

import kr.adapterz.springboot.global.exception.BadRequestException;
import kr.adapterz.springboot.image.dto.ImageUploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg",
            "jpeg",
            "png",
            "gif",
            "webp"
    );

    private final Path uploadPath;

    public ImageService(
            @Value("${file.upload-dir:uploads}") String uploadDir
    ) {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public ImageUploadResponse upload(MultipartFile file) {
        validate(file);

        String extension = getExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + "." + extension;
        Path targetPath = uploadPath.resolve(fileName).normalize();

        if (!targetPath.startsWith(uploadPath)) {
            throw new BadRequestException("invalid_file_name");
        }

        try {
            Files.createDirectories(uploadPath);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("image_upload_failed", e);
        }

        String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(fileName)
                .toUriString();

        return new ImageUploadResponse(
                imageUrl,
                "![이미지](" + imageUrl + ")"
        );
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("empty_file");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("file_size_exceeded");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new BadRequestException("invalid_image_type");
        }

        String extension = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException("invalid_image_extension");
        }
    }

    private String getExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BadRequestException("invalid_file_name");
        }

        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex < 0 || dotIndex == originalFilename.length() - 1) {
            throw new BadRequestException("invalid_file_name");
        }

        return originalFilename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }
}
