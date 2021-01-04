package ya.boilerplate.thebasic.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

@Service
public class S3Util {

	private static final Log log = LogFactory.getLog(S3Util.class);

	@Autowired
	AmazonS3 amazonS3Client;

	public String uploadFile(MultipartFile multipartFile, String bucketName) {

		String fileUrl = "";
		String bucketFolder = "hemant_demo/ This is a dummy bucket not actual...";
		try {
			File file = convertMultiPartFileToFile(multipartFile);
			String fileName = bucketFolder + generateFileName(multipartFile);
			fileUrl = uploadFileTos3bucket(fileName, file, bucketName);
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return fileUrl;
	}

	private File convertMultiPartFileToFile(MultipartFile file) {
		File convertedFile = new File(file.getOriginalFilename());
		try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
			fos.write(file.getBytes());
		} catch (IOException e) {
			log.error("Error converting multipartFile to file", e);
		}
		return convertedFile;
	}

	private String generateFileName(MultipartFile multiPart) {
		return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
	}

	private String uploadFileTos3bucket(String fileName, File file, String bucketName) {
		amazonS3Client.putObject(
				new PutObjectRequest(bucketName, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));

		return String.valueOf(amazonS3Client.getUrl(bucketName, fileName));
	}

	public void uploadMultipleFiles(List<MultipartFile> files, String bucketName) {
		if (files != null) {
			files.forEach(multipartFile -> {
				File file = convertMultiPartFileToFile(multipartFile);
				String uniqueFileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
				uploadFileTos3bucket(uniqueFileName, file, bucketName);
			});
		}
	}

	public S3Object downloadFileFromS3bucket(String fileName, String bucketName) {
		S3Object object = amazonS3Client.getObject(bucketName, fileName);
		return object;

	}

	private void deleteFileFromS3bucket(String fileName, String bucketName) {
		amazonS3Client.deleteObject(bucketName, fileName);
	}

}
