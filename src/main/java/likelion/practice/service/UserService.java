//UserService
package likelion.practice.service;

import likelion.practice.dto.MyPageDTO;
import likelion.practice.dto.UserDTO;
import likelion.practice.entity.User;
import likelion.practice.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;

  }


  @Override
  public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

    return org.springframework.security.core.userdetails.User
        .withUsername(user.getUserId())
        .password(user.getPassword())
        .authorities("USER")
        .build();
  }

  // 회원가입 기능
  public User registerUser(UserDTO userDTO) {
    if (userRepository.existsByUserId(userDTO.getUserId())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID already exists.");
    }

    User user = new User();
    user.setUserId(userDTO.getUserId());
    user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
    user.setName(userDTO.getName());
    user.setPhone(userDTO.getPhone());
    user.setEmail(userDTO.getEmail());

    return userRepository.save(user);
  }

  // 로그인 기능
  public User loginUser(String userId, String password) {
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password.");
    }

    return user;
  }

  public boolean idDuplicate(String userId) {
    return userRepository.existsByUserId(userId);
  }


  public MyPageDTO getMyPage(String userId) {

    User user = userRepository.findByUserId(userId).orElseThrow();

    return MyPageDTO.builder()
        .userId(user.getUserId())
        .email(user.getEmail())
        .phone(user.getPhone())
        .name(user.getName())
        .profileImgUrl(user.getProfileImageUrl())
        .build();
  }
  
}
