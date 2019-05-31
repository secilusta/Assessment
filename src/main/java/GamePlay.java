import java.util.Map;

/**
 * GamePlay.java - This class controls interactions with User and Computer.
 */
class GamePlay {

    static final int DIGIT_COUNT = 4; // Digit count of game
    private User user;
    private Computer computer;

    GamePlay() {
        user = new User();
        computer = new Computer();
    }

    boolean isUserWinner() {
        return user.isUserWinner();
    }

    void setUserWinner(boolean userWinner) {
        user.setUserWinner(userWinner);
    }

    boolean isComputerWinner() {
        return computer.isComputerWinner();
    }

    void setComputerWinner(boolean computerWinner) {
        computer.setComputerWinner(computerWinner);
    }

    boolean canGuessMore() {
        return computer.canGuessMore();
    }

    /**
     * Handles given hint by user.
     *
     * @param rightPosition the number of digits of guess which in the right position
     * @param wrongPosition the number of digits of guess which in the wrong position
     * @param guess         the number guessed
     */
    void handleHintInternal(int rightPosition, int wrongPosition, String guess) {
        computer.clearGuessList(rightPosition, wrongPosition, guess);
    }

    /**
     * Calculates hint from guessed number.
     *
     * @param guessStr the number guessed
     * @return calculated hint
     */
    Map.Entry<Integer, Integer> calculateHint(String guessStr) {
        return computer.calculateHint(guessStr);
    }

    /**
     * @return new guess of computer
     */
    String getNewGuessFromPC() {
        return computer.getNewGuess();
    }

    /**
     * Checks if {@code number} has repetitive numbers.
     *
     * @param number the value to be checked
     * @return {@code true} if if {@code number} has repetitive numbers, {@code false} otherwise.
     */
    static boolean hasDuplicates(int number) {
        boolean[] digits = new boolean[10];
        while (number > 0) {
            if (digits[number % 10]) {
                return true;
            }
            digits[number % 10] = true;
            number /= 10;
        }
        return false;
    }
}
