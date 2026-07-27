package kr.adapterz.springboot.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
public class CommentDetailResponse {
    private Long commentId;
    private Long authorId;
    private String content;
    private String authorNickname;
    private Boolean authorDeleted;
    private Boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ReplyCreateResponse> replies;
}
