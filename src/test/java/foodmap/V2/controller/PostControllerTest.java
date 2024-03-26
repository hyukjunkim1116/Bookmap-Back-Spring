package foodmap.V2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import foodmap.V2.config.BookmapMockUser;
import foodmap.V2.config.S3MockConfig;
import foodmap.V2.domain.UserInfo;
import foodmap.V2.domain.post.Post;
import foodmap.V2.dto.request.post.PostCreate;
import foodmap.V2.dto.request.post.PostEdit;
import foodmap.V2.dto.request.post.PostSearch;
import foodmap.V2.repository.UserRepository;
import foodmap.V2.repository.post.PostRepository;
import foodmap.V2.service.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Slf4j
@Import(S3MockConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private S3Service s3Service;


    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        postRepository.deleteAll();
    }
    @Test
    @DisplayName("게시글 여러건 조회")
    void get() throws Exception {
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
                        .title("foo" + i)
                        .content("bar" + i)
                        .user(user)
                        .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);
        PostSearch postSearch = PostSearch.builder().build();
        //when
        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/")
                        .content(objectMapper.writeValueAsString(postSearch))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @BookmapMockUser
    @DisplayName("게시글 생성")
    void create() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        String json = objectMapper.writeValueAsString(request);
        // when
        mockMvc.perform(post("/api/posts/")
                        .content(json)
                        .contentType(APPLICATION_JSON))
                        .andDo(print());
        // then
        assertEquals(1L, postRepository.count());
        Post post = postRepository.findAll().getFirst();
        assertEquals("제목입니다.", post.getTitle());
        assertEquals("내용입니다.", post.getContent());
    }

    @Test
    @DisplayName("게시글 상세조회")
    void getPostDetail() throws Exception {
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
        Post post = Post.builder()
                .user(user)
                .content("foo")
                .title("bar")
                .build();
        postRepository.save(post);
        //when
        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/"+post.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
    @Test
    @BookmapMockUser
    @DisplayName("게시글 수정")
    void edit() throws Exception {
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
        Post post = Post.builder()
                .user(user)
                .content("foo")
                .title("bar")
                .build();
        postRepository.save(post);
        PostEdit postEdit = PostEdit.builder()
                .title("change")
                .content("change")
                .build();
        String json = objectMapper.writeValueAsString(postEdit);
        //when
        // expected
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/"+post.getId())
                        .content(json)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());

    }
    @Test
    @BookmapMockUser
    @DisplayName("게시글 삭제")
    void delete() throws Exception {
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
        Post post = Post.builder()
                .user(user)
                .content("foo")
                .title("bar")
                .build();
        postRepository.save(post);
        //when
        // expected
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/"+post.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @BookmapMockUser
    @DisplayName("게시글 좋아요")
    void toggleLike() throws Exception {
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
        Post post = Post.builder()
                .user(user)
                .content("foo")
                .title("bar")
                .build();
        postRepository.save(post);
        //when
        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/"+post.getId()+"/like")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_liked").value(true))
                .andDo(print());
    }

    @Test
    @BookmapMockUser
    @DisplayName("게시글 싫어요")
    void toggleDislike() throws Exception {
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
        Post post = Post.builder()
                .user(user)
                .content("foo")
                .title("bar")
                .build();
        postRepository.save(post);
        //when
        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/"+post.getId()+"/dislike")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_disliked").value(true))
                .andDo(print());
    }

    @Test
    @BookmapMockUser
    @DisplayName("게시글 북마크")
    void toggleBookmark() throws Exception {
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
        Post post = Post.builder()
                .user(user)
                .content("foo")
                .title("bar")
                .build();
        postRepository.save(post);
        //when
        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/"+post.getId()+"/bookmark")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.is_bookmarked").value(true))
                .andDo(print());
    }

    @Test
    @BookmapMockUser
    @DisplayName("게시글 이미지 저장")
    void savePostImage() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "filename.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test data".getBytes());
        //expected
        mockMvc.perform(multipart("/api/posts/image")
                        .file(file))
                .andExpect(status().isOk());
    }

    @Test
    @BookmapMockUser
    @DisplayName("게시글 이미지 삭제")
    void deletePostImage() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "filename.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test data".getBytes());
        String url = s3Service.saveFile(file);
        // expected
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/image?url="+url)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}