package likelion.practice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 아이디 (Primary Key)

    @Column(name = "user_id", length = 20, nullable = false, unique = true)
    private String userId;  // 아이디

    @Column(name = "password", length = 255, nullable = false)
    private String password;  // 비밀번호

    @Column(name = "name", length = 20, nullable = false)
    private String name;  // 이름

    @Column(name = "profile_image", length = 1000)
    private String profileImage;  // 프로필 사진
}

