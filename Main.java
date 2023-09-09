import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import okhttp3.*;

public class Main {
    private static String webhookURL = "";
    private static final OkHttpClient client = new OkHttpClient();
    private static boolean running = false;
    private static boolean taggingEnabled = false;
    private static final Scanner scanner = new Scanner(System.in);
    private static Timer timer;

    public static void main(String[] args) {
        System.out.print("Enter the Discord Webhook URL: ");
        webhookURL = scanner.nextLine();
        listenForCommands();
    }

    private static void startBot() {
        System.out.println("Bot started.");
        running = true;
        taggingEnabled = true;

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!running) {
                    System.out.println("Bot stopped.");
                    timer.cancel();
                    return;
                }

                if (taggingEnabled) {
                    sendMessage("@everyone");
                }
            }
        }, 0, 500); // 每秒兩次，間隔為 500 毫秒
    }

    private static void sendMessage(String content) {
        Thread messageThread = new Thread(() -> {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            String json = "{\"content\":\"" + content + "\"}";

            RequestBody requestBody = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(webhookURL)
                    .post(requestBody)
                    .build();

            try {
                client.newCall(request).execute();
            } catch (IOException e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        });

        messageThread.start();
    }

    private static void listenForCommands() {
        new Thread(() -> {
            while (true) {
                String input = scanner.nextLine();
                if (input.startsWith("/")) {
                    handleCommand(input);
                }
            }
        }).start();
    }

    private static void handleCommand(String command) {
        if ("/stop".equalsIgnoreCase(command)) {
            stopBot();
        } else if ("/start".equalsIgnoreCase(command)) {
            startBot();
        } else if ("/end".equalsIgnoreCase(command)) {
            endProgram();
        } else {
            System.out.println("Unknown command: " + command);
        }
    }

    private static void stopBot() {
        running = false;
        taggingEnabled = false;
        timer.cancel();
        System.out.println("Bot stopped.");
    }

    private static void endProgram() {
        running = false;
        taggingEnabled = false;
        timer.cancel();
        scanner.close();
        System.out.println("Program ended.");
        System.exit(0);
    }
}
