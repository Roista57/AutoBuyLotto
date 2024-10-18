package com.backend.lotto.scheduler;

import com.backend.lotto.dto.LottoBuyResponse;
import com.backend.lotto.service.LottoService;
import com.backend.member.entity.Member;
import com.backend.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Component
public class LottoScheduler {
    private static final Logger logger = LoggerFactory.getLogger(LottoScheduler.class);

    @Autowired
    private MemberService memberService;

    @Autowired
    private LottoService lottoService;

    // 5분마다 스케줄러 상태를 로그로 출력하는 함수
    @Scheduled(fixedRate = 300000)  // 5분마다 실행 (300,000 밀리초 = 5분)
    public void logSchedulerStatus() {
        logger.info("스케줄러가 정상적으로 실행되고 있습니다. (5분 스케줄러)");
    }

    // 매일 6시에 로또 구매 시도 (한국 시간 기준)
    @Scheduled(cron = "0 0 6 * * ?", zone = "Asia/Seoul")
    public void scheduledLottoBuyForAllMembers() {
        logger.info("스케줄러가 시작되었습니다: 모든 회원의 로또 구매 시도");

        try {
            // 모든 회원 목록 조회
            List<Member> members = memberService.getAllMembers();

            for (Member member : members) {
                logger.info("회원 {}의 로또 구매를 시도합니다.", member.getUserid());

                // 각 회원의 비밀번호를 복호화
                Optional<Member> memberOptional = memberService.getMember(member.getId());

                if (memberOptional.isPresent()) {
                    Member decryptedMember = memberOptional.get();

                    // 회원의 ID와 복호화된 비밀번호를 LottoService에 설정
                    lottoService.setUserCredentials(decryptedMember.getUserid(), decryptedMember.getUserPassword());

                    // 로또 구매 자동화 실행 (티켓 수는 임의로 1로 설정)
                    LottoBuyResponse response = lottoService.performLottoAutomation(1);

                    if (response.getState() == 1) {
                        logger.info("{}님 로또 구매 성공: {}", decryptedMember.getUserid(), response.getMsg());
                    } else {
                        logger.warn("{}님 로또 구매 실패: {}", decryptedMember.getUserid(), response.getMsg());
                    }
                } else {
                    logger.warn("회원 {} 정보를 찾을 수 없습니다.", member.getUserid());
                }
            }
        } catch (Exception e) {
            logger.error("스케줄러 실행 중 오류 발생: {}", e.getMessage(), e);
        }

        logger.info("스케줄러 작업이 완료되었습니다.");
    }
}
