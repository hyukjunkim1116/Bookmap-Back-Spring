package foodmap.V2.post.repository;

import foodmap.V2.post.dto.request.PostSearchRequestDTO;
import foodmap.V2.post.domain.Post;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearchRequestDTO postSearchRequestDTO);
    Boolean hasNextPage(PostSearchRequestDTO postSearchRequestDTO);
}
