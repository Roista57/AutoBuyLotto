package com.backend.lotto.service;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LottoService {
    @Value("${lotto.user.id}")
    private String userIdValue;

    @Value("${lotto.user.password}")
    private String userPasswordValue;

    public int performLottoAutomation(int ticket) throws InterruptedException {
        WebDriver driver = null;
        int resultMessage = 0;

        try {
            // ChromeOptions 설정 (헤드리스 모드 추가)
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");  // 헤드리스 모드 (GUI 없음)
            options.addArguments("--no-sandbox"); // 특정 Linux 환경에서 권한 문제 해결
            options.addArguments("--disable-dev-shm-usage"); // /dev/shm 문제 해결
            options.addArguments("--disable-gpu"); // GPU 사용 안 함 (헤드리스 모드에서 필요)
            options.addArguments("--window-size=1920x1080"); // 기본 화면 크기 설정

            // WebDriver 초기화 (ChromeDriver)
            driver = new ChromeDriver(options);

            // 페이지 접속 및 로그인
            driver.get("https://dhlottery.co.kr/user.do?method=login&returnUrl=");
            Thread.sleep(500);

            WebElement userId = driver.findElement(By.name("userId"));
            WebElement userPw = driver.findElement(By.name("password"));
            WebElement login = driver.findElement(By.cssSelector("#article > div:nth-child(2) > div > form > div > div.inner > fieldset > div.form > a"));

            userId.sendKeys(userIdValue);
            userPw.sendKeys(userPasswordValue);
            Thread.sleep(500);
            login.click();
            Thread.sleep(2000);

            // 메인 페이지의 윈도우 핸들 저장
            String mainPageHandle = driver.getWindowHandle();

            // 모든 윈도우 핸들을 가져옴
            Set<String> allWindowHandles = driver.getWindowHandles();

            // 각 윈도우를 확인
            for (String windowHandle : allWindowHandles) {
                // 현재 윈도우로 전환
                driver.switchTo().window(windowHandle);

                // 현재 윈도우의 URL을 가져옴
                String currentURL = driver.getCurrentUrl();

                // URL이 메인 페이지와 다른 경우 해당 윈도우를 닫음
                if (!currentURL.equals("https://www.dhlottery.co.kr/common.do?method=main")) {
                    driver.close();  // 해당 윈도우 닫기
                }
            }

            driver.switchTo().window(mainPageHandle);
            Thread.sleep(500);
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

                for (int i = 0; i < 6; i++) {
                    String labelFor = "label[for='check645num" + result.get(i) + "']";
                    WebElement label = driver.findElement(By.cssSelector(labelFor));
                    label.click();
                }
                Thread.sleep(500);

                WebElement btnSelectNum = driver.findElement(By.cssSelector("#btnSelectNum"));
                btnSelectNum.click();

                Thread.sleep(500);
                System.out.println("Shuffled list: " + result);
            }

            // 구매 버튼 클릭
            WebElement btnBuy = driver.findElement(By.cssSelector("#btnBuy"));
            btnBuy.click();
            Thread.sleep(500);

            WebElement popupLayerConfirm = driver.findElement(By.cssSelector("#popupLayerConfirm > div > div.btns > input:nth-child(1)"));
            popupLayerConfirm.click();
            Thread.sleep(500);

            WebElement closeLayer = driver.findElement(By.cssSelector("#closeLayer"));
            closeLayer.click();
            Thread.sleep(500);

            resultMessage = 1;
        } catch (NoSuchElementException e) {
            System.out.println("요소를 찾을 수 없습니다: " + e.getMessage());
        } catch (ElementClickInterceptedException e) {
            System.out.println("요소를 클릭할 수 없습니다: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("쓰레드 인터럽트 오류: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("오류가 발생했습니다: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
        return resultMessage;
    }
}