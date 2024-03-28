package foodmap.V2.comment.repository;

import foodmap.V2.comment.domain.Comment;
import foodmap.V2.comment.dto.request.CommentSearchRequestDTO;
import java.util.List;
public interface CommentRepositoryCustom {

    List<Comment> getList(Long postId, CommentSearchRequestDTO commentSearchRequestDTO);
    Boolean hasNextPage(Long postId, CommentSearchRequestDTO commentSearchRequestDTO);
}
