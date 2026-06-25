package com.example.demo.orchestrator.process.job;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.integration.chunk.ChunkMessageChannelItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class RemoteTestJob {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteTestJob.class);

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    private final Integer LIMIT = 200;

    private static final String JOB_NAME = "REMOTE_TEST_JOB";
    private static final String STEP_ONE_NAME = "REMOTE_TEST_STEP_ONE";

    @Bean
    public ActiveMQConnectionFactory mqConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL("tcp://localhost:61616");
        return connectionFactory;
    }

    /* for outbound flow to the workers */
    @Bean
    public DirectChannel request() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow outBoundFlow(ActiveMQConnectionFactory mqConnectionFactory) {
        return IntegrationFlow.from(request())
                .handle(Jms.outboundAdapter(mqConnectionFactory).destination("requests"))
                .get();
    }

    /* for inbound flow to manager / orchestrator again */
    @Bean
    public QueueChannel replies() {
        return new QueueChannel();
    }

    /*Configure the inbound flow as well to pull messages from workers */

    @Bean
    public IntegrationFlow inBoundFlow(ActiveMQConnectionFactory mqConnectionFactory) {
        // Use the JMS message-driven adapter as the message source and send messages to the replies channel
        return IntegrationFlow.from(Jms.messageDrivenChannelAdapter(mqConnectionFactory).destination("replies"))
                .channel(replies())
                .get();
    }

    @Bean("integerItemReader")
    public ItemReader<Integer> itemReader(){
        AtomicInteger atomicCount = new AtomicInteger(0);
        return () -> atomicCount.get() <= LIMIT ? atomicCount.incrementAndGet() : null;
    }

    @Bean("remoteItemWriter")
    public ItemWriter<Integer> itemWriter() {
        MessagingTemplate messagingTemplate = new MessagingTemplate();
        messagingTemplate.setDefaultChannel(request());
        messagingTemplate.setReceiveTimeout(2000);

        ChunkMessageChannelItemWriter<Integer> chunkMessageChannelItemWriter
                = new ChunkMessageChannelItemWriter<>();
        chunkMessageChannelItemWriter.setMessagingOperations(messagingTemplate);
        chunkMessageChannelItemWriter.setReplyChannel(replies());
        return chunkMessageChannelItemWriter;
    }

    @Bean("remoteProcessingJob")
    public Job remoteProcessingJob(JobRepository jobRepository,
                                   @Qualifier("remoteItemWriter") ItemWriter<Integer> itemWriter,
                                   @Qualifier("integerItemReader") ItemReader<Integer> itemReader) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(
                        new StepBuilder(STEP_ONE_NAME, jobRepository)
                                .<Integer,Integer>chunk(50)
                                .reader(itemReader)
                                .writer(itemWriter)
                                .build()
                )
                .build();
    }
}
