import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Computer.java - class includes methods and variables of computer
 */
class Computer {

    private boolean isComputerWinner;
    private boolean canGuessMore; // keeps if computer still have a number to guess, if not that shows a mistake.
    private final String DIGITS = "0123456789";
    private String targetNumberByComputer; // the number computer keeps in mind
    private ArrayList<String> allPossibleGuessesList; // all possible 4-digit without repetition

    Computer() {
        isComputerWinner = false;
        canGuessMore = true;
        targetNumberByComputer = generateRandomNumber();
        allPossibleGuessesList = allPossibleNumbers(GamePlay.DIGIT_COUNT);
    }

    boolean isComputerWinner() {
        return isComputerWinner;
    }

    void setComputerWinner(boolean computerWinner) {
        isComputerWinner = computerWinner;
    }

    boolean canGuessMore() {
        return canGuessMore;
    }

    /**
     * Generates a random {@code GamePlay.DIGIT_COUNT}-digit number without repetition.
     *
     * @return the generated random
     */
    private String generateRandomNumber() {
        Random randomNumberGenerator = new Random();
        int tempRandomNumber;
        do {
            tempRandomNumber = randomNumberGenerator.nextInt(
                    (int) ((Math.pow(10, GamePlay.DIGIT_COUNT - 1) * 9)
                            + Math.pow(10, GamePlay.DIGIT_COUNT - 1)));
        } while (GamePlay.hasDuplicates(tempRandomNumber));
        return String.valueOf(tempRandomNumber);
    }

    /**
     * Iterates over possible guesses list, removes the ones do not match with given hint.
     *
     * @param rightPosition the number of digits of guess which in the right position
     * @param wrongPosition the number of digits of guess which in the wrong position
     * @param guess         the number guessed
     */
    void clearGuessList(int rightPosition, int wrongPosition, String guess) {
        for (var guessIndex = 0; guessIndex < allPossibleGuessesList.size(); guessIndex++) {
            String element = allPossibleGuessesList.get(guessIndex);
            int tmpRightPos = 0, tmpWrongPos = 0;
            for (var digitIndex = 0; digitIndex < GamePlay.DIGIT_COUNT; digitIndex++) {
                if (element.charAt(digitIndex) == guess.charAt(digitIndex)) {
                    tmpRightPos++;
                } else if (element.contains(guess.charAt(digitIndex) + "")) {
                    tmpWrongPos--;
                }
            }
            if (tmpRightPos != rightPosition || tmpWrongPos != wrongPosition) {
                allPossibleGuessesList.remove(element);
                guessIndex--;
            }
        }
        if (allPossibleGuessesList.size() <= 0) {
            canGuessMore = false;
        }
    }

    /**
     * Generates all possible numbers with {@code length} distinct digits.
     *
     * @param length specifies the length of generated numbers
     * @return the list of generated numbers
     */
    private ArrayList<String> allPossibleNumbers(int length) {
        if (length > 1) {
            ArrayList<String> newList = new ArrayList<>();
            allPossibleNumbers(length - 1).forEach(element -> {
                for (var chr : DIGITS.toCharArray()) {
                    if (!element.contains(chr + "")) {
                        newList.add(element + chr);
                    }
                }
            });
            return newList;
        }
        return DIGITS.substring(1) // This is to prevent 0 to be first digit
                .chars()
                .mapToObj(value -> String.valueOf((char) value))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Calculates hint from guess and {@link #targetNumberByComputer}.
     *
     * @param guessStr the guessed number
     * @return hint values
     */
    Map.Entry<Integer, Integer> calculateHint(String guessStr) {
        int rightPosition = 0;
        int wrongPosition = 0;
        for (var digitIndex = 0; digitIndex < GamePlay.DIGIT_COUNT; digitIndex++) {
            if (guessStr.charAt(digitIndex) == targetNumberByComputer.charAt(digitIndex)) {
                rightPosition++;
            } else if (targetNumberByComputer.contains(guessStr.charAt(digitIndex) + "")) {
                wrongPosition--;
            }
        }
        return new AbstractMap.SimpleEntry<>(rightPosition, wrongPosition);
    }

    /**
     * Gets a guess from all possible guesses.
     *
     * @return the new guessed value
     */
    String getNewGuess() {
        return getNewGuess(allPossibleGuessesList);
    }

    /**
     * Generates a random index and returns a guess.
     *
     * @param guessList the list includes all possible guesses
     * @return new guessed number
     */
    private static String getNewGuess(ArrayList<String> guessList) {
        Random randomNumberGenerator = new Random();
        int index = randomNumberGenerator.nextInt(guessList.size());
        return guessList.remove(index);
    }
}