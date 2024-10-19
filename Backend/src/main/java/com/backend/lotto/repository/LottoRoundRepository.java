package com.backend.lotto.repository;

import com.backend.lotto.entity.LottoRound;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LottoRoundRepository extends JpaRepository<LottoRound, Long> {
    // 특정 회차의 당첨 번호를 가져오는 메서드
    LottoRound findByRound(Integer round);
}
