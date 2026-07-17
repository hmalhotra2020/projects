package com.example.processor.model;

/**
 * Represents a petrol pump purchase order.
 * Wire format (pipe-delimited): petrolPumpId|machineId|city|purchaseTime|fuelType|qnty|amt|pType
 */
public record PurchaseOrder(
    String petrolPumpId,
    String machineId,
    String city,
    String purchaseTime,
    int fuelType,
    int qnty,
    int amt,
    int pType
) {
    public static PurchaseOrder fromPipeString(String line) {
        String[] f = line.split("\\|");
        if (f.length < 8) throw new IllegalArgumentException("Invalid order: " + line);
        return new PurchaseOrder(f[0], f[1], f[2], f[3],
                Integer.parseInt(f[4].trim()),
                Integer.parseInt(f[5].trim()),
                Integer.parseInt(f[6].trim()),
                Integer.parseInt(f[7].trim()));
    }
}
