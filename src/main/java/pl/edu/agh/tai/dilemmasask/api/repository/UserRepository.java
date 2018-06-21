package pl.edu.agh.tai.dilemmasask.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.tai.dilemmasask.api.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByPrincipalId(String principalId);
}
