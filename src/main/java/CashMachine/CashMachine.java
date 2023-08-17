package CashMachine;

import java.util.Scanner;

public class CashMachine{
    private final String KEY = "34Y59F8";
    private String name;
    private String company;

    private boolean isCardIn = false;
    private int cash;

    public CashMachine() {
        name = "DeCash M1982";
        company = "CashIndustries";
        cash = 100000;
    }
    public CashMachine(String name, String company) {
            this.name = name;
            this.company = company;
    }
    private void inputCard(CashMachine cashMachine, Scanner scan, Card card) {
        card.setCashMachine(cashMachine);
        card.singIn(scan);
    }
    protected int getCash() {
        return this.cash;
    }
    protected void inputCash(int cash) {
        try {
            this.cash += cash;
        }catch (Exception e) {
            System.err.println("Введенная сумма в неправильном формате! Используйте целочисленные значения!\n" +
                    "Ошибка: " + e);
        }
    }
    public void getTextInterface(Scanner scanner) {
        byte STATE = -1;
        while (STATE != 0) {
            System.out.println("Выберите совершаемую операцию: " +
                    "\n [1] Вставить карту" +
                    "\n [2] Инкассация (взятие)" +
                    "\n [3] Инкассация (пополнение)" +
                    "\n [4] Открыть банкомат (логи)" +
                    "\n [0] Завершение работы");
            try {
                STATE = Byte.parseByte(scanner.nextLine());
                switch(STATE) {
                    case 0:
                        break;
                    case 1:
                        System.out.println("Вставьте карту: ");
                        isCardIn = true;
                        String inputNumber = scanner.nextLine();
                        if(new BankAccount().verifyNumber(inputNumber)
                                && DataBase.getInstance().findCard(inputNumber).size() > 0)
                            inputCard(this , scanner, new Card(inputNumber));
                        else System.out.println("Карта не зарегистрирована!");
                        System.out.println("Заберите карту...");
                        isCardIn = false;
                        break;
                    case 2:
                        System.out.println("Введите ключ: ");
                        if(scanner.nextLine().equals(KEY)) {
                            System.out.println("Какое количество денег желаете забрать?");
                            incashingOut(Integer.parseInt(scanner.nextLine()));
                        } else {
                            System.out.println("Введенный ключ неверен!");
                        }
                        break;
                    case 3:
                        System.out.println("Введите ключ: ");
                        if(scanner.nextLine().equals(KEY)) {
                            System.out.println("Какое количество денег желаете положить?");
                            incashingIn(Integer.parseInt(scanner.nextLine()));
                        } else {
                            System.out.println("Введенный ключ неверен!");
                        }
                        break;
                    case 4:
                        System.out.println("Введите ключ: ");
                        if(scanner.nextLine().equals(KEY)) {
                            System.out.println("Банкомат: " + company + " " + name);
                            DataBase dataBase = DataBase.getInstance();
                            System.err.println("Логи: ");
                            dataBase.printCacheLogs();
                            System.err.println("Данные по картам: ");
                            dataBase.printCacheCards();
                            break;
                        } else {
                            System.out.println("Введенный ключ неверен!");
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
    private void incashingIn(int cash) {
        try {
            this.cash += cash;
            System.err.println("Внесено денег инкассацией: " + cash + ". Остаток в банкомате: " + getCash());
        } catch (Exception e) {
            System.err.println("Введенная сумма в неправильном формате! Используйте целочисленные значения!\n" +
                    "Ошибка: " + e);
        }
    }
    private void incashingOut(int cash) {
        try {
            if (cash < this.cash) {
                this.cash -= cash;
                System.err.println("Взято денег инкассацией: " + cash + ". Остаток в банкомате: " + getCash());
            } else {
                this.cash = 0;
                System.err.println("В банкомате недостаточно наличных. Все деньги забирает инкассация." +
                        ". Остаток в банкомате: " + getCash());
            }
        } catch (Exception e) {
            System.err.println("Введенная сумма в неправильном формате! Используйте целочисленные значения!\n" +
                    "Ошибка: " + e);
        }
    }
}
