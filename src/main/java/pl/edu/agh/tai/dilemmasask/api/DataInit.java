package pl.edu.agh.tai.dilemmasask.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import pl.edu.agh.tai.dilemmasask.api.model.Answer;
import pl.edu.agh.tai.dilemmasask.api.model.Poll;
import pl.edu.agh.tai.dilemmasask.api.model.Post;
import pl.edu.agh.tai.dilemmasask.api.model.User;
import pl.edu.agh.tai.dilemmasask.api.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataInit implements ApplicationRunner {
    @Autowired
    private PostRepository postRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        postRepository.save(new Post(
                LocalDateTime.of(2018, 3, 5, 12, 0, 12),
                new User("dawid"),
                new Poll("What it is?",
                        Arrays.asList(new Answer("123"), new Answer("great"), new Answer("perfect")))));

        postRepository.save(new Post(
                LocalDateTime.of(2018, 3, 10, 10, 2, 44),
                new User("michał"),
                new Poll("What should I choose?",
                        Arrays.asList(new Answer("1"), new Answer("2"), new Answer("3")))));

    }
}
