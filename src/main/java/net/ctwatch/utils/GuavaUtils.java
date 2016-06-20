package net.ctwatch.utils;

import com.google.common.util.concurrent.Service;
import io.dropwizard.lifecycle.Managed;

public class GuavaUtils {
    public static Managed fromService(final Service s) {
        return new Managed() {
            public void start() throws Exception {
                s.startAsync().awaitRunning();
            }

            public void stop() throws Exception {
                s.stopAsync().awaitTerminated();
            }
        };
    }
}
