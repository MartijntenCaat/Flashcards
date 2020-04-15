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
    private ArrayList<String> logFile;
    private Scanner userScanner;
    private boolean isUpAndRunning;

    private Main() {
        this.userScanner = new Scanner(System.in);
        this.cardMap = new LinkedHashMap<>();
        this.logFile = new ArrayList<>();
        this.isUpAndRunning = true;
    }

    private void exitGame() {
        isUpAndRunning = false;
        userScanner.close();
        System.out.println("Bye bye!");
    }

    private void runGameByAction() {
        System.out.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
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
                //code
                break;
            case "hardest card":
                //code
                break;
            case "reset stats":
                //code
                break;
            default:
                break;
        }
    }

    private void applicationLogger(String logLine) {
        logFile.add(logLine);
    }

    private void removeCards() {
        System.out.println("The card:");
        String cardToBeRemoved = userScanner.nextLine();

        if (cardMap.containsKey(cardToBeRemoved)) {
            cardMap.remove(cardToBeRemoved);
            System.out.println("The card has been removed.");
        } else {
            System.out.println("Can't remove \"" + cardToBeRemoved + "\": there is no such card.");
        }
    }

    private void importCards() {
        System.out.println("File name:");
        File file = new File(userScanner.nextLine());

        int cardsImported = 0;

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNext()) {
                String cardQuestion = fileScanner.nextLine();
                String cardDefinition = fileScanner.nextLine();

                cardMap.put(cardQuestion, cardDefinition);
                cardsImported++;
            }
            System.out.println(cardsImported + " cards have been loaded.");
        } catch (IOException e) {
            System.out.println("File not found.");
        }
    }

    private void exportCards() {
        System.out.println("File name:");
        File file = new File(userScanner.nextLine());

        int numberOfSavedCards = 0;

        try (FileWriter cardWriter = new FileWriter(file, false)) {
            for (String cardQuestion : cardMap.keySet()) {
                cardWriter.write(cardQuestion + "\n");
                cardWriter.write(cardMap.get(cardQuestion) + "\n");
                numberOfSavedCards++;
            }
        } catch (IOException e) {
            System.out.println("Something went wrong: \n" + e);
        }
        System.out.println(numberOfSavedCards + " cards have been saved.");
    }

    private void fillCardMap() {
        System.out.println("The card:");
        String cardQuestion = userScanner.nextLine();

        if (cardMap.containsKey(cardQuestion)) {
            System.out.println("The card \"" + cardQuestion + "\" already exists.");
            return;
        }

        System.out.println("The definition of the card:");
        String cardDefinition = userScanner.nextLine();

        if (cardMap.containsValue(cardDefinition)) {
            System.out.println("The definition \"" + cardDefinition + "\" already exists.");
            return;
        }

        cardMap.put(cardQuestion, cardDefinition);
        System.out.println("The pair (\"" + cardQuestion + "\"" + ":" + "\"" + cardDefinition + "\") has been added.");
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
            return "Wrong answer. The correct one is \"" + cardMap.get(cardQuestion) + "\".";
        }

        if (cardMap.containsValue(cardAnswer) && !cardMap.get(cardQuestion).equals(cardAnswer)) {
            return "Wrong answer. The correct one is \"" + cardMap.get(cardQuestion) + "\", " +
                    "you've just written the definition of \"" + getRightCardQuestion(cardAnswer) + "\"";
        }
        return null;
    }

    private int askDurationOfGame() {
        System.out.println("How many times to ask?");
        return Integer.parseInt(userScanner.nextLine());
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
            System.out.println("Print the definition of \"" + cardQuestion + "\":");
            String cardAnswerByPlayer = userScanner.nextLine();

            String result = checkAnswer(cardQuestion, cardAnswerByPlayer);
            System.out.println(result);
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        while (main.isUpAndRunning) {
            main.runGameByAction();
        }
    }
}