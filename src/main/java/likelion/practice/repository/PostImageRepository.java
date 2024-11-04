package likelion.practice.repository;

import java.util.List;
import likelion.practice.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {


  List<PostImage> findByPostId(Long postId);
}