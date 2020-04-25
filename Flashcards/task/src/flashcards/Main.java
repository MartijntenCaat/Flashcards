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

    public int getErrors() {
        return errors;
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

    private LinkedHashMap<String, String> cardMap;
    private LinkedHashMap<String, Integer> hardestCardMap;

    private Main() {
        this.flashcardList = new ArrayList<>();
        this.userScanner = new Scanner(System.in);
        this.logFile = new ArrayList<>();
        this.isUpAndRunning = true;

        this.cardMap = new LinkedHashMap<>(); // uitfaseren
        this.hardestCardMap = new LinkedHashMap<>(); // uitfaseren
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
            case "hardest card":
                hardestCard();
                break;
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

        if (checkCardQuestionInList(cardQuestion) == null) {
            outputMsgAndLog("The card \"" + cardQuestion + "\" already exists.");
            return;
        }

        outputMsgAndLog("The definition of the card:");
        String cardDefinition = userScanner.nextLine();
        applicationLogger(cardDefinition);

        if (checkCardDefinitionInList(cardDefinition) == null) {
            outputMsgAndLog("The definition \"" + cardDefinition + "\" already exists.");
            return;
        }

        Flashcard flashcard = new Flashcard(cardQuestion, cardDefinition, 0);
        flashcardList.add(flashcard);
        outputMsgAndLog("The pair (\"" + cardQuestion + "\"" + ":" + "\"" + cardQuestion + "\") has been added.");
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

    private void hardestCard() {
        if (hardestCardPresent()) {
            outputMsgAndLog("There are no cards with errors.");
            return;
        }

        ArrayList<String> hardestCard = new ArrayList<>();
        int hardestCardNumber = 0;

        for (String card : hardestCardMap.keySet()) {
            if (hardestCardMap.get(card) > hardestCardNumber) {
                hardestCard.clear();
                hardestCard.add(card);
                hardestCardNumber = hardestCardMap.get(card);
            } else if (hardestCardMap.get(card) == hardestCardNumber) {
                hardestCard.add(card);
            }
        }

        String hardestCardOutput = hardestCard.get(0);
        if (hardestCard.size() == 1) {
            outputMsgAndLog("The hardest card is \"" + hardestCardOutput + "\". You have "
                    + hardestCardNumber + " errors answering it.");
            return;
        }

        for (int i = 1; i < hardestCard.size(); i++) {
            hardestCardOutput = hardestCardOutput.concat("\", \"" + hardestCard.get(i));
        }

        outputMsgAndLog("The hardest cards are \"" + hardestCardOutput + "\". You have "
                + hardestCardNumber + " errors answering them.");
    }

    private void hardestCardPlusOne(String card) {
        if (!hardestCardMap.containsKey(card)) {
            hardestCardMap.put(card, 1);
        } else {
            hardestCardMap.put(card, hardestCardMap.get(card) + 1);
        }
    }

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

                cardMap.put(cardQuestion, cardDefinition);
                hardestCardMap.put(cardQuestion, Integer.parseInt(cardErrors));
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
            for (String cardQuestion : cardMap.keySet()) {
                cardWriter.write(cardQuestion + "\n");
                cardWriter.write(cardMap.get(cardQuestion) + "\n");
                cardWriter.write(hardestCardMap.get(cardQuestion) + "\n");
                numberOfSavedCards++;
            }
        } catch (IOException e) {
            outputMsgAndLog("Something went wrong: \n" + e);
        }
        outputMsgAndLog(numberOfSavedCards + " cards have been saved.");
    }

    private String getRightCardQuestion(String answer) {
        for (String cardQuestionToFind : cardMap.keySet()) {
            if (answer.equals(cardMap.get(cardQuestionToFind))) {
                return cardQuestionToFind;
            }
        }
        return null;
    }

    private String checkAnswer(String cardQuestion, String cardAnswer) {
        if (cardMap.get(cardQuestion).equals(cardAnswer)) {
            return "Correct answer.";
        }

        if (!cardMap.containsValue(cardAnswer)) {
            hardestCardPlusOne(cardQuestion);
            return "Wrong answer. The correct one is \"" + cardMap.get(cardQuestion) + "\".";
        }

        if (cardMap.containsValue(cardAnswer) && !cardMap.get(cardQuestion).equals(cardAnswer)) {
            hardestCardPlusOne(cardQuestion);
            return "Wrong answer. The correct one is \"" + cardMap.get(cardQuestion) + "\", " +
                    "you've just written the definition of \"" + getRightCardQuestion(cardAnswer) + "\"";
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

    private String[] prepareGame(int duration) {
        Object[] allTheCardQuestions = cardMap.keySet().toArray();
        Random randomised = new Random();
        String[] cardQuestionList = new String[duration];

        for (int i = 0; i < duration; i++) {
            cardQuestionList[i] = allTheCardQuestions[randomised.nextInt(allTheCardQuestions.length)].toString();
        }
        return cardQuestionList;
    }

    private void runFlashCardGame() {
        int duration = askDurationOfGame();
        String[] cardQuestionList = prepareGame(duration);

        for (String cardQuestion : cardQuestionList) {
            outputMsgAndLog("Print the definition of \"" + cardQuestion + "\":");
            String cardAnswerByPlayer = userScanner.nextLine();
            applicationLogger(cardAnswerByPlayer);

            String result = checkAnswer(cardQuestion, cardAnswerByPlayer);
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