package foodmap.V2.comment.repository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import foodmap.V2.comment.domain.Comment;
import foodmap.V2.comment.dto.request.CommentSearchRequestDTO;
import foodmap.V2.exception.post.PostNotFound;
import foodmap.V2.post.domain.Post;
import foodmap.V2.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import static foodmap.V2.comment.domain.QComment.comment1;

@Slf4j
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final PostRepository postRepository;
    public JPAQuery<Comment> getInitialComment(Long postId, CommentSearchRequestDTO commentSearchRequestDTO) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFound::new);
        return jpaQueryFactory.selectFrom(comment1)
                .where(comment1.post.eq(post))
                .orderBy(comment1.id.desc());
    }

    @Override
    public List<Comment> getList(Long postId, CommentSearchRequestDTO commentSearchRequestDTO) {
        var initialList = getInitialComment(postId, commentSearchRequestDTO);
        var list = initialList
                .limit(commentSearchRequestDTO.getSize())
                .offset(commentSearchRequestDTO.getOffset())
                .fetch();
        log.info("listasd,{}",list);
            return list;
        }

    public Boolean hasNextPage(Long postId, CommentSearchRequestDTO commentSearchRequestDTO) {
        JPAQuery<Comment> initialList = getInitialComment(postId, commentSearchRequestDTO);
        var totalCount = initialList.stream().count();
        int currentPage = commentSearchRequestDTO.getPage();
        int pageSize = commentSearchRequestDTO.getSize();
        // 총 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        return currentPage < totalPages;
    }
    }
