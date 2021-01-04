package ya.boilerplate.thebasic;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import ya.boilerplate.thebasic.configuration.WebAppProps;
import ya.boilerplate.thebasic.entity.RoleName;
import ya.boilerplate.thebasic.service.RoleService;

@SpringBootApplication
@EnableConfigurationProperties(WebAppProps.class)
public class TheBasicApplication {

	// Just for initial setup (One time), to create a role in role table.
	@Autowired
	RoleService roleService;

	@PostConstruct
	void init() {
		roleService.createRole(RoleName.ROLE_USER);
	}

	public static void main(String[] args) {
		SpringApplication.run(TheBasicApplication.class, args);
	}

}
