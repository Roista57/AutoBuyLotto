package com.backend.lotto.controller;

import com.backend.lotto.dto.LottoBuyResponse;
import com.backend.lotto.service.LottoService;

import com.backend.member.entity.Member;
import com.backend.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RestController
@RequestMapping("/lotto")
public class LottoController {
    @Autowired
    private LottoService lottoService;

    @Autowired
    private MemberService memberService;

    // 로또 구매 자동화
    @GetMapping("/buy")
    public ResponseEntity<String> buyLotto(@RequestParam(name = "ticket", required = true) int ticket, @RequestParam(name = "id", required = true) Long id) {
        // KST(한국 표준시) 시간대 기준으로 현재 시간 및 날짜 가져오기
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        LocalTime currentTime = now.toLocalTime();

        // 로또 판매 시간 조건 검사
        if (!isSaleTime(dayOfWeek, currentTime)) {
            // 현재 시간을 포맷하여 반환 메시지에 포함 (한국 시간 기준)
            String currentTimeFormatted = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String message = String.format("현재 KST 시간: %s. 현재는 로또 판매 시간이 아닙니다.", currentTimeFormatted);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try {
            Optional<Member> memberOptional = memberService.getMember(id);
            LottoBuyResponse response = null;

            if (memberOptional.isPresent()) {
                Member member = memberOptional.get();
                
                response = lottoService.performLottoAutomation(ticket, member);
                int state = response.getState();
                if (state == 1) {
                    return ResponseEntity.ok("로또 구매 성공: "+response.getMsg());
                }
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("로또 구매 실패: "+response.getMsg());
        } catch (InterruptedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 로또 판매 시간 체크
    private boolean isSaleTime(DayOfWeek dayOfWeek, LocalTime currentTime) {
        LocalTime startTime = LocalTime.of(6, 0);  // 6시
        LocalTime endTime = LocalTime.of(23, 59, 59);   // 24시

        // 추첨일(토요일) 조건: 오후 8시 마감
        if (dayOfWeek == DayOfWeek.SATURDAY) {
            LocalTime saturdayEndTime = LocalTime.of(20, 0);  // 오후 8시
            return !currentTime.isAfter(saturdayEndTime) && !currentTime.isBefore(startTime);
        }

        // 추첨 이후 일요일 오전 6시까지 판매 정지
        if (dayOfWeek == DayOfWeek.SUNDAY) {
            LocalTime sundayStartTime = LocalTime.of(6, 0);   // 오전 6시
            return !currentTime.isBefore(sundayStartTime);
        }

        // 평일의 경우 6시 ~ 24시 판매 가능
        return currentTime.isAfter(startTime) && currentTime.isBefore(endTime);
    }
}
