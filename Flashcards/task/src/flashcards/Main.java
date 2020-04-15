package flashcards;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private LinkedHashMap<String, String> cardMap;
    private LinkedHashMap<String, Integer> hardestCardMap;
    private ArrayList<String> logFile;
    private Scanner userScanner;
    private boolean isUpAndRunning;

    private Main() {
        this.userScanner = new Scanner(System.in);
        this.cardMap = new LinkedHashMap<>();
        this.hardestCardMap = new LinkedHashMap<>();
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
                //code
                break;
            default:
                break;
        }
    }

    private void hardestCard() {
        if (hardestCardMap.isEmpty()) {
            outputMsgAndLog("There are no cards with errors.");
        } else {

            String hardestCard = null;
            int hardestCardNumber = 0;

            for (String card : hardestCardMap.keySet()) {
                if (hardestCardMap.get(card) > hardestCardNumber) {
                    hardestCard = card;
                    hardestCardNumber = hardestCardMap.get(card);
                }
            }
            outputMsgAndLog("The hardest card is \"" + hardestCard + "\". You have "
                    + hardestCardNumber + " errors answering it.");
        }
    }

    private void hardestCardPlusOne(String card) {
        if (!hardestCardMap.containsKey(card)) {
            hardestCardMap.put(card, 1);
            System.out.println(hardestCardMap.keySet() + " - " + hardestCardMap.values());
        } else {
            hardestCardMap.put(card, hardestCardMap.get(card) + 1);
            System.out.println(hardestCardMap.keySet() + " - " + hardestCardMap.values());
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

        if (cardMap.containsKey(cardToBeRemoved)) {
            cardMap.remove(cardToBeRemoved);
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

                cardMap.put(cardQuestion, cardDefinition);
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
                numberOfSavedCards++;
            }
        } catch (IOException e) {
            outputMsgAndLog("Something went wrong: \n" + e);
        }
        outputMsgAndLog(numberOfSavedCards + " cards have been saved.");
    }

    private void fillCardMap() {
        outputMsgAndLog("The card:");
        String cardQuestion = userScanner.nextLine();
        applicationLogger(cardQuestion);

        if (cardMap.containsKey(cardQuestion)) {
            outputMsgAndLog("The card \"" + cardQuestion + "\" already exists.");
            return;
        }

        outputMsgAndLog("The definition of the card:");
        String cardDefinition = userScanner.nextLine();
        applicationLogger(cardDefinition);

        if (cardMap.containsValue(cardDefinition)) {
            outputMsgAndLog("The definition \"" + cardDefinition + "\" already exists.");
            return;
        }

        cardMap.put(cardQuestion, cardDefinition);
        outputMsgAndLog("The pair (\"" + cardQuestion + "\"" + ":" + "\"" + cardDefinition + "\") has been added.");
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
        outputMsgAndLog("How many times to ask?");
        String duration = userScanner.nextLine();
        applicationLogger(duration);
        return Integer.parseInt(duration);
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