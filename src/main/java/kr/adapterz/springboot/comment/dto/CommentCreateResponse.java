package kr.adapterz.springboot.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class CommentCreateResponse {
    private Long commentId;
    private Long userId;
    private String content;
    private String authorNickname;
    private LocalDateTime createdAt;
    private Long commentCount;
}
