package ya.boilerplate.thebasic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ya.boilerplate.thebasic.entity.Role;
import ya.boilerplate.thebasic.entity.RoleName;
import ya.boilerplate.thebasic.model.response.ApiResponse;
import ya.boilerplate.thebasic.repository.RoleRepository;

@Service
public class RoleService {

	@Autowired
	RoleRepository roleRepository;

	public ApiResponse createRole(RoleName roleName) {
		ApiResponse apiReponse = new ApiResponse(false, "Failed to create role");
		try {

			Role userRole = new Role(roleName, 'Y');
			this.roleRepository.save(userRole);
			apiReponse.setSuccess(true);
			apiReponse.setMessage("Yay!");
			return apiReponse;

		} catch (Exception e) {
			e.printStackTrace();
			apiReponse = new ApiResponse(false, "Failed to create role: " + e.getMessage());
			return apiReponse;
		}

	}

}
