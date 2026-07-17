package com.example.pumps.producer;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Spring component that provides a list of initialised {@link PetrolPump} instances.
 * Callers specify how many pumps they need; each is assigned a random UUID-based ID
 * and has its machines initialised via {@link PetrolPump#init()}.
 */
@Component
public class PPInfoProvider {

    /**
     * Creates and initialises {@code n} petrol pump instances.
     *
     * @param n number of pumps to create
     * @return immutable-style list of initialised {@link PetrolPump} objects
     */
    public List<PetrolPump> loadPumps(int n) {
        List<PetrolPump> pumps = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            PetrolPump pump = new PetrolPump(
                    "PP-" + UUID.randomUUID().toString().substring(0, 8),
                    1000 + (i * 500)   // capacity varies per pump: 1000, 1500, 2000, ...
            );
            pump.init();
            pumps.add(pump);
        }
        return pumps;
    }
}
