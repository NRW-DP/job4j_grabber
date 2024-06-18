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
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class).build();
            int intervalSeconds = readIntervalFromProperties();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(intervalSeconds)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    private static int readIntervalFromProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            properties.load(inputStream);
            return Integer.parseInt(properties.getProperty("rabbit.interval"));
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
        }
    }
}