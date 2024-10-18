package com.backend.lotto.service;

import com.backend.lotto.dto.LottoBuyResponse;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
public class LottoService {
    @Value("${lotto.user.id}")
    private String userIdValue;

    @Value("${lotto.user.password}")
    private String userPasswordValue;

    public LottoBuyResponse performLottoAutomation(int ticket) throws InterruptedException {
        WebDriver driver = null;
        int resultMessage = 0;
        String message = "";

        try {
            // ChromeDriver를 자동으로 설치하도록 설정
            WebDriverManager.chromedriver().setup();


            // ChromeOptions 설정 (헤드리스 모드 추가)
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");  // 헤드리스 모드 (GUI 없음)
            options.addArguments("--no-sandbox"); // 특정 Linux 환경에서 권한 문제 해결
            options.addArguments("--disable-dev-shm-usage"); // /dev/shm 문제 해결
            options.addArguments("--disable-gpu"); // GPU 사용 안 함 (헤드리스 모드에서 필요)
            options.addArguments("--window-size=1920x1080"); // 기본 화면 크기 설정
            options.addArguments("--remote-allow-origins=*"); // CORS 관련 오류 방지
            options.addArguments("--disable-popup-blocking"); // 팝업 차단 비활성화
            options.addArguments("--disable-blink-features=AutomationControlled"); // 자동화 감지 방지


            // WebDriver 초기화 (ChromeDriver)
            driver = new ChromeDriver(options);

            // WebDriverWait 인스턴스 생성
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // 페이지 접속 및 로그인
            driver.get("https://dhlottery.co.kr/user.do?method=login&returnUrl=");

            WebElement userId = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("userId")));
            WebElement userPw = driver.findElement(By.name("password"));
            WebElement login = driver.findElement(By.cssSelector("#article > div:nth-child(2) > div > form > div > div.inner > fieldset > div.form > a"));

            userId.sendKeys(userIdValue);
            userPw.sendKeys(userPasswordValue);
            login.click();

            // 필요에 따라 추가적인 대기 시간 설정
            Thread.sleep(2000);

            // 헤드리스 모드에서는 윈도우 핸들 전환이 불필요할 수 있으므로 해당 코드 제거 또는 주석 처리
            // 메인 페이지의 윈도우 핸들 저장
            String mainPageHandle = driver.getWindowHandle();

            // 모든 윈도우 핸들을 가져옴
            Set<String> allWindowHandles = driver.getWindowHandles();

            // 각 윈도우를 확인
            for (String windowHandle : allWindowHandles) {
                if (!windowHandle.equals(mainPageHandle)) {
                    // 현재 윈도우로 전환
                    driver.switchTo().window(windowHandle);

                    // 현재 윈도우의 URL을 가져옴
                    String currentURL = driver.getCurrentUrl();

                    // 필요한 조건에 따라 윈도우 닫기
                    if (!currentURL.equals("https://www.dhlottery.co.kr/common.do?method=main")) {
                        driver.close();
                    }
                }
            }

            // 메인 윈도우로 다시 전환
            driver.switchTo().window(mainPageHandle);

            driver.get("https://ol.dhlottery.co.kr/olotto/game/game645.do");
            Thread.sleep(500);

            // 보유 예치금 확인
            WebElement moneyBalance = driver.findElement(By.cssSelector("#moneyBalance"));
            System.out.println("보유 예치금: " + moneyBalance.getText());

            // 랜덤한 숫자 6개를 추출하는 코드
            for (int T = 0; T < ticket; T++) {
                long seed = System.currentTimeMillis();
                Random random = new Random(seed);
                List<Integer> list = new ArrayList<>();
                for (int i = 1; i <= 45; i++) list.add(i);
                Collections.shuffle(list, random);

                List<Integer> result = list.subList(0, 6);
                Collections.sort(result);
                System.out.println("Shuffled list: " + result);
                
                for (int i = 0; i < 6; i++) {
                    WebElement checkbox = driver.findElement(By.cssSelector("input[name='check645num'][value='" + result.get(i) + "']"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);

                }

                WebElement btnSelectNum = driver.findElement(By.cssSelector("#btnSelectNum"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnSelectNum);
                Thread.sleep(500);
            }

            // 구매 버튼 클릭
            WebElement btnBuy = driver.findElement(By.cssSelector("#btnBuy"));
            btnBuy.click();
            Thread.sleep(500);

            WebElement popupLayerConfirm = driver.findElement(By.cssSelector("#popupLayerConfirm > div > div.btns > input:nth-child(1)"));
            popupLayerConfirm.click();
            Thread.sleep(500);
            
            // 구매한도 알림 팝업의 visible 확인
            WebElement recommend720Plus = driver.findElement(By.id("recommend720Plus"));

            // 요소가 화면에 표시되는지 확인
            if (recommend720Plus.isDisplayed()) {
                message = "이번 주 로또 구매한도 5천원을 모두 채우셨습니다. 다음 회차 판매개시 후부터 다시 구매할 수 있습니다.";
                System.out.println("이번 주 로또 구매한도 5천원을 모두 채우셨습니다. 다음 회차 판매개시 후부터 다시 구매할 수 있습니다.");
                WebElement limitCloseLayer = driver.findElement(By.cssSelector("#recommend720Plus > div > div.btns > a:nth-child(2)"));
                limitCloseLayer.click();
                Thread.sleep(500);
            } else {
                WebElement closeLayer = driver.findElement(By.cssSelector("#closeLayer"));
                closeLayer.click();
                Thread.sleep(500);
                resultMessage = 1;
            }
        } catch (NoSuchElementException e) {
            message = "요소를 찾을 수 없습니다: " + e.getMessage();
        } catch (ElementClickInterceptedException e) {
            message = "요소를 클릭할 수 없습니다: " + e.getMessage();
        } catch (InterruptedException e) {
            message = "쓰레드 인터럽트 오류: " + e.getMessage();
        } catch (Exception e) {
            message = "오류가 발생했습니다: " + e.getMessage();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
        return new LottoBuyResponse(resultMessage, message);
    }
}
