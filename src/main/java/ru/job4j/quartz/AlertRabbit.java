package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) {
        try {
            Properties props = loadProperties("rabbit.properties");
            int interval = parsInterval(props);
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class).build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(20000);
            scheduler.shutdown(true);
        } catch (SchedulerException | InterruptedException se) {
            se.printStackTrace();
        }

    }

    private static Properties loadProperties(String resours) {
        Properties props = new Properties();
        try (InputStream is = AlertRabbit.class.getClassLoader().getResourceAsStream(resours)) {
            props.load(is);
            return props;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private static int parsInterval(Properties props) {
        int defaultInterval = 10;
        String property = props.getProperty("interval");
        if (property == null) {
            return defaultInterval;
        }
        return Integer.parseInt(property) > 0 ? Integer.parseInt(property) : defaultInterval;
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ..." + System.currentTimeMillis());
        }
    }
}