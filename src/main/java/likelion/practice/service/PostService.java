package likelion.practice.service;

import java.util.ArrayList;
import java.util.List;
import likelion.practice.dto.PostResponseDto;
import likelion.practice.entity.Post;
import likelion.practice.entity.PostImage;
import likelion.practice.entity.User;
import likelion.practice.repository.PostImageRepository;
import likelion.practice.repository.PostRepository;
import likelion.practice.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@AllArgsConstructor

public class PostService {

  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final PostImageRepository postImageRepository;
  private S3Service s3Service;


  public Post createPost(String userId, String title, String content, List<MultipartFile> images)
      throws Exception {
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

    Post post = new Post();
    post.setUser(user);
    post.setTitle(title);
    post.setContent(content);
    post = postRepository.save(post);

    List<PostImage> postImages = new ArrayList<>();
    for (MultipartFile image : images) {
      String fileName = image.getOriginalFilename();
      String fileUrl = s3Service.upload(image, "toy_test"); // 이미지 저장 로직
      PostImage postImage = new PostImage();
      postImage.setPost(post);
      postImage.setFileName(fileName);
      postImage.setFileUrl(fileUrl);
      postImages.add(postImage);
    }
    postImageRepository.saveAll(postImages);

    return post;
  }

  public PostResponseDto getPostDetail(Long postId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException(("Invalid postId")));
    List<PostImage> postImages = postImageRepository.findByPostId(postId);
    List<String> postImgUrls = new ArrayList<>();
    for (PostImage postImage : postImages) {
      postImgUrls.add(postImage.getFileUrl());
    }
    return new PostResponseDto(postId, post.getTitle(),
        post.getContent(), postImgUrls, post.getCreatedDate());

  }

  public Page<Post> getUserPostTitles(String userId, Pageable pageable) {
    Long id = userRepository.findByUserId(userId).orElseThrow().getId();
    Page<Post> postsPage = postRepository.findByUserId(id, pageable);

    return postsPage;
  }

  public Page<Post> searchPostsByKeyword(String keyword, Pageable pageable) {
    return postRepository.findByTitleContaining(keyword, pageable);
  }


}
