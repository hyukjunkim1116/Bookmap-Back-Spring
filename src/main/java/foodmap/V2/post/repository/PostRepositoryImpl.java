package foodmap.V2.post.repository;


import com.querydsl.jpa.impl.JPAQuery;
import foodmap.V2.post.dto.request.PostSearchRequestDTO;
import foodmap.V2.post.domain.Post;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static foodmap.V2.post.domain.QPost.post;

@Slf4j
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    public JPAQuery<Post> getInitialPost(PostSearchRequestDTO postSearchRequestDTO) {
        // query 변수를 한 번만 선언하고 초기화
        return jpaQueryFactory.selectFrom(post)
                .where(post.title.containsIgnoreCase(postSearchRequestDTO.getSearch()));
    }
    public List<Post> getQuery(JPAQuery<Post> query, PostSearchRequestDTO postSearchRequestDTO) {
        return switch (postSearchRequestDTO.getSort()) {
            case "likeCount" -> {
                query.orderBy(post.likedUser.size().desc());
                yield query.fetch();
            }
            case "readCount" -> {
                query.orderBy(post.readCount.desc());
                yield query.fetch();
            }
            case "bookMark" -> {
                query.orderBy(post.bookmark.size().desc());
                yield query.fetch();
            }
            default -> {
                query.orderBy(post.createdAt.desc());
                yield query.fetch();
            }
        };
    }
    @Override
    public List<Post> getList(PostSearchRequestDTO postSearchRequestDTO) {
        log.info("postsearchPostrepo,{},{},{},{}", postSearchRequestDTO.getSearch(), postSearchRequestDTO.getSort(), postSearchRequestDTO.getPage(), postSearchRequestDTO.getOffset());
        JPAQuery<Post> query = getInitialPost(postSearchRequestDTO).limit(postSearchRequestDTO.getSize())
                .offset(postSearchRequestDTO.getOffset());
        return getQuery(query, postSearchRequestDTO);
    }

    @Override
    public Boolean hasNextPage(PostSearchRequestDTO postSearchRequestDTO) {
        JPAQuery<Post> query = getInitialPost(postSearchRequestDTO);
        List<Post> totalPosts = getQuery(query, postSearchRequestDTO);
        var totalCount = totalPosts.size();
        int currentPage = postSearchRequestDTO.getPage();
        int pageSize = postSearchRequestDTO.getSize();
        // 총 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        return currentPage < totalPages;
    }
}
