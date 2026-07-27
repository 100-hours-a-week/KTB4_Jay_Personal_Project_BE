package kr.adapterz.springboot.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
public class PostListResponse {
    private Long postId;
    private String title;
    private String authorNickname;
    private Long commentCount;
    private Long viewCount;
    private Long likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean authorDeleted;
    private Boolean blinded;
}
