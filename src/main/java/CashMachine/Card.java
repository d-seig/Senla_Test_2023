package CashMachine;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Card implements CashInterface {

    private final BankAccount bankAccount;
    private final String number;
    private final long dayInMillis = 8640000;
    private final int PIN;
    private final int maxTakingCash = 1000000;
    private final byte PIN_tries = 3;
    private CashMachine cashMachine = new CashMachine();
    private boolean isBlocked;
    private boolean isVerified = false;
    public Card(String number) {
        List<String> accountParams = DataBase.getInstance().findCard(number);

        bankAccount = new BankAccount(accountParams.get(0), Integer.parseInt(accountParams.get(2)));
        this.number = accountParams.get(0);

        PIN = Integer.parseInt(accountParams.get(1));

        String lastblockDate = accountParams.get(3);
        long lastBlockDate = Long.parseLong(lastblockDate);
        if(!lastblockDate.equals("null") && (new Date().getTime()) < lastBlockDate + dayInMillis)
            isBlocked = true;
        else {
            isBlocked = false;
            DataBase.getInstance().writeLastBlockDate(number, null, true);
        }
    }
    private void blockIsAll() {
        List<String> accountParams = DataBase.getInstance().findCard(number);
        String lastblockDate = accountParams.get(3);
        long lastBlockDate = Long.parseLong(lastblockDate);
        if(!lastblockDate.equals("null") && (new Date().getTime()) < lastBlockDate + dayInMillis)
            isBlocked = true;
        else {
            isBlocked = false;
            DataBase.getInstance().writeLastBlockDate(number, null, true);
        }
    }
    public void singIn(Scanner scanner) {
        int PIN;

        byte i = 1;
        for(; i<= PIN_tries; i++) {
            System.out.println("Введите пин-код: ");
            PIN = Integer.parseInt(scanner.nextLine());

            if(this.PIN == PIN) {
                isVerified = true;
                actions(scanner);
                break;
            }
            System.out.println("Пин-код неверен. Осталось попыток: " + (PIN_tries - i));
        }
        if(i >= 3) {
            isBlocked = true;
            DataBase.getInstance().writeLastBlockDate(number, "" + new Date().getTime(), true);
        }
    }

    private void actions(Scanner scanner) {
        byte STATE = -1;
        DataBase dataBase = DataBase.getInstance();
        while (STATE != 0) {
            System.out.println("Выберите совершаемую операцию: " +
                    "\n [1] Узнать баланс (остаток на счете)" +
                    "\n [2] Снять наличные" +
                    "\n [3] Пополнить баланс" +
                    "\n [4] Перевести на другой счет" +
                    "\n [0] Выход");
            blockIsAll();
            try {
                STATE = Byte.parseByte(scanner.nextLine());
                int cashes;
                switch (STATE) {
                    case 0:
                        break;
                    case 1:
                        System.out.println("Остаток на карте: " + getBalance() + " денег");
                        break;
                    case 2:
                        if(!isBlocked) {
                            System.out.println("Какую сумму желаете снять?");
                            cashes = getCash(Integer.parseInt(scanner.nextLine()));
                            dataBase.writeStringLogs(number + " " + -cashes + " " + bankAccount.getBalance() + " ");
                            dataBase.writeNewCardBalance(number, bankAccount.getBalance());
                            System.err.println("Банкомат выдает: " + getCashFromMachine(cashes) + " денег");
                        } else {
                            System.out.println("Карта заблокирована! Подойдите завтра...");
                        }
                        break;
                    case 3:
                        if(!isBlocked) {
                            System.out.println("Внесите необходимую сумму: ");
                            cashes = pushCash(Integer.parseInt(scanner.nextLine()));
                            dataBase.writeStringLogs(number + " " + cashes + " " + bankAccount.getBalance() + " ");
                            dataBase.writeNewCardBalance(number, bankAccount.getBalance());
                            System.err.println("Банкомат получает: " + inputCashToMachine(cashes) + " денег");
                        } else {
                            System.out.println("Карта заблокирована! Подойдите завтра...");
                        }
                        break;
                    case 4:
                        if(!isBlocked) {
                            System.out.println("Данная операция недоступна. Повторите попытку позднее...");
                        } else {
                            System.out.println("Карта заблокирована! Подойдите завтра...");
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                System.err.println("Неверный формат ввода! Вводите только цифры!\n" +
                        "Ошибка: " + e);
            }
        }
    }
    private int getBalance() {
        return bankAccount.getBalance();
    }
    private int getCash(int cash) {
        try {
            if (!isBlocked && isVerified) {
                bankAccount.takesMoney(cash);
                return cash;
            } else {
                System.out.println("Неверно введен пин или карта заблокирована.");
                return 0;
            }
        } catch (Exception e) {
            System.err.println("Введенная сумма в неправильном формате! Используйте целочисленные значения!\n" +
                    "Ошибка: " + e);
            return 0;
        }
    }

    private int pushCash(int cash) {
        try {
            if (cash < maxTakingCash) {
                bankAccount.pushesMoney(cash);
                return cash;
            } else {
                System.out.println("Операция прервана. Превышен лимит единоразового снятия денег!");
                return 0;
            }
        } catch (Exception e) {
            System.err.println("Введенная сумма в неправильном формате! Используйте целочисленные значения!\n" +
                    "Ошибка: " + e);
            return 0;
        }
    }

    protected void setCashMachine(CashMachine cashMachine) {
        this.cashMachine = cashMachine;
    }

    @Override
    public int getCashFromMachine(int cash) {
        try {
            if (cash > cashMachine.getCash() && cash > bankAccount.getBalance()) {
                System.out.println("Операция отменена. Банкомат пуст!");
                return 0;
            } else {
                cashMachine.inputCash(-cash);
                return cash;
            }
        } catch (Exception e) {
            System.err.println("Введенная сумма в неправильном формате! Используйте целочисленные значения!\n" +
                    "Ошибка: " + e);
            return 0;
        }
    }

    @Override
    public int inputCashToMachine(int cash) {
        try {
            if (cash < maxTakingCash) {
                cashMachine.inputCash(cash);
                return cash;
            } else {
                System.err.println("Банкомат не может принять эту сумму!");
                return 0;
            }
        } catch (Exception e) {
            System.err.println("Введенная сумма в неправильном формате! Используйте целочисленные значения!\n" +
                    "Ошибка: " + e);
            return 0;
        }
    }

}
