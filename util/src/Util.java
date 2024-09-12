import java.io.*;
import java.util.*;

public class Util {
    public static void main(String[] args) {
        String outputPath = "";
        String prefix = "";
        boolean isAppended = false;
        boolean shortStat = false;
        boolean fullStat = false;

        int intCounter = 0;
        int doubleCounter = 0;
        int stringCounter = 0;
        Long intMin = null;
        Long intMax = null;
        long intSum = 0;
        Double doubleMin = null;
        Double doubleMax = null;
        double doubleSum = 0;
        Long lengthMin = null;
        Long lengthMax = null;

        PrintWriter integerWriter = null;
        PrintWriter doubleWriter = null;
        PrintWriter stringWriter = null;

        String[] arrayCommands = {"-o", "-p", "-a", "-s", "-f"};
        List<String> commands = new ArrayList<>(Arrays.asList(arrayCommands));

        List<String> inputFiles = new ArrayList<>();

        // Обработка введенных опций.
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-o": {
                    if (i + 1 < args.length && !commands.contains(args[i + 1]) && !args[i+1].contains(".txt")) {
                        outputPath = args[i + 1];
                        if (!outputPath.endsWith("\\")) outputPath += "\\";
                        i++;
                    } else {
                        System.err.println("Отсутвует новый путь. Файлы сохранены в папке по умолчанию.");
                    }
                    break;
                }
                case "-p": {
                    if (i + 1 < args.length && !commands.contains(args[i + 1]) && !args[i+1].contains(".txt")) {
                        prefix = args[i + 1];
                        i++;
                    } else {
                        System.err.println("Отсутвует префикс. Файлы сохранены с именем по умолчанию.");
                    }
                    break;
                }
                case "-a": {
                    isAppended = true;
                    break;
                }
                case "-s": {
                    shortStat = true;
                    break;
                }
                case "-f": {
                    fullStat = true;
                    break;
                }
                default: {
                    if (args[i].startsWith("-")) {
                        System.err.println("Ошибка: Неизвестная опция: " + args[i]);
                    } else {
                        inputFiles.add(args[i]);
                    }
                    break;
                }
            }
        }

        String integersPath = outputPath + prefix + "integers.txt";
        String doublesPath = outputPath + prefix + "doubles.txt";
        String stringsPath = outputPath + prefix + "strings.txt";

        if (inputFiles.isEmpty()) System.err.println("Ошибка: Файлы с данными не указаны");

        for (String inputFile : inputFiles)
        {
            try (Scanner scanner = new Scanner(new File(inputFile)))
            {
                while (scanner.hasNextLine())
                {
                    // Выбор целых чисел.
                    if (scanner.hasNextLong())
                    {
                        Long number = scanner.nextLong();
                        if (integerWriter == null)
                        {
                            try
                            {
                                integerWriter = new PrintWriter(new FileWriter(integersPath, isAppended));
                            } catch (Exception ex) {
                                System.out.println("Ошибка: Не удалось открыть файл для записи: " + integersPath);
                                System.out.println("Файл: " + inputFile + " был пропущен.");
                                break;
                            }
                        }
                        WriteToFile(integerWriter, integersPath, number.toString());

                        // Подсчет статистики по целым числам.
                        if (shortStat || fullStat)
                        {
                            intCounter++;
                        }
                        if (fullStat)
                        {
                            if (intMin == null) {
                                intMin = number;
                                intMax = number;
                            }
                            if (number < intMin) intMin = number;
                            if (number > intMax) intMax = number;
                            intSum += number;
                        }
                    } else {
                        String line = scanner.nextLine();

                        //Выбор вещественных чисел.
                        try {
                            Double number = Double.parseDouble(line);
                            if (doubleWriter == null)
                            {
                                try {
                                    doubleWriter = new PrintWriter(new FileWriter(doublesPath, isAppended));
                                } catch (Exception ex) {
                                    System.out.println("Ошибка: Не удалось открыть файл для записи: " + doublesPath);
                                    System.out.println("Файл: " + inputFile + " был пропущен.");
                                    break;
                                }
                            }
                            WriteToFile(doubleWriter, doublesPath, number.toString());

                            // Статистика по вещественным числам.
                            if (shortStat || fullStat) {
                                doubleCounter++;
                            }
                            if (fullStat) {
                                if (doubleMin == null) {
                                    doubleMin = number;
                                    doubleMax = number;
                                }
                                if (number < doubleMin) doubleMin = number;
                                if (number > doubleMax) doubleMax = number;
                                doubleSum += number;
                            }
                        } catch (NumberFormatException ex) {

                            // Запись в файл того, что не является целым или вещественным числом.
                            if (Objects.equals(line, "")) continue;
                            if (stringWriter == null)
                            {
                                try {
                                    stringWriter = new PrintWriter(new FileWriter(stringsPath, isAppended));
                                } catch (Exception e) {
                                    System.out.println("Ошибка: Не удалось открыть файл для записи: " + stringsPath);
                                    System.out.println("Файл: " + inputFile + " был пропущен.");
                                    break;
                                }
                            }
                            WriteToFile(stringWriter, stringsPath, line);

                            // Статистика по строкам.
                            if (shortStat || fullStat) {
                                stringCounter++;
                            }
                            if (fullStat) {
                                if (lengthMin == null) {
                                    lengthMin = (long) line.length();
                                    lengthMax = (long) line.length();
                                }
                                if (line.length() < lengthMin) lengthMin = (long) line.length();
                                if (line.length() > lengthMax) lengthMax = (long) line.length();
                            }
                        }
                    }
                }
            } catch (FileNotFoundException ex) {
                System.err.println("Ошибка: Файл " + inputFile + " не найден и был пропущен.");
            }
        }

        // Закрытие файлов после использования.
        try {
            if (integerWriter != null) integerWriter.close();
            if (doubleWriter != null) doubleWriter.close();
            if (stringWriter != null) stringWriter.close();
        } catch (Exception ex) {
            System.err.println("Ошибка при закрытии файла.");
        }

        // Вывод статистики.
        if (shortStat) PrintShortStat(intCounter, doubleCounter, stringCounter);
        if (fullStat) {
            PrintShortStat(intCounter, doubleCounter, stringCounter);
            PrintFullStat(intCounter, doubleCounter, stringCounter, intMin, intMax, intSum,
                    doubleMin, doubleMax, doubleSum, lengthMin, lengthMax);
        }
    }

    // Выполняет запись в файл, используя определенный PrintWriter.
    private static void WriteToFile(PrintWriter writer, String filePath, String data) {
        try {
            writer.println(data);
        } catch (Exception ex) {
            System.err.println("Ошибка при записи в файл: " + filePath + ". Строка была пропущена.");
        }
    }

    // Выводит краткую статистику в консоль.
    private static void PrintShortStat(int intCounter, int doubleCounter, int stringCounter) {
        System.out.println("Количество целых чисел: " + intCounter);
        System.out.println("Количество дробных чисел: " + doubleCounter);
        System.out.println("Количество строк: " + stringCounter);
        System.out.println();
    }

    // Выводит полную статистику в консоль.
    private static void PrintFullStat(int intCounter, int doubleCounter, int stringCounter,
                                      Long intMin, Long intMax, long intSum, Double doubleMin,
                                      Double doubleMax, double doubleSum, Long lengthMin, Long lengthMax) {
        if (intCounter != 0) {
            System.out.println("Минимальное целое число: " + intMin);
            System.out.println("Максимальное целое число: " + intMax);
            System.out.println("Сумма целых чисел: " + intSum);
            System.out.println("Среднее: " + intSum / intCounter);
            System.out.println();
        }
        if (doubleCounter != 0) {
            System.out.println("Минимальное дробное число: " + doubleMin);
            System.out.println("Максимальное дробное число: " + doubleMax);
            System.out.println("Сумма дробных чисел: " + doubleSum);
            System.out.println("Среднее: " + doubleSum / doubleCounter);
            System.out.println();
        }
        if (stringCounter != 0) {
            System.out.println("Минимальная длина строки: " + lengthMin);
            System.out.println("Максимальная длина строки: " + lengthMax);
        }
    }
}