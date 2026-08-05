package kr.adapterz.springboot.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PostRankingBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job postRankingJob;

    @Scheduled(fixedRateString = "300000")
    public void runPostRankingJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("requestTime", System.currentTimeMillis())
                    .toJobParameters();

            log.info("Post ranking batch job started");
            jobLauncher.run(postRankingJob, jobParameters);
            log.info("Post ranking batch job finished");
        } catch (Exception e) {
            log.error("Post ranking batch job failed", e);
        }
    }
}