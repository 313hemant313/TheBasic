package ya.boilerplate.thebasic.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ya.boilerplate.thebasic.entity.User;
import ya.boilerplate.thebasic.entity.UserAuthorization;
import ya.boilerplate.thebasic.repository.UserAuthorizationRepository;
import ya.boilerplate.thebasic.repository.UserRepository;


@Service
public class OurUserDetailsService implements UserDetailsService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserAuthorizationRepository userAuthorizationRepository;

	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

		User user = userRepository.findByUsernameOrEmailId(usernameOrEmail, usernameOrEmail).orElseThrow(
				() -> new UsernameNotFoundException("User not found with username or email : " + usernameOrEmail));

		UserAuthorization userAuthorization = userAuthorizationRepository.findById(user.getId()).orElseThrow(
				() -> new UsernameNotFoundException("UserAuthorization not found with user id : " + user.getId()));
		
		return OurUserDetails.create(user, userAuthorization);
	}

	@Transactional
	public UserDetails loadUserById(Long id) {


		User user = userRepository.findById(Integer.parseInt(id.toString()))
				.orElseThrow(() -> new UsernameNotFoundException("User not found with id : " + id));

		UserAuthorization userAuthorization = userAuthorizationRepository.findById(user.getId()).orElseThrow(
				() -> new UsernameNotFoundException("UserAuthorization not found with user id : " + user.getId()));


		return OurUserDetails.create(user, userAuthorization);
	}
}