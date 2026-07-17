package atm;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// --- DTO ---
class TransactionRequest {
    private final String operation;
    private final List<int[]> depositCurrPairs;
    private final Integer withdrawalAmount;

    public TransactionRequest(String operation, List<int[]> depositCurrPairs, Integer withdrawalAmount) {
        this.operation = operation;
        this.depositCurrPairs = depositCurrPairs;
        this.withdrawalAmount = withdrawalAmount;
    }

    public String getOperation() { return operation; }
    public List<int[]> getDepositCurrPairs() { return depositCurrPairs; }
    public Integer getWithdrawalAmount() { return withdrawalAmount; }
}

// --- Helper ---
class Parser {
    public TransactionRequest parse(String... args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Illegal Number of Arguments");
        }

        String operation = args[0];
        if ("deposit".equals(operation)) {
            List<int[]> pairs = new ArrayList<>();
            for (int i = 1; i < args.length; i++) {
                String[] tokens = args[i].replace(",", "").split(":");
                int currDenom = Integer.parseInt(tokens[0]);
                int currNo = Integer.parseInt(tokens[1]);
                pairs.add(new int[]{currDenom, currNo});
            }
            return new TransactionRequest(operation, pairs, 0);
        } else if ("withdraw".equals(operation)) {
            Integer withdrawalAmount = Integer.parseInt(args[1]);
            return new TransactionRequest(operation, null, withdrawalAmount);
        }
        return null;
    }
}

// --- Core ---
class ATM {
    private static final ATM instance = new ATM();

    private final Map<Integer, Integer> currencyDenomHolder;
    private final ConcurrentSkipListSet<Integer> denomSet;
    private int total;
    private final AtomicInteger depositCount = new AtomicInteger(0);
    private final AtomicInteger withdrawCount = new AtomicInteger(0);

    public ATM() {
        this.currencyDenomHolder = new ConcurrentSkipListMap<>();
        this.denomSet = new ConcurrentSkipListSet<>();
        this.total = 0;
    }

    public static ATM getInstance() {
        return instance;
    }

    public void addNewDenominations(int[] denominations) {
        for (int denom : denominations) {
            this.currencyDenomHolder.putIfAbsent(denom, 0);
            this.denomSet.add(denom);
        }
    }

    public boolean deposit(List<int[]> pairs) {
        printDepositMessage(pairs);

        if (!validPairs(pairs)) {
            return false;
        }

        pairs.forEach(pair -> {
            int currDenom = pair[0];
            int currNo = pair[1];
            this.currencyDenomHolder.put(currDenom, this.currencyDenomHolder.getOrDefault(currDenom, 0) + currNo);
        });

        this.calculateTotal();
        this.printBalances();
        return true;
    }

    public boolean withdraw(Integer withdrawalAmount) {
        System.out.println("\nWithdraw " + this.withdrawCount.incrementAndGet() + ": " + withdrawalAmount);

        if (withdrawalAmount > this.total || withdrawalAmount <= 0) {
            System.out.println("Incorrect or insufficient funds");
            return false;
        }

        NavigableSet<Integer> descendingSet = this.denomSet.descendingSet();
        List<Integer> keys = new ArrayList<>(descendingSet);
        int[] dispensedNotes = new int[keys.size()];
        int remainingAmount = withdrawalAmount;

        for (int i = 0; i < keys.size(); i++) {
            int denom = keys.get(i);
            int availableNotes = this.currencyDenomHolder.getOrDefault(denom, 0);

            if (remainingAmount >= denom && availableNotes > 0) {
                int needed = remainingAmount / denom;
                int actualDispensed = Math.min(needed, availableNotes);
                remainingAmount -= (actualDispensed * denom);
                dispensedNotes[i] = actualDispensed;
            }
        }

        if (remainingAmount > 0) {
            System.out.println("Incorrect or insufficient funds");
            return false;
        }

        // Apply changes cleanly since transaction successfully matched completely
        for (int i = 0; i < keys.size(); i++) {
            if (dispensedNotes[i] > 0) {
                int denom = keys.get(i);
                this.currencyDenomHolder.put(denom, this.currencyDenomHolder.get(denom) - dispensedNotes[i]);
            }
        }

        this.calculateTotal();
        this.printWithdrawalAmounts(keys, dispensedNotes);
        this.printBalances();
        return true;
    }

