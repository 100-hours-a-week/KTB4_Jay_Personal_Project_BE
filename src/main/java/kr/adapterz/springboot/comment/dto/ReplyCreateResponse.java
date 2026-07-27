package kr.adapterz.springboot.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ReplyCreateResponse {
    private Long replyId;
    private Long paretnsCommentId;
    private String replyContent;
    private String authorNickname;
    private Boolean authordeleted;
    private LocalDateTime createdAt;
}
