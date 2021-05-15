package com.company;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        boolean condition = true;
        int labaNumber = 1;
        Path folderPath = null; // Путь к папке
        Path resultPath = null;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Выберите номер лабораторной работы, 1, 2 или 3");
        while (condition) {
            String value = scanner.nextLine();
            if ("1".equals(value) || "2".equals(value) || "3".equals(value)) {
                condition = false;
                labaNumber = Integer.parseInt(value);
            } else {
                System.out.println("Некорректный ввод");
            }
        }

        condition = true;
        System.out.println("Введите путь до рабочей директории");
        while (condition) {
            String value = scanner.nextLine();
            folderPath = Paths.get(value);
            if (folderPath.isAbsolute()) {
                File file = new File(value);
                if (file.isDirectory()) {
                    file = new File(value + "/result.txt");
                    if (file.exists()) {
                        condition = false;
                        resultPath = Paths.get(file.getAbsolutePath());
                    } else {
                        System.out.println("В директории нет файла result.txt");
                    }
                }
            } else {
                System.out.println("Введен некорректный путь");
            }
        }

        switch (labaNumber) {
            case 1:
                List<String> rpg = readDocument(resultPath, "RPG", "");
                writeDocument(folderPath, "Laba_" + labaNumber + "_RPG_", rpg);
            break;
            case 2:
                List<String> veMin = readDocument(resultPath, "VstE", "LCopt,");
                List<String> vcMin = readDocument(resultPath, "VCmin", "VstE, LCopt,");
                List<String> v = readDocument(resultPath, "V", "");
                List<String> rg = new ArrayList<>();

                for (int i = 0; i < v.size(); i++) {
                    double Rg = (Double.parseDouble(vcMin.get(i)) + Double.parseDouble(veMin.get(i))) / Double.parseDouble(v.get(i));
                    rg.add(String.valueOf(Rg));
                }

                writeDocument(folderPath, "Laba_" + labaNumber + "_VstE_", veMin);
                writeDocument(folderPath, "Laba_" + labaNumber + "_Vcmin_", vcMin);
                writeDocument(folderPath, "Laba_" + labaNumber + "_V_", v);
                writeDocument(folderPath, "Laba_" + labaNumber + "_Rg_", rg);
            break;
            case 3:
                List<String> rq = readDocument(resultPath, "Rq", "");
                List<String> ro = readDocument(resultPath, "Ro", "");
                writeDocument(folderPath, "Laba_" + labaNumber + "_Rq_", rq);
                writeDocument(folderPath, "Laba_" + labaNumber + "_Ro_", ro);
            break;
        }
        System.out.println("Программа завершила работу, нажмите Enter для выхода");
        scanner.nextLine();
    }

    public static void writeDocument(Path folderPath, String prefix, List<String> stringList) throws IOException {
        File file = new File(folderPath + "/" + prefix);

        if (!file.exists()) {
            file = File.createTempFile(prefix, ".txt", new File(String.valueOf(folderPath)));
        }

        FileWriter writer = new FileWriter(file);

        stringList.forEach(string -> {
            try {
                writer.append(string).append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        writer.flush();
        writer.close();
    }

    public static List<String> readDocument(Path filePath, String positiveRegex, String negativeRegex) throws IOException {
        FileReader reader = new FileReader(String.valueOf(filePath));
        BufferedReader bufferedReader = new BufferedReader(reader);
        List<String> stringList = new ArrayList<>();
        while (bufferedReader.ready()) { // построчный цикл
            String line = bufferedReader.readLine();

            if (!"".equals(negativeRegex)) {
                if (line.contains(permutationRegex(positiveRegex)) && !line.contains(negativeRegex)) { // ищем в строке
                    try {
                        line = lineParser(line, positiveRegex);
                        stringList.add(line);
                    } catch (Exception e) {
                        System.out.println("Line number = " + stringList.size());
                        System.out.println(e.getMessage());
                    }
                }
            } else {
                if (line.contains(permutationRegex(positiveRegex))) { // ищем в строке
                    try {
                        line = lineParser(line, positiveRegex);
                        stringList.add(line);
                    } catch (Exception e) {
                        System.out.println("Line number = " + stringList.size());
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
        return stringList;
    }

    public static String permutationRegex(String regex) {
        switch (regex) {
            case "Rq": return "Rq = ";
            case "Ro": return  "Ro";
            case "VstE": return  "VstE: ";
            case "VCmin": return  "VCmin: ";
            case "V": return  "V, DeltaC: ";
            case "RPG": return  "RPG:  ";
            default: throw new IllegalStateException("Unexpected value: " + regex);
        }
    }

    public static String lineParser(String line, String positiveRegex) {
        if ("V".equals(positiveRegex)) {
            line = line.replaceAll(permutationRegex(positiveRegex), "");
            line = line.substring(0, line.indexOf(" "));
        } else if ("Ro".equals(positiveRegex)) {
            line = line.replaceAll("[\\D]", "").replaceAll(" ", "");
            line = line.charAt(0) + "." + line.substring(1);
        } else {
            line = line.replaceAll(permutationRegex(positiveRegex), "").replaceAll(" ", "");
        }
        return line;
    }
}