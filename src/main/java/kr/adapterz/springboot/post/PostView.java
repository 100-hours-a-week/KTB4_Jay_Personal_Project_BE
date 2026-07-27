package kr.adapterz.springboot.post;

import jakarta.persistence.*;
import kr.adapterz.springboot.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "post_views",
        uniqueConstraints ={
                @UniqueConstraint(name = "uk_post_views_post_user"
                ,columnNames = {"post_id","user_id"})
        },
        indexes = {
                @Index(name = "idx_post_views_user_id", columnList = "user_id"),
                @Index(name = "idx_post_views_post_id", columnList = "post_id")
        }

)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_view_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;

    public PostView(Post post, User user, LocalDateTime viewedAt){
        this.post = post;
        this.user = user;
        this.viewedAt = viewedAt;
    }

    public void updatedViewedAt(LocalDateTime viewedAt){
        this.viewedAt = viewedAt;
    }

}
