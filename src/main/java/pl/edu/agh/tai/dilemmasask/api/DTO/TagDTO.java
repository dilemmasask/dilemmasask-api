package pl.edu.agh.tai.dilemmasask.api.DTO;

public class TagDTO {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TagDTO{" +
                "name='" + name + '\'' +
                '}';
    }
}
