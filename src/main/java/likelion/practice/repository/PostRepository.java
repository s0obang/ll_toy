package likelion.practice.repository;

import java.util.Optional;
import likelion.practice.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

  @Override
  Optional<Post> findById(Long postId);

  Page<Post> findByUserId(Long userId, Pageable pageable);

  Page<Post> findByTitleContaining(String titleKeyword,
      Pageable pageable);
}
