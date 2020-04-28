package flashcards;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Scanner;

class Flashcard {
    private String question;
    private String definition;
    private int errors;

    Flashcard(String question, String definition, int errors) {
        this.question = question;
        this.definition = definition;
        this.errors = errors;
    }

    public String getQuestion() {
        return  question;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        definition = definition;
    }

    public int getErrors() {
        return errors;
    }

    public void addOneError() {
        errors++;
    }

    public void setErrors(int errors) {
        errors = errors;
    }

    public void resetStats() {
        errors = 0;
    }
}

public class Main {
    private ArrayList<Flashcard> flashcardList;
    private ArrayList<String> logFile;
    private Scanner userScanner;
    private boolean isUpAndRunning;

    private Main() {
        this.flashcardList = new ArrayList<>();
        this.userScanner = new Scanner(System.in);
        this.logFile = new ArrayList<>();
        this.isUpAndRunning = true;
    }

    private void exitGame() {
        isUpAndRunning = false;
        userScanner.close();
        outputMsgAndLog("Bye bye!");
    }

    private void runGameByAction() {
        outputMsgAndLog("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
        String action = userScanner.nextLine();
        applicationLogger(action);

        switch (action) {
            case "add":
                fillCardMap();
                break;
            case "remove":
                removeCards();
                break;
            case "import":
                importCards();
                break;
            case "export":
                exportCards();
                break;
            case "ask":
                runFlashCardGame();
                break;
            case "exit":
                exitGame();
                break;
            case "log":
                exportLog();
                break;
//            case "hardest card":
//                hardestCard();
//                break;
            case "reset stats":
                resetStats();
                break;
            default:
                break;
        }
    }

    private void fillCardMap() {
        outputMsgAndLog("The card:");
        String cardQuestion = userScanner.nextLine();
        applicationLogger(cardQuestion);

        if (checkCardQuestionInList(cardQuestion) != null) {
            outputMsgAndLog("The card \"" + cardQuestion + "\" already exists.");
            return;
        }

        outputMsgAndLog("The definition of the card:");
        String cardDefinition = userScanner.nextLine();
        applicationLogger(cardDefinition);

        if (checkCardDefinitionInList(cardDefinition) != null) {
            outputMsgAndLog("The definition \"" + cardDefinition + "\" already exists.");
            return;
        }

        Flashcard flashcard = new Flashcard(cardQuestion, cardDefinition, 0);
        flashcardList.add(flashcard);
        outputMsgAndLog("The pair (\"" + cardQuestion + "\"" + ":" + "\"" + cardDefinition + "\") has been added.");
    }

    private Flashcard checkCardQuestionInList(String cardQuestion) {
        for (Flashcard flashcard : flashcardList) {
            if (flashcard.getQuestion().equals(cardQuestion)) {
                return flashcard;
            }
        }
        return null;
    }

    private Flashcard checkCardDefinitionInList(String cardDefinition) {
        for (Flashcard flashcard : flashcardList) {
            if (flashcard.getDefinition().equals(cardDefinition)) {
                return flashcard;
            }
        }
        return null;
    }

    private void resetStats() {
        for (Flashcard flashcard : flashcardList) {
            flashcard.resetStats();
        }
        outputMsgAndLog("Card statistics has been reset.");
    }

    private boolean hardestCardPresent() {
        for (Flashcard flashcard : flashcardList) {
            if (flashcard.getErrors() > 0) {
                return true;
            }
        }
        return false;
    }

//    private void hardestCard() {
//        if (hardestCardPresent()) {
//            outputMsgAndLog("There are no cards with errors.");
//            return;
//        }
//
//        ArrayList<String> hardestCard = new ArrayList<>();
//        int hardestCardNumber = 0;
//
//        for (String card : hardestCardMap.keySet()) {
//            if (hardestCardMap.get(card) > hardestCardNumber) {
//                hardestCard.clear();
//                hardestCard.add(card);
//                hardestCardNumber = hardestCardMap.get(card);
//            } else if (hardestCardMap.get(card) == hardestCardNumber) {
//                hardestCard.add(card);
//            }
//        }
//
//        String hardestCardOutput = hardestCard.get(0);
//        if (hardestCard.size() == 1) {
//            outputMsgAndLog("The hardest card is \"" + hardestCardOutput + "\". You have "
//                    + hardestCardNumber + " errors answering it.");
//            return;
//        }
//
//        for (int i = 1; i < hardestCard.size(); i++) {
//            hardestCardOutput = hardestCardOutput.concat("\", \"" + hardestCard.get(i));
//        }
//
//        outputMsgAndLog("The hardest cards are \"" + hardestCardOutput + "\". You have "
//                + hardestCardNumber + " errors answering them.");
//    }

    private void applicationLogger(String logLine) {
        logFile.add(logLine);
    }

    private void outputMsgAndLog(String output) {
        applicationLogger(output);
        System.out.println(output);
    }

    private void exportLog() {
        outputMsgAndLog("File name:");
        File file = new File(userScanner.nextLine());
        applicationLogger(file.toString());

        try (FileWriter logWriter = new FileWriter(file, false)) {
            for (String logLine : logFile) {
                logWriter.write(logLine + "\n");
            }
        } catch (IOException e) {
            outputMsgAndLog("Something went wrong: \n" + e);
        }
        outputMsgAndLog("The log has been saved.");
    }

    private void removeCards() {
        outputMsgAndLog("The card:");
        String cardToBeRemoved = userScanner.nextLine();
        applicationLogger(cardToBeRemoved);

        Flashcard flashcard = checkCardQuestionInList(cardToBeRemoved);
        if (flashcard != null) {
            flashcardList.remove(flashcard);
            outputMsgAndLog("The card has been removed.");
        } else {
            outputMsgAndLog("Can't remove \"" + cardToBeRemoved + "\": there is no such card.");
        }
    }

    private void importCards() {
        outputMsgAndLog("File name:");
        File file = new File(userScanner.nextLine());
        applicationLogger(file.toString());

        int cardsImported = 0;

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNext()) {
                String cardQuestion = fileScanner.nextLine();
                applicationLogger(cardQuestion);
                String cardDefinition = fileScanner.nextLine();
                applicationLogger(cardDefinition);
                String cardErrors = fileScanner.nextLine();
                applicationLogger(cardErrors);

                Flashcard flashcard = checkCardQuestionInList(cardQuestion);

                if (flashcard == null) {
                    flashcard = new Flashcard(cardQuestion, cardDefinition, Integer.parseInt(cardErrors));
                } else {
                    flashcard.setDefinition(cardDefinition);
                    flashcard.setErrors(Integer.parseInt(cardErrors));
                }

                flashcardList.add(flashcard);
                cardsImported++;
            }
            outputMsgAndLog(cardsImported + " cards have been loaded.");
        } catch (IOException e) {
            outputMsgAndLog("File not found.");
        }
    }

    private void exportCards() {
        outputMsgAndLog("File name:");
        File file = new File(userScanner.nextLine());
        applicationLogger(file.toString());

        int numberOfSavedCards = 0;

        try (FileWriter cardWriter = new FileWriter(file, false)) {
            for (Flashcard flashcard : flashcardList) {
                cardWriter.write(flashcard.getQuestion() + "\n");
                cardWriter.write(flashcard.getDefinition() + "\n");
                cardWriter.write(flashcard.getErrors() + "\n");
                numberOfSavedCards++;
            }
        } catch (IOException e) {
            outputMsgAndLog("Something went wrong: \n" + e);
        }
        outputMsgAndLog(numberOfSavedCards + " cards have been saved.");
    }

    private String checkAnswer(Flashcard playedFlashcard, String cardAnswer) {
        if (playedFlashcard.getDefinition().equals(cardAnswer)) {
            return "Correct answer.";
        }

        Flashcard actualFlashcard = checkCardDefinitionInList(cardAnswer);

        if (actualFlashcard == null) {
            playedFlashcard.addOneError();
            return "Wrong answer. The correct one is \"" + playedFlashcard.getDefinition() + "\".";
        }

        if (actualFlashcard != null) {
            playedFlashcard.addOneError();
            return "Wrong answer. The correct one is \"" + playedFlashcard.getDefinition() + "\", " +
                    "you've just written the definition of \"" + actualFlashcard.getQuestion() + "\"";
        }
        return null;
    }

    private int askDurationOfGame() {
        try {
            outputMsgAndLog("How many times to ask?");
            String duration = userScanner.nextLine();
            applicationLogger(duration);

            return Integer.parseInt(duration);
        } catch (NumberFormatException e) {
            return askDurationOfGame();
        }
    }

    private void runFlashCardGame() {
        int duration = askDurationOfGame();
        Random random = new Random();
        for (int i = 0; i < duration; i++) {
            int randomNumber = random.nextInt(flashcardList.size());
            Flashcard flashcard = flashcardList.get(randomNumber);

            outputMsgAndLog("Print the definition of \"" + flashcard.getQuestion() + "\":");
            String cardAnswerByPlayer = userScanner.nextLine();
            applicationLogger(cardAnswerByPlayer);

            String result = checkAnswer(flashcard, cardAnswerByPlayer);
            outputMsgAndLog(result);
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        while (main.isUpAndRunning) {
            main.runGameByAction();
        }
    }
}