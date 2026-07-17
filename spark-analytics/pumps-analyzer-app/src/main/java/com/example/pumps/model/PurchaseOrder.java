package com.example.pumps.model;

import lombok.Builder;
import lombok.Data;

/**
 * Represents a single petrol pump purchase order.
 * Serialized to/from a pipe-delimited string for Kafka transport.
 *
 * Pipe format (0-indexed fields):
 *   0: petrolPumpId | 1: machineId | 2: city | 3: purchaseTime
 *   4: fuelType     | 5: qnty      | 6: amt  | 7: pType
 */
@Data
@Builder
public class PurchaseOrder {

    /** Unique identifier of the petrol pump station. */
    private String petrolPumpId;

    /** Identifier for the specific dispenser machine. */
    private String machineId;

    /** City where the pump is located. */
    private String city;

    /**
     * Timestamp of the purchase in SQL format, e.g. {@code 2024-01-14 09:30:00.0}.
     */
    private String purchaseTime;

    /**
     * Fuel type: 1 = Petrol, 2 = Diesel.
     */
    private int fuelType;

    /** Quantity of fuel dispensed (in litres). */
    private int qnty;

    /** Total amount charged (in INR). */
    private int amt;

    /**
     * Payment type: 1 = Cash, 2 = Card, 3 = UPI.
     */
    private int pType;

    /**
     * Serializes this order to a pipe-delimited string suitable for Kafka.
     *
     * @return pipe-delimited string representation
     */
    @Override
    public String toString() {
        return petrolPumpId + "|" + machineId + "|" + city + "|" + purchaseTime
                + "|" + fuelType + "|" + qnty + "|" + amt + "|" + pType;
    }

    /**
     * Deserializes a pipe-delimited string (from Kafka) back into a {@link PurchaseOrder}.
     *
     * @param line the pipe-delimited Kafka message value
     * @return a populated {@link PurchaseOrder}
     * @throws IllegalArgumentException if the line does not contain exactly 8 fields
     */
    public static PurchaseOrder fromPipeString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 8) {
            throw new IllegalArgumentException(
                    "Expected 8 pipe-delimited fields, got " + parts.length + " in: " + line);
        }
        return PurchaseOrder.builder()
                .petrolPumpId(parts[0])
                .machineId(parts[1])
                .city(parts[2])
                .purchaseTime(parts[3])
                .fuelType(Integer.parseInt(parts[4].trim()))
                .qnty(Integer.parseInt(parts[5].trim()))
                .amt(Integer.parseInt(parts[6].trim()))
                .pType(Integer.parseInt(parts[7].trim()))
                .build();
    }
}
