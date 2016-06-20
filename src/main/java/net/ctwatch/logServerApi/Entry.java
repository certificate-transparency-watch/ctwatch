package net.ctwatch.logServerApi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Entry {
    public abstract Merkle leafInput();

    @JsonCreator
    public static Entry create(@JsonProperty("leaf_input") Merkle leafInput, @JsonProperty("extra_data") String extraData) {
        return new AutoValue_Entry(leafInput);
    }
}
