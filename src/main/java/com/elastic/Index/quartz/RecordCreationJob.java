package com.elastic.Index.quartz;

import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;


@Component//https://advenoh.tistory.com/55
public class RecordCreationJob extends QuartzJobBean implements InterruptableJob{

    private volatile boolean isJobInterrupted = false;
    private volatile Thread currThread;
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String dataPassed = context.getMergedJobDataMap().get("XXXXX").toString();
        JobKey jobKey = context.getJobDetail().getKey();
        if (!isJobInterrupted) { //flag
        }
    }


    public JobDetail buildJobDetails(){
        //create map to store key-value (can be received from request) which can be passed to job
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("dataPassed", "Data can be passed to job it can be request Data or other" );

        return  JobBuilder.newJob(RecordCreationJob.class)
                .withIdentity(UUID.randomUUID().toString(),"record-jobs") // unique job key
                .withDescription("Record Job Description")
                .usingJobData(jobDataMap) // values to pass to job
                .storeDurably(true) //false - delete job once job executed
//                .requestRecovery()
                .build();
    }
//Note: one job can have multiple trigger but one trigger can only have one job
    public Trigger buildTrigger(JobDetail jobDetail){
        ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDate.parse("2023-04-01"),
                LocalTime.of(8,30), ZoneId.of("Asia/Kolkata"));

        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(),"records-tigger")
                .withIdentity("Records Trigger Description")
                .startAt(Date.from(zonedDateTime.toInstant()))
                /*
                     // Some Scheduler Example
                     .withSchedule(impleScheduleBuilder.simpleSchedule().withIntervalInHours(1))

                      --> cron scheduler
                     .withSchedule(CronScheduleBuilder.cronSchedule("0 0 16 * * ?")
                             .withMisfireHandlingInstructionFireAndProceed())
                */
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(5).repeatForever())
                .build();
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        isJobInterrupted = true; //interrupt
        if (currThread != null) {
            System.out.println("interrupting - {}"+ currThread.getName());
            currThread.interrupt();
        }
    }
}
