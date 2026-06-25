package com.example.demo.worker.config;

import com.example.demo.worker.processor.CustomChunkProcessor;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.integration.chunk.ChunkProcessorChunkRequestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.jms.dsl.Jms;

@Configuration
public class WorkerQueueConfiguration {

    @Bean
    public ActiveMQConnectionFactory connectionFactory(){
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL("tcp://localhost:61616");
        return connectionFactory;
    }

    @Bean
    public DirectChannel inputChannel(){
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow inboundFlow(ActiveMQConnectionFactory mqConnectionFactory){
        return IntegrationFlow.from(Jms.messageDrivenChannelAdapter(mqConnectionFactory).destination("requests"))
                .channel(inputChannel())
                .get();
    }

    @Bean
    public DirectChannel  outputChannel(){
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow outboundFlow(ActiveMQConnectionFactory mqConnectionFactory){
        return IntegrationFlow.from(outputChannel())
                .handle(Jms.outboundAdapter(mqConnectionFactory).destination("replies"))
                .get();
    }

    @Bean
    @ServiceActivator(inputChannel = "requests",outputChannel = "replies")
    public ChunkProcessorChunkRequestHandler<Integer> chunkProcessorChunkRequestHandler(CustomChunkProcessor customChunkProcessor){
        ChunkProcessorChunkRequestHandler<Integer> chunkProcessorChunkRequestHandler = new ChunkProcessorChunkRequestHandler<>();
        chunkProcessorChunkRequestHandler.setChunkProcessor(customChunkProcessor);
        return chunkProcessorChunkRequestHandler;

    }

    /*
    * Adding sink Datasource related Connection here itself
    * */

    @Bean
    public ItemWriter<Double> itemWriter(){
        return items -> {
            for(Double item: items){
                System.out.println("Writing item: " + item);
            }
        };
    }
}
