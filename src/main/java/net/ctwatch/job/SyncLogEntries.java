package net.ctwatch.job;

import com.google.common.collect.Range;
import com.google.common.util.concurrent.AbstractScheduledService;
import net.ctwatch.db.Db;
import net.ctwatch.logServerApi.Entry;
import net.ctwatch.logServerApi.GetEntries;
import net.ctwatch.logServerApi.LogServerService;
import net.ctwatch.model.CertificateParser;
import net.ctwatch.model.LogEntry;
import net.ctwatch.utils.ExponentialBackoffScheduler;
import net.ctwatch.utils.SlidingWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;

public class SyncLogEntries extends AbstractScheduledService {

    private static final Logger LOG = LoggerFactory.getLogger(SyncLogEntries.class);

    private final LogServerService logServerService;
    private final ExponentialBackoffScheduler scheduler = new ExponentialBackoffScheduler(Duration.ofMillis(1), Duration.ofSeconds(59));
    private final Db db;

    private final SlidingWindow slidingWindow = new SlidingWindow();
    private int nextIndex = 0;

    public SyncLogEntries(LogServerService logServerService, Db db) {
        this.logServerService = logServerService;
        this.db = db;
    }

    protected void runOneIteration() throws Exception {
        try {
            scheduler.onJobCompletion(runOneIterationWithStatus());
            slidingWindow.onSuccess();
        } catch (Exception e) {
            LOG.error(e.getMessage(),e);
            scheduler.onJobCompletion(ExponentialBackoffScheduler.JobStatus.FAILURE);
            slidingWindow.onFailure();
        }
    }

    private ExponentialBackoffScheduler.JobStatus runOneIterationWithStatus() throws IOException {
        Range<Integer> range = Range.closed(nextIndex, nextIndex + slidingWindow.windowSize());
        GetEntries body = logServerService.getEntries(range.lowerEndpoint(), range.upperEndpoint()).execute().body();
        int index = range.lowerEndpoint();
        for (Entry entry : body.entries()) {
            LogEntry logEntry = LogEntry.create(index, CertificateParser.fromJREClass(entry.leafInput().timestampedEntry().certificate()));
            db.writeLogEntry(logEntry);
            index++;
        }
        nextIndex = index;
        return ExponentialBackoffScheduler.JobStatus.SUCCESS;
    }

    protected Scheduler scheduler() {
        return scheduler;
    }
}
