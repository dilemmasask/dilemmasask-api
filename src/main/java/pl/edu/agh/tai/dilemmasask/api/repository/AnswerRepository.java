package pl.edu.agh.tai.dilemmasask.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.tai.dilemmasask.api.model.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
