package likelion.practice.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class S3Service {

  private final AmazonS3 s3Client;

  @Value("${aws.s3.bucket}")
  private String bucketName;

  public S3Service(@Value("${aws.credentials.access-key}") String accessKey,
      @Value("${aws.credentials.secret-key}") String secretKey,
      @Value("${aws.region}") String region) {
    BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
    this.s3Client = AmazonS3ClientBuilder.standard()
        .withRegion(Regions.fromName(region))
        .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
        .build();
  }

  // dirName은 S3의 어떤 폴더인지 같이 넣어주면 됨 예를들면 profileImage.. shopImage.....etc
  public String upload(MultipartFile multipartFile, String dirName) throws IOException {
    String originalFileName = multipartFile.getOriginalFilename();
    String uuid = UUID.randomUUID().toString();
    String uniqueFileName = uuid + "_" + originalFileName.replaceAll("\\s", "_");

    String fileName = dirName + "/" + uniqueFileName;
    log.info("fileName: " + fileName);

    File uploadFile = convert(multipartFile);
    String uploadImageUrl = putS3(uploadFile, fileName);
    removeNewFile(uploadFile);
    return uploadImageUrl;
  }

  private File convert(MultipartFile file) throws IOException {
    File convertFile = new File(file.getOriginalFilename());
    if (convertFile.createNewFile()) {
      try (FileOutputStream fos = new FileOutputStream(convertFile)) {
        fos.write(file.getBytes());
      }
      return convertFile;
    }
    throw new IllegalArgumentException("파일 변환에 실패했습니다: " + file.getOriginalFilename());
  }

  private String putS3(File uploadFile, String fileName) {
    s3Client.putObject(
        new PutObjectRequest(bucketName, fileName, uploadFile)
            .withCannedAcl(CannedAccessControlList.PublicRead));
    return s3Client.getUrl(bucketName, fileName).toString();
  }

  // 전환할 때 임시파일이 생성돼서 삭제해야함
  private void removeNewFile(File targetFile) {
    if (targetFile.delete()) {
      log.info("파일이 삭제되었습니다.");
    } else {
      log.info("파일이 삭제되지 못했습니다.");
    }
  }

  public void deleteFile(String fileName) {
    try {
      // URL 디코딩을 통해 원래의 파일 이름을 가져옴
      String decodedFileName = URLDecoder.decode(fileName, "UTF-8");
      log.info("Deleting file from S3: " + decodedFileName);
      s3Client.deleteObject(bucketName, decodedFileName);
    } catch (UnsupportedEncodingException e) {
      log.error("Error while decoding the file name: {}", e.getMessage());
    }
  }

  public String updateFile(MultipartFile newFile, String oldFileName, String dirName)
      throws IOException {
    // 기존 파일 삭제
    log.info("S3 oldFileName: " + oldFileName);
    deleteFile(oldFileName);
    // 새 파일 업로드
    return upload(newFile, dirName);
  }

}
