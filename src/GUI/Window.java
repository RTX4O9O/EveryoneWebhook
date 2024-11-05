package GUI;

import okhttp3.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class Window extends JFrame implements ActionListener {

    JButton buttonStart;
    JButton buttonPause;
    JTextArea textField;
    String webhook = "";
    public boolean tagging = false;
    Timer timer;

    public Window() {

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());
        this.setResizable(false);

        textField = new JTextArea();
        textField.setPreferredSize(new Dimension(500, 70));
        textField.setFont(new Font("Consolas", Font.PLAIN, 16));
        textField.setAlignmentX(SwingConstants.LEFT);
        textField.setAlignmentY(SwingConstants.TOP);
        textField.setLineWrap(true);
        textField.setBackground(Color.black);
        textField.setForeground(Color.green);
        textField.setCaretColor(Color.white);
        this.add(textField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setSize(100, 70);
        buttonPanel.setLayout(new GridLayout(2, 1, 0, 6));

        buttonStart = new JButton("Start");
        buttonStart.addActionListener(this);
        buttonStart.setFocusable(false);
        buttonStart.setPreferredSize(new Dimension(100, 32));
        buttonStart.setFont(new Font("Consolas", Font.BOLD, 18));
        buttonStart.setBackground(Color.DARK_GRAY);
        buttonStart.setForeground(Color.WHITE);
        buttonPanel.add(buttonStart);

        buttonPause = new JButton("Pause");
        buttonPause.addActionListener(this);
        buttonPause.setFocusable(false);
        buttonPause.setPreferredSize(new Dimension(100, 32));
        buttonPause.setFont(new Font("Consolas", Font.BOLD, 18));
        buttonPause.setBackground(Color.DARK_GRAY);
        buttonPause.setForeground(Color.WHITE);
        buttonPanel.add(buttonPause);

        this.add(buttonPanel);

        this.pack();
        this.setVisible(true);
    }

    void start() throws InterruptedException {
        System.out.println("Nuking started.");
        tagging = true;

        timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!tagging) {
                    System.out.println("Nuking paused.");
                    timer.cancel();
                    return;
                } else {
                    try {
                        sendMessage("@everyone");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, 0, 500);
    }
    void sendMessage(String message) throws InterruptedException {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"content\":\"" + "@everyone" + "\"}");

        Request request = new Request.Builder()
                .url(webhook)
                .post(body)
                .build();
        while (tagging) {
            try {
                Response response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            TimeUnit.MILLISECONDS.sleep(500);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonStart) {
            tagging = false;
            webhook = textField.getText();
            try {
                start();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        } else if (e.getSource()==buttonPause) {
            tagging = false;
        }
    }


}

