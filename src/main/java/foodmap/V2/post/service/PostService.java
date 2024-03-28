package foodmap.V2.post.service;
import foodmap.V2.event.publisher.EventPublisher;
import foodmap.V2.post.domain.Post;
import foodmap.V2.post.domain.PostEditor;
import foodmap.V2.exception.post.PostNotFound;
import foodmap.V2.exception.user.AccessDenied;
import foodmap.V2.post.dto.request.PostCreateRequestDTO;
import foodmap.V2.post.dto.request.PostEditRequestDTO;
import foodmap.V2.post.dto.request.PostSearchRequestDTO;
import foodmap.V2.post.dto.response.*;
import foodmap.V2.post.repository.PostRepository;
import foodmap.V2.user.domain.UserInfo;
import foodmap.V2.util.AmazonS3.S3Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final S3Service s3Service;
    private final EventPublisher eventPublisher;
    public Optional<Post> getPost(Long postId) {
        return postRepository.findById(postId);
    }
    public PostDetailResponseDTO write(UserInfo user, PostCreateRequestDTO postCreateRequestDTO) {
        Post newPost = saveNewPost(user, postCreateRequestDTO);
        return buildPostDetailResponse(newPost,user.getId());
    }
    public PostDetailResponseDTO get(Post post, Long userId) {
        return buildPostDetailResponse(post, userId);
    }
    @Transactional
    public PostListResponseDTO getList(PostSearchRequestDTO postSearchRequestDTO, Long loginUserId) {
        List<Post> postList = postRepository.getList(postSearchRequestDTO);
        List<PostDetailResponseDTO> postDetailResponseDTOList = postList.stream()
                .map(post -> buildPostDetailResponse(post, loginUserId))
                .collect(Collectors.toList());
        boolean hasNext = postRepository.hasNextPage(postSearchRequestDTO);
        return PostListResponseDTO.builder()
                .count((long) postDetailResponseDTOList.size())
                .next(hasNext)
                .results(postDetailResponseDTOList)
                .build();
    }
    public PostDetailResponseDTO edit(Post post, Long userId, PostEditRequestDTO postEditRequestDTO) {
        if (post.getUserId().equals(userId)) {
            var postEditor = post.toEditor()
                    .title(postEditRequestDTO.getTitle())
                    .content(postEditRequestDTO.getContent())
                    .build();
            post.edit(postEditor);
            return buildPostDetailResponse(post, userId);
        } else {
            throw new AccessDenied();
        }
    }
    public void delete(Post post,Long userId){
        if (post.getUserId().equals(userId)) {
            String content = post.getContent();
            Document doc = Jsoup.parse(content);
            Elements imgTags = doc.select("img");
            if (!imgTags.isEmpty()) {
                for (Element imgTag : imgTags) {
                    String src = imgTag.attr("src");
                    this.deletePostImage(src);
                    log.info("success");
                }
            }
            // 각 이미지 태그에서 src 속성 값 가져오기
            postRepository.delete(post);
        } else {
            throw new AccessDenied();
        }
    }
    @Transactional
    public PostLikeResponseDTO toggleLike(Long userId, Post post) {
        if (!post.getLikedUser().contains(userId)) {
            post.getLikedUser().add(userId);
            eventPublisher.saveNotification(post.getUserId(),userId,post);
        } else {
            post.getLikedUser().remove(userId);
        }
        postRepository.save(post);
        return PostLikeResponseDTO.builder()
                .like(post.getLikedUser())
                .likes_count((long) post.getLikedUser().size())
                .is_liked(post.getLikedUser().stream().anyMatch(uid->uid.equals(userId)))
                .build();
    }
    @Transactional
    public PostDislikeResponseDTO toggleDislike(Long userId, Post post) {
        if (!post.getDislike().contains(userId)) {
            post.getDislike().add(userId);
        } else {
            post.getDislike().remove(userId);
        }
        postRepository.save(post);
        return PostDislikeResponseDTO.builder()
                .dislike(post.getDislike())
                .dislikes_count((long) post.getDislike().size())
                .is_disliked(post.getDislike().stream().anyMatch(uid->uid.equals(userId)))
                .build();
    }
    @Transactional
    public PostBookmarkResponseDTO toggleBookmark(Long userId, Post post) {
        if (!post.getBookmark().contains(userId)) {
            post.getBookmark().add(userId);
        } else {
            post.getBookmark().remove(userId);
        }
        postRepository.save(post);
        return PostBookmarkResponseDTO.builder()
                .bookmark(post.getBookmark())
                .is_bookmarked(post.getBookmark().stream().anyMatch(uid->uid.equals(userId)))
                .build();
    }
    public String savePostImage(MultipartFile image) throws IOException {
        return s3Service.saveFile(image);
    }
    public void deletePostImage(String imageUrl){
        s3Service.deleteImage(imageUrl);
    }

    public void handleHitCookieAndReturnPost(Long postId, String hit, HttpServletResponse response, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            if (Arrays.stream(cookies).anyMatch(cookie -> cookie.getName().equals("hit"))) {
                this.handleHitCookie(postId, hit, response,cookies);
            } else {
                this.setHitCookie(postId, response);
            }
        } else {
            this.setHitCookie(postId, response);
        }
    }
    private Post saveNewPost(UserInfo user, PostCreateRequestDTO postCreateRequestDTO) {
        Post post = Post.builder()
                .user(user)
                .title(postCreateRequestDTO.getTitle())
                .content(postCreateRequestDTO.getContent())
                .build();
        return postRepository.save(post);
    }
    private PostDetailResponseDTO buildPostDetailResponse(Post post, Long userId) {
        return PostDetailResponseDTO.builder()
                .author(buildPostAuthorResponse(post.getUser()))
                .likes_count((long) post.getLikedUser().size())
                .dislikes_count((long) post.getDislike().size())
                .comments_count((long) post.getComments().size())
                .is_bookmarked(post.getBookmark().contains(userId))
                .is_disliked(post.getDislike().contains(userId))
                .is_liked(post.getLikedUser().contains(userId))
                .bookmark(post.getBookmark())
                .content(post.getContent())
                .created_at(String.valueOf(post.getCreatedAt()))
                .dislike(post.getDislike())
                .id(post.getId())
                .image(post.getImage())
                .like(post.getLikedUser())
                .read_count(post.getReadCount())
                .title(post.getTitle())
                .updated_at(String.valueOf(post.getCreatedAt()))
                .build();
    }

    private PostAuthorResponseDTO buildPostAuthorResponse(UserInfo user) {
        return PostAuthorResponseDTO.builder()
                .uid(user.getId())
                .username(user.getUsername())
                .image(user.getImage())
                .build();
    }
    private void handleHitCookie(Long postId, String hit, HttpServletResponse response, jakarta.servlet.http.Cookie[] cookies) {
        for (var cookie : cookies) {
            if (cookie.getName().equals("hit")) {
                String hitValue = cookie.getValue();
                String[] hitLists = hitValue.split("\\|");
                if (Arrays.stream(hitLists).noneMatch(post -> post.equals(String.valueOf(postId)))) {
                    String addedCookie = String.format("|%d", postId);
                    cookie.setValue(cookie.getValue() + addedCookie);
                    cookie.setMaxAge(24 * 60 * 60); // 쿠키의 유효 시간 설정 (초 단위, 예시는 하루)
                    response.addCookie(cookie); // 쿠키를 HTTP 응답에 추가
                    this.increaseReadCount(postId);
                }
                break;
            }
        }
    }
    private void setHitCookie(Long postId, HttpServletResponse response) {
        var newCookie = new Cookie("hit", String.valueOf(postId)); // 쿠키 이름과 값을 설정
        newCookie.setMaxAge(24 * 60 * 60); // 쿠키의 유효 시간 설정 (초 단위, 예시는 하루)
        response.addCookie(newCookie); // 쿠키를 HTTP 응답에 추가
        this.increaseReadCount(postId);
    }
    private void increaseReadCount(Long id) {
        Post post=postRepository.findById(id).orElseThrow(PostNotFound::new);
        post.increaseReadCount();
        postRepository.saveAndFlush(post);
    }
}

