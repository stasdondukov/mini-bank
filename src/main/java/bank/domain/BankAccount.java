package bank.domain;

import java.util.Objects;

public abstract class BankAccount implements Identifiable<AccountNumber> {
    private static final Object EQUAL_HASH_LOCK = new Object();
    private final AccountNumber number;
    private final String owner;
    private double balance;

    protected BankAccount(AccountNumber number, String owner, double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Начальный баланс не может быть отрицательным");
        }
        this.number = number;
        this.owner = owner;
        this.balance = initialBalance;
    }

    protected BankAccount(String number, String owner, double initialBalance) {
        this(new AccountNumber(number), owner, initialBalance);
    }

    public synchronized void deposit(double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }
        this.balance += amount;
    }

    public synchronized void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (amount > getAvailableAmount()) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        decreaseBalance(amount);
    }

    public void transferTo(BankAccount destination, double amount, double commission) {
        Objects.requireNonNull(destination);
        int sourceHash = System.identityHashCode(this);
        int destinationHash = System.identityHashCode(destination);

        if (sourceHash == destinationHash && this != destination) {
            synchronized (EQUAL_HASH_LOCK) {
                transferWithLocks(destination, amount, commission, true);
            }
        } else {
            transferWithLocks(destination, amount, commission, sourceHash <= destinationHash);
        }
    }

    private void transferWithLocks(BankAccount destination, double amount, double commission,
                                   boolean sourceFirst) {
        BankAccount first = sourceFirst ? this : destination;
        BankAccount second = sourceFirst ? destination : this;
        synchronized (first) {
            synchronized (second) {
                transferLocked(destination, amount, commission);
            }
        }
    }

    private void transferLocked(BankAccount destination, double amount, double commission) {
        double sourceBalance = getBalance();
        double destinationBalance = destination.getBalance();
        try {
            withdraw(amount + commission);
            destination.deposit(amount);
        } catch (RuntimeException exception) {
            setBalance(sourceBalance);
            destination.setBalance(destinationBalance);
            throw exception;
        }
    }

    protected abstract double getAvailableAmount();

    protected synchronized void decreaseBalance(double amount) {
        this.balance -= amount;
    }

    public synchronized double getBalance() {
        return balance;
    }

    protected synchronized void setBalance(double balance) {
        this.balance = balance;
    }

    public AccountNumber getNumber() {
        return number;
    }

    public String getOwner() {
        return owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BankAccount other)) return false;
        return number.equals(other.number);
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "number='" + number.value() + '\'' +
                ", owner='" + owner + '\'' +
                ", balance=" + getBalance() +
                '}';
    }

    @Override
    public AccountNumber getId() {
        return number;
    }
}
