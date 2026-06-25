package com.example.demo.orchestrator.cron;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class RemoteTestjob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteTestjob.class);

    @Qualifier("remoteProcessingJob")
    @Autowired
    private org.springframework.batch.core.job.Job job;

    @Autowired
    private JobOperator jobOperator;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LOG.info("Starting to execute ");

        /*
        * prep job parameters to be sent to the spring job
        * */

        JobParameters jobParameters = new org.springframework.batch.core.job.parameters.JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        try {
            jobOperator.start(job, jobParameters);
        } catch (Exception e) {
            LOG.error("Found error while trying to process RemoteTestJob", e);
            throw new RuntimeException(e);
        }

        LOG.info("Completed processing of the {}",context);
    }
}
