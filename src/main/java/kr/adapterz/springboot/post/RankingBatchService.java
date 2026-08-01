package kr.adapterz.springboot.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RankingBatchService {

    private final PostRankingRepository postRankingRepository;

    @Scheduled(fixedRateString = "300000")
    @Transactional
    public void refreshRankingsBySchedule() {
        log.info("Ranking batch started");
        refreshRanking(RankingPeriod.DAILY);
        refreshRanking(RankingPeriod.WEEKLY);
        log.info("Ranking batch finished");
    }

    @Transactional
    public void refreshRanking(RankingPeriod period) {
        postRankingRepository.deleteByPeriodType(period);
        postRankingRepository.insertRankings(
                period.name(),
                period.getStartDateTime()
        );
    }


}