import java.util.Random;

public class Bank {
    private final Account accountA;
    private final Account accountB;

    public Bank(Account accountA, Account accountB) {
        this.accountA = accountA;
        this.accountB = accountB;
    }

    public void depositOperation(String cashierName, int accountId, double amount) {
        Account targetAccount = getAccountById(accountId);
        if (targetAccount != null) {
            targetAccount.deposit(amount);
            System.out.printf("[%s] Ingreso de %.2f€ realizado en Cuenta %d. Saldo actual: %.2f€\n",
                    cashierName, amount, targetAccount.getId(), targetAccount.getBalance());
        }
    }

    public void withdrawOperation(String cashierName, int accountId, double amount) {
        Account targetAccount = getAccountById(accountId);
        if (targetAccount != null) {
            boolean success = targetAccount.withdraw(amount);
            if (success) {
                System.out.printf("[%s] Retiro de %.2f€ exitoso en Cuenta %d. Saldo actual: %.2f€\n",
                        cashierName, amount, targetAccount.getId(), targetAccount.getBalance());
            } else {
                System.out.printf("[%s] Retiro fallido en Cuenta %d (fondos insuficientes o importe inválido).\n",
                        cashierName, targetAccount.getId());
            }
        }
    }

    public void transferOperation(String cashierName, int sourceId, int targetId, double amount) {
        if (sourceId == targetId) {
            return;
        }

        Account sourceAccount = getAccountById(sourceId);
        Account targetAccount = getAccountById(targetId);

        if (sourceAccount == null || targetAccount == null) {
            return;
        }

        Account firstLock = (sourceAccount.getId() < targetAccount.getId()) ? sourceAccount : targetAccount;
        Account secondLock = (sourceAccount.getId() < targetAccount.getId()) ? targetAccount : sourceAccount;

        synchronized (firstLock) {
            synchronized (secondLock) {
                if (sourceAccount.getBalance() < amount) {
                    System.out.printf("[%s] Transferencia fallida de Cuenta %d a Cuenta %d: Saldo insuficiente (%.2f€ disponibles, %.2f€ solicitados).\n",
                            cashierName, sourceAccount.getId(), targetAccount.getId(), sourceAccount.getBalance(), amount);
                    return;
                }

                sourceAccount.withdraw(amount);
                targetAccount.deposit(amount);

                System.out.printf("[%s] Transferencia interna completada de Cuenta %d a Cuenta %d por %.2f€.\n",
                        cashierName, sourceAccount.getId(), targetAccount.getId(), amount);
            }
        }

        try {
            Thread.sleep(50);
            System.out.printf("[%s] [AUDITORÍA] Registro en archivo de transferencia: %.2f€ (Cuenta %d -> Cuenta %d)\n",
                    cashierName, amount, sourceAccount.getId(), targetAccount.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Account getAccountById(int id) {
        if (accountA.getId() == id) {
            return accountA;
        } else if (accountB.getId() == id) {
            return accountB;
        }
        return null;
    }
}