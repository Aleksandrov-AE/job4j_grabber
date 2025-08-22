package ru.job4j.grabber.service;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import ru.job4j.grabber.stores.Store;

public class SuperJobGrab implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        var store = (Store) context.getJobDetail().getJobDataMap().get("store");
        var parse = (Parse) context.getJobDetail().getJobDataMap().get("parse");
        for (var post : parse.fetch()) {
            store.save(post);
            System.out.println("Stored post: " + post);
        }
        System.out.println("_______________");
    }
}