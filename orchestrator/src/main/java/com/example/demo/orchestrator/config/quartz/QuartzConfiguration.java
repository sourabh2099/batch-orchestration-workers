package com.example.demo.orchestrator.config.quartz;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;


/*
* this file is used to enable autowiring in quartz jobs, without this,
* the @Autowired annotation in the job class will not work and the dependencies will not be injected.
* */

@Configuration
public class QuartzConfiguration extends SpringBeanJobFactory implements ApplicationContextAware {
    private AutowireCapableBeanFactory autowireCapableBeanFactory;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
    }
    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object jobInstance = super.createJobInstance(bundle);
        autowireCapableBeanFactory.autowireBean(jobInstance);
        return jobInstance;
    }


}
