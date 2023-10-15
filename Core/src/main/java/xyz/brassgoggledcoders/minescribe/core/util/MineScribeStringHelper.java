package xyz.brassgoggledcoders.minescribe.core.util;

public class MineScribeStringHelper {
    public static String toTitleCase(String phrase) {

        // convert the string to an array
        char[] phraseChars = phrase.toCharArray();
        if (phraseChars.length > 0) {
            phraseChars[0] = Character.toTitleCase(phraseChars[0]);
        }

        for (int i = 0; i < phraseChars.length - 1; i++) {
            if (Character.isWhitespace(phraseChars[i])) {
                phraseChars[i + 1] = Character.toUpperCase(phraseChars[i + 1]);
            }
        }

        // convert the array to string
        return String.valueOf(phraseChars);
    }
}
