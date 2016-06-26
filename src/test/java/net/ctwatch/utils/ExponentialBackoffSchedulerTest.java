package net.ctwatch.utils;

import org.junit.Test;

import java.time.Duration;

import static org.junit.Assert.assertEquals;

public class ExponentialBackoffSchedulerTest {

    @Test
    public void itShouldDoubleDelayTimeAfterAFailure() throws Exception {
        ExponentialBackoffScheduler scheduler = new ExponentialBackoffScheduler(Duration.ofSeconds(1), Duration.ofSeconds(7));

        assertEquals(Duration.ofSeconds(1), scheduler.getNextScheduleDuration());
        scheduler.onJobCompletion(ExponentialBackoffScheduler.JobStatus.FAILURE);
        assertEquals(Duration.ofSeconds(2), scheduler.getNextScheduleDuration());
    }

    @Test
    public void itShouldNeverDelayLongerThanMaximum() {
        ExponentialBackoffScheduler exponentialBackoffScheduler = new ExponentialBackoffScheduler(Duration.ofSeconds(1), Duration.ofSeconds(7));

        exponentialBackoffScheduler.onJobCompletion(ExponentialBackoffScheduler.JobStatus.FAILURE);
        exponentialBackoffScheduler.onJobCompletion(ExponentialBackoffScheduler.JobStatus.FAILURE);
        assertEquals(Duration.ofSeconds(4), exponentialBackoffScheduler.getNextScheduleDuration());
        exponentialBackoffScheduler.onJobCompletion(ExponentialBackoffScheduler.JobStatus.FAILURE);
        assertEquals(Duration.ofSeconds(7), exponentialBackoffScheduler.getNextScheduleDuration());
    }

    @Test
    public void itShouldSetDelayToInitialAfterASuccess() {
        ExponentialBackoffScheduler exponentialBackoffScheduler = new ExponentialBackoffScheduler(Duration.ofSeconds(1), Duration.ofSeconds(7));

        exponentialBackoffScheduler.onJobCompletion(ExponentialBackoffScheduler.JobStatus.FAILURE);
        exponentialBackoffScheduler.onJobCompletion(ExponentialBackoffScheduler.JobStatus.FAILURE);
        exponentialBackoffScheduler.onJobCompletion(ExponentialBackoffScheduler.JobStatus.FAILURE);
        assertEquals(Duration.ofSeconds(7), exponentialBackoffScheduler.getNextScheduleDuration());

        exponentialBackoffScheduler.onJobCompletion(ExponentialBackoffScheduler.JobStatus.SUCCESS);

        assertEquals(Duration.ofSeconds(1), exponentialBackoffScheduler.getNextScheduleDuration());
    }

}