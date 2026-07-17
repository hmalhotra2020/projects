package com.example.pumps.store;

import com.example.pumps.entity.DailyRevenue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link DailyRevenue} entities.
 *
 * <p>Provides finder methods for hourly fuel-type revenue slots used by the
 * Kafka consumer (upsert) and the dashboard API (trend chart).
 */
public interface DailyRevenueRepository extends JpaRepository<DailyRevenue, Long> {

    /**
     * Looks up a specific revenue slot identified by date, hour, and fuel type.
     * Used by the consumer to decide whether to insert or accumulate.
     *
     * @param pdate    the date
     * @param hour     the hour-of-day (0–23)
     * @param fuelType fuel type (1 = Petrol, 2 = Diesel)
     * @return an {@link Optional} wrapping the found entity, or empty if none exists
     */
    Optional<DailyRevenue> findByPdateAndHourAndFuelType(LocalDate pdate, int hour, int fuelType);

    /**
     * Returns the 30 most recent daily-revenue records ordered by date descending.
     * Used by the dashboard API to power the daily trend area chart.
     *
     * @return list of up to 30 {@link DailyRevenue} records
     */
    List<DailyRevenue> findTop30ByOrderByPdateDesc();
}
