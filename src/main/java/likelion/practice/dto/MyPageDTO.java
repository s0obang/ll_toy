package likelion.practice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MyPageDTO {

  private String userId;
  private String password;
  private String name;
  private String email;
  private String phone;
  private String profileImgUrl;

}
