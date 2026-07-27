package kr.adapterz.springboot.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommentDeleteResponse {
    private Long commentId;
    private Boolean deleted;
    private String content;
    private Long commentCount;
}
