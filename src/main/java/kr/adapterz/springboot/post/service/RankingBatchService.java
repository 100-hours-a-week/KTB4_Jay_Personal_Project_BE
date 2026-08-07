package kr.adapterz.springboot.post.service;

import kr.adapterz.springboot.post.RankingPeriod;
import kr.adapterz.springboot.post.repository.PostRankingRepository;
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


}