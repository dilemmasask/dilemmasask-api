package pl.edu.agh.tai.dilemmasask.api.DTO;

import java.util.ArrayList;
import java.util.List;

public class PostsListDTO {
    List<PostDTO> posts = new ArrayList<>();

    public List<PostDTO> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDTO> posts) {
        this.posts = posts;
    }

    public void addPost(PostDTO postDTO){
        posts.add(postDTO);
    }

    @Override
    public String toString() {
        return "PostsListDTO{" +
                "posts=" + posts +
                '}';
    }
}
