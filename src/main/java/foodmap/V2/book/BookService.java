package foodmap.V2.book;
import foodmap.V2.book.dto.BookCrawlingResponseDTO;
import foodmap.V2.book.dto.BookDetailRequestDTO;
import foodmap.V2.book.dto.BookSearchRequestDTO;
import foodmap.V2.util.BookCrawler;
import foodmap.V2.util.DistanceCalculator;
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
    public Mono<String> getBooks(BookSearchRequestDTO bookSearchRequestDTO) {
        log.info("asd :{}: {}",bookSearchRequestDTO.getQuery(),bookSearchRequestDTO.getStart());
        WebClient client = WebClient.builder()
                .baseUrl("https://openapi.naver.com/v1/search/book.json")
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", secretId)
                .build();
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("query", bookSearchRequestDTO.getQuery())
                        .queryParam("start", bookSearchRequestDTO.getStart())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
    }
    public Mono<String> getBookDetail(String isbn) {
        WebClient client = WebClient.builder()
                .baseUrl("https://openapi.naver.com/v1/search/book_adv.json")
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", secretId)
                .build();
        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("d_isbn", isbn)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
    }

    public List<BookCrawlingResponseDTO> getStoreList(BookDetailRequestDTO bookDetailRequestDTO) throws IOException {
        List<BookCrawlingResponseDTO> closeStoreList = new ArrayList<>();
        List<BookCrawlingResponseDTO> bookCrawlingResponseDTOS = this.getBookCrawling(bookDetailRequestDTO.getIsbn());
        for (BookCrawlingResponseDTO bookCrawlingResponseDTO :bookCrawlingResponseDTOS) {
            double result = this.getDistance(bookCrawlingResponseDTO.getLatitude(),bookCrawlingResponseDTO.getLongitude(),bookDetailRequestDTO.getCurrentLatitude(),bookDetailRequestDTO.getCurrentLongitude());
            if (result <15) {
                closeStoreList.add(bookCrawlingResponseDTO);
            }
        }
        return closeStoreList;
    }
    private List<BookCrawlingResponseDTO> getBookCrawling(String isbn) throws IOException {
        return bookCrawler.scrapeWebsite(isbn);
    };
    private double getDistance(double targetLatitude,double targetLongitude,double currentLatitude,double currentLongitude) {
        return DistanceCalculator.calculateDistance(targetLatitude,targetLongitude,currentLatitude,currentLongitude);
    }
}