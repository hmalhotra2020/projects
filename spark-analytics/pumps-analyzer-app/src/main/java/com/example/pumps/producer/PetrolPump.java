package com.example.pumps.producer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a petrol pump station with multiple fuel-dispensing machines.
 */
public class PetrolPump {

    /** Unique identifier for this pump station. */
    private String id;

    /** Maximum fuel capacity of the station (in litres). */
    private int capacity;

    /** List of machines (dispensers) at this station. */
    private List<Machine> machines;

    public PetrolPump() {}

    public PetrolPump(String id, int capacity) {
        this.id = id;
        this.capacity = capacity;
        this.machines = new ArrayList<>();
    }

    /**
     * Initialises this pump with 4 machines — 2 petrol (type=1) and 2 diesel (type=2).
     * Each machine gets a UUID-based name so it is uniquely identifiable.
     */
    public void init() {
        machines = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            machines.add(new Machine("M-P-" + UUID.randomUUID().toString().substring(0, 8), 1));
        }
        for (int i = 1; i <= 2; i++) {
            machines.add(new Machine("M-D-" + UUID.randomUUID().toString().substring(0, 8), 2));
        }
    }

    // -------------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------------

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public List<Machine> getMachines() { return machines; }
    public void setMachines(List<Machine> machines) { this.machines = machines; }

    // -------------------------------------------------------------------------
    // Nested static class: Machine
    // -------------------------------------------------------------------------

    /**
     * Represents a single fuel-dispensing machine at a pump station.
     */
    public static class Machine {

        /** Unique machine name (UUID-based). */
        private String name;

        /**
         * Machine type: 1 = Petrol dispenser, 2 = Diesel dispenser.
         */
        private int type;

        public Machine() {}

        public Machine(String name, int type) {
            this.name = name;
            this.type = type;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getType() { return type; }
        public void setType(int type) { this.type = type; }

        @Override
        public String toString() {
            return "Machine{name='" + name + "', type=" + type + "}";
        }
    }

    @Override
    public String toString() {
        return "PetrolPump{id='" + id + "', capacity=" + capacity
                + ", machines=" + machines + "}";
    }
}
