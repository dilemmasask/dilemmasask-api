package pl.edu.agh.tai.dilemmasask.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.tai.dilemmasask.api.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
