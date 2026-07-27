package kr.adapterz.springboot.post;

import kr.adapterz.springboot.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostReader {

    private final PostRepository postRepository;

    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("post_not_found"));
    }

    public Post getActivePost(Long postId) {
        Post post = getPost(postId);

        if (post.isDeleted()) {
            throw new NotFoundException("post_not_found");
        }

        return post;
    }

    public Post getActivePostWithAuthor(Long postId) {
        Post post = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new NotFoundException("post_not_found"));

        if (post.isDeleted()) {
            throw new NotFoundException("post_not_found");
        }

        return post;
    }
}