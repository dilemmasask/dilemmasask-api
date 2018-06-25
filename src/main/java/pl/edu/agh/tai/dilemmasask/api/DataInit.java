package pl.edu.agh.tai.dilemmasask.api;

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
                new Poll("Xbox One vs PS4?",
                        Arrays.asList(new Answer("Xbox One"), new Answer("PS4"), new Answer("Show answers")))));

        Post post = new Post(
                LocalDateTime.of(2018, 3, 10, 10, 2, 44),
                new User("michał"),
                new Poll("What should I choose?",
                        Arrays.asList(new Answer("1"), new Answer("2"), new Answer("3"))));
        post.addTag(new Tag("simple"));
        postRepository.save(post);


    }
}
