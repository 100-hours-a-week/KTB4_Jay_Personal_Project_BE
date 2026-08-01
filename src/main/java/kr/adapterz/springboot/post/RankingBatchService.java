package kr.adapterz.springboot.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RankingBatchService {

    private final PostRankingRepository postRankingRepository;

    @Transactional
    public void refreshRanking(RankingPeriod period) {
        postRankingRepository.deleteByPeriodType(period);
        postRankingRepository.insertRankings(
                period.name(),
                period.getStartDateTime()
        );
    }

    @Transactional
    public void refreshAllRankings() {
        refreshRanking(RankingPeriod.DAILY);
        refreshRanking(RankingPeriod.WEEKLY);
    }
}