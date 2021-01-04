package ya.boilerplate.thebasic.controller;

import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ya.boilerplate.thebasic.model.request.LoginRequest;
import ya.boilerplate.thebasic.model.request.SignUpRequest;
import ya.boilerplate.thebasic.model.response.ApiResponse;
import ya.boilerplate.thebasic.model.response.JwtAuthenticationResponse;
import ya.boilerplate.thebasic.service.UserAuthService;
import ya.boilerplate.thebasic.service.UserService;


/**
 * Auth Controller
 */
@RestController
@RequestMapping("/api/account")
public class AuthController {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	public static final String LOGIN_ROUTE = "/login";
	public static final String REGISTER_ROUTE = "/register";
	public static final String TOKEN_REFRESH_ROUTE = "/token/refresh";
	public static final String LOGOUT_ROUTE = "/logout";


	@Autowired
	UserService userService;

	@Autowired
	UserAuthService userAuthService;

	@PostMapping(LOGIN_ROUTE)
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		logger.info("AuthenticateUser with Username Or Email: " + loginRequest.getUsernameOrEmail());

		JwtAuthenticationResponse response = userAuthService.createToken(loginRequest.getUsernameOrEmail(),
				loginRequest.getPassword());

		logger.info("JWT Generated for Username Or Email: " + loginRequest.getUsernameOrEmail());

		return ResponseEntity.ok(response);
	}

	@PostMapping(REGISTER_ROUTE)
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

		logger.info("Register User with Email: " + signUpRequest.getEmail());

		try {
			return userService.registerUser(signUpRequest);
		} catch (Exception e) {
			return ResponseEntity.ok(new ApiResponse(false, "Failed to register user: " + e.getMessage()));
		}

	}

	@PostMapping(TOKEN_REFRESH_ROUTE)
	public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> payload) {
		String refreshToken = payload.get("refresh_token");
		Optional<JwtAuthenticationResponse> response = userAuthService.refreshAccessToken(refreshToken);
		if (response.isPresent()) {
			return ResponseEntity.ok(response);
		} else {
			return new ResponseEntity<>("Please login first :D", HttpStatus.UNAUTHORIZED);
		}
	}

	@DeleteMapping(LOGOUT_ROUTE)
	public ResponseEntity<?> logoutUser(@RequestBody Map<String, String> payload) {
		try {
			String refreshToken = payload.get("refresh_token");
			userService.logoutUser(refreshToken);
			return ResponseEntity.ok(new ApiResponse(true, "Yay!"));
		} catch (Exception e) {
			return ResponseEntity.ok(new ApiResponse(false, "Failed to logout" + e.getMessage()));
		}
	}

}
