package kr.adapterz.springboot.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class PostRankingBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job postRankingJob;

    @Value("${ranking.batch.run-slot-minutes:5}")
    private int runSlotMinutes;

    @Scheduled(fixedRateString = "${ranking.batch.fixed-rate-ms}")
    @SchedulerLock(
            name = "postRankingJob",
            lockAtMostFor = "${ranking.batch.lock-at-most-for}",
            lockAtLeastFor = "${ranking.batch.lock-at-least-for}"
    )
    public void runPostRankingJob() {
        long start = System.nanoTime();
        try {
            LocalDateTime now = LocalDateTime.now();
            int slotMinutes = Math.max(runSlotMinutes, 1);
            LocalDateTime runSlot = now
                    .withMinute(now.getMinute() / slotMinutes * slotMinutes)
                    .withSecond(0)
                    .withNano(0);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("runSlot", runSlot.toString())
                    .toJobParameters();

            log.info("Post ranking batch job started");
            jobLauncher.run(postRankingJob, jobParameters);
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            log.info("Post ranking batch job finished. elapsedMs={}", elapsedMs);
        } catch (Exception e) {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            log.error("Post ranking batch job failed. elapsedMs={}", elapsedMs, e);
        }
    }
}
