package atm;

import java.util.*;

public class StandardATM {

    // Sorted descending by denomination value automatically
    private final Map<Integer, Integer> vault = new TreeMap<>(Collections.reverseOrder());
    private int runningTotal = 0;
    private int depositCounter = 0;
    private int withdrawalCounter = 0;

    public StandardATM(int... initialSupportedDenominations) {
        for (int denom : initialSupportedDenominations) {
            vault.put(denom, 0);
        }
    }

    public synchronized void deposit(Map<Integer, Integer> notesToDeposit) {
        depositCounter++;

        // Print requested deposit input format
        System.out.print("\nDeposit " + depositCounter + ": ");
        printMapFormat(notesToDeposit);

        // Validation Rules
        if (notesToDeposit.values().stream().anyMatch(count -> count < 0)) {
            System.out.println("Incorrect deposit amount");
            return;
        }
        if (notesToDeposit.values().stream().allMatch(count -> count == 0)) {
            System.out.println("Deposit amount cannot be zero");
            return;
        }

        // Process additions
        for (Map.Entry<Integer, Integer> entry : notesToDeposit.entrySet()) {
            int denom = entry.getKey();
            if (!vault.containsKey(denom)) {
                System.out.println("All Denominations do not exist.");
                return;
            }
            vault.put(denom, vault.get(denom) + entry.getValue());
            runningTotal += denom * entry.getValue();
        }

        printCurrentInventory();
    }

    public synchronized void withdraw(int amount) {
        withdrawalCounter++;
        System.out.println("\nWithdraw " + withdrawalCounter + ": " + amount);

        if (amount <= 0 || amount > runningTotal) {
            System.out.println("Incorrect or insufficient funds");
            return;
        }

        Map<Integer, Integer> dispensed = new LinkedHashMap<>();
        int remaining = amount;

        // Greedy algorithm calculation using our naturally sorted vault
        for (Map.Entry<Integer, Integer> entry : vault.entrySet()) {
            int denom = entry.getKey();
            int availableNotes = entry.getValue();

            if (remaining >= denom && availableNotes > 0) {
                int requiredNotes = remaining / denom;
                int actualNotesToDispense = Math.min(requiredNotes, availableNotes);

                dispensed.put(denom, actualNotesToDispense);
                remaining -= actualNotesToDispense * denom;
            }
        }

        // If the exact value cannot be matched using existing bills
        if (remaining > 0) {
            System.out.println("Incorrect or insufficient funds");
            return;
        }

        // Apply mutations onto the state only after confirmation
        dispensed.forEach((denom, count) -> {
            vault.put(denom, vault.get(denom) - count);
            runningTotal -= denom * count;
        });

        System.out.print("Dispensed: ");
        printMapFormat(dispensed);
        printCurrentInventory();
    }

    private void printCurrentInventory() {
        StringBuilder sb = new StringBuilder("Balance: ");
        vault.forEach((denom, count) -> sb.append(denom).append("s:").append(count).append(", "));
        sb.append("Total=").append(runningTotal);
        System.out.println(sb);
    }

    private void printMapFormat(Map<Integer, Integer> map) {
        StringBuilder sb = new StringBuilder();
        map.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .forEach(e -> sb.append(e.getKey()).append("s:").append(e.getValue()).append(", "));
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
        }
        System.out.println(sb);
    }

    // --- Elegant Console Interface Handler ---
    public static void main(String[] args) {
        // Easily expand by adding 50, 100 right here without modifying logic.
        StandardATM atm = new StandardATM(100, 50, 20, 10, 5, 1);

        System.out.println("atm.ATM Booted successfully. Accepting transactions...");
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) continue;

                String[] parts = input.split("\\s+");
                String command = parts[0].toLowerCase();

                try {
                    if ("deposit".equals(command)) {
                        Map<Integer, Integer> deposits = new LinkedHashMap<>();
                        for (int i = 1; i < parts.length; i++) {
                            String[] token = parts[i].split(":");
                            deposits.put(Integer.parseInt(token[0]), Integer.parseInt(token[1]));
                        }
                        atm.deposit(deposits);
                    } else if ("withdraw".equals(command)) {
                        atm.withdraw(Integer.parseInt(parts[1]));
                    } else {
                        System.out.println("Unknown action command.");
                    }
                } catch (Exception ex) {
                    System.out.println("Parsing error. Formats: 'deposit 20:3 10:2' or 'withdraw 40'");
                }
            }
        }
    }
}