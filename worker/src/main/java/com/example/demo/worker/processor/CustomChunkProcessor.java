package com.example.demo.worker.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.item.ChunkProcessor;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomChunkProcessor implements ChunkProcessor<Integer> {

    @Autowired
    private ItemWriter<Double> itemWriter;

    private static final Logger LOG = LoggerFactory.getLogger(CustomChunkProcessor.class);
    @Override
    public void process(Chunk<Integer> chunk, StepContribution contribution) throws Exception {
        if(chunk.isEmpty()){
            return;
        }
        Chunk<Double> output = transformChunk(contribution, chunk);
        contribution.incrementFilterCount(computeFilterCount(chunk,output));
        itemWriter.write(output);
    }

    private Chunk<Double> transformChunk(StepContribution contribution, Chunk<Integer> chunk) {
        Chunk<Double> outputChunk = new Chunk<>();
        for(Chunk<Integer>.ChunkIterator iterator = chunk.iterator(); iterator.hasNext();){
            try {
                final Integer item = iterator.next();
                outputChunk.add(processItem(item));
            } catch (Exception e) {

                chunk.clear();
                throw e;
            }
        }
        if(chunk.isEnd()){
            outputChunk.setEnd();
        }
        return outputChunk;
    }

    private Double processItem(Integer item) {
        /*
        * One can add custom ItemProcessor here if here according to free will
        * */
        return item * 2.0;
    }

    private int computeFilterCount(Chunk<Integer> inputs, Chunk<Double> outputs){
        return (Integer) inputs.getUserData() - outputs.size();
    }

}