    private void calculateTotal() {
        this.total = currencyDenomHolder.entrySet().stream()
                .mapToInt(e -> e.getKey() * e.getValue()).sum();
    }

    private boolean validPairs(List<int[]> pairs) {
        if (pairs.isEmpty()) {
            System.out.println("Incorrect deposit amount.");
            return false;
        }

        if (pairs.stream().anyMatch(n -> n[1] < 0)) {
            System.out.println("Incorrect deposit amount.");
            return false;
        }

        if (pairs.stream().allMatch(n -> n[1] == 0)) {
            System.out.println("Deposit amount cannot be zero.");
            return false;
        }

        List<Integer> denomArray = pairs.stream().map(p -> p[0]).collect(Collectors.toList());
        if (!this.denomSet.containsAll(denomArray)) {
            System.out.println("All Denominations do not exist.");
            return false;
        }
        return true;
    }

    private void printBalances() {
        StringBuilder balance = new StringBuilder("Balance: ");
        List<Integer> reverseKeys = new ArrayList<>(this.currencyDenomHolder.keySet());
        Collections.reverse(reverseKeys);

        for (int i = 0; i < reverseKeys.size(); i++) {
            int denom = reverseKeys.get(i);
            balance.append(denom).append("s:").append(currencyDenomHolder.get(denom));
            if (i < reverseKeys.size() - 1) balance.append(", ");
        }
        balance.append(", Total=").append(this.total);
        System.out.println(balance);
    }

    private void printWithdrawalAmounts(List<Integer> keys, int[] notes) {
        StringBuilder message = new StringBuilder("Dispensed: ");
        boolean added = false;
        for (int i = 0; i < keys.size(); i++) {
            if (notes[i] > 0) {
                if (added) message.append(", ");
                message.append(keys.get(i)).append("s:").append(notes[i]);
                added = true;
            }
        }
        System.out.println(message);
    }

    private void printDepositMessage(List<int[]> pairs) {
        StringBuilder message = new StringBuilder("\nDeposit " + this.depositCount.incrementAndGet() + ": ");
        for (int i = 0; i < pairs.size(); i++) {
            message.append(pairs.get(i)[0]).append("s:").append(pairs.get(i)[1]);
            if (i < pairs.size() - 1) message.append(", ");
        }
        System.out.println(message);
    }
}

// --- Main App Runner ---
public class AtmApp {
    public static void main(String[] args) {
        Parser parser = new Parser();
        ATM atmInstance = ATM.getInstance();
        atmInstance.addNewDenominations(new int[]{20, 10, 5, 1});

        System.out.println("atm.ATM operational. Examples: 'deposit 10:8 5:20' or 'withdraw 75'");

        // Fix: Kept outside loop to avoid leaking system resources
        try (Scanner in = new Scanner(System.in)) {
            while (in.hasNextLine()) {
                String line = in.nextLine().trim();
                if (line.isEmpty()) continue;

                try {
                    String[] arguments = line.split("\\s+");
                    TransactionRequest transactionRequest = parser.parse(arguments);

                    if (transactionRequest == null) continue;

                    if ("deposit".equals(transactionRequest.getOperation())) {
                        atmInstance.deposit(transactionRequest.getDepositCurrPairs());
                    } else if ("withdraw".equals(transactionRequest.getOperation())) {
                        atmInstance.withdraw(transactionRequest.getWithdrawalAmount());
                    }
                } catch (Exception e) {
                    System.out.println("Error processing command: " + e.getMessage());
                }
            }
        }
    }
}