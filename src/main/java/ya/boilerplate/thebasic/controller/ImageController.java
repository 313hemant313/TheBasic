package ya.boilerplate.thebasic.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ya.boilerplate.thebasic.configuration.AmazonClientConfig;
import ya.boilerplate.thebasic.helper.S3Util;


/**
 * Image / File Controller
 */
@RestController
@RequestMapping("/api/images")
public class ImageController {

	private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
	
	public static final String UPLOAD_ROUTE = "/upload";

	@Autowired
	S3Util s3Util;

	@Autowired
	AmazonClientConfig amazonClientConfig;

	@PostMapping(UPLOAD_ROUTE)
	public String uploadFile(@RequestPart(value = "file") MultipartFile file) {
		
		logger.debug("Uploading File!");
		return s3Util.uploadFile(file, amazonClientConfig.getBucketName());
	}

}
