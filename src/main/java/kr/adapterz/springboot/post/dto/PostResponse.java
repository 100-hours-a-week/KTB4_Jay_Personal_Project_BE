package kr.adapterz.springboot.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class PostResponse {
    private Long postId;
    private String title;
    private String content;
    private String thumbnailUrl;
    private String author;
    private LocalDateTime createdAt;

    public PostResponse(Long postId, String title, String content, String author, LocalDateTime createdAt) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.thumbnailUrl = null;
        this.author = author;
        this.createdAt = createdAt;
    }
}
