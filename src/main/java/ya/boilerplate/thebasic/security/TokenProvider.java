package ya.boilerplate.thebasic.security;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import ya.boilerplate.thebasic.configuration.WebAppProps;
import ya.boilerplate.thebasic.entity.User;
import ya.boilerplate.thebasic.entity.UserAuthorization;

@Service
public class TokenProvider {

	private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

	private WebAppProps webAppProperties;

	public TokenProvider(WebAppProps webAppProperties) {
		this.webAppProperties = webAppProperties;
	}

	public String createToken(User user, UserAuthorization userAuthorization) {

		OurUserDetails ourUserDetails = OurUserDetails.create(user, userAuthorization);

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + webAppProperties.getAuth().getTokenExpirationMsec());

		return Jwts.builder().setSubject(Long.toString(ourUserDetails.getId())).setIssuedAt(new Date())
				.setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, webAppProperties.getAuth().getTokenSecret()).compact();

	}

	public String createToken(Integer userId) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + webAppProperties.getAuth().getTokenExpirationMsec());

		return Jwts.builder().setSubject(Long.toString(userId)).setIssuedAt(new Date()).setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, webAppProperties.getAuth().getTokenSecret()).compact();
	}

	public Long getUserIdFromToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(webAppProperties.getAuth().getTokenSecret()).parseClaimsJws(token)
				.getBody();
		
		return Long.parseLong(claims.getSubject());
	}

	public boolean validateToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(webAppProperties.getAuth().getTokenSecret()).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException ex) {
			logger.error("Invalid JWT signature");
		} catch (MalformedJwtException ex) {
			logger.error("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			logger.error("Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			logger.error("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			logger.error("JWT claims string is empty.");
		}
		return false;
	}

}
