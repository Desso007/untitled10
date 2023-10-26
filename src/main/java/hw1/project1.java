package hw1;

import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.Scanner;

public class Project1 {
    public static void main(String[] args) {
        Dictionary dictionary = new Dictionary() {
            @Override
            public @NotNull String randomWord() {
                String[] words = {"hangman", "apple", "banana", "computer", "programming"};
                Random random = new Random();
                return words[random.nextInt(words.length)];
            }
        };

        String wordToGuess = dictionary.randomWord();
        int maxAttempts = 5;
        Session session = new Session(wordToGuess, maxAttempts);

        ConsoleHangman game = new ConsoleHangman();
        game.run(session);
    }
}

class ConsoleHangman {
    public void run(Session session) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("> Guess a letter:");

        while (true) {
            System.out.print("< ");
            String input = scanner.nextLine().toLowerCase();
            char guess = input.charAt(0);

            if (input.equals("give up")) {
                System.out.println("> You gave up!");
                break;
            }

            GuessResult result = session.guess(guess);
            printState(result);

            if (result instanceof GuessResult.Defeat || result instanceof GuessResult.Win) {
                break;
            }
        }
    }

    private void printState(GuessResult result) {
        System.out.println("> " + result.message());
        System.out.println();
        System.out.println("> The word: " + new String(result.state()));
        System.out.println();
    }
}

interface Dictionary {
    @NotNull String randomWord();
}

class Session {
    private final String answer;
    private final char[] userAnswer;
    private final int maxAttempts;
    private int attempts;

    public Session(String answer, int maxAttempts) {
        this.answer = answer;
        this.userAnswer = new char[answer.length()];
        this.maxAttempts = maxAttempts;
        this.attempts = 0;
    }

    @NotNull
    public GuessResult guess(char guess) {
        if (attempts >= maxAttempts) {
            return new GuessResult.Defeat(answer.toCharArray(), attempts, maxAttempts);
        }

        boolean correctGuess = false;
        for (int i = 0; i < answer.length(); i++) {
            if (answer.charAt(i) == guess && userAnswer[i] != guess) {
                userAnswer[i] = guess;
                correctGuess = true;
            }
        }

        if (answer.equals(new String(userAnswer))) {
            return new GuessResult.Win(userAnswer, attempts, maxAttempts);
        }

        if (!correctGuess) {
            attempts++;
            if (attempts >= maxAttempts) {
                return new GuessResult.Defeat(answer.toCharArray(), attempts, maxAttempts);
            }
            return new GuessResult.FailedGuess(userAnswer, attempts, maxAttempts);
        }

        return new GuessResult.SuccessfulGuess(userAnswer, attempts, maxAttempts);
    }
}

sealed interface GuessResult {
    char[] state();
    int attempt();
    int maxAttempts();
    @NotNull String message();

    record Defeat(char[] state, int attempt, int maxAttempts) implements GuessResult {
        @Override
        public String message() {
            return "You lost!";
        }
    }

    record Win(char[] state, int attempt, int maxAttempts) implements GuessResult {
        @Override
        public String message() {
            return "You won!";
        }
    }

    record SuccessfulGuess(char[] state, int attempt, int maxAttempts) implements GuessResult {
        @Override
        public String message() {
            return "Hit!";
        }
    }

    record FailedGuess(char[] state, int attempt, int maxAttempts) implements GuessResult {
        @Override
        public String message() {
            return "Mistake " + attempt + " out of " + maxAttempts;
        }
    }
}
