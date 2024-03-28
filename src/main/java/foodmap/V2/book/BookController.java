package foodmap.V2.book;

import foodmap.V2.book.dto.BookCrawlingResponseDTO;
import foodmap.V2.book.dto.BookDetailRequestDTO;
import foodmap.V2.book.dto.BookSearchRequestDTO;
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
    @GetMapping("")
    public Mono<String> get(@ModelAttribute BookSearchRequestDTO bookSearchRequestDTO) {
        return bookService.getBooks(bookSearchRequestDTO);
    }
    @GetMapping("/detail")
    private Mono<String> getBookDetail(@RequestParam String isbn) {
        return bookService.getBookDetail(isbn);
    }
    @GetMapping("/crawling")
    private List<BookCrawlingResponseDTO> getBookCrawling(@ModelAttribute BookDetailRequestDTO bookDetailRequestDTO) throws IOException {
        return bookService.getStoreList(bookDetailRequestDTO);
    }
}
