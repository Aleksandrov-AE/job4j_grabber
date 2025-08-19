package ru.job4j.grabber.model;

import java.util.Objects;

public class Post {
    private Long id;             // идентификатор вакансии (PK в БД)
    private String title;        // название
    private String link;         // ссылка
    private String description;  // описание
    private Long time = System.currentTimeMillis();          // время создания в мс

    public Post() {
    }

    public Post(String title, String link, String description) {
        this.title = title;
        this.link = link;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Post)) {
            return false;
        }
        Post post = (Post) o;
        return Objects.equals(link, post.link); // уникальность по ссылке
    }

    @Override
    public int hashCode() {
        return Objects.hash(link);
    }

    @Override
    public String toString() {
        return "Post{"
                + "id=" + id
                + ", title='" + title + '\''
                + ", link='" + link + '\''
                + ", description='" + description + '\''
                + ", time=" + time + '}';
    }
}
