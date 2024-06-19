package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import java.io.*;
import java.sql.*;
import java.util.*;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {

    private static Properties loadConfig() {
        try {
            Properties config = new Properties();
            InputStream input = AlertRabbit.class.getClassLoader().getResourceAsStream("db/rabbit.properties");
            if (input == null) {
                throw new IllegalArgumentException("File not found: rabbit.properties");
            }
            config.load(input);
            return config;
        } catch (Exception e) {
            throw new IllegalStateException("Error loading configuration", e);
        }
    }

    private static Connection getConnection() {
        try {
            Properties config = loadConfig();
            Class.forName(config.getProperty("driver-class-name"));
            return DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static int readIntervalFromProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = AlertRabbit.class.getClassLoader().getResourceAsStream("db/rabbit.properties")) {
            properties.load(inputStream);
            return Integer.parseInt(properties.getProperty("rabbit.interval"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to read 'rabbit.interval' from properties file", e);
        }
    }

    public static void main(String[] args) {
        try {
            List<Long> store = new ArrayList<>();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("store", store);
            data.put("connection", getConnection());
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            int intervalSeconds = readIntervalFromProperties();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(intervalSeconds)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
            System.out.println(store);
        } catch (Exception  se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        private static final String INSERT_TABLE = "INSERT INTO rabbit(created_date) VALUES (?)";

        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");

            @SuppressWarnings("unchecked")
            List<Long> store = (List<Long>) context.getJobDetail().getJobDataMap().get("store");
            long timeStamp = System.currentTimeMillis();
            store.add(timeStamp);
            try {
                Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("connection");
                writeTimestampToDB(connection, timeStamp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void writeTimestampToDB(Connection connection, long timeStamp) {
            try (PreparedStatement statement =
                         connection.prepareStatement(INSERT_TABLE)) {
                statement.setTimestamp(1, new Timestamp(timeStamp));
                statement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}