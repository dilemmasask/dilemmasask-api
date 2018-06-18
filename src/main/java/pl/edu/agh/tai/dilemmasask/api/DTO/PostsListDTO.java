package pl.edu.agh.tai.dilemmasask.api.DTO;

import java.util.LinkedList;
import java.util.List;

public class PostsListDTO {
    List<PostDTO> posts = new LinkedList<>();

    public List<PostDTO> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDTO> posts) {
        this.posts = posts;
    }

    public void addPost(PostDTO postDTO){
        posts.add(postDTO);
    }
}
