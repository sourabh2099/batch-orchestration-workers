package com.example.demo.orchestrator.config.quartz;

import com.example.demo.orchestrator.cron.RemoteTestjob;
import com.example.demo.orchestrator.cron.SampleJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzTriggerConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(QuartzTriggerConfiguration.class);

    @Bean("jobTest1")
    public JobDetail jobTest1() {
        return JobBuilder.newJob().ofType(SampleJob.class)
                .storeDurably()
                .withIdentity("FIRST_JOB", "group1")
                .withDescription("Sample Job for Testing functionality").build();
    }

    @Bean("cronTriggerTest1")
    public CronTrigger cronTriggerTest1(@Qualifier("jobTest1") JobDetail jobDetail) {
        return TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity("FIRST_TRIGGER", "group1")
                .withDescription("Sample Trigger for functionality testing")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 * * * * ?")).build();
    }

    @Bean("jobTest2")
    public JobDetail jobDetailTest2() {
        return JobBuilder.newJob().ofType(RemoteTestjob.class)
                .storeDurably()
                .withDescription("Job to test properly about the processing of records by worker threads")
                .build();
    }


    @Bean("cronTriggerTest2")
    public CronTrigger cronTriggerTest2(@Qualifier("jobTest2") JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("SECOND_TRIGGER", "group_2")
                .withDescription("Sample trigger to test the functionality of remote processing by worker threads")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?"))
                .build();
    }
}
