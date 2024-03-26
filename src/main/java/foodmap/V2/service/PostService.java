package foodmap.V2.service;

import foodmap.V2.config.publisher.EventPublisher;

import foodmap.V2.domain.post.Post;
import foodmap.V2.domain.post.PostEditor;
import foodmap.V2.dto.request.post.PostEdit;
import foodmap.V2.dto.request.post.PostSearch;
import foodmap.V2.dto.response.post.*;
import foodmap.V2.exception.post.PostNotFound;
import foodmap.V2.exception.user.AccessDenied;
import foodmap.V2.exception.user.Unauthorized;
import foodmap.V2.exception.user.UserNotFound;
import foodmap.V2.repository.post.PostRepository;
import foodmap.V2.repository.UserRepository;
import foodmap.V2.dto.request.post.PostCreate;
import foodmap.V2.service.JwtService;
import foodmap.V2.service.S3Service;
import foodmap.V2.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
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

import java.util.List;

import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final S3Service s3Service;
    private final EventPublisher eventPublisher;
    private final UserService userService;
    public PostDetailResponseDTO write(Long userId, PostCreate postCreate) {
        var user = userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);
        Post post = Post.builder()
                .user(user)
                .title(postCreate.getTitle())
                .content(postCreate.getContent())
                .build();
        Post newPost =postRepository.save(post);
        return PostDetailResponseDTO.builder()
                .author(
                        PostAuthorResponseDTO.builder()
                                .uid(newPost.getUser().getId())
                                .username(newPost.getUser().getUsername())
                                .image(newPost.getUser().getImage())
                                .build()
                )
                .is_bookmarked(false)
                .is_disliked(false)
                .is_liked(false)
                .bookmark(newPost.getBookmark())
                .content(newPost.getContent())
                .created_at(String.valueOf(newPost.getCreatedAt()))
                .dislike(newPost.getDislike())
                .id(newPost.getId())
                .image(newPost.getImage())
                .like(newPost.getLikedUser())
                .read_count(newPost.getReadCount())
                .title(newPost.getTitle())
                .updated_at(String.valueOf(newPost.getCreatedAt()))
                .build();
    }
    @Transactional
    public PostDetailResponseDTO get(Long id,Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);
        return PostDetailResponseDTO.builder()
                .author(
                        PostAuthorResponseDTO.builder()
                                .uid(post.getUser().getId())
                                .username(post.getUser().getUsername())
                                .image(post.getUser().getImage())
                                .build()
                )
                .likes_count((long) post.getLikedUser().size())
                .dislikes_count((long) post.getDislike().size())
                .comments_count((long) post.getComments().size())
                .is_bookmarked(post.getBookmark().stream().anyMatch(uid-> uid.equals(userId)))
                .is_disliked(post.getDislike().stream().anyMatch(uid->uid.equals(userId)))
                .is_liked(post.getLikedUser().stream().anyMatch(uid->uid.equals(userId)))
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
    @Transactional
    public PostListResponseDTO getList(PostSearch postSearch,Long loinUserId) {
        var postList = postRepository.getList(postSearch);
        log.info("postlist,{}",postList);
        var postCount = postList.size();
        var hasNext = postRepository.hasNextPage(postSearch);
        List<PostDetailResponseDTO> postDetailResponseDTOList = postList.stream().map(
                post -> PostDetailResponseDTO.builder()
                        .author(
                                PostAuthorResponseDTO.builder()
                                        .uid(post.getUser().getId())
                                        .username(post.getUser().getUsername())
                                        .image(post.getUser().getImage())
                                        .build()
                        )
                        .likes_count((long) post.getLikedUser().size())
                        .dislikes_count((long) post.getDislike().size())
                        .comments_count((long) post.getComments().size())
                        .is_bookmarked(post.getBookmark().stream().anyMatch(uid->uid.equals(loinUserId)))
                        .is_disliked(post.getDislike().stream().anyMatch(uid->uid.equals(loinUserId)))
                        .is_liked(post.getLikedUser().stream().anyMatch(uid->uid.equals(loinUserId)))
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
                        .build()
        ).collect(Collectors.toList());
        return PostListResponseDTO.builder()
                .count((long) postCount)
                .next(hasNext)
                .results(postDetailResponseDTOList).build();
    }
    public Post increaseReadCount(Long id) {
        Post post=postRepository.findById(id).orElseThrow(PostNotFound::new);
        post.increaseReadCount();
        return postRepository.saveAndFlush(post);
    }


    @Transactional
    public PostDetailResponseDTO edit(Long postId, Long userId,PostEdit postEdit) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFound::new);
        if (post.getUserId().equals(userId)) {
            PostEditor.PostEditorBuilder editorBuilder = post.toEditor();
            PostEditor postEditor = editorBuilder.title(postEdit.getTitle())
                    .content(postEdit.getContent())
                    .build();
            post.edit(postEditor);
            return PostDetailResponseDTO.builder()
                    .author(
                            PostAuthorResponseDTO.builder()
                                    .uid(post.getUser().getId())
                                    .username(post.getUser().getUsername())
                                    .image(post.getUser().getImage())
                                    .build()
                    )
                    .likes_count((long) post.getLikedUser().size())
                    .dislikes_count((long) post.getDislike().size())
                    .comments_count((long) post.getComments().size())
                    .is_bookmarked(post.getBookmark().stream().anyMatch(uid->uid.equals(userId)))
                    .is_disliked(post.getDislike().stream().anyMatch(uid->uid.equals(userId)))
                    .is_liked(post.getLikedUser().stream().anyMatch(uid->uid.equals(userId)))
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
        } else {
            throw new AccessDenied();
        }
    }

    public void delete(Long postId,Long userId){
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFound::new);
        if (post.getUserId().equals(userId)) {
            String content = post.getContent();
            // Jsoup을 사용하여 HTML을 파싱
            Document doc = Jsoup.parse(content);
            // 모든 이미지 태그 가져오기
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
    public PostLikeDTO toggleLike(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFound::new);
        if (!post.getLikedUser().contains(userId)) {
            post.getLikedUser().add(userId);
            eventPublisher.saveNotification(post.getUserId(),userId,post);
        } else {
            post.getLikedUser().remove(userId);
        }
        postRepository.save(post);
        return PostLikeDTO.builder()
                .like(post.getLikedUser())
                .likes_count((long) post.getLikedUser().size())
                .is_liked(post.getLikedUser().stream().anyMatch(uid->uid.equals(userId)))
                .build();
    }
    @Transactional
    public PostDislikeDTO toggleDislike(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFound::new);
        if (!post.getDislike().contains(userId)) {
            post.getDislike().add(userId);
        } else {
            post.getDislike().remove(userId);
        }
        postRepository.save(post);
        return PostDislikeDTO.builder()
                .dislike(post.getDislike())
                .dislikes_count((long) post.getDislike().size())
                .is_disliked(post.getDislike().stream().anyMatch(uid->uid.equals(userId)))
                .build();
    }
    @Transactional
    public PostBookmarkDTO toggleBookmark(Long userId,Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFound::new);
        if (!post.getBookmark().contains(userId)) {
            post.getBookmark().add(userId);
        } else {
            post.getBookmark().remove(userId);
        }
        postRepository.save(post);
        return PostBookmarkDTO.builder()
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
}

