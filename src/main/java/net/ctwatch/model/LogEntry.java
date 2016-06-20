package net.ctwatch.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class LogEntry {
    public abstract int index();
    public abstract Certificate certificate();

    public static LogEntry create(int index, Certificate certificate) {
        return new AutoValue_LogEntry(index, certificate);
    }
}
