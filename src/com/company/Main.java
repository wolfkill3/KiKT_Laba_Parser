package com.company;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class Main {
    public static Path folderPath = null; // Путь к папке
    public static Path resultPath = null;
    public static Map<String, RegexNode> regexNodeMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        int labaNumber = getLabaNumber(scanner);
        generatePaths(scanner);
        generateRegexMap();

        switch (labaNumber) {
            case 1 -> {
                List<String> rpg = readDocument(resultPath, regexNodeMap.get("RPG"));
                writeDocument(folderPath, "Laba_" + labaNumber + "_RPG_", rpg);
            }
            case 2 -> {
                List<String> vstE = readDocument(resultPath, regexNodeMap.get("VstE"));
                List<String> vcMin = readDocument(resultPath, regexNodeMap.get("VCmin"));
                List<String> v = readDocument(resultPath, regexNodeMap.get("V"));
                List<String> rg = new ArrayList<>();

                for (int i = 0; i < v.size(); i++) {
                    double Rg = (Double.parseDouble(vcMin.get(i)) + Double.parseDouble(vstE.get(i))) / Double.parseDouble(v.get(i));
                    rg.add(String.valueOf(Rg));
                }

                writeDocument(folderPath, "Laba_" + labaNumber + "_VstE_", vstE);
                writeDocument(folderPath, "Laba_" + labaNumber + "_VCmin_", vcMin);
                writeDocument(folderPath, "Laba_" + labaNumber + "_V_", v);
                writeDocument(folderPath, "Laba_" + labaNumber + "_Rg_", rg);
            }
            case 3 -> {
                List<String> rq = readDocument(resultPath, regexNodeMap.get("Rq"));
                List<String> ro = readDocument(resultPath, regexNodeMap.get("Ro"));
                writeDocument(folderPath, "Laba_" + labaNumber + "_Rq_", rq);
                writeDocument(folderPath, "Laba_" + labaNumber + "_Ro_", ro);
            }
        }
        System.out.println("Программа завершила работу, нажмите Enter для выхода");
        scanner.nextLine();
    }

    private static void generatePaths(final Scanner scanner) {
        System.out.println("Введите путь до рабочей директории");
        while (true) {
            String value = scanner.nextLine();
            folderPath = Path.of(value);

            if (folderPath.isAbsolute()) {
                File file = new File(value);
                if (file.isDirectory()) {
                    file = new File(value + "/result.txt");
                    if (file.exists()) {
                        resultPath = Path.of(file.getAbsolutePath());
                        break;
                    } else {
                        System.out.println("В директории нет файла result.txt");
                    }
                }
            } else {
                System.out.println("Введен некорректный путь");
            }
        }
    }

    public static int getLabaNumber(final Scanner scanner) {
        System.out.println("Выберите номер лабораторной работы, 1, 2 или 3");
        while (true) {
            String value = scanner.nextLine();

            if ("1".equals(value) || "2".equals(value) || "3".equals(value)) {
                return Integer.parseInt(value);
            } else {
                System.out.println("Некорректный ввод");
            }
        }
    }

    public static void writeDocument(Path folderPath, String prefix, List<String> stringList) throws IOException {
        File file = new File(folderPath + "/" + prefix);

        if (!file.exists()) {
            file = File.createTempFile(prefix, ".txt", new File(String.valueOf(folderPath)));
        }

        FileWriter writer = new FileWriter(file);

        stringList.forEach(string -> {
            try {
                writer.append(string.replaceAll("\\.", ",")).append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        writer.flush();
        writer.close();
    }

    public static List<String> readDocument(Path filePath, RegexNode regexNode) throws IOException {
        FileReader reader = new FileReader(String.valueOf(filePath));
        BufferedReader bufferedReader = new BufferedReader(reader);
        List<String> stringList = new ArrayList<>();
        while (bufferedReader.ready()) { // построчный цикл
            String line = bufferedReader.readLine();

            if (!"".equals(regexNode.negativeRegex)) {
                if (line.contains(regexNode.positiveRegex) && !line.contains(regexNode.negativeRegex)) { // ищем в строке
                    try {
                        line = lineParser(line, regexNode);
                        stringList.add(line);
                    } catch (Exception e) {
                        System.out.println("Line number = " + stringList.size());
                        System.out.println(e.getMessage());
                    }
                }
            } else {
                if (line.contains(regexNode.positiveRegex)) { // ищем в строке
                    try {
                        line = lineParser(line, regexNode);
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

    public static void generateRegexMap() {
        regexNodeMap.put("Rq", new RegexNode("Rq", "Rq = ", ""));
        regexNodeMap.put("Ro", new RegexNode("Ro", "Ro", ""));
        regexNodeMap.put("VstE", new RegexNode("Vste", "VstE: ", "LCopt,"));
        regexNodeMap.put("VCmin", new RegexNode("VCmin", "VCmin: ", "VstE, LCopt,"));
        regexNodeMap.put("V", new RegexNode("V", "V, DeltaC: ", ""));
        regexNodeMap.put("RPG", new RegexNode("RPG", "RPG:  ", ""));
    }

    public static String lineParser(String line, RegexNode regexNode) {
        if ("V".equals(regexNode.nameRegex)) {
            line = line.replaceAll(regexNode.positiveRegex, "");
            line = line.substring(0, line.indexOf(" "));
        } else if ("Ro".equals(regexNode.nameRegex)) {
            line = line.replaceAll("[\\D]", "").replaceAll(" ", "");
            line = line.charAt(0) + "." + line.substring(1);
        } else {
            line = line.replaceAll(regexNode.positiveRegex, "").replaceAll(" ", "");
        }
        return line;
    }
}