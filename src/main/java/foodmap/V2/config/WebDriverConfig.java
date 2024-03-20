package foodmap.V2.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebDriverConfig {
    @Bean
    public WebDriver webDriver() {
        // 크롬 드라이버 설정
        System.setProperty("webdriver.chrome.driver", "/Users/kim/Downloads/chromedriver-mac-arm64/chromedriver");
        // 웹 드라이버 초기화
        return new ChromeDriver();
    }
}
