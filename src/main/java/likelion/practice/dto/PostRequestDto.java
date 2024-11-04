package likelion.practice.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PostRequestDto {

  private String title;
  private String content;
  private List<MultipartFile> images;
}
