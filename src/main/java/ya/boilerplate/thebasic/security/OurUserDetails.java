package ya.boilerplate.thebasic.security;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import ya.boilerplate.thebasic.entity.RoleName;
import ya.boilerplate.thebasic.entity.User;
import ya.boilerplate.thebasic.entity.UserAuthorization;

public class OurUserDetails implements UserDetails {

	private Integer id;
	private String email;
	private String password;

	private Collection<? extends GrantedAuthority> authorities;
	private Map<String, Object> attributes;

	public OurUserDetails(Integer id, String email, String password,
			Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.authorities = authorities;
	}

	public static OurUserDetails create(User user, UserAuthorization userAuthorization) {

		List<GrantedAuthority> authorities = Collections
				.singletonList(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()));

		return new OurUserDetails(user.getId(), user.getEmailId(), userAuthorization.getAuthKey(), authorities);
	}

	public static OurUserDetails create(User user, UserAuthorization userAuthorities, Map<String, Object> attributes) {
		OurUserDetails ourUserDetails = OurUserDetails.create(user, userAuthorities);
		ourUserDetails.setAttributes(attributes);
		return ourUserDetails;
	}

	public Integer getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

}
