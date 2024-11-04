package likelion.practice.controller;

import java.io.IOException;
import java.util.List;
import likelion.practice.entity.Image;
import likelion.practice.repository.ImageRepository;
import likelion.practice.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class ImageController {

  @Autowired
  private S3Service s3Service;

  @Autowired
  private ImageRepository imageRepository;

  @PostMapping("/upload")
  public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file)
      throws IOException {
    String fileUrl = s3Service.upload(file, "toy_test");
    return ResponseEntity.ok(fileUrl);
  }

  @GetMapping
  public ResponseEntity<List<Image>> listImages() {
    List<Image> images = imageRepository.findAll();
    return ResponseEntity.ok(images);
  }
}
