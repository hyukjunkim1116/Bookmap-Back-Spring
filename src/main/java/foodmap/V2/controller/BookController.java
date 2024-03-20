package foodmap.V2.controller;

import foodmap.V2.dto.request.BookRequestDTO;
import foodmap.V2.dto.response.BookCrawlingResponseDTO;
import foodmap.V2.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;
    @GetMapping("/")
    public Mono<String> get(@RequestParam(required = false) Long start, @RequestParam(required = false) String query) {
        BookRequestDTO bookRequestDTO = BookRequestDTO.builder()
                .query(query)
                .start(start)
                .build();
        return bookService.getBooks(bookRequestDTO);
    }
    @GetMapping("/detail")
    private Mono<String> getBookDetail(@RequestParam String isbn) {
        return bookService.getBookDetail(isbn);
    }
    @GetMapping("/crawling")
    private List<BookCrawlingResponseDTO> getBookCrawling(@RequestParam String isbn,@RequestParam double currentLatitude,
                                                          @RequestParam double currentLongitude) throws IOException {
        return bookService.getStoreList(isbn,currentLatitude,currentLongitude);
    }
}
