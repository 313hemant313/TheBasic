package ya.boilerplate.thebasic.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "webapp")
public class WebAppProps {

	private final Auth auth = new Auth();

	public static class Auth {
		private String tokenSecret;
		private long tokenExpirationMsec;

		public String getTokenSecret() {
			return tokenSecret;
		}

		public void setTokenSecret(String tokenSecret) {
			this.tokenSecret = tokenSecret;
		}

		public long getTokenExpirationMsec() {
			return tokenExpirationMsec;
		}

		public void setTokenExpirationMsec(long tokenExpirationMsec) {
			this.tokenExpirationMsec = tokenExpirationMsec;
		}
	}

	public Auth getAuth() {
		return auth;
	}

}
