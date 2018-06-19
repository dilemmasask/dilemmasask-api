package pl.edu.agh.tai.dilemmasask.api;

import javafx.geometry.Pos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import pl.edu.agh.tai.dilemmasask.api.model.*;
import pl.edu.agh.tai.dilemmasask.api.repository.PostRepository;
import pl.edu.agh.tai.dilemmasask.api.repository.TagsRepository;
import pl.edu.agh.tai.dilemmasask.api.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInit implements ApplicationRunner {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TagsRepository tagsRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        postRepository.save(new Post(
                LocalDateTime.of(2018, 3, 5, 12, 0, 12),
                new User("dawid"),
                new Poll("What it is?",
                        Arrays.asList(new Answer("123"), new Answer("great"), new Answer("perfect")))));
        Answer answer = new Answer("1");
        User newUser = new User("abcd");
        userRepository.save(newUser);

        Post post = new Post(
                LocalDateTime.of(2018, 3, 10, 10, 2, 44),
                new User("michał"),
                new Poll("What should I choose?",
                        Arrays.asList(answer, new Answer("2"), new Answer("3"))));
        answer.vote(newUser);
        postRepository.save(post);


    }
}
