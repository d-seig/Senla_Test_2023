package CashMachine;

import java.util.regex.Pattern;

public class BankAccount {
    private String number; // XXXX-XXXX-XXXX-XXXX
    private String regularNumber = "(\\d{4}-){3}\\d{4}";
    private int allMoney;
    protected BankAccount(String number, int money) {
        this.number = number;
        allMoney = money;
    }
    public BankAccount() {
        number = null;
        allMoney = 0;
    }
    protected int getBalance() {
        return allMoney;
    }
    protected void takesMoney(int money) {
        try {
            if (money < allMoney) {
                allMoney -= money;
                System.out.println("Со счета *" + number.substring(number.length() - 4) + " списано " + money + " денег");
            } else {
                System.out.println("В операции отказано. Недостаточно средств!");
            }
        } catch (Exception e) {
            System.err.println("Введенная сумма в неправильном формате! Используйте целочисленные значения!\n" +
                    "Ошибка: " + e);
        }
    }
    protected void pushesMoney(int money) {
        try {
            allMoney += money;
            System.out.println("На счет *" + number.substring(number.length() - 4) + " начислено " + money + " денег");
        } catch (Exception e) {
            System.err.println("Введенная сумма в неправильном формате! Используйте целочисленные значения!\n" +
                    "Ошибка: " + e);
        }
    }
    public boolean verifyNumber(String number) {
        if(Pattern.matches(regularNumber, number))
        {
            System.out.println("Верный номер карты!");
            return true;
        } else {
            System.out.println("Неверный номер карты!");
            return false;
        }
    }
}
