package foodmap.V2.post.controller;
import foodmap.V2.exception.post.PostNotFound;
import foodmap.V2.post.domain.Post;
import foodmap.V2.post.dto.request.PostCreateRequestDTO;
import foodmap.V2.post.dto.request.PostEditRequestDTO;
import foodmap.V2.post.dto.request.PostSearchRequestDTO;
import foodmap.V2.post.dto.response.*;
import foodmap.V2.post.service.PostService;
import foodmap.V2.user.domain.UserInfo;
import foodmap.V2.exception.user.UserNotFound;
import foodmap.V2.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Optional;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final UserService userService;


    @GetMapping("")
    public PostListResponseDTO get(@ModelAttribute PostSearchRequestDTO postSearchRequestDTO, HttpServletRequest request) {
        Optional<UserInfo> user = userService.getUserByRequest(request);
        Long loginUserId = user.map(UserInfo::getId).orElse(null);
        return postService.getList(postSearchRequestDTO, loginUserId);
    }
    @PostMapping("")
    public PostDetailResponseDTO create(HttpServletRequest request, @RequestBody PostCreateRequestDTO postCreateRequestDTO) {
        UserInfo user = userService.getUserByRequest(request).orElseThrow(UserNotFound::new);
        return postService.write(user, postCreateRequestDTO);
    }
    @GetMapping("/{postId}")
    public PostDetailResponseDTO getPostDetail(@PathVariable Long postId, @CookieValue(value = "hit", required = false) String hit, HttpServletResponse response, HttpServletRequest request) {
        Post post = postService.getPost(postId).orElseThrow(PostNotFound::new);
        Optional<UserInfo> user = userService.getUserByRequest(request);
        var userId = user.map(UserInfo::getId).orElse(null);
        postService.handleHitCookieAndReturnPost(postId, hit, response, request);
        return postService.get(post, userId);
    }

//    @PreAuthorize("hasPermission(#postId, 'POST', 'DELETE')")
    @PutMapping("/{postId}")
    public PostDetailResponseDTO edit(HttpServletRequest request, @RequestBody @Valid PostEditRequestDTO postEditRequestDTO, @PathVariable Long postId) {
        Post post = postService.getPost(postId).orElseThrow(PostNotFound::new);
        UserInfo user = userService.getUserByRequest(request).orElseThrow(UserNotFound::new);
        return postService.edit(post,user.getId(), postEditRequestDTO);
    }
    @DeleteMapping("/{postId}")
    public void delete(HttpServletRequest request,@PathVariable Long postId){
        Post post = postService.getPost(postId).orElseThrow(PostNotFound::new);
        UserInfo user = userService.getUserByRequest(request).orElseThrow(UserNotFound::new);
        postService.delete(post,user.getId());
    }
    @PostMapping("/{postId}/like")
    public PostLikeResponseDTO toggleLike(HttpServletRequest request, @PathVariable Long postId) {
        Post post = postService.getPost(postId).orElseThrow(PostNotFound::new);
        Optional<UserInfo> user = userService.getUserByRequest(request);
        Long loginUserId = user.map(UserInfo::getId).orElse(null);
        return postService.toggleLike(loginUserId,post);
    }
    @PostMapping("/{postId}/dislike")
    public PostDislikeResponseDTO toggleDislike(HttpServletRequest request, @PathVariable Long postId) {
        Post post = postService.getPost(postId).orElseThrow(PostNotFound::new);
        Optional<UserInfo> user = userService.getUserByRequest(request);
        Long loginUserId = user.map(UserInfo::getId).orElse(null);
        return postService.toggleDislike(loginUserId,post);
    }
    @PostMapping("/{postId}/bookmark")
    public PostBookmarkResponseDTO toggleBookmark(HttpServletRequest request, @PathVariable Long postId) {
        Post post = postService.getPost(postId).orElseThrow(PostNotFound::new);
        Optional<UserInfo> user = userService.getUserByRequest(request);
        Long loginUserId = user.map(UserInfo::getId).orElse(null);
        return postService.toggleBookmark(loginUserId,post);
    }
    @PostMapping("/image")
    public String savePostImage(@RequestBody MultipartFile image) throws IOException {
        return postService.savePostImage(image);
    }
    @DeleteMapping("/image")
    public void deletePostImage(@RequestParam("url") String imageUrl){
        postService.deletePostImage(imageUrl);
    }
}
