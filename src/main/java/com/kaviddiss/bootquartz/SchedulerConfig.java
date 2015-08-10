package com.kaviddiss.bootquartz;

import com.kaviddiss.bootquartz.job.SampleJob;
import com.kaviddiss.bootquartz.job.TestJob;
import com.kaviddiss.bootquartz.spring.AutowiringSpringBeanJobFactory;
import liquibase.integration.spring.SpringLiquibase;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by david on 2015-01-20.
 */
@Configuration
@ConditionalOnProperty(name = "quartz.enabled")
public class SchedulerConfig {

    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext,
        // injecting SpringLiquibase to ensure liquibase is already initialized and created the quartz tables:
        SpringLiquibase springLiquibase)
    {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    List<Trigger> cronTriggers(Trigger cronJob1Trigger, Trigger cronJob2Trigger){
        List<Trigger> triggers = new ArrayList<>();
//        triggers.add(cronJob1Trigger);
        triggers.add(cronJob2Trigger);
        return triggers;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource, JobFactory jobFactory,
                                                      Trigger... cronTriggers) throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        // this allows to update triggers in DB when updating settings in config file:
        factory.setOverwriteExistingJobs(true);
        factory.setDataSource(dataSource);
        factory.setJobFactory(jobFactory);

        factory.setQuartzProperties(quartzProperties());
        factory.setTriggers(cronTriggers);

        return factory;
    }

    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    @Bean
    public JobDetailFactoryBean sampleJobDetail() {
        return createJobDetail(SampleJob.class);
    }

    @Bean
    public JobDetailFactoryBean testJobDetail() {
        return createJobDetail(TestJob.class);
    }

//    @Bean
//    public CronTriggerFactoryBean cronJob1Trigger(JobDetail sampleJobDetail, @Value("${job.cron.expression}") String cron) {
//        return createCronTrigger(sampleJobDetail, cron);
//    }

    @Bean
    public CronTriggerFactoryBean cronJob2Trigger(JobDetail testJobDetail) {
        return createCronTrigger(testJobDetail, "* 10 * * * ?");
    }

    private static JobDetailFactoryBean createJobDetail(Class jobClass) {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        // job has to be durable to be stored in DB:
        factoryBean.setDurability(true);
        return factoryBean;
    }

    private static CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail, String cronExpression) {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setCronExpression(cronExpression);
        return factoryBean;
    }

}
