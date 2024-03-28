package foodmap.V2.util;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class FaviconController {
    @GetMapping("/favicon.ico")
    public void returnEmptyFavicon() {
        // 아무런 내용도 반환하지 않음
    }
}