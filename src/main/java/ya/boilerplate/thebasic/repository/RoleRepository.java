package ya.boilerplate.thebasic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ya.boilerplate.thebasic.entity.Role;
import ya.boilerplate.thebasic.entity.RoleName;


@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
	
	@Query
	Optional<Role> findByRoleName(RoleName roleName);
}
