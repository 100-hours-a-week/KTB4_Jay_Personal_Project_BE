package kr.adapterz.springboot.post;

import java.time.LocalDateTime;

public interface RankedPostProjection {
    Long getPostId();
    String getTitle();
    String getAuthorNickname();
    Boolean getAuthorDeleted();
    Long getLikeCount();
    Long getCommentCount();
    Long getViewCount();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    Long getPeriodLikeCount();
    Long getPeriodViewCount();
}
