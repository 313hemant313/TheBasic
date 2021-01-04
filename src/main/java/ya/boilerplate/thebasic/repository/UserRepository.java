package ya.boilerplate.thebasic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ya.boilerplate.thebasic.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	@Query
	Optional<User> findByEmailId(String emailId);

	@Query
	Optional<User> findByUsernameOrEmailId(String username, String emailId);

	@Query
	Optional<User> findByUsernameOrEmailIdOrMobile(String username, String emailId, String mobile);

	@Query
	Optional<User> findByMobile(String mobile);

	Boolean existsByMobile(String mobile);

	Boolean existsByUsername(String username);

	Boolean existsByEmailId(String emailId);

}
