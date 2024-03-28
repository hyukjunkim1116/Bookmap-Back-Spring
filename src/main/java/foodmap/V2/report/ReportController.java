package foodmap.V2.report;

import foodmap.V2.exception.post.PostNotFound;
import foodmap.V2.exception.user.UserNotFound;
import foodmap.V2.jwt.JwtService;
import foodmap.V2.post.domain.Post;
import foodmap.V2.post.service.PostService;
import foodmap.V2.user.domain.UserInfo;
import foodmap.V2.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final UserService userService;
    private final PostService postService;
    @PostMapping("/api/reports/{postId}")
    public void create(HttpServletRequest request, @RequestBody ReportRequestDTO reportRequestDTO, @PathVariable Long postId){
        UserInfo user = userService.getUserByRequest(request).orElseThrow(UserNotFound::new);
        Post post = postService.getPost(postId).orElseThrow(PostNotFound::new);
        reportService.write(user,post,reportRequestDTO);
    }
}