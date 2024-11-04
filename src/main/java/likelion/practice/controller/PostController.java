package likelion.practice.controller;


import likelion.practice.dto.PostRequestDto;
import likelion.practice.dto.PostResponseDto;
import likelion.practice.entity.Post;
import likelion.practice.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/post")
public class PostController {

  private final PostService postService;

  @PostMapping
  public ResponseEntity<?> createPost(@AuthenticationPrincipal UserDetails userDetails,
      @ModelAttribute
      PostRequestDto postRequestDto) throws Exception {
    String userId = userDetails.getUsername();
    postService.createPost(userId, postRequestDto.getTitle(), postRequestDto.getContent(),
        postRequestDto.getImages());

    return ResponseEntity.ok(HttpStatus.OK);
  }

  @GetMapping("/detail")
  public ResponseEntity<PostResponseDto> getDetailPost(@RequestParam Long postId) {
    PostResponseDto postResponseDto = postService.getPostDetail(postId);
    return ResponseEntity.ok(postResponseDto);
  }

  @GetMapping("/all")
  public ResponseEntity<Page<Post>> getUserPostTitles(
      @AuthenticationPrincipal UserDetails userDetails,
      @PageableDefault(sort = {"id"}) Pageable pageable
  ) {
    String userId = userDetails.getUsername();
    Page<Post> posts = postService.getUserPostTitles(userId, pageable);
    return ResponseEntity.ok(posts);
  }

  @GetMapping("/posts/search")
  public ResponseEntity<Page<Post>> searchPosts(
      @RequestParam("keyword") String keyword,
      @PageableDefault(sort = {"id"}) Pageable pageable) {
    Page<Post> postsPage = postService.searchPostsByKeyword(keyword, pageable);
    return ResponseEntity.ok(postsPage);
  }

}
