package ya.boilerplate.thebasic.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ya.boilerplate.thebasic.helper.BasicUtility;
import ya.boilerplate.thebasic.service.UserService;

/**
 * User Controller
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	public static final String GET_USER_ROUTE = "/get";
	public static final String UPDATE_PASSWORD_ROUTE = "/update/password";
	public static final String UPDATE_USER_DETAILS_ROUTE = "/update/details";

	@Autowired
	UserService userService;

	@Autowired
	BasicUtility basicUtility;

	@GetMapping(GET_USER_ROUTE)
	public ResponseEntity<?> getUser() {
		Integer userId = basicUtility.getUserIdFromToken();
		logger.debug("Get details of User with user id: " + userId);
		return userService.getUserDetail(userId);
	}

	@PutMapping(UPDATE_USER_DETAILS_ROUTE)
	public ResponseEntity<?> editUserDetails(@RequestBody Map<String, Object> payload) {
		Integer userId = basicUtility.getUserIdFromToken();
		logger.debug("Update details of User with user id: " + userId);
		return userService.editUser(userId, payload);
	}

	// Many use case needs different implementation of password update, So thats
	// why a separate end-point is exposed (Like some web sites only allow to
	// reset password and send it on email there registered email :D)
	@PutMapping(UPDATE_PASSWORD_ROUTE)
	public ResponseEntity<?> editPassword(@RequestBody Map<String, String> payload) {
		Integer userId = basicUtility.getUserIdFromToken();
		logger.debug("Update password of User with user id: " + userId);
		String password = payload.get("password");

		return userService.editPassword(userId, password);
	}

}
