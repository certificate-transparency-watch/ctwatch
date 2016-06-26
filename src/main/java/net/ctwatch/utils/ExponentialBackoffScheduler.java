package net.ctwatch.utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.AbstractScheduledService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ExponentialBackoffScheduler extends AbstractScheduledService.CustomScheduler {

    private final Duration initialDuration;
    private final Duration maxDuration;

    private AtomicReference<Duration> currentDuration;

    public ExponentialBackoffScheduler(Duration initial, Duration max) {
        this.initialDuration = initial;
        this.maxDuration = max;
        this.currentDuration = new AtomicReference<Duration>(initialDuration);
    }

    protected Schedule getNextSchedule() throws Exception {
        Duration duration = getNextScheduleDuration();
        return new Schedule(duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    @VisibleForTesting  Duration getNextScheduleDuration() {
        return currentDuration.get();
    }

    public void onJobCompletion(JobStatus jobStatus) {
        Duration duration = currentDuration.get();
        Duration nextDuration;
        switch (jobStatus) {
            case SUCCESS:
                nextDuration = initialDuration;
                break;
            case FAILURE:
                nextDuration = min(duration.multipliedBy(2), maxDuration);
                break;
            default:
                throw new AssertionError();
        }
        currentDuration.set(nextDuration);
    }

    private static <T extends Comparable<? super T>> T min(T a, T b) {
        if (a == null) {
            if (b == null) return a;
            else return b;
        }
        if (b == null)
            return a;
        return a.compareTo(b) < 0 ? a : b;
    }

    public enum JobStatus {
        SUCCESS,
        FAILURE
    }

}
