import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Map;

/**
 * Controller.java - This class controls interactions with elements of FXML, therefore controls the flow of game.
 */
public class Controller {
    @FXML
    private ListView<String> userGuessListView;
    @FXML
    private ListView<String> pcGuessListView;
    @FXML
    private TextField inputTextField;
    @FXML
    private Button sendButton;

    private boolean willUserGuessNext;
    private GamePlay gamePlay;

    @FXML
    public void initialize() {
        // Creates tutorial dialog in first time opening
        createMessage(
                "First, guess a " + GamePlay.DIGIT_COUNT + "-digit number with no duplicate digits. \n" +
                        "Then, give a hint for computer's guess. \n\n" +
                        "Example hint: 1,-2 means 1 digit is on the right position, \n" +
                        "2 digits are right but on the wrong position",
                Alert.AlertType.NONE);
        restartGame();
    }

    /**
     * Resets class fields and fxml elements.
     */
    @FXML
    public void restartGame() {
        willUserGuessNext = true;
        inputTextField.clear();
        userGuessListView.getItems().clear();
        pcGuessListView.getItems().clear();
        gamePlay = new GamePlay();
        arrangeTheTurn();
    }

    /**
     * Triggers necessary actions when focus is on {@link #inputTextField} and enter key is pressed.
     */
    @FXML
    public void triggerSendButtonFromTextField(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            triggerSendButton();
        }
    }

    /**
     * Decides which actions are required according to turn.
     */
    private void triggerSendButton() {
        // Checks if it's users turn to guess
        if (willUserGuessNext) {
            // Checks if user's guess is valid and necessary actions are done
            if (handleUserGuess()) {
                guessForComputer();
            }
        } else {
            handleUserHint();
        }
    }

    /**
     * Checks if conditions met to end the game.
     *
     * @return {@code true} if game is ended, {@code false} otherwise.
     */
    private boolean isGameStillOn() {
        // Checks if there is a winner
        if (gamePlay.isUserWinner() || gamePlay.isComputerWinner()) {
            createMessage((gamePlay.isUserWinner()) ? "You won!" : "I won!", Alert.AlertType.INFORMATION);
            restartGame();
            return false;
        }
        // Checks if computer can guess more
        else if (!gamePlay.canGuessMore()) {
            createMessage("No possible answers with given hints :(", Alert.AlertType.ERROR);
            restartGame();
            return false;
        }
        return true;
    }

    /**
     * Changes button actions/fields according to turn.
     */
    private void arrangeTheTurn() {
        // Arrange fxml elements
        if (willUserGuessNext) {
            sendButton.setText("Guess!");
            inputTextField.clear();
            inputTextField.setPromptText("Enter the guess...");
            pcGuessListView.getStyleClass().remove("focusList");
            userGuessListView.getStyleClass().add("focusList");
        } else {
            sendButton.setText("Give Hint!");
            inputTextField.clear();
            inputTextField.setPromptText("Enter the hint...");
            userGuessListView.getStyleClass().remove("focusList");
            pcGuessListView.getStyleClass().add("focusList");
        }

        // Change button's action
        sendButton.setOnAction(actionEvent -> triggerSendButton());
    }

    /**
     * Alert creation method with general purposes.
     *
     * @param message relevant message that will shown to user
     * @param type    Alert type
     */
    private void createMessage(String message, Alert.AlertType type) {
        var alert = new Alert(type);
        alert.setTitle(type.name());
        if (type == Alert.AlertType.WARNING) {
            alert.setHeaderText("Something went wrong..."); // user made a mistake
        } else if (type == Alert.AlertType.INFORMATION) {
            alert.setHeaderText("Game ended..."); // someone won
            alert.getButtonTypes().addAll(ButtonType.CLOSE);
            ((Button)alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Play Again");
        } else if (type == Alert.AlertType.ERROR) {
            alert.setHeaderText("Game ended..."); // no possible guesses
            alert.getButtonTypes().add(ButtonType.CLOSE);
            ((Button)alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Play Again");
        } else { // information about game at the beginning
            alert.setHeaderText("Tutorial");
            alert.getButtonTypes().add(ButtonType.OK);
            alert.setTitle("Game Info");
        }
        alert.setContentText(message);
        alert.showAndWait()
                .filter(response -> response == ButtonType.CLOSE)
                .ifPresent(buttonType -> System.exit(0));
    }

    /**
     * Checks if users guess fits the rules.
     *
     * @return {@code true} if guess fits the rules, {@code false} otherwise.
     */
    private boolean checkUserGuess() {
        int guessFromUser;
        try {
            guessFromUser = Integer.parseInt(inputTextField.getText());
        } catch (Exception e) {
            createMessage("This is not a valid integer.", Alert.AlertType.WARNING);
            return false;
        }
        if (GamePlay.hasDuplicates(guessFromUser)
                || guessFromUser < (Math.pow(10, GamePlay.DIGIT_COUNT - 1))
                || guessFromUser > (Math.pow(10, GamePlay.DIGIT_COUNT))) {
            createMessage("Your guess is breaking the rules. Try again.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    /**
     * Checks user's guess and puts it into the list
     */
    private boolean handleUserGuess() {
        if (!checkUserGuess()) {
            return false;
        }
        var guessFromUser = Integer.parseInt(inputTextField.getText());

        // Calculate the hint from guess
        Map.Entry<Integer, Integer> hintFromUserGuess = gamePlay.calculateHint(String.valueOf(guessFromUser));
        int rightPosition = hintFromUserGuess.getKey();
        int wrongPosition = hintFromUserGuess.getValue();

        if (rightPosition == GamePlay.DIGIT_COUNT) {
            gamePlay.setUserWinner(true);
        }
        // Add new user guess to list
        userGuessListView.getItems().add(guessFromUser + " / " + rightPosition + "," + wrongPosition);
        scrollListsDown();
        willUserGuessNext = false;
        if (isGameStillOn()) {
            arrangeTheTurn();
        }
        return true;
    }

    /**
     * Add computers guess to computer's guess list.
     */
    private void guessForComputer() {
        var guessFromPC = gamePlay.getNewGuessFromPC();
        pcGuessListView.getItems().add(guessFromPC + " /  ?,?");
        scrollListsDown();
    }

    /**
     * Checks is given hint is fits the rules.
     *
     * @return {@code true} if hint fits the rules, {@code false} otherwise.
     */
    private boolean checkUserHint() {
        String[] hintForComputerGuess = inputTextField.getText().split(",");
        if (hintForComputerGuess.length < 2) {
            createMessage("This is not a valid hint.", Alert.AlertType.WARNING);
            return false;
        }
        int rightPosition;
        int wrongPosition;
        try {
            rightPosition = Integer.parseInt(hintForComputerGuess[0]);
            wrongPosition = Integer.parseInt(hintForComputerGuess[1]);
            if (rightPosition > GamePlay.DIGIT_COUNT
                    || Math.abs(wrongPosition) > GamePlay.DIGIT_COUNT
                    || rightPosition + Math.abs(wrongPosition) > GamePlay.DIGIT_COUNT
                    || wrongPosition > 0
                    || rightPosition < 0) {
                createMessage("This is not a valid hint.", Alert.AlertType.WARNING);
                return false;
            }
        } catch (Exception e) {
            createMessage("This is not a valid hint.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    /**
     * Does the necessary actions to handle hint from user.
     */
    private void handleUserHint() {
        if (!checkUserHint()) {
            return;
        }
        String[] hintForComputerGuess = inputTextField.getText().split(",");
        int rightPosition = Integer.parseInt(hintForComputerGuess[0]);
        int wrongPosition = Integer.parseInt(hintForComputerGuess[1]);
        if (rightPosition == GamePlay.DIGIT_COUNT) {
            gamePlay.setComputerWinner(true);
        }
        // Get last guess element of computer from the list and remove it
        var listElement = pcGuessListView.getItems().remove(pcGuessListView.getItems().size() - 1);
        // Get last guess from list element
        var guessFromPC = listElement.split("/")[0].trim();
        // Add new element includes guess and hint to computer's guess list
        pcGuessListView.getItems().add(guessFromPC + " / " + rightPosition + "," + wrongPosition);
        scrollListsDown();
        gamePlay.handleHintInternal(rightPosition, wrongPosition, guessFromPC.trim());
        willUserGuessNext = true;
        if (isGameStillOn()) {
            arrangeTheTurn();
        }
    }

    /**
     * Scrolls both guess lists to the end of lists.
     */
    private void scrollListsDown() {
        pcGuessListView.scrollTo(pcGuessListView.getItems().size() - 1);
        userGuessListView.scrollTo(userGuessListView.getItems().size() - 1);
    }
}