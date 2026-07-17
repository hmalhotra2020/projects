package com.example.pumps.store;

import com.example.pumps.entity.CityRevenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link CityRevenue} entities.
 *
 * <p>Provides finder methods for city-level revenue queries used by the
 * Kafka consumer (upsert) and the dashboard API (leaderboard).
 */
public interface CityRevenueRepository extends JpaRepository<CityRevenue, Long> {

    /**
     * Looks up an existing city-revenue record for the given date and city name.
     * Used by the consumer to decide whether to insert a new row or update an existing one.
     *
     * @param pdate date of the revenue record
     * @param city  city name
     * @return an {@link Optional} wrapping the found entity, or empty if none exists
     */
    Optional<CityRevenue> findByPdateAndCity(LocalDate pdate, String city);

    /**
     * Returns all city-revenue records grouped by city, ordered by total revenue descending.
     * Useful for building a global leaderboard that spans multiple days.
     *
     * @return list of {@link CityRevenue} records
     */
    @Query("SELECT c FROM CityRevenue c GROUP BY c.city, c.id ORDER BY c.totalAmt DESC")
    List<CityRevenue> findTopCities();

    /**
     * Returns the top 10 city-revenue records ordered by total revenue descending.
     * Used by the dashboard API to power the doughnut and bar charts.
     *
     * @return list of up to 10 {@link CityRevenue} records
     */
    List<CityRevenue> findTop10ByOrderByTotalAmtDesc();
}
