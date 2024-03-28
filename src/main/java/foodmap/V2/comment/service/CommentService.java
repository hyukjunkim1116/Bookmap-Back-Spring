package foodmap.V2.comment.service;
import foodmap.V2.comment.domain.Comment;
import foodmap.V2.comment.domain.CommentEditor;
import foodmap.V2.comment.dto.request.CommentCreateRequestDTO;
import foodmap.V2.comment.dto.request.CommentEditRequestDTO;
import foodmap.V2.comment.dto.request.CommentSearchRequestDTO;
import foodmap.V2.comment.dto.response.CommentDetailResponseDTO;
import foodmap.V2.comment.dto.response.CommentListResponseDTO;
import foodmap.V2.comment.repository.CommentRepository;
import foodmap.V2.user.domain.UserInfo;
import foodmap.V2.post.domain.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    public CommentListResponseDTO get(Post post, CommentSearchRequestDTO commentSearchRequestDTO) {
        List<Comment> commentList = commentRepository.getList(post.getId(), commentSearchRequestDTO);
        var commentCount = commentList.size();
        Boolean hasNext = commentRepository.hasNextPage(post.getId(), commentSearchRequestDTO);
        List<CommentDetailResponseDTO> commentDetailResponseDTO =commentList.stream()
                .map(
                        comment -> CommentDetailResponseDTO.builder()
                                .author(comment.getAuthor().getId())
                                .comment(comment.getComment())
                                .created_at(String.valueOf(comment.getCreatedAt()))
                                .id(comment.getId())
                                .image(comment.getAuthor().getImage())
                                .post(comment.getPost().getId())
                                .updated_at(String.valueOf(comment.getCreatedAt()))
                                .username(comment.getAuthor().getUsername())
                                .build())
                .collect(Collectors.toList());
        return CommentListResponseDTO.builder()
                .count((long) commentCount)
                .next(hasNext)
                .results(commentDetailResponseDTO).build();
    }
    @Transactional
    public CommentDetailResponseDTO write(UserInfo user, Post post, CommentCreateRequestDTO request) {
        Comment comment = createComment(user, request);
        post.addComment(comment);
        return saveAndMapToResponse(comment);
    }

    @Transactional
    public CommentDetailResponseDTO edit(Comment comment, CommentEditRequestDTO commentEditRequestDTO) {
        CommentEditor commentEditor = comment.toEditor()
                .comment(commentEditRequestDTO.getComment())
                .build();
        comment.edit(commentEditor);
        return saveAndMapToResponse(comment);
    }
    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }
    private CommentDetailResponseDTO saveAndMapToResponse(Comment comment) {
        Comment savedComment = commentRepository.save(comment);
        return CommentDetailResponseDTO.builder()
                .author(savedComment.getAuthor().getId())
                .comment(savedComment.getComment())
                .created_at(String.valueOf(savedComment.getCreatedAt()))
                .id(savedComment.getId())
                .image(savedComment.getAuthor().getImage())
                .post(savedComment.getPost().getId())
                .updated_at(String.valueOf(savedComment.getCreatedAt()))
                .username(savedComment.getAuthor().getUsername())
                .build();
    }
    private Comment createComment(UserInfo user, CommentCreateRequestDTO request) {
        return Comment.builder()
                .author(user)
                .comment(request.getComment())
                .build();
    }
    public Optional<Comment> getComment(Long commentId) {
        return commentRepository.findById(commentId);
    }
}
