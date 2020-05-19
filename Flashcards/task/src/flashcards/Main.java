package flashcards;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
        return question;
    }

    public String getDefinition() {
        return definition;
    }

    public boolean isCorrectAnswer(String answer) {
        return this.getDefinition().equals(answer);
    }

    public void setDefinition(String newDefinition) {
        definition = newDefinition;
    }

    public int getErrors() {
        return errors;
    }

    public void incError() {
        errors++;
    }

    public void setErrors(int newErrors) {
        errors = newErrors;
    }

    public void resetErrors() {
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

    private void runGameByArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String action = args[i];
            i++;
            String location = args[i];

            switch (action) {
                case "-import":
                    System.out.println("it's import!");
                    break;
                case "-export":
                    System.out.println("it's export!");
                    break;
                default:
                    break;
            }
        }
    }

    private void runGameByAction() {
        outputMsgAndLog("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
        String action = userScanner.nextLine();
        applicationLogger(action);

        switch (action) {
            case "add":
                addCard();
                break;
            case "remove":
                removeCard();
                break;
            case "import":
                importCardFile(askFileName());
                break;
            case "export":
                exportCardListToFile();
                break;
            case "ask":
                runFlashCardGame();
                break;
            case "exit":
                exitGame();
                break;
            case "log":
                exportLogToFile();
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

    private String askFileName() {
        outputMsgAndLog("File name:");
        String filename = userScanner.nextLine();
        applicationLogger(filename);
        return filename;
    }

    private void addCard() {
        outputMsgAndLog("The card:");
        String cardQuestion = userScanner.nextLine();
        applicationLogger(cardQuestion);

        if (findCardQuestionInList(cardQuestion) != null) {
            outputMsgAndLog("The card \"" + cardQuestion + "\" already exists.");
            return;
        }

        outputMsgAndLog("The definition of the card:");
        String cardDefinition = userScanner.nextLine();
        applicationLogger(cardDefinition);

        if (findCardDefinitionInList(cardDefinition) != null) {
            outputMsgAndLog("The definition \"" + cardDefinition + "\" already exists.");
            return;
        }

        Flashcard flashcard = new Flashcard(cardQuestion, cardDefinition, 0);
        flashcardList.add(flashcard);
        outputMsgAndLog("The pair (\"" + cardQuestion + "\"" + ":" + "\"" + cardDefinition + "\") has been added.");
    }

    private Flashcard findCardQuestionInList(String cardQuestion) {
        for (Flashcard flashcard : flashcardList) {
            if (flashcard.getQuestion().equals(cardQuestion)) {
                return flashcard;
            }
        }
        return null;
    }

    private Flashcard findCardDefinitionInList(String cardDefinition) {
        for (Flashcard flashcard : flashcardList) {
            if (flashcard.getDefinition().equals(cardDefinition)) {
                return flashcard;
            }
        }
        return null;
    }

    private void resetStats() {
        for (Flashcard flashcard : flashcardList) {
            flashcard.resetErrors();
        }
        outputMsgAndLog("Card statistics has been reset.");
    }

    private void hardestCard() {
        int hardestCardErrors = 0;
        ArrayList<String> hardestCardList = new ArrayList<>();

        for (Flashcard flashcard : flashcardList) {
            int errors = flashcard.getErrors();

            if (errors > hardestCardErrors) {
                hardestCardList.clear();
                hardestCardList.add(flashcard.getQuestion());
                hardestCardErrors = errors;
                continue;
            }

            if (errors == hardestCardErrors && errors > 0) {
                hardestCardList.add(flashcard.getQuestion());
            }
        }

        if (hardestCardList.isEmpty()) {
            outputMsgAndLog("There are no cards with errors.");
            return;
        }
        outputMsgAndLog(createHardestCardOutput(hardestCardList, hardestCardErrors));
    }

    private String createHardestCardOutput(ArrayList<String> hardestCardList, int hardestCardNumber) {

        if (hardestCardList.size() == 1) {
            return ("The hardest card is \"" + hardestCardList.get(0) + "\". You have "
                    + hardestCardNumber + " errors answering it.");
        }

        StringBuilder hardestCardOutput = new StringBuilder();
        for (String card : hardestCardList) {
            hardestCardOutput.append("\"").append(card).append("\"").append(", ");
        }
        hardestCardOutput.delete(hardestCardOutput.length() -  2, hardestCardOutput.length());

        return ("The hardest cards are " + hardestCardOutput + ". You have "
                + hardestCardNumber + " errors answering them.");
    }

    private void applicationLogger(String logLine) {
        logFile.add(logLine);
    }

    private void outputMsgAndLog(String output) {
        applicationLogger(output);
        System.out.println(output);
    }

    private void exportLogToFile() {
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

    private void removeCard() {
        outputMsgAndLog("The card:");
        String cardToBeRemoved = userScanner.nextLine();
        applicationLogger(cardToBeRemoved);

        Flashcard flashcard = findCardQuestionInList(cardToBeRemoved);
        if (flashcard != null) {
            flashcardList.remove(flashcard);
            outputMsgAndLog("The card has been removed.");
        } else {
            outputMsgAndLog("Can't remove \"" + cardToBeRemoved + "\": there is no such card.");
        }
    }

    private void importCardFile(String filename) {
        File file = new File(filename);
        int cardsImported = 0;

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNext()) {
                String cardQuestion = fileScanner.nextLine();
                applicationLogger(cardQuestion);
                String cardDefinition = fileScanner.nextLine();
                applicationLogger(cardDefinition);
                String cardErrors = fileScanner.nextLine();
                applicationLogger(cardErrors);

                Flashcard flashcard = findCardQuestionInList(cardQuestion);
                if (flashcard == null) {
                    flashcard = new Flashcard(cardQuestion, cardDefinition, Integer.parseInt(cardErrors));
                    flashcardList.add(flashcard);
                } else {
                    flashcard.setDefinition(cardDefinition);
                    flashcard.setErrors(Integer.parseInt(cardErrors));
                }
                cardsImported++;
            }
            outputMsgAndLog(cardsImported + " cards have been loaded.");
        } catch (IOException e) {
            outputMsgAndLog("File not found.");
        }
    }

    private void exportCardListToFile() {
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

            if (flashcard.isCorrectAnswer(cardAnswerByPlayer)) {
                outputMsgAndLog("Correct answer.");
                continue;
            }

            flashcard.incError();
            Flashcard actualFlashcard = findCardDefinitionInList(cardAnswerByPlayer);

            if (actualFlashcard == null) {
                outputMsgAndLog("Wrong answer. The correct one is \"" + flashcard.getDefinition() + "\".");
            } else {
                outputMsgAndLog("Wrong answer. The correct one is \"" + flashcard.getDefinition() + "\", " +
                        "you've just written the definition of \"" + actualFlashcard.getQuestion() + "\"");
            }

        }
    }

    public static void main(String[] args) {
        Main main = new Main();

        main.runGameByArgs(args);

        while (main.isUpAndRunning) {
            main.runGameByAction();
        }
    }
}