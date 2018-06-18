package pl.edu.agh.tai.dilemmasask.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.agh.tai.dilemmasask.api.model.Post;
import pl.edu.agh.tai.dilemmasask.api.model.Tag;

import java.time.LocalDateTime;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByTagsName(String tag, Pageable pageable);
    Page<Post> findByTagsNameAndDateTimeBetween(String tag, LocalDateTime from, LocalDateTime to, Pageable pageable);
}
