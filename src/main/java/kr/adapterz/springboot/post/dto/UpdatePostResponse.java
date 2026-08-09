package kr.adapterz.springboot.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class UpdatePostResponse {
    private Long id;
    private String title;
    private String content;
    private String thumbnailUrl;
    private String author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UpdatePostResponse(Long id, String title, String content, String author, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.thumbnailUrl = null;
        this.author = author;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
