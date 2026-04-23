package com.smartcampus.services;

import com.smartcampus.exceptions.ConflictException;
import com.smartcampus.exceptions.ResourceNotFoundException;
import com.smartcampus.models.Room;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorStatus;
import com.smartcampus.store.InMemoryStore;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class RoomService {
    private final InMemoryStore store;

    public RoomService(InMemoryStore store) {
        this.store = store;
    }

    public List<Room> listRooms() {
        List<Room> rooms = new ArrayList<>(store.getRooms().values());
        rooms.sort(Comparator.comparing(Room::getId));
        return rooms;
    }

    public Room getRoomOrThrow(String id) {
        Room room = store.getRooms().get(id);
        if (room == null) {
            throw new ResourceNotFoundException("Room not found: " + id);
        }
        return room;
    }

    public Room createRoom(Room input) {
        String id = (input != null && input.getId() != null && !input.getId().isBlank()) 
                ? input.getId() 
                : store.nextRoomId();
        
        if (store.getRooms().containsKey(id)) {
            throw new ConflictException("Room already exists with ID: " + id);
        }

        // If the ID is numeric, sync the sequence to avoid future collisions
        try {
            long numericId = Long.parseLong(id);
            store.updateRoomSequence(numericId);
        } catch (NumberFormatException ignored) {
            // Not a numeric ID, no need to sync sequence
        }

        Room room = new Room();
        room.setId(id);
        room.setName(input == null ? null : input.getName());
        room.setCapacity(input == null ? 0 : input.getCapacity());
        room.setSensorIds(null);
        store.getRooms().put(room.getId(), room);
        return room;
    }

    public Room updateRoom(String id, Room input) {
        Room existing = getRoomOrThrow(id);
        synchronized (existing) {
            if (input != null) {
                if (input.getName() != null) {
                    existing.setName(input.getName());
                }
                // For capacity, we assume 0 means "not provided" if it's an int, 
                // but since it's a primitive in Room model, we check if it's > 0
                if (input.getCapacity() > 0) {
                    existing.setCapacity(input.getCapacity());
                }
            }
        }
        return existing;
    }

    public void deleteRoom(String id) {
        Room room = getRoomOrThrow(id);
        Map<String, Sensor> sensors = store.getSensors();
        boolean hasActiveSensor = sensors.values().stream()
                .anyMatch(s -> id.equals(s.getRoomId()) && s.getStatus() == SensorStatus.ACTIVE);
        if (hasActiveSensor) {
            throw new ConflictException("Cannot delete room with active sensors");
        }
        store.getRooms().remove(room.getId());
    }
}
