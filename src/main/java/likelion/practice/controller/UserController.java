//UserController
package likelion.practice.controller;

import java.util.HashMap;
import java.util.Map;
import likelion.practice.dto.MyPageDTO;
import likelion.practice.dto.UserDTO;
import likelion.practice.entity.User;
import likelion.practice.repository.UserRepository;
import likelion.practice.security.JwtTokenProvider;
import likelion.practice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;

  public UserController(UserService userService, JwtTokenProvider jwtTokenProvider,
      AuthenticationManager authenticationManager, UserRepository userRepository) {
    this.userService = userService;
    this.jwtTokenProvider = jwtTokenProvider;
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
  }

  // 회원가입 API
  @PostMapping("/register")
  public ResponseEntity<User> registerUser(@RequestBody UserDTO userDTO) {
    User registeredUser = userService.registerUser(userDTO);
    return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
  }

  // 로그인 API
  @PostMapping("/login")
  public ResponseEntity<Map<String, String>> loginUser(@RequestBody UserDTO userDTO) {
    try {
      // 사용자 인증 시도
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(userDTO.getUserId(), userDTO.getPassword())
      );

      // 인증 성공 시 JWT 토큰 생성
      String jwtToken = jwtTokenProvider.createToken(authentication.getName());

      // 응답으로 토큰을 Map 형태로 반환
      Map<String, String> response = new HashMap<>();
      response.put("token", jwtToken);

      return ResponseEntity.ok(response);

    } catch (AuthenticationException ex) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password.");
    }
  }

  @PostMapping("/isDuplicate")
  public ResponseEntity<Boolean> isDuplicateUser(@RequestParam String user_id) {
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userService.idDuplicate(user_id));
  }

  @GetMapping("/mypage")
  public ResponseEntity<MyPageDTO> getMyPage(@AuthenticationPrincipal UserDetails userDetails) {
    String userId = userDetails.getUsername();
    MyPageDTO myPageDTO = userService.getMyPage(userId);
    if (myPageDTO == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } else {
      return ResponseEntity.ok(myPageDTO);
    }
  }
}
