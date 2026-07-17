package com.example.pumps.producer;

import com.example.pumps.model.PurchaseOrder;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Simulates petrol pump purchase activity by generating random {@link PurchaseOrder}s
 * and publishing them to the Kafka {@code purchases} topic on a fixed-delay schedule.
 *
 * <p>Configuration knobs (application.yml):
 * <ul>
 *   <li>{@code simulator.no-pumps}      — number of pump stations to create</li>
 *   <li>{@code simulator.hits-per-tick} — orders generated per scheduling tick</li>
 *   <li>{@code simulator.tick-time}     — delay (ms) between ticks (default 5000)</li>
 * </ul>
 */
@Slf4j
@Service
public class PurchaseSimulator {

    private static final String TOPIC = "purchases";
    private static final Random RANDOM = new Random();

    // -------------------------------------------------------------------------
    // Injected dependencies
    // -------------------------------------------------------------------------

    private final PPInfoProvider ppInfoProvider;
    private final CityProvider cityProvider;
    private final KafkaTemplate<String, String> kafkaTemplate;

    // -------------------------------------------------------------------------
    // Configuration properties
    // -------------------------------------------------------------------------

    @Value("${simulator.no-pumps:10}")
    private int noPumps;

    @Value("${simulator.hits-per-tick:10}")
    private int hitsPerTick;

    // -------------------------------------------------------------------------
    // Runtime state
    // -------------------------------------------------------------------------

    private List<PetrolPump> pumps;
    private List<String> cities;

    public PurchaseSimulator(PPInfoProvider ppInfoProvider,
                             CityProvider cityProvider,
                             KafkaTemplate<String, String> kafkaTemplate) {
        this.ppInfoProvider = ppInfoProvider;
        this.cityProvider = cityProvider;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Initialises pump and city data after the bean is constructed.
     */
    @PostConstruct
    public void init() {
        pumps = ppInfoProvider.loadPumps(noPumps);
        cities = cityProvider.getCityList();
        log.info("PurchaseSimulator initialised: {} pumps, {} cities",
                pumps.size(), cities.size());
    }

    /**
     * Scheduled method that fires every {@code simulator.tick-time} ms (default 5 s)
     * with an initial 2-second delay to allow Kafka to be ready.
     *
     * <p>Each invocation produces {@code simulator.hits-per-tick} random purchase orders.
     */
    @Scheduled(fixedDelayString = "${simulator.tick-time:5000}", initialDelay = 2000)
    public void producePurchaseOrders() {
        for (int i = 0; i < hitsPerTick; i++) {
            PurchaseOrder order = generateRandomOrder();
            kafkaTemplate.send(TOPIC, order.toString());
            log.info("Produced: {}", order);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Generates a single random {@link PurchaseOrder} from the available pumps and cities.
     */
    private PurchaseOrder generateRandomOrder() {
        PetrolPump pump = pumps.get(RANDOM.nextInt(pumps.size()));
        PetrolPump.Machine machine = pump.getMachines().get(
                RANDOM.nextInt(pump.getMachines().size()));
        String city = cities.get(RANDOM.nextInt(cities.size()));

        // fuelType driven by the machine type
        int fuelType = machine.getType();  // 1=petrol, 2=diesel

        int qnty = 1 + RANDOM.nextInt(10);           // 1–10 litres
        int amt = qnty * (fuelType == 1 ? 105 : 92); // petrol=105/L, diesel=92/L
        int pType = 1 + RANDOM.nextInt(3);            // 1=Cash, 2=Card, 3=UPI

        // Timestamp as SQL-style string: "2024-01-14 09:30:00.0"
        String purchaseTime = Timestamp.valueOf(LocalDateTime.now()).toString();

        return PurchaseOrder.builder()
                .petrolPumpId(pump.getId())
                .machineId(machine.getName())
                .city(city)
                .purchaseTime(purchaseTime)
                .fuelType(fuelType)
                .qnty(qnty)
                .amt(amt)
                .pType(pType)
                .build();
    }
}
