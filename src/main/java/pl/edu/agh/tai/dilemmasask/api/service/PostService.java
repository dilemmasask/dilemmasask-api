package pl.edu.agh.tai.dilemmasask.api.service;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.edu.agh.tai.dilemmasask.api.DTO.NotVotedPostDTO;
import pl.edu.agh.tai.dilemmasask.api.DTO.PostDTO;
import pl.edu.agh.tai.dilemmasask.api.DTO.PostsListDTO;
import pl.edu.agh.tai.dilemmasask.api.DTO.VotedPostDTO;
import pl.edu.agh.tai.dilemmasask.api.model.Poll;
import pl.edu.agh.tai.dilemmasask.api.model.Post;
import pl.edu.agh.tai.dilemmasask.api.model.User;
import pl.edu.agh.tai.dilemmasask.api.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PostService {

    private PostRepository postRepository;
    private static final ModelMapper modelMapper = new ModelMapper();

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public ResponseEntity getPosts(User user, Integer pageNumber, Integer pollsPerPage, String sortBy, LocalDateTime from, LocalDateTime to, String tag) {

        Page<Post> posts;
        switch (sortBy){
            case "new":
                posts = getSortedPosts(pageNumber, pollsPerPage, from, to, tag, "dateTime");
                break;
            case "top":
                posts = getSortedPosts(pageNumber, pollsPerPage, from, to, tag, "totalVotes");
                break;
            case "random":
            default:
                posts = getRandomPosts(pageNumber, pollsPerPage, tag);
        }
        PostsListDTO postsListDTO = new PostsListDTO();

        posts.getContent().forEach(post -> postsListDTO.addPost(getProperPost(post, user)));

        return ResponseEntity.status(HttpStatus.OK).body(postsListDTO);
    }

    private Page<Post> getSortedPosts(Integer pageNumber, Integer pollsPerPage, LocalDateTime dateFrom, LocalDateTime dateTo, String tag, String sortBy) {
        Pageable pageable = PageRequest.of(pageNumber-1, pollsPerPage, new Sort(Sort.Direction.DESC, sortBy));
        if(tag != null){
            return postRepository.findByTagsNameAndDateTimeBetween(tag, dateFrom, dateTo, pageable);
        } else {
            return postRepository.findByDateTimeBetween(dateFrom, dateTo, pageable);
        }
    }

    private Page<Post> getRandomPosts(Integer pageNumber, Integer pollsPerPage, String tag) {
        Pageable pageable = PageRequest.of(pageNumber-1, pollsPerPage);
        if(tag != null) {
            return postRepository.findByTagsName(tag, pageable);
        } else {
            return postRepository.findAll(pageable);
        }
    }

    public ResponseEntity getPost(User user, Long postId) {
        Optional<Post> op = postRepository.findById(postId);
        Post post = op.get();
        if(post == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(getProperPost(post, user));
    }

    private PostDTO getProperPost(Post post, User user) {
        if(user != null && userVotedForPost(post, user)){
            VotedPostDTO votedPostDTO = modelMapper.map(post, VotedPostDTO.class);
            votedPostDTO.setVotedAnswerId(getVotedAnswerId(post, user));
            return votedPostDTO;
        } else {
            return modelMapper.map(post, NotVotedPostDTO.class);
        }
    }

    public ResponseEntity addNewPost(User user, Poll poll) {
        Post post = new Post(user, poll);
        postRepository.save(post);
        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.map(post, NotVotedPostDTO.class));
    }

    public ResponseEntity deletePost(Long postId, User user) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(!userIsAllowedToDeletePost(post, user)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        postRepository.deleteById(postId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private boolean userIsAllowedToDeletePost(Post post, User user) {
        return post.getAuthor().getId().equals(user.getId());
    }

    public ResponseEntity vote(User user, Long postId, Long answerId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null || !postContainsAnswer(post, answerId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if(!userVotedForPost(post, user)){
            post.voteAnswer(user, answerId);
            postRepository.save(post);
        }

        VotedPostDTO votedPostDTO = modelMapper.map(post, VotedPostDTO.class);
        votedPostDTO.setVotedAnswerId(getVotedAnswerId(post, user));
        return ResponseEntity.status(HttpStatus.OK).body(votedPostDTO);
    }

    private boolean postContainsAnswer(Post post, Long answerId) {
        return post.getPoll().getAnswers().stream().anyMatch(a -> a.getId().equals(answerId));
    }

    private static boolean userVotedForPost(Post post, User user) {
        return post.getPoll().getAnswers().stream().anyMatch(a -> a.getVoters().stream().anyMatch(v->v.getId().equals(user.getId())));
    }

    private Long getVotedAnswerId(Post post, User user) {
        return post.getPoll().getAnswers()
                .stream()
                .filter(answer -> answer.getVoters()
                        .stream()
                        .anyMatch(v-> v.getId().equals(user.getId())))
                .findFirst().get().getId();
    }

}
