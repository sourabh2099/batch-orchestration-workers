package com.example.demo.orchestrator.cron;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Properties;

@Component
public class SampleJob implements Job {

    @Autowired
    private org.springframework.batch.core.job.Job job;

    @Autowired
    private JobOperator jobOperator;

    private static final Logger LOG = LoggerFactory.getLogger(SampleJob.class);


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOG.info("Job execution started at {}", LocalDateTime.now());
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        try {
            LOG.info("Starting job execution");
//            jobOperator.start(job, jobParameters);
        }catch (Exception e){
            LOG.error("Found error while trying to process the job",e);
        }
        LOG.info("Job Execution ended at {}",LocalDateTime.now());

    }
}
