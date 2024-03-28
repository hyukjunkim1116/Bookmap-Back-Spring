package foodmap.V2.comment.controller;
import foodmap.V2.comment.domain.Comment;
import foodmap.V2.comment.dto.request.CommentCreateRequestDTO;
import foodmap.V2.comment.dto.request.CommentEditRequestDTO;
import foodmap.V2.comment.dto.request.CommentSearchRequestDTO;
import foodmap.V2.comment.dto.response.CommentDetailResponseDTO;
import foodmap.V2.comment.dto.response.CommentListResponseDTO;
import foodmap.V2.comment.service.CommentService;
import foodmap.V2.exception.comment.CommentNotFound;
import foodmap.V2.exception.post.PostNotFound;
import foodmap.V2.exception.user.InvalidRequest;
import foodmap.V2.post.domain.Post;
import foodmap.V2.post.service.PostService;
import foodmap.V2.user.domain.UserInfo;
import foodmap.V2.user.service.UserService;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;
    private final PostService postService;
    @GetMapping("/{postId}/comment")
    public CommentListResponseDTO get(@PathVariable Long postId, @ModelAttribute CommentSearchRequestDTO commentSearchRequestDTO) {
        Post post = postService.getPost(postId)
                .orElseThrow(PostNotFound::new);
        return commentService.get(post, commentSearchRequestDTO);
    }
    @PostMapping("/{postId}/comment")
    public CommentDetailResponseDTO write(HttpServletRequest request, @PathVariable Long postId, @RequestBody @Valid CommentCreateRequestDTO commentCreateRequestDTO) {
        UserInfo user = userService.getUserByRequest(request)
                .orElseThrow(InvalidRequest::new);
        Post post = postService.getPost(postId)
                .orElseThrow(PostNotFound::new);
        return commentService.write(user, post, commentCreateRequestDTO);
    }
    @PutMapping("/comment/{commentId}")
    public CommentDetailResponseDTO edit(@PathVariable Long commentId, @RequestBody @Valid CommentEditRequestDTO request) {
        Comment comment = commentService.getComment(commentId)
                .orElseThrow(CommentNotFound::new);
        return commentService.edit(comment,request);
    }
    @DeleteMapping("/comment/{commentId}")
    public void delete(@PathVariable Long commentId) {
        Comment comment = commentService.getComment(commentId)
                .orElseThrow(CommentNotFound::new);
        commentService.delete(comment);
    }
}
