package com.elastic.Index.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SimpleJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(SimpleJob.class);
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        This is where the scheduled task runs and the information on the JobDetail and Trigger is retrieved using the JobExecutionContext.
        String userId = jobExecutionContext.getJobDetail().getJobDataMap().getString("userId");
        System.out.println("Ravan welcomes simple job "+ userId);
    }
}
