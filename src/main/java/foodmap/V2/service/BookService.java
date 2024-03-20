package foodmap.V2.service;

import foodmap.V2.dto.request.BookRequestDTO;
import foodmap.V2.dto.response.BookCrawlingResponseDTO;
import foodmap.V2.utils.BookCrawler;
import foodmap.V2.utils.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {
    @Value("${book.client-id}")
    private String clientId;
    @Value("${book.secret}")
    private String secretId;
    private final BookCrawler bookCrawler;
    public Mono<String> getBooks(BookRequestDTO bookRequestDTO) {
        log.info("bookqreqdTo,{}",bookRequestDTO);
        // WebClient 객체 생성
        WebClient client = WebClient.builder()
                .baseUrl("https://openapi.naver.com/v1/search/book.json")
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", secretId)
                .build();
        String url = String.format("?query=%s&start=%d",bookRequestDTO.getQuery(),bookRequestDTO.getStart());
        // 요청 보내기
        return client.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
    }
        public Mono<String> getBookDetail(String isbn) {
        // WebClient 객체 생성
        WebClient client = WebClient.builder()
                .baseUrl("https://openapi.naver.com/v1/search/book_adv.json")
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", secretId)
                .build();

        String url = String.format("?d_isbn=%s",isbn);
        // 요청 보내기
        return client.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
    }
    public List<BookCrawlingResponseDTO> getBookCrawling(String isbn) throws IOException {

        return bookCrawler.scrapeWebsite(isbn);
    };
    public double getDistance(double targetLatitude,double targetLongitude,double currentLatitude,double currentLongitude) {
        return DistanceCalculator.calculateDistance(targetLatitude,targetLongitude,currentLatitude,currentLongitude);
    }
    public List<BookCrawlingResponseDTO> getStoreList(String isbn,double currentLatitude,double currentLongitude) throws IOException {
        List<BookCrawlingResponseDTO> closeStoreList = new ArrayList<>();
        List<BookCrawlingResponseDTO> bookCrawlingResponseDTO = this.getBookCrawling(isbn);
        for (BookCrawlingResponseDTO bookCrawlingResponseDTO1 :bookCrawlingResponseDTO) {
            double result = this.getDistance(bookCrawlingResponseDTO1.getLatitude(),bookCrawlingResponseDTO1.getLongitude(),currentLatitude,currentLongitude);
            if (result <15) {
                closeStoreList.add(bookCrawlingResponseDTO1);
            }
        }
        return closeStoreList;
    }
}