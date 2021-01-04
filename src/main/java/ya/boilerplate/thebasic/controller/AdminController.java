package ya.boilerplate.thebasic.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Admin Controller
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	/* Just a dummy end-point in case someone need to check */
	@GetMapping("/dummyapi")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> dummyAdminFunction() {

		return null;
	}

}
