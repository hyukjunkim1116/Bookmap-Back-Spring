package foodmap.V2.comment.repository;

import foodmap.V2.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CommentRepository extends JpaRepository<Comment, Long>,CommentRepositoryCustom{
}
