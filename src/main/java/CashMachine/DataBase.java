package CashMachine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DataBase {
    private final String pathname_cards = "C://SENLA_STAGE//cards.txt",
            pathname_logs = "C://SENLA_STAGE//logs.txt";
    private ArrayList<String> cardCache, logsCache;
    private static DataBase instance;
    private boolean isChangedCards = false;
    private DataBase() {
        openFolderOrCreate();
        openOrCreate(pathname_cards);
        openOrCreate(pathname_logs);

        cardCache = getCacheCards();
    }
    public static DataBase getInstance() {
        if(instance == null) {
            synchronized (DataBase.class) {
                if(instance == null)
                    instance = new DataBase();
            }
        }
        return instance;
    }

    private void openFolderOrCreate() {
        try {
            File file = new File("C://SENLA_STAGE");
            if(!file.exists())
                file.mkdir();
        }
        catch (Exception e) {
            System.err.println("Ошибка!");
            e.printStackTrace();
        }
    }
    private void openOrCreate(String filename) {
        try {
            File file = new File(filename);
            if(!file.exists()) {
                file.createNewFile();
                if(filename.equals(pathname_cards)) {
                    addExampleCards();
                }
            }
        }
        catch (Exception e) {
            System.err.println("Ошибка!");
            e.printStackTrace();
        }
    }
    protected ArrayList<String> getCacheCards() {
        if (cardCache == null || isChangedCards) {
            ArrayList<String> lines = new ArrayList<>();

            try {
                lines = (ArrayList<String>) Files.readAllLines(
                        Paths.get(pathname_cards),
                        StandardCharsets.UTF_8);
            }
            catch (IOException e) {
                System.err.println("Ошибка получения доступа к файловой системе!");
                e.printStackTrace();
            }
            cardCache = lines;
        }
        return cardCache;
    }
    protected ArrayList<String> getCacheLogs() {
        if (logsCache == null) {
            ArrayList<String> lines = new ArrayList<>();

            try {
                lines = (ArrayList<String>) Files.readAllLines(
                        Paths.get(pathname_logs),
                        StandardCharsets.UTF_8);
            }
            catch (IOException e) {
                System.err.println("Ошибка получения доступа к файловой системе!");
                e.printStackTrace();
            }
            logsCache = lines;
        }
        return logsCache;
    }
    protected void printCacheCards() {
        for (String s : cardCache) {
            System.err.println(s);
        }
    }
    protected void printCacheLogs() {
        logsCache = getCacheLogs();
        for (String s : logsCache) {
            System.err.println(s);
        }
    }
    private void writeStringCards(String text, boolean append) {
        try(FileWriter fw = new FileWriter(pathname_cards, append); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(text);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл");
            e.printStackTrace();
        }
    }
    private void addExampleCards() {
        writeStringCards("9890-8473-3872-3745 7584 38922 null", true);
        writeStringCards("2340-7432-9743-5372 0973 23000 null", true);
        writeStringCards("4923-1234-7328-8373 8743 7 null", true);
        writeStringCards("2839-3982-8352-3499 3743 100000 null", true);
    }
    protected void writeNewCardBalance(String cardNumber, int balance) {
        for (int i = 0; i< cardCache.size(); ++i) {
            String[] arr = cardCache.get(i).split(" ");
            if (cardNumber.equals(arr[0])) {
                String temp = arr[0] + " " + arr[1] + " " + balance + " " + arr[3];
                cardCache.set(i, temp);
                break;
            }
        }
        writeStringCardsWithoutNewLine("", false);
        for(String s: cardCache) {
            writeStringCards(s, true);
        }
        isChangedCards = true;
        cardCache = getCacheCards();
        isChangedCards = false;
    }
    private void writeStringCardsWithoutNewLine(String text, boolean append) {
        try(FileWriter fw = new FileWriter(pathname_cards, append); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(text);
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл!\n" +
                    "Ошибка: " + e);
        }
    }
    protected void writeLastBlockDate(String cardNumber, String date, boolean append) {
        for (int i = 0; i< cardCache.size(); ++i) {
            String[] arr = cardCache.get(i).split(" ");
            if (cardNumber.equals(arr[0])) {
                String temp = arr[0] + " " + arr[1] + " " + arr[2] + " " + date;
                cardCache.set(i, temp);
                break;
            }
        }
        writeStringCardsWithoutNewLine("", false);
        for(String s: cardCache) {
            writeStringCards(s, append);
        }
        isChangedCards = true;
        cardCache = getCacheCards();
        isChangedCards = false;
    }
    protected ArrayList<String> findCard(String number) {
        ArrayList<String> out = new ArrayList<>();
        for (String s : cardCache) {
            String[] arr = s.split(" ");
            if (number.equals(arr[0]) && isValid(arr)) {
                out.add(arr[0]);
                out.add(arr[1]);
                out.add(arr[2]);
                out.add(arr[3]);
                break;
            } else {
                System.err.println("Данные о карте повреждены!");
                break;
            }
        }
        return out;
    }
    private boolean isValid(String[] arr) {
        if (arr.length > 0 && arr.length < 5) {
            if(new BankAccount().verifyNumber(arr[0]) &&
                    arr[1].length() == 4 && getInstance().IsParsedFromStringToInt(arr[1]) &&
                    getInstance().IsParsedFromStringToInt(arr[2]) &&
                    (arr[3] == null || getInstance().isParsedFromStringToLong(arr[3]))) {
                return true;
            }
        }
        return false;
    }
    private boolean IsParsedFromStringToInt(String value) {
        try {
            int temp = Integer.parseInt(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    private boolean isParsedFromStringToLong(String value) {
        try {
            long temp = Long.parseLong(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    protected void writeStringLogs(String text) {
        try(FileWriter fw = new FileWriter(pathname_logs, true); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(text);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл!\n" +
                    "Ошибка: " + e);
        }
        logsCache = getCacheLogs();
    }
    protected void clearCache() {
        cardCache = null;
    }
}
