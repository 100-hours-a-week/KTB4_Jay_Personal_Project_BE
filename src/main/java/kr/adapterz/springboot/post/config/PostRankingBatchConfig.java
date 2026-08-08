package kr.adapterz.springboot.post.config;

import kr.adapterz.springboot.post.service.RankingBatchService;
import kr.adapterz.springboot.post.RankingPeriod;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
@Configuration
public class PostRankingBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RankingBatchService rankingBatchService;

    @Bean
    public Job postRankingJob() {
        return new JobBuilder("postRankingJob", jobRepository)
                .start(dailyRankingRefreshStep())
                .next(weeklyRankingRefreshStep())
                .build();
    }

    @Bean
    public Step dailyRankingRefreshStep() {
        return new StepBuilder("dailyRankingRefreshStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    rankingBatchService.refreshRanking(RankingPeriod.DAILY);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step weeklyRankingRefreshStep() {
        return new StepBuilder("weeklyRankingRefreshStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    rankingBatchService.refreshRanking(RankingPeriod.WEEKLY);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
