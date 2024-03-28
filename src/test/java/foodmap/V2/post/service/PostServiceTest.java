package foodmap.V2.post.service;
import foodmap.V2.post.dto.request.PostCreateRequestDTO;
import foodmap.V2.post.dto.request.PostEditRequestDTO;
import foodmap.V2.post.dto.request.PostSearchRequestDTO;
import foodmap.V2.post.dto.response.*;
import foodmap.V2.post.repository.PostRepository;
import foodmap.V2.post.service.PostService;
import foodmap.V2.util.AmazonS3.S3Service;
import foodmap.V2.config.BookmapMockUser;
import foodmap.V2.config.S3MockConfig;
import foodmap.V2.user.domain.UserInfo;
import foodmap.V2.post.domain.Post;
import foodmap.V2.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@SpringBootTest
@Import(S3MockConfig.class)
class PostServiceTest {
    @Autowired
    private S3Service s3Service;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostService postService;
    @BeforeEach
    void before() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }
    @AfterEach
    void tearDown() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }
    @Test
    @BookmapMockUser
    @DisplayName("글 작성")
    void write()  {
        // given
        UserInfo user = UserInfo.builder()
                .email("1@1.com")
                .password(passwordEncoder.encode("1234"))
                .username("test")
                .image(null)
                .isVerified(false)
                .social(false)
                .build();
        userRepository.save(user);
        PostCreateRequestDTO postCreateRequestDTO = PostCreateRequestDTO.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        // when
        postService.write(user, postCreateRequestDTO);
        // then
        Post post = postRepository.findAll().getFirst();
        assertEquals(1L, postRepository.count());
        assertEquals("제목입니다.", post.getTitle());
        assertEquals("내용입니다.", post.getContent());
    }

    @Test
    @DisplayName("글 1개 조회")
    void get() {
        // given
        UserInfo user = UserInfo.builder()
                .email("1@1.com")
                .password(passwordEncoder.encode("1234"))
                .username("test")
                .image(null)
                .isVerified(false)
                .social(false)
                .build();
        userRepository.save(user);
        Post requestPost = Post.builder()
                .user(user)
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(requestPost);
        // when
        PostDetailResponseDTO response = postService.get(requestPost,user.getId());
        // then
        assertNotNull(response);
        assertEquals(1L, postRepository.count());
        assertEquals("foo", response.getTitle());
        assertEquals("bar", response.getContent());
    }

    @Test
    @DisplayName("글 여러개 조회")
    void getList() {
        // given
        UserInfo user = UserInfo.builder()
                .email("1@1.com")
                .password(passwordEncoder.encode("1234"))
                .username("test")
                .image(null)
                .isVerified(false)
                .social(false)
                .build();
        userRepository.save(user);
        List<Post> requestPosts = IntStream.range(0, 20)
                .mapToObj(i -> Post.builder()
                        .user(user)
                        .title("foo" + i)
                        .content("bar1" + i)
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);
        PostSearchRequestDTO postSearchRequestDTO = PostSearchRequestDTO.builder().build();
        // when
        PostListResponseDTO postListResponseDTO = postService.getList(postSearchRequestDTO,null);
        // then
        assertEquals(6L, postListResponseDTO.getResults().size());
        assertEquals("foo19", postListResponseDTO.getResults().getFirst().getTitle());
    }

    @Test
    @DisplayName("글 수정")
    void edit() {
        // given
        UserInfo user = UserInfo.builder()
                .email("1@1.com")
                .password(passwordEncoder.encode("1234"))
                .username("test")
                .image(null)
                .isVerified(false)
                .social(false)
                .build();
        userRepository.save(user);
        Post requestPost = Post.builder()
                .user(user)
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(requestPost);
        PostEditRequestDTO postEditRequestDTO = PostEditRequestDTO.builder()
                .title("change")
                .content("change")
                .build();
        // when
        PostDetailResponseDTO postDetailResponseDTO=postService.edit(requestPost,user.getId(), postEditRequestDTO);
        // then

        assertEquals("change", postDetailResponseDTO.getTitle());
        assertEquals("change", postDetailResponseDTO.getContent());
    }

    @Test
    @DisplayName("글 삭제")
    void delete() {
        // given
        UserInfo user = UserInfo.builder()
                .email("1@1.com")
                .password(passwordEncoder.encode("1234"))
                .username("test")
                .image(null)
                .isVerified(false)
                .social(false)
                .build();
        userRepository.save(user);
        Post requestPost = Post.builder()
                .user(user)
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(requestPost);
        // when
        postService.delete(requestPost,user.getId());
        // then
        assertEquals(0L, postRepository.count());
    }

    @Test
    @DisplayName("글 좋아요 토글")
    void toggleLike() {
        // given
        UserInfo user = UserInfo.builder()
                .email("1@1.com")
                .password(passwordEncoder.encode("1234"))
                .username("test")
                .image(null)
                .isVerified(false)
                .social(false)
                .build();
        userRepository.save(user);
        Post requestPost = Post.builder()
                .user(user)
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(requestPost);
        // when
        PostLikeResponseDTO postLikeResponseDTO =postService.toggleLike(user.getId(),requestPost);
        // then
        assertTrue(postLikeResponseDTO.getIs_liked());
    }

    @Test
    @DisplayName("글 싫어요 토글")
    void toggleDislike() {
        // given
        UserInfo user = UserInfo.builder()
                .email("1@1.com")
                .password(passwordEncoder.encode("1234"))
                .username("test")
                .image(null)
                .isVerified(false)
                .social(false)
                .build();
        userRepository.save(user);
        Post requestPost = Post.builder()
                .user(user)
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(requestPost);
        // when
        PostDislikeResponseDTO postDislikeResponseDTO =postService.toggleDislike(user.getId(),requestPost);
        // then
        assertTrue(postDislikeResponseDTO.getIs_disliked());
    }

    @Test
    @DisplayName("글 북마크 토글")
    void toggleBookmark() {
        // given
        UserInfo user = UserInfo.builder()
                .email("1@1.com")
                .password(passwordEncoder.encode("1234"))
                .username("test")
                .image(null)
                .isVerified(false)
                .social(false)
                .build();
        userRepository.save(user);
        Post requestPost = Post.builder()
                .user(user)
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(requestPost);
        // when
        PostBookmarkResponseDTO postBookmarkResponseDTO =postService.toggleBookmark(user.getId(),requestPost);
        // then
        assertTrue(postBookmarkResponseDTO.getIs_bookmarked());
    }
    @Test
    @DisplayName("글 이미지 저장")
    void savePostImage() throws IOException {
        //given
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "filename.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test data".getBytes());
        //when
        String url = postService.savePostImage(file);
        int lastIndex = url.lastIndexOf("/");
        if (lastIndex != -1) {
            url = url.substring(0, lastIndex);
        }
        //then
        assertNotNull(url);
        assertEquals("http://localhost:8001/foodmapbucket",url);
    }
    @Test
    @DisplayName("글 이미지 삭제")
    void deletePostImage() throws IOException {
        //given
        String path = "test.png";
        String contentType = "image/png";
        MockMultipartFile file = new MockMultipartFile("test", path, contentType, "test".getBytes());
        //when
        String url = postService.savePostImage(file);
        //then
        assertEquals(1L,s3Service.getFileCountInBucket());
        assertDoesNotThrow(()->postService.deletePostImage(url));
        assertEquals(0L,s3Service.getFileCountInBucket());
    }
}