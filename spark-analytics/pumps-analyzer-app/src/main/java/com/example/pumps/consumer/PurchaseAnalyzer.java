package com.example.pumps.consumer;

import com.example.pumps.entity.CityRevenue;
import com.example.pumps.entity.DailyRevenue;
import com.example.pumps.model.PurchaseOrder;
import com.example.pumps.store.CityRevenueRepository;
import com.example.pumps.store.DailyRevenueRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Kafka consumer that subscribes to the {@code purchases} topic and aggregates
 * incoming {@link PurchaseOrder} events into the PostgreSQL database.
 *
 * <p>Two tables are maintained:
 * <ul>
 *   <li>{@code city_revenue} — cumulative revenue per city per day</li>
 *   <li>{@code daily_revenue} — revenue broken down by day, hour, and fuel type</li>
 * </ul>
 */
@Slf4j
@Service
public class PurchaseAnalyzer {

    /** Format used to extract a date string from the SQL-style purchase timestamp. */
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final CityRevenueRepository cityRevenueRepo;
    private final DailyRevenueRepository dailyRevenueRepo;

    public PurchaseAnalyzer(CityRevenueRepository cityRevenueRepo,
                            DailyRevenueRepository dailyRevenueRepo) {
        this.cityRevenueRepo = cityRevenueRepo;
        this.dailyRevenueRepo = dailyRevenueRepo;
    }

    /**
     * Receives a raw pipe-delimited Kafka message, parses it into a {@link PurchaseOrder},
     * and upserts the aggregated values into both {@code city_revenue} and
     * {@code daily_revenue} tables.
     *
     * @param message the raw Kafka message value (pipe-delimited)
     */
    @Transactional
    @KafkaListener(topics = "purchases", groupId = "pumps-analyzer")
    public void analyze(String message) {
        try {
            PurchaseOrder order = PurchaseOrder.fromPipeString(message);
            log.debug("Consumed: {}", order);

            // Extract date from "2024-01-14 09:30:00.0" → LocalDate 2024-01-14
            LocalDate pdate = LocalDate.parse(order.getPurchaseTime().substring(0, 10), DATE_FORMATTER);

            // Extract hour from purchase timestamp
            int hour = LocalDateTime.parse(
                    order.getPurchaseTime().substring(0, 19),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            ).getHour();

            upsertCityRevenue(pdate, order.getCity(), order.getAmt());
            upsertDailyRevenue(pdate, hour, order.getFuelType(), order.getQnty(), order.getAmt());

        } catch (Exception e) {
            log.error("Failed to process message: '{}' — {}", message, e.getMessage(), e);
        }
    }

    // -------------------------------------------------------------------------
    // Private upsert helpers
    // -------------------------------------------------------------------------

    /**
     * Upserts a {@link CityRevenue} row for the given date and city.
     * If a row already exists, {@code totalAmt} is incremented; otherwise a new row is created.
     */
    private void upsertCityRevenue(LocalDate pdate, String city, int amt) {
        cityRevenueRepo.findByPdateAndCity(pdate, city).ifPresentOrElse(
                existing -> {
                    existing.setTotalAmt(existing.getTotalAmt() + amt);
                    cityRevenueRepo.save(existing);
                },
                () -> cityRevenueRepo.save(new CityRevenue(pdate, city, (long) amt))
        );
    }

    /**
     * Upserts a {@link DailyRevenue} row for the given date, hour, and fuel type.
     * If a row already exists, both {@code qty} and {@code amt} are incremented.
     */
    private void upsertDailyRevenue(LocalDate pdate, int hour, int fuelType, int qty, int amt) {
        dailyRevenueRepo.findByPdateAndHourAndFuelType(pdate, hour, fuelType).ifPresentOrElse(
                existing -> {
                    existing.setQty(existing.getQty() + qty);
                    existing.setAmt(existing.getAmt() + amt);
                    dailyRevenueRepo.save(existing);
                },
                () -> dailyRevenueRepo.save(
                        new DailyRevenue(pdate, hour, fuelType, (long) qty, (long) amt))
        );
    }
}
