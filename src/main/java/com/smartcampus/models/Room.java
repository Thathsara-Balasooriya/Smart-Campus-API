package com.smartcampus.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Room {
    private String id;
    private String name;
    private int capacity;
    private List<String> sensorIds;

    public Room() {
        this.sensorIds = new ArrayList<>();
    }

    public Room(String id, String name, int capacity, List<String> sensorIds) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.sensorIds = sensorIds == null ? new ArrayList<>() : new ArrayList<>(sensorIds);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<String> getSensorIds() {
        return Collections.unmodifiableList(sensorIds);
    }

    public void setSensorIds(List<String> sensorIds) {
        this.sensorIds = sensorIds == null ? new ArrayList<>() : new ArrayList<>(sensorIds);
    }

    public void addSensorId(String sensorId) {
        if (sensorId == null) {
            return;
        }
        if (this.sensorIds == null) {
            this.sensorIds = new ArrayList<>();
        }
        if (!this.sensorIds.contains(sensorId)) {
            this.sensorIds.add(sensorId);
        }
    }

    public void removeSensorId(String sensorId) {
        if (sensorId == null || this.sensorIds == null) {
            return;
        }
        this.sensorIds.remove(sensorId);
    }

    public boolean hasSensorId(String sensorId) {
        return this.sensorIds != null && this.sensorIds.contains(sensorId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Room)) {
            return false;
        }
        Room room = (Room) o;
        return Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
