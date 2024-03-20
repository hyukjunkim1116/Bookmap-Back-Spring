package foodmap.V2.utils;
import foodmap.V2.dto.response.BookCrawlingResponseDTO;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookCrawler {
    private final LocationData locationData;
public String getCurrentBookCode(String uri) {
    String[] pathSegments = uri.split("/");
    String lastPathSegment = pathSegments[pathSegments.length - 1];
    log.info("lastPathSegment : {}",lastPathSegment);
    return lastPathSegment;
}
    public List<BookCrawlingResponseDTO> scrapeWebsite(String isbn) throws IOException {
       WebDriver webDriver = new ChromeDriver();
        List<String> storeList = new ArrayList<>();
        List<String> countList = new ArrayList<>();
        List<BookCrawlingResponseDTO> resultList = new ArrayList<>();
       String url = String.format("https://search.kyobobook.co.kr/search?keyword=%s",isbn);
       webDriver.get(url);
       WebElement clickable = webDriver.findElement(By.className("prod_info"));
       clickable.click();
       String uri = webDriver.getCurrentUrl();
       String code = this.getCurrentBookCode(uri);
       List<WebElement> elements = webDriver.findElements(By.className("tbl_col"));
       log.info("currentUrl : {}",webDriver.getCurrentUrl());
        for (WebElement element : elements) {
            List<WebElement> tds = element.findElement(By.tagName("tbody"))
                    .findElement(By.tagName("tr"))
                    .findElements(By.tagName("td"));
            for (WebElement td : tds) {
                try {
                    String count = td.findElement(By.tagName("span")).getAttribute("innerText");
                    countList.add(count);
                } catch (NoSuchElementException e) {
                    log.info(String.valueOf(e));
                }
            }
            List<WebElement> ths = element.findElement(By.tagName("thead"))
                    .findElement(By.tagName("tr"))
                    .findElements(By.tagName("th"));
            for (WebElement th : ths) {
                String store = th.getAttribute("innerText");
                if (!store.isEmpty()) {
                    storeList.add(store);
                }
            }
        }
        for (int i = 0; i < storeList.size(); i++) {
            List<LocationData.Location> locationList = locationData.getLocationList();
            if (!Objects.equals(countList.get(i), "0")) {
                resultList.add(new BookCrawlingResponseDTO(storeList.get(i),countList.get(i),locationList.get(i).getLatitude(),locationList.get(i).getLongitude(),code));
            }
        }
        webDriver.close();
        return resultList;
        }
}
