package net.ctwatch.logServerApi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
public abstract class GetEntries {
    public abstract List<Entry> entries();

    @JsonCreator
    public static GetEntries create(@JsonProperty("entries") List<Entry> entries) {
        return new AutoValue_GetEntries(entries);
    }
}
