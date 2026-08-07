package kr.adapterz.springboot.post.repository;

import kr.adapterz.springboot.post.entity.PostViewEvents;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostViewEventsRepository extends JpaRepository <PostViewEvents, Long>{
}
