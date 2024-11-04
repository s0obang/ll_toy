package likelion.practice.dto;


import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostResponseDto {

  private Long Id;
  private String title;
  private String content;
  private List<String> ImageUrls;
  private LocalDateTime createdDate;


}
