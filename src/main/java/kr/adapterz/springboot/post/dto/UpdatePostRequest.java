package kr.adapterz.springboot.post.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdatePostRequest {
    @NotBlank(message = "empty_title")
    private String title;

    @NotBlank(message = "empty_content")
    private String content;

    @JsonAlias("imageUrl")
    private String thumbnailUrl;

    public UpdatePostRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getThumbnailUrl() {
        return normalizeImageUrl(thumbnailUrl);
    }

    private String normalizeImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }

        return imageUrl.trim();
    }
}
