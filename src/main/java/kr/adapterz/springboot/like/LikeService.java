package kr.adapterz.springboot.like;

import kr.adapterz.springboot.post.entity.PostReader;
import kr.adapterz.springboot.post.repository.PostRepository;
import kr.adapterz.springboot.user.UserReader;
import org.springframework.transaction.annotation.Transactional;
import kr.adapterz.springboot.global.exception.ConflictException;
import kr.adapterz.springboot.global.exception.NotFoundException;
import kr.adapterz.springboot.like.dto.LikeResponse;
import kr.adapterz.springboot.post.entity.Post;
import kr.adapterz.springboot.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final PostReader postReader;
    private final UserReader userReader;

    // 좋아요 누르기
    @Transactional
    public LikeResponse like(
            Long currentUserId,
            Long postId) {
        Post post = postReader.getActivePost(postId);
        User user = userReader.getActiveUser(currentUserId);

        if (likeRepository.existsByPost_IdAndUser_Id(postId, currentUserId)) {
            throw new ConflictException("already_liked");
        }

        likeRepository.save(new Like(post, user));
        postRepository.increaseLikeCount(postId);

        return new LikeResponse(postRepository.findLikeCountById(postId));
    }

    // 좋아요 취소
    @Transactional
    public LikeResponse unlike(
            Long currentUserId,
            Long postId) {
        Like like = likeRepository.findByPost_IdAndUser_Id(postId, currentUserId)
                .orElseThrow(() -> new NotFoundException("like_not_found"));
        Post post = postReader.getActivePost(postId);

        likeRepository.delete(like);
        postRepository.decreaseLikeCount(postId);

        return new LikeResponse(postRepository.findLikeCountById(postId));
    }
}
