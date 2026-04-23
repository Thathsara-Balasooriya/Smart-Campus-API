package com.smartcampus.services;

import com.smartcampus.exceptions.ConflictException;
import com.smartcampus.exceptions.ForbiddenOperationException;
import com.smartcampus.exceptions.ResourceNotFoundException;
import com.smartcampus.exceptions.UnprocessableEntityException;
import com.smartcampus.models.Room;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorStatus;
import com.smartcampus.store.InMemoryStore;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class SensorService {
    private final InMemoryStore store;

    public SensorService(InMemoryStore store) {
        this.store = store;
    }

    public List<Sensor> listSensors(String type) {
        List<Sensor> sensors = new ArrayList<>(store.getSensors().values());
        if (type != null && !type.isBlank()) {
            sensors = sensors.stream()
                    .filter(s -> type.equalsIgnoreCase(s.getType()))
                    .collect(Collectors.toList());
        }
        sensors.sort(Comparator.comparing(Sensor::getId));
        return sensors;
    }

    public Sensor getSensorOrThrow(String id) {
        Sensor sensor = store.getSensors().get(id);
        if (sensor == null) {
            throw new ResourceNotFoundException("Sensor not found: " + id);
        }
        return sensor;
    }

    public Sensor createSensor(Sensor input) {
        String roomId = input == null ? null : input.getRoomId();
        if (roomId == null || roomId.isBlank() || !store.getRooms().containsKey(roomId)) {
            throw new UnprocessableEntityException("roomId must reference an existing room");
        }

        String id = (input != null && input.getId() != null && !input.getId().isBlank()) 
                ? input.getId() 
                : store.nextSensorId();

        if (store.getSensors().containsKey(id)) {
            throw new ConflictException("Sensor already exists with ID: " + id);
        }

        // If the ID is numeric, sync the sequence to avoid future collisions
        try {
            long numericId = Long.parseLong(id);
            store.updateSensorSequence(numericId);
        } catch (NumberFormatException ignored) {
            // Not a numeric ID, no need to sync sequence
        }

        Sensor sensor = new Sensor();
        sensor.setId(id);
        sensor.setType(input == null ? null : input.getType());
        sensor.setStatus(input == null || input.getStatus() == null ? SensorStatus.INACTIVE : input.getStatus());
        sensor.setCurrentValue(input == null ? null : input.getCurrentValue());
        sensor.setRoomId(roomId);
        store.getSensors().put(sensor.getId(), sensor);

        Room room = store.getRooms().get(roomId);
        if (room != null) {
            synchronized (room) {
                room.addSensorId(sensor.getId());
            }
        }

        return sensor;
    }

    public Sensor updateSensor(String id, Sensor input) {
        Sensor existing = getSensorOrThrow(id);
        if (existing.getStatus() == SensorStatus.MAINTENANCE) {
            throw new ForbiddenOperationException("Sensor in MAINTENANCE cannot be modified");
        }

        String requestedRoomId = input == null ? null : input.getRoomId();
        String newRoomId = requestedRoomId == null ? existing.getRoomId() : requestedRoomId;
        if (newRoomId == null || newRoomId.isBlank() || !store.getRooms().containsKey(newRoomId)) {
            throw new UnprocessableEntityException("roomId must reference an existing room");
        }

        String oldRoomId = existing.getRoomId();
        if (!Objects.equals(oldRoomId, newRoomId)) {
            Room oldRoom = store.getRooms().get(oldRoomId);
            Room newRoom = store.getRooms().get(newRoomId);
            if (newRoom == null) {
                throw new UnprocessableEntityException("roomId must reference an existing room");
            }
            if (oldRoom != null) {
                synchronized (oldRoom) {
                    oldRoom.removeSensorId(existing.getId());
                }
            }
            synchronized (newRoom) {
                newRoom.addSensorId(existing.getId());
            }
        }

        synchronized (existing) {
            if (input != null) {
                if (input.getType() != null) {
                    existing.setType(input.getType());
                }
                if (input.getStatus() != null) {
                    existing.setStatus(input.getStatus());
                }
                if (input.getCurrentValue() != null) {
                    existing.setCurrentValue(input.getCurrentValue());
                }
                existing.setRoomId(newRoomId);
            }
        }

        return existing;
    }

    public void deleteSensor(String id) {
        Sensor existing = getSensorOrThrow(id);
        if (existing.getStatus() == SensorStatus.MAINTENANCE) {
            throw new ForbiddenOperationException("Sensor in MAINTENANCE cannot be deleted");
        }

        store.getSensors().remove(existing.getId());
        store.getReadingsBySensorId().remove(existing.getId());

        Room room = store.getRooms().get(existing.getRoomId());
        if (room != null) {
            synchronized (room) {
                room.removeSensorId(existing.getId());
            }
        }
    }
}
