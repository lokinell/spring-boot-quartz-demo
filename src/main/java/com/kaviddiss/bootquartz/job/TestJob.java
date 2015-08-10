package com.kaviddiss.bootquartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO 写javadoc.
 *
 * @auther loki 15/8/10
 */
public class TestJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(TestJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOG.info("Test job");
    }
}
