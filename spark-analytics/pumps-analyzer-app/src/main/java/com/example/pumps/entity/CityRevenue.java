package com.example.pumps.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JPA entity representing the aggregated revenue for a specific city on a given date.
 *
 * <p>Maps to the {@code city_revenue} table with a unique constraint on
 * {@code (pdate, city)} to allow idempotent upsert operations.
 */
@Entity
@Table(name = "city_revenue",
        uniqueConstraints = @UniqueConstraint(name = "uq_city_revenue", columnNames = {"pdate", "city"}))
public class CityRevenue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The date on which the revenue was collected. */
    @Column(nullable = false)
    private LocalDate pdate;

    /** Name of the city. */
    @Column(nullable = false, length = 100)
    private String city;

    /** Cumulative revenue (INR) collected for this city on this date. */
    @Column(name = "total_amt")
    private Long totalAmt = 0L;

    /** Timestamp when this record was first created. */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** Timestamp of the last update to this record. */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public CityRevenue() {}

    public CityRevenue(LocalDate pdate, String city, Long totalAmt) {
        this.pdate = pdate;
        this.city = city;
        this.totalAmt = totalAmt;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // -------------------------------------------------------------------------
    // Lifecycle callbacks
    // -------------------------------------------------------------------------

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // -------------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getPdate() { return pdate; }
    public void setPdate(LocalDate pdate) { this.pdate = pdate; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Long getTotalAmt() { return totalAmt; }
    public void setTotalAmt(Long totalAmt) { this.totalAmt = totalAmt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "CityRevenue{id=" + id + ", pdate=" + pdate
                + ", city='" + city + "', totalAmt=" + totalAmt + "}";
    }
}
