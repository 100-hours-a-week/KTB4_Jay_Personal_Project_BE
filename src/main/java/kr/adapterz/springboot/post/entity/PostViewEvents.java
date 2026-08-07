package kr.adapterz.springboot.post.entity;

import jakarta.persistence.*;
import kr.adapterz.springboot.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "post_view_events",
        indexes = {
                @Index(name = "idx_post_view_events_post_viewed_at", columnList = "post_id, viewed_at"),
                @Index(name = "idx_post_view_events_viewed_at_post", columnList = "viewed_at, post_id")
        }
)

@Getter
@NoArgsConstructor(access =AccessLevel.PROTECTED)
public class PostViewEvents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_view_events_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(name = "viewed_at",nullable = false)
    private LocalDateTime viewedAt;

    public PostViewEvents(Post post, User user, LocalDateTime viewedAt){
        this.post = post;
        this.user = user;
        this.viewedAt = viewedAt;
    }
}
