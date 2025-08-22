package ru.job4j.grabber.stores;

import ru.job4j.grabber.model.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcStore implements Store {
    private final Connection connection;
    private static final String INSERT_QUERY = """
            INSERT INTO posts (title, link, description, created)
                            VALUES (?, ?, ?, ?)
                            ON CONFLICT (link) DO UPDATE
                            SET title = EXCLUDED.title,
                                description = EXCLUDED.description,
                                created = EXCLUDED.created
                            RETURNING id;""";
    private static final String SELECT_QUERY = "SELECT id, title, link, description, created  FROM posts";
    private static final String SELECT_BY_ID_QUERY = "SELECT id, title, link, description, created FROM posts WHERE id = ?";

    public JdbcStore(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, post.getTitle());
            preparedStatement.setString(2, post.getLink());
            preparedStatement.setString(3, post.getDescription());
            preparedStatement.setTimestamp(4, new Timestamp(post.getTime() * 1000));
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    post.setId((long) resultSet.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

        @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_QUERY)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(map(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return List.copyOf(posts);
    }

    @Override
    public Optional<Post> findById(Long id) {
        Optional<Post> post = Optional.empty();
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_ID_QUERY)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    post = Optional.of(map(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return post;
    }

    private Post map(ResultSet resultSet) throws SQLException {
        Post post = new Post();
        post.setId(resultSet.getLong(1));
        post.setTitle(resultSet.getString(2));
        post.setLink(resultSet.getString(3));
        post.setDescription(resultSet.getString(4));
        Timestamp time = resultSet.getTimestamp(5);
        post.setTime(time.getTime() / 1000);
        return post;
    }
}