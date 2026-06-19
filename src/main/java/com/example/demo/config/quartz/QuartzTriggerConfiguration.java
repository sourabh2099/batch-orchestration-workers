package com.example.demo.config.quartz;

import com.example.demo.cron.SampleJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzTriggerConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(QuartzTriggerConfiguration.class);

    @Bean
    public JobDetail jobTest1() {
        return JobBuilder.newJob().ofType(SampleJob.class)
                .storeDurably()
                .withIdentity("FIRST_JOB", "group1")
                .withDescription("Sample Job for Testing functionality").build();
    }

    @Bean
    public CronTrigger cronTriggerTest1(JobDetail jobDetail) {
        return TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity("FIRST_TRIGGER", "group1")
                .withDescription("Sample Trigger for functionality testing")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?")).build();
    }
}
