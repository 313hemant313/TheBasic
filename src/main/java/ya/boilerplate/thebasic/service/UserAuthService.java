package ya.boilerplate.thebasic.service;

import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ya.boilerplate.thebasic.entity.User;
import ya.boilerplate.thebasic.entity.UserAuthorization;
import ya.boilerplate.thebasic.model.response.JwtAuthenticationResponse;
import ya.boilerplate.thebasic.repository.UserAuthorizationRepository;
import ya.boilerplate.thebasic.security.TokenProvider;
import ya.boilerplate.thebasic.security.OurUserDetails;

@Service
public class UserAuthService {

	private static final Logger logger = LoggerFactory.getLogger(UserAuthService.class);

	@Autowired
	UserAuthorizationRepository userAuthorizationRepository;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	TokenProvider tokenProvider;

	@Autowired
	PasswordEncoder passwordEncoder;

	public UserAuthorization saveUserAuth(Integer userId, String password) {
		logger.debug("Saving user auth with user id: " + userId);
		UserAuthorization authorization = new UserAuthorization();
		authorization.setId(userId);
		authorization.setAuthKey(passwordEncoder.encode(password));
		return userAuthorizationRepository.save(authorization);

	}

	public JwtAuthenticationResponse createToken(String usernameOrEmail, String password) {
		logger.debug("Creating token for user: " + usernameOrEmail);
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(usernameOrEmail, password));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		OurUserDetails ourUserDetails = (OurUserDetails) authentication.getPrincipal();
		String jwt = tokenProvider.createToken(ourUserDetails.getId());
		String refreshToken = createRefreshToken(ourUserDetails.getId());
		return new JwtAuthenticationResponse(jwt, refreshToken);
	}

	public JwtAuthenticationResponse createToken(Object userObj, Object userAuthorization) {
		User user = (User) userObj;
		logger.debug("Creating token for user: " + user.getUsername());
		String jwt = tokenProvider.createToken(user, (UserAuthorization) userAuthorization);
		String refreshToken = createRefreshToken(user.getId());
		return new JwtAuthenticationResponse(jwt, refreshToken);
	}

	private String createRefreshToken(Integer userId) {
		logger.debug("Creating refresh token for user id: " + userId);
		String token = RandomStringUtils.randomAlphanumeric(128);
		getUserAuth(userId).map(userAuthorization -> {
			userAuthorization.setRefreshToken(token);
			return userAuthorizationRepository.save(userAuthorization);
		});
		return token;

	}

	public Optional<JwtAuthenticationResponse> refreshAccessToken(String refreshToken) {
		return userAuthorizationRepository.findByRefreshToken(refreshToken).map(
				userAuthorization -> new JwtAuthenticationResponse(tokenProvider.createToken(userAuthorization.getId()),
						null));
	}

	public Optional<UserAuthorization> updateUserPassword(Integer userId, String password) {
		logger.debug("Update user password for user id: " + userId);
		return getUserAuth(userId).map(userAuthorization -> {
			userAuthorization.setAuthKey(passwordEncoder.encode(password));
			return userAuthorizationRepository.save(userAuthorization);
		});
	}

	public Optional<UserAuthorization> getUserAuth(Integer id) {
		return userAuthorizationRepository.findById(id);
	}

	// Maybe not the best way to find UserAuthorization, should have taken user
	// id from access token.. anyways
	public Optional<UserAuthorization> unsetRefreshToken(String refreshToken) {
		return userAuthorizationRepository.findByRefreshToken(refreshToken).map(userAuthorization -> {
			userAuthorization.setRefreshToken(null);
			return userAuthorizationRepository.save(userAuthorization);
		});
	}

}
