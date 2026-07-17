package com.example.pumps.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JPA entity representing the aggregated revenue broken down by date, hour, and fuel type.
 *
 * <p>Maps to the {@code daily_revenue} table. A unique constraint on
 * {@code (pdate, hour, fuel_type)} ensures a single row per slot for idempotent upserts.
 */
@Entity
@Table(name = "daily_revenue",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_daily_revenue",
                columnNames = {"pdate", "hour", "fuel_type"}))
public class DailyRevenue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The date of the revenue slot. */
    @Column(nullable = false)
    private LocalDate pdate;

    /** The hour-of-day (0–23) for this revenue slot. */
    @Column(nullable = false)
    private int hour;

    /**
     * Fuel type for this slot: 1 = Petrol, 2 = Diesel.
     */
    @Column(name = "fuel_type", nullable = false)
    private int fuelType;

    /** Total quantity of fuel dispensed (litres) in this slot. */
    @Column(nullable = false)
    private Long qty = 0L;

    /** Total revenue (INR) collected in this slot. */
    @Column(nullable = false)
    private Long amt = 0L;

    /** Timestamp when this record was first created. */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DailyRevenue() {}

    public DailyRevenue(LocalDate pdate, int hour, int fuelType, Long qty, Long amt) {
        this.pdate = pdate;
        this.hour = hour;
        this.fuelType = fuelType;
        this.qty = qty;
        this.amt = amt;
        this.createdAt = LocalDateTime.now();
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    // -------------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getPdate() { return pdate; }
    public void setPdate(LocalDate pdate) { this.pdate = pdate; }

    public int getHour() { return hour; }
    public void setHour(int hour) { this.hour = hour; }

    public int getFuelType() { return fuelType; }
    public void setFuelType(int fuelType) { this.fuelType = fuelType; }

    public Long getQty() { return qty; }
    public void setQty(Long qty) { this.qty = qty; }

    public Long getAmt() { return amt; }
    public void setAmt(Long amt) { this.amt = amt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "DailyRevenue{id=" + id + ", pdate=" + pdate
                + ", hour=" + hour + ", fuelType=" + fuelType
                + ", qty=" + qty + ", amt=" + amt + "}";
    }
}
