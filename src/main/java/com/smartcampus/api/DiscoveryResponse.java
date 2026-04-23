package com.smartcampus.api;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DiscoveryResponse {
    private String name;
    private String version;
    private Instant timestamp;
    private List<ApiLink> links;

    public DiscoveryResponse() {
        this.links = new ArrayList<>();
    }

    public DiscoveryResponse(String name, String version, Instant timestamp, List<ApiLink> links) {
        this.name = name;
        this.version = version;
        this.timestamp = timestamp;
        this.links = links == null ? new ArrayList<>() : new ArrayList<>(links);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public List<ApiLink> getLinks() {
        return Collections.unmodifiableList(links);
    }

    public void setLinks(List<ApiLink> links) {
        this.links = links == null ? new ArrayList<>() : new ArrayList<>(links);
    }
}
