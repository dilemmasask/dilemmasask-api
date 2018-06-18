package pl.edu.agh.tai.dilemmasask.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.tai.dilemmasask.api.model.Tag;

@Repository
public interface TagsRepository extends JpaRepository<Tag, Long> {

}
