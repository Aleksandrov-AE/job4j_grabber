package ru.job4j;

import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.service.Config;
import ru.job4j.grabber.service.SchedulerManager;
import ru.job4j.grabber.service.SuperJobGrab;
import ru.job4j.grabber.stores.JdbcStore;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static ru.job4j.grabber.service.Config.LOG;

public class Main {
    public static void main(String[] args) {
        var config = new Config();
        config.load("application.properties");
        try (Connection connection = DriverManager.getConnection(
                config.get("db.url"),
                config.get("db.username"),
                config.get("db.password")
        ); var scheduler = new SchedulerManager()) {
            var store = new JdbcStore(connection);
            var post = new Post();
            post.setTitle("Super Java Job");
            post.setLink("www.google123.com");
            post.setDescription("Java Programming");
            store.save(post);
            scheduler.init();
            scheduler.load(
                    Integer.parseInt(config.get("rabbit.interval")),
                    SuperJobGrab.class,
                    store);
            Thread.sleep(100000);
        } catch (SQLException e) {
            LOG.error("When create a connection", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}