package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbit {

    public static void main(String[] args) {
        try (FileInputStream propertyFile = new FileInputStream("src/main/resources/rabbit.properties")) {
            Properties property = new Properties();
            property.load(propertyFile);
            Class.forName(property.getProperty("jdbc.driver"));
            try {
                Connection con = DriverManager.getConnection(
                        property.getProperty("jdbc.url"),
                        property.getProperty("jdbc.username"),
                        property.getProperty("jdbc.password"));
                Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                JobDataMap data = new JobDataMap();
                data.put("connection", con);
                scheduler.start();
                JobDetail job = newJob(Rabbit.class)
                        .usingJobData(data)
                        .build();
                int sec = Integer.parseInt(property.getProperty("rabbit.interval"));
                SimpleScheduleBuilder times = simpleSchedule()
                        .withIntervalInSeconds(sec)
                        .repeatForever();
                Trigger trigger = newTrigger()
                        .startNow()
                        .withSchedule(times)
                        .build();
                scheduler.scheduleJob(job, trigger);
                Thread.sleep(10000);
                scheduler.shutdown();
            } catch (SchedulerException | InterruptedException se) {
                se.printStackTrace();
            }
        } catch (ClassNotFoundException | IOException | SQLException se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) {
            String message = "Rabbit runs here ...";
            System.out.println(message);
            Connection con = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            try (PreparedStatement ps = con.prepareStatement(
                    "insert into rabbit(hashCode, work_text, \"end_unix-time_of_work\") values ((?), (?), (?));")) {
                ps.setInt(1, hashCode());
                ps.setString(2, message);
                ps.setLong(3, System.currentTimeMillis());
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}