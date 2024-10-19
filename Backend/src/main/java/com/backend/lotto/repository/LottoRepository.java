package com.backend.lotto.repository;

import com.backend.lotto.entity.Lotto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LottoRepository extends JpaRepository<Lotto, Long> {
    // 특정 사용자에 대한 모든 로또 구매 기록을 가져오는 메서드
    List<Lotto> findByMemberId(Long memberId);
    
    // 특정 회차의 로또 구매 기록을 가져오는 메서드
    List<Lotto> findByRound(Integer round);
}
