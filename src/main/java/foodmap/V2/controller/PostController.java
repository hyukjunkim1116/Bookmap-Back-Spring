package foodmap.V2.controller;

import foodmap.V2.domain.UserInfo;
import foodmap.V2.domain.post.Post;
import foodmap.V2.dto.request.post.PostCreate;
import foodmap.V2.dto.request.post.PostEdit;
import foodmap.V2.dto.request.post.PostSearch;

import foodmap.V2.dto.response.post.*;
import foodmap.V2.exception.post.PostNotFound;
import foodmap.V2.exception.user.AccessDenied;
import foodmap.V2.exception.user.InvalidRequest;
import foodmap.V2.exception.user.UserNotFound;
import foodmap.V2.service.JwtService;

import foodmap.V2.service.PostService;
import foodmap.V2.service.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final UserService userService;


    @GetMapping("/")
    public PostListResponseDTO get(@ModelAttribute PostSearch postSearch, HttpServletRequest request) {
        Optional<UserInfo> user = userService.getUserByRequest(request);
        Long loginUserId =null;
        if (user.isPresent()) {
            loginUserId = user.get().getId();
        }
        return postService.getList(postSearch, loginUserId);
    }
    @PostMapping("/")
    public PostDetailResponseDTO create(HttpServletRequest request,@RequestBody PostCreate postCreate) {
        Optional<UserInfo> user = userService.getUserByRequest(request);
        if (user.isPresent()) {
            return postService.write(user.get().getId(),postCreate);
        } else {
            throw new InvalidRequest();
        }
    }
    @GetMapping("/{postId}")
    public PostDetailResponseDTO getPostDetail(@PathVariable Long postId, @CookieValue(value = "hit",required = false) String hit, HttpServletResponse response,HttpServletRequest request) {
        Optional<UserInfo> user = userService.getUserByRequest(request);
        Long loginUserId = user.map(UserInfo::getId).orElse(null);
        var cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("hit")) {
                    String hitValue = cookie.getValue();
                    String[] hitLists = hitValue.split("\\|");
                    if (Arrays.stream(hitLists).anyMatch(post -> post.equals(String.valueOf(postId)))) {
                        return postService.get(postId, loginUserId);
                    }
                    else {
                        String addedCookie = String.format("|%d",postId);
                        cookie.setValue(cookie.getValue()+addedCookie);
                        cookie.setMaxAge(24 * 60 * 60); // 쿠키의 유효 시간 설정 (초 단위, 예시는 하루)
                        response.addCookie(cookie); // 쿠키를 HTTP 응답에 추가
                        postService.increaseReadCount(postId);
                    }
                    break;
                } else {
                    Cookie newCookie = new Cookie("hit", String.valueOf(postId)); // 쿠키 이름과 값을 설정
                    newCookie.setMaxAge(24 * 60 * 60); // 쿠키의 유효 시간 설정 (초 단위, 예시는 하루)
                    response.addCookie(newCookie); // 쿠키를 HTTP 응답에 추가
                    postService.increaseReadCount(postId);
                }
            }
        }
        return postService.get(postId,loginUserId);
    }

//    @PreAuthorize("hasPermission(#postId, 'POST', 'DELETE')")
    @PutMapping("/{postId}")
    public PostDetailResponseDTO edit(HttpServletRequest request,@RequestBody @Valid PostEdit postEdit,@PathVariable Long postId) {
        Optional<UserInfo> user = userService.getUserByRequest(request);
        if (user.isPresent()) {
            return postService.edit(postId,user.get().getId(),postEdit);
        } else {
            throw new UserNotFound();
        }
    }

    @DeleteMapping("/{postId}")
    public void delete(HttpServletRequest request,@PathVariable Long postId){
        Optional<UserInfo> user = userService.getUserByRequest(request);
        if (user.isPresent()) {
                postService.delete(postId,user.get().getId());
            }
         else {
            throw new UserNotFound();
        }

    }
    @PostMapping("/{postId}/like")
    public PostLikeDTO toggleLike(HttpServletRequest request, @PathVariable Long postId) {
        Optional<UserInfo> user = userService.getUserByRequest(request);
        Long loginUserId = user.map(UserInfo::getId).orElse(null);
        return postService.toggleLike(loginUserId,postId);
    }
    @PostMapping("/{postId}/dislike")
    public PostDislikeDTO toggleDislike(HttpServletRequest request, @PathVariable Long postId) {
        Optional<UserInfo> user = userService.getUserByRequest(request);
        Long loginUserId = user.map(UserInfo::getId).orElse(null);
        return postService.toggleDislike(loginUserId,postId);
    }
    @PostMapping("/{postId}/bookmark")
    public PostBookmarkDTO toggleBookmark(HttpServletRequest request, @PathVariable Long postId) {
        Optional<UserInfo> user = userService.getUserByRequest(request);
        Long loginUserId = user.map(UserInfo::getId).orElse(null);
        return postService.toggleBookmark(loginUserId,postId);
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
