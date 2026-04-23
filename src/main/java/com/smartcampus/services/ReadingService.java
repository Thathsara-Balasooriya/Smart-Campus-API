package com.smartcampus.services;

import com.smartcampus.exceptions.ConflictException;
import com.smartcampus.exceptions.ForbiddenOperationException;
import com.smartcampus.exceptions.ResourceNotFoundException;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;
import com.smartcampus.models.SensorStatus;
import com.smartcampus.store.InMemoryStore;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ReadingService {
    private final InMemoryStore store;

    public ReadingService(InMemoryStore store) {
        this.store = store;
    }

    public List<SensorReading> listReadings(String sensorId) {
        Sensor sensor = store.getSensors().get(sensorId);
        if (sensor == null) {
            throw new ResourceNotFoundException("Sensor not found: " + sensorId);
        }
        List<SensorReading> readings = new ArrayList<>(store.readingsListForSensor(sensorId));
        readings.sort(Comparator.comparing(SensorReading::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())));
        return readings;
    }

    public SensorReading addReading(String sensorId, SensorReading input) {
        Sensor sensor = store.getSensors().get(sensorId);
        if (sensor == null) {
            throw new ResourceNotFoundException("Sensor not found: " + sensorId);
        }
        if (sensor.getStatus() == SensorStatus.MAINTENANCE) {
            throw new ForbiddenOperationException("Sensor in MAINTENANCE cannot accept readings");
        }

        String id = (input != null && input.getId() != null && !input.getId().isBlank()) 
                ? input.getId() 
                : store.nextReadingId();

        // Check for duplicates in the sensor's reading list
        boolean duplicate = store.readingsListForSensor(sensorId).stream()
                .anyMatch(r -> id.equals(r.getId()));
        if (duplicate) {
            throw new ConflictException("Reading already exists with ID: " + id);
        }

        // If the ID is numeric, sync the sequence to avoid future collisions
        try {
            long numericId = Long.parseLong(id);
            store.updateReadingSequence(numericId);
        } catch (NumberFormatException ignored) {
            // Not a numeric ID, no need to sync sequence
        }

        SensorReading reading = new SensorReading();
        reading.setId(id);
        reading.setTimestamp(input == null || input.getTimestamp() == null ? Instant.now() : input.getTimestamp());
        reading.setValue(input == null ? 0.0 : input.getValue());

        store.readingsListForSensor(sensorId).add(reading);
        synchronized (sensor) {
            sensor.setCurrentValue(reading.getValue());
        }
        return reading;
    }
}
