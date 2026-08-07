package kr.adapterz.springboot.post.entity;

import jakarta.persistence.*;
import kr.adapterz.springboot.post.RankingPeriod;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "post_rankings",
        uniqueConstraints ={
                @UniqueConstraint(
                        name = "uk_post_rankings_period_post",
                        columnNames = {"period_type","post_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_post_rankings_period_rank",
                        columnList = "period_type, rank_position"
                )
        }

)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostRanking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ranking_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "period_type",nullable = false)
    private RankingPeriod periodType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;

    @Column(name = "score", nullable = false)
    private Long score;

    @Column(name = "rank_position", nullable = false)
    private Integer rankPosition;

    @Column(name = "ranked_at", nullable = false)
    private LocalDateTime rankedAt;

    public PostRanking(
            RankingPeriod periodType,
            Post post,
            Long likeCount,
            Long viewCount,
            Long score,
            Integer rankPosition,
            LocalDateTime rankedAt
    ){
        this.periodType = periodType;
        this.post = post;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.score = score;
        this.rankPosition = rankPosition;
        this.rankedAt = rankedAt;
    }




}
