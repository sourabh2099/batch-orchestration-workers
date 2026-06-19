package com.example.demo.process.job;

import com.example.demo.pojo.sink.SinkAE;
import com.example.demo.pojo.source.SourceAE;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcPagingItemReader;
import org.springframework.batch.infrastructure.item.database.Order;
import org.springframework.batch.infrastructure.item.database.PagingQueryProvider;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableBatchProcessing
@EnableJdbcJobRepository
public class SimpleTestJob {

    private final DataSource readDataSource;

    public SimpleTestJob(@Qualifier("readerDataSource") DataSource readDataSource) {
        this.readDataSource = readDataSource;
    }

    @Bean
    public Step readFromOtherDb(ItemReader<SourceAE> itemReader,
                                ItemProcessor<SourceAE, SinkAE> itemProcessor,
                                ItemWriter<SinkAE> itemWriter, JobRepository jobRepository) {
        return new StepBuilder(jobRepository)
                .<SourceAE, SinkAE>chunk(10)
                .faultTolerant()
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<SourceAE> reader() {
        JdbcPagingItemReader<SourceAE> objectJdbcPagingItemReader = new JdbcPagingItemReader<>(readDataSource, readerQueryProvider());
        objectJdbcPagingItemReader.setFetchSize(10);
        objectJdbcPagingItemReader.setRowMapper(
                ((rs, rowNum) -> {
                    SourceAE sourceAE = new SourceAE();
                    sourceAE.setProduct(rs.getString("product"));
                    sourceAE.setReportedEventTerm(rs.getString("reported_event_term"));
                    sourceAE.setSubjectId(rs.getString("subject"));
                    sourceAE.setReporterSeriousness(rs.getString("reporter_seriousness"));
                    return sourceAE;
                })
        );
        return objectJdbcPagingItemReader;
    }

    @Bean
    @StepScope
    public ItemProcessor<SourceAE, SinkAE> itemProcessor() {
        return (sourceAe) -> {
            SinkAE sinkAE = new SinkAE();
            sinkAE.setProduct(sourceAe.getProduct());
            sinkAE.setReportedEventTerm(sourceAe.getReportedEventTerm());
            sinkAE.setSubjectId(sourceAe.getSubjectId());
            sinkAE.setReporterSeriousness(sourceAe.getReporterSeriousness());
           return sinkAE;
        } ;
    }

    @Bean
    @StepScope
    public ItemWriter<SinkAE> itemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<SinkAE>()
                .sql("INSERT INTO public.adverse_event_sink (id, reported_event_term, subject, product, reporter_seriousness, aesi, last_fetched_at) VALUES(nextVal('adverse_event_seq'), :reportedEventTerm, :subjectId, :product, :reporterSeriousness, false, now());")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

    public PagingQueryProvider readerQueryProvider() {
        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("*");
        queryProvider.setFromClause("from adverse_event_source");
        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);
        queryProvider.setSortKeys(sortKeys);
        return queryProvider;
    }

    @Bean
    public Job job(JobRepository jobRepository, Step readFromOtherDb) {
        return new JobBuilder("simpleTestJob", jobRepository)
                .start(readFromOtherDb)
                .build();
    }

}
