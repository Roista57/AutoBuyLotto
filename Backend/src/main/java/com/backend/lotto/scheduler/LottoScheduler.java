package com.backend.lotto.scheduler;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.backend.lotto.dto.LottoBuyResponse;
import com.backend.lotto.service.LottoService;
import com.backend.member.entity.Member;
import com.backend.member.service.MemberService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.jsoup.Jsoup;

@Component
public class LottoScheduler {
    private static final Logger logger = LoggerFactory.getLogger(LottoScheduler.class);

    @Autowired
    private MemberService memberService;

    @Autowired
    private LottoService lottoService;

    // 매 정각 마다 스케줄러 상태를 로그로 출력하는 함수
    @Scheduled(cron = "0 0 * * * ?", zone = "Asia/Seoul")  // 매 1시간마다 정각에 실행
    public void logSchedulerStatus() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        logger.info("스케줄러가 정상적으로 실행되고 있습니다. {날짜:" + now.format(formatter) + "}");
    }
    
    // TODO: LottoRound를 저장하는 코드를 추가, cron을 토요일 21시에 작업하도록 수정, 예외처리 수정
    @Scheduled(cron = "0 0 9 * * ?", zone = "Asia/Seoul")
    public void scheduledGetLottoRoundInfo() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://dhlottery.co.kr/gameResult.do?method=byWin";

        String text = null;
        int[] resultNumbers = new int[6];
        int bonusNumber = 0;

        // 요청에 필요한 폼 데이터 설정
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        // 요청 전송
        ResponseEntity<String> response = restTemplate.postForEntity(url, formData, String.class);

        // HTML 응답 데이터에서 원하는 부분을 파싱
        if (response.getStatusCode().is2xxSuccessful()) {
            String html = response.getBody();
            Document doc = Jsoup.parse(html);
            // 메타 태그의 id가 desc인 부분 추출
            Element strongTag = doc.selectFirst("#article > div:nth-child(2) > div > div.win_result > h4 > strong");
            Element resultNumberElement = doc.selectFirst("#article > div:nth-child(2) > div > div.win_result > div > div.num.win > p");
            Element bonusNumberElement = doc.selectFirst("#article > div:nth-child(2) > div > div.win_result > div > div.num.bonus > p");

            if(strongTag != null && resultNumberElement != null && bonusNumberElement != null) {
                text = strongTag.text().replace("회", "");
                System.out.println(text);

                String[] resultNumberStr = resultNumberElement.text().split(" ");
                for (int i = 0; i < 6; i++) {
                    resultNumbers[i] = Integer.parseInt(resultNumberStr[i]);
                }
                System.out.println(Arrays.toString(resultNumbers));

                bonusNumber = Integer.parseInt(bonusNumberElement.text());
                System.out.println(bonusNumber);
            }else{
                System.out.println("strongTag, resultNumberElement, bonusNumberElement을 찾을 수 없습니다.");
            }
        }else{
            System.out.println("파싱에 실패하였습니다.");
        }
    }

    // 매일 6시에 로또 구매 시도 (한국 시간 기준)
    @Scheduled(cron = "0 0 9 * * ?", zone = "Asia/Seoul")
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

                    // 로또 구매 자동화 실행 (티켓 수는 임의로 1로 설정)
                    LottoBuyResponse response = lottoService.performLottoAutomation(1, member);

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
