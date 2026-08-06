package kr.adapterz.springboot.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class PostRankingBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job postRankingJob;

    @Scheduled(fixedRateString = "300000")
    @SchedulerLock(
            name = "postRankingJob",
            lockAtMostFor = "PT10M",
            lockAtLeastFor = "PT4M30S"
    )
    public void runPostRankingJob() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime runSlot = now
                    .withMinute(now.getMinute() / 5 * 5)
                    .withSecond(0)
                    .withNano(0);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("runSlot", runSlot.toString())
                    .toJobParameters();

            log.info("Post ranking batch job started");
            jobLauncher.run(postRankingJob, jobParameters);
            log.info("Post ranking batch job finished");
        } catch (Exception e) {
            log.error("Post ranking batch job failed", e);
        }
    }
}