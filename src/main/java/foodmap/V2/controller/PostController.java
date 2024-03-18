package foodmap.V2.controller;

import foodmap.V2.dto.request.post.PostCreate;
import foodmap.V2.dto.request.post.PostEdit;
import foodmap.V2.dto.request.post.PostSearch;

import foodmap.V2.dto.response.post.*;
import foodmap.V2.service.JwtService;

import foodmap.V2.service.post.PostService;
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


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;
    private final JwtService jwtService;


    @GetMapping("/")
    public PostListResponseDTO get(@ModelAttribute PostSearch postSearch,@RequestHeader(value = "Authorization", required = false) String token) {
        log.info("controller,{},{},{}",postSearch.getSearch(),postSearch.getSort(),postSearch.getPage());
        return postService.getList(postSearch, token);
    }
    @PostMapping("/")
    public PostDetailResponseDTO create(@RequestHeader("Authorization") String token,@RequestBody PostCreate postCreate) {
        String accessToken = token.substring(7);
        var email = jwtService.extractUsername(accessToken);
        return postService.write(email,postCreate);
    }
    @GetMapping("/{postId}")
    public PostDetailResponseDTO getPostDetail(@RequestHeader("Authorization") String token,@PathVariable Long postId, @CookieValue(value = "hit",required = false) String hit, HttpServletResponse response,HttpServletRequest request) {
        String accessToken = token.substring(7);
        Long userId= Long.valueOf(jwtService.extractUserid(accessToken));
        var cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("hit")) {
                String hitValue = cookie.getValue();
                String[] hitLists = hitValue.split("\\|");
                if (Arrays.stream(hitLists).anyMatch(post -> post.equals(String.valueOf(postId)))) {
                    return postService.get(postId, userId);
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

        return postService.get(postId,userId);
    }

//    @PreAuthorize("hasPermission(#postId, 'POST', 'DELETE')")
    @PutMapping("/{postId}")
    public PostDetailResponseDTO edit(@RequestHeader("Authorization") String token,@RequestBody @Valid PostEdit request,@PathVariable Long postId) {
        String accessToken = token.substring(7);
        Long userId= Long.valueOf(jwtService.extractUserid(accessToken));
        log.info("userid,{}",userId);
        return postService.edit(postId,userId,request);
    }

    @DeleteMapping("/{postId}")
    public void delete(@RequestHeader("Authorization") String token,@PathVariable Long postId){
        String accessToken = token.substring(7);
        Long userId= Long.valueOf(jwtService.extractUserid(accessToken));
        postService.delete(postId,userId);
    }
    @PostMapping("/{postId}/like")
    public PostLikeDTO toggleLike(@RequestHeader("Authorization") String token, @PathVariable Long postId) {
        return postService.toggleLike(token,postId);
    }
    @PostMapping("/{postId}/dislike")
    public PostDislikeDTO toggleDislike(@RequestHeader("Authorization") String token, @PathVariable Long postId) {
        return postService.toggleDislike(token,postId);
    }
    @PostMapping("/{postId}/bookmark")
    public PostBookmarkDTO toggleBookmark(@RequestHeader("Authorization") String token, @PathVariable Long postId) {
        return postService.toggleBookmark(token,postId);
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
