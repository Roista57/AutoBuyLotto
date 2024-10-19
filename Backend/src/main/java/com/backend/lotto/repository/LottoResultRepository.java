package com.backend.lotto.repository;

import com.backend.lotto.entity.LottoResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LottoResultRepository extends JpaRepository<LottoResult, Long> {
    // 특정 사용자의 당첨 결과를 가져오는 메서드
    LottoResult findByMemberIdAndLottoIdx(Long memberId, Long lottoIdx);

    // 특정 로또 구매에 대한 결과를 가져오는 메서드
    LottoResult findByLottoIdx(Long lottoIdx);
}
