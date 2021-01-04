package ya.boilerplate.thebasic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ya.boilerplate.thebasic.entity.UserAuthorization;

@Repository
public interface UserAuthorizationRepository extends JpaRepository<UserAuthorization, Integer> {

	@Query
	Optional<UserAuthorization> findById(int id);

	@Query
	Optional<UserAuthorization> findByRefreshToken(String refreshToken);

}
