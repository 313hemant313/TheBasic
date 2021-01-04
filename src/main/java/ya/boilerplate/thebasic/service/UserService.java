package ya.boilerplate.thebasic.service;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import ya.boilerplate.thebasic.entity.Role;
import ya.boilerplate.thebasic.entity.RoleName;
import ya.boilerplate.thebasic.entity.User;
import ya.boilerplate.thebasic.entity.UserAuthorization;
import ya.boilerplate.thebasic.helper.BasicUtility;
import ya.boilerplate.thebasic.model.UserDetailView;
import ya.boilerplate.thebasic.model.request.SignUpRequest;
import ya.boilerplate.thebasic.model.response.ApiResponse;
import ya.boilerplate.thebasic.repository.RoleRepository;
import ya.boilerplate.thebasic.repository.UserRepository;

@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	BasicUtility basicUtility;

	@Autowired
	UserAuthService userAuthService;

	@Transactional
	public ResponseEntity<?> registerUser(SignUpRequest signUpRequest) {

		try {
			logger.debug("Register new user with username: " + signUpRequest.getUsername());
			if (userRepository.existsByUsername(signUpRequest.getUsername()))
				return ResponseEntity.ok(new ApiResponse(false, "Username is already taken!"));

			if (userRepository.existsByEmailId(signUpRequest.getEmail()))
				return ResponseEntity.ok(new ApiResponse(false, "Email Address already in use!"));

			User user = new User();
			String[] nameParts = signUpRequest.getName().split("\\s+");
			if (nameParts.length != 2) {
				user.setFirstName(nameParts[0].trim());
				user.setSecondName("Depending of Use case...");
			} else {
				user.setFirstName(nameParts[0].trim());
				user.setSecondName(nameParts[1].trim());
			}

			user.setUsername(signUpRequest.getUsername());
			user.setEmailId(signUpRequest.getEmail());
			user.setAddress(signUpRequest.getAddress());
			user.setDob(signUpRequest.getDob());
			user.setActive('Y');

			if (null != signUpRequest.getImageUrl())
				user.setImageUrl(signUpRequest.getImageUrl());

			if (null != signUpRequest.getMobile()) {
				if (userRepository.existsByMobile(signUpRequest.getMobile()))
					return ResponseEntity.ok(new ApiResponse(false, "Mobile Number already in use!"));

				user.setMobile(signUpRequest.getMobile());
			}

			Optional<Role> userRoleOptional = roleRepository.findByRoleName(RoleName.ROLE_USER);
			if (!userRoleOptional.isPresent())
				return ResponseEntity.ok(new ApiResponse(false, "Error finding proper auth role!"));

			Role userRole = userRoleOptional.get();
			user.setRole(userRole);

			User result = userRepository.save(user);
			UserAuthorization authorization = userAuthService.saveUserAuth(result.getId(), signUpRequest.getPassword());

			return ResponseEntity.ok(userAuthService.createToken(user, authorization));

		} catch (Exception e) {
			return ResponseEntity.ok(new ApiResponse(false, "Failed to register user: " + e.getMessage()));
		}

	}

	public User getUserByUsernameOrEmail(String usernameOrEmail) {

		return userRepository.findByUsernameOrEmailId(usernameOrEmail, usernameOrEmail).orElseThrow(
				() -> new UsernameNotFoundException("User not found with username or email : " + usernameOrEmail));
	}

	public Optional<User> getUserByMobile(String mobile) {

		return userRepository.findByMobile(mobile);
	}

	public Optional<User> getUserById(String userId) {

		return userRepository.findById(Integer.parseInt(userId));
	}

	public ResponseEntity<?> getUserDetail(Integer id) {

		try {
			Optional<User> userOptional = userRepository.findById(id);
			Optional<UserAuthorization> userAuthorization = userAuthService.getUserAuth(id);

			if (!userOptional.isPresent() || !userAuthorization.isPresent())
				return new ResponseEntity<>(new ApiResponse(false, "User not found!"), HttpStatus.BAD_REQUEST);

			// We should not return database entity as it is, Lets convert it to
			// a normal view
			User user = userOptional.get();
			return ResponseEntity.ok(basicUtility.convertUserEntityToView(user));

		} catch (IllegalArgumentException | IllegalAccessException | NullPointerException e) {
			e.printStackTrace();
			return new ResponseEntity<>(new ApiResponse(false, "Failed to get user details, Error: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Transactional
	public ResponseEntity<?> editUser(Integer userId, Map<String, Object> fields_to_update) {
		try {
			logger.debug("Update user with user id: " + userId);
			User user = userRepository.findById(userId).get();
			String key_username = "username";
			String key_emailid = "emailId";

			// Just in case we want user to change user name :D
			if (null != fields_to_update.get(key_username)
					&& !user.getUsername().toString().equals(fields_to_update.get(key_username))
					&& userRepository.existsByUsername(fields_to_update.get(key_username).toString()))
				return ResponseEntity.ok(new ApiResponse(false, "Username is already taken!"));

			// Just in case we want user to change email too :D
			if (null != fields_to_update.get(key_emailid)
					&& !user.getEmailId().toString().equals(fields_to_update.get(key_emailid))
					&& userRepository.existsByEmailId(fields_to_update.get(key_emailid).toString()))
				return ResponseEntity.ok(new ApiResponse(false, "Email Address already in use!"));

			fields_to_update.forEach((k, v) -> {
				Field field = ReflectionUtils.findField(User.class, k);
				field.setAccessible(true);
				if (field.getType().isAssignableFrom(Date.class)) {
					Date date = new Date();
					try {
						date = basicUtility.convertStringToDate(v.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
					ReflectionUtils.setField(field, user, date);
				} else {
					ReflectionUtils.setField(field, user, v);
				}
			});

			User editedUser = userRepository.save(user);

			// We should not return database entity as it is, Lets convert it to
			// a normal view
			UserDetailView userDetailView = basicUtility.convertUserEntityToView(editedUser);

			return ResponseEntity.ok(userDetailView);
		} catch (NullPointerException e) {
			return new ResponseEntity<>("Failed to update user details, Please check the fields passed",
					HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Failed to update user details, Error: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public ResponseEntity<?> editPassword(Integer userId, String password) {
		logger.debug("Update user password with user id: " + userId);
		ApiResponse apiResponse = new ApiResponse(false, "Not able to update your password");
		try {
			userAuthService.updateUserPassword(userId, password);
			apiResponse.setSuccess(true);
			apiResponse.setMessage("Your password has been updated... Yay!");
			return ResponseEntity.ok(apiResponse);

		} catch (Exception e) {
			e.printStackTrace();
			apiResponse.setMessage("Not able to update your password, Error: " + e.getMessage());
		}
		return ResponseEntity.ok(apiResponse);
	}

	public void logoutUser(String refreshToken) {
		userAuthService.unsetRefreshToken(refreshToken);
	}

}
