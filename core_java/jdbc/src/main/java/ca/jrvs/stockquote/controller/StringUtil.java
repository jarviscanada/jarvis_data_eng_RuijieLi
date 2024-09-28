package ca.jrvs.stockquote.controller;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {
    public static String toUserString(String[] values, String[] titles) {
        List<String> sbTitles = new ArrayList<>();
        List<String> sbValues = new ArrayList<>();
        List<String> separators = new ArrayList<>();

        for(int i = 0; i < titles.length; i++) {
            String value = values[i];
            String title = titles[i];
            int max = Math.max(value.length(), title.length());
            sbTitles.add(title + " ".repeat(max - title.length()));
            sbValues.add(value + " ".repeat(max - value.length()));
            separators.add("-".repeat(max));
        }
        String titlesSTR        = "|" + String.join("|", sbTitles)      + "|\n";
        String valuesSTR        = "|" + String.join("|", sbValues)      + "|\n";
        String separatorLine    = "+" + String.join("+", separators)    + "+\n";
        return titlesSTR + separatorLine + valuesSTR;
    }

    public static String toUserString(List<String[]> values, String[] titles) {
        List<Integer> maxLengths = new ArrayList<>();

        for(int j = 0; j < values.get(0).length; j++) {
            int maxLen = -1;
            for(int i = 0; i < values.size(); i++) {
                int strlen = values.get(i)[j].length();
                if(strlen > maxLen) {
                    maxLen = strlen;
                }
            }
            maxLengths.add(maxLen);
        }
        List<String> separators = new ArrayList<>();
        for(int i = 0; i < maxLengths.size(); i++) {
            maxLengths.set(i, Math.max(maxLengths.get(i), titles[i].length()));
            separators.add("-".repeat(maxLengths.get(i)));
        }

        List<String> titlesToPrint = new ArrayList<>();
        for(int i = 0; i < titles.length; i++) {
            titlesToPrint.add(titles[i] + " ".repeat(maxLengths.get(i)));
        }
        List<String> finalStrings = new ArrayList<>();
        String titleString = "|" + String.join("|", titlesToPrint) + "|";
        String separatorString = "+" + String.join("+", separators) + "+";
        
        finalStrings.add(titleString);
        finalStrings.add(separatorString);

        for(String[] valuesOfOneObj: values) {
            List<String> valuesToPrint = new ArrayList<>();
            for(int i = 0; i < valuesOfOneObj.length; i++) {
                valuesToPrint.add(valuesOfOneObj[i] + " ".repeat(maxLengths.get(i)));
            }
            String row = "|" + String.join("|", valuesToPrint) + "|";
            finalStrings.add(row);
            finalStrings.add(separatorString);
        }

        return String.join("\n", finalStrings);
    }
}
