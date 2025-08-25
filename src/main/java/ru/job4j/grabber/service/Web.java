package ru.job4j.grabber.service;

import io.javalin.Javalin;
import ru.job4j.grabber.stores.Store;

public class Web {
    private final Store store;

    public Web(Store store) {
        this.store = store;
    }

    public void start(int port) {
        // Создаем сервер Javalin
        var app = Javalin.create(config -> {
            config.http.defaultContentType = "text/html; charset=utf-8";
        });

        // Указываем порт, на котором будет работать сервер
        app.start(port);
        // Формируем страницу с вакансиями
        app.get("/", ctx -> {
            ctx.contentType("text/html; charset=utf-8");

            var page = new StringBuilder();
            page.append("<!DOCTYPE html>");
            page.append("<html>");
            page.append("<head>");
            page.append("<meta charset='UTF-8'>");
            page.append("<title>Все посты</title>");
            page.append("<style>");
            page.append(".post { padding: 10px; margin: 10px 0; border: 1px solid #ccc; border-radius: 5px; }");
            page.append("</style>");
            page.append("</head>");
            page.append("<body>");
            page.append("<h1>Список постов</h1>");

            // Формируем блоки для каждого поста
            store.getAll().forEach(post ->
                    page.append("<div class='post'>")
                            .append(post.toString())
                            .append("</div>")
            );

            page.append("</body>");
            page.append("</html>");

            ctx.result(page.toString());
        });
    }
}
