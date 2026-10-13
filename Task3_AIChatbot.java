import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class Task3_AIChatbot extends JFrame {

    // ---------- NLP + rule-based engine ----------
    static class Intent {
        String name;
        Set<String> keywords = new HashSet<>();
        List<String> responses = new ArrayList<>();
        Intent(String name, String[] kw, String[] resp) {
            this.name = name;
            for (String k : kw) keywords.add(stem(k));
            responses.addAll(Arrays.asList(resp));
        }
    }

    static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList(
            "a", "an", "the", "is", "are", "am", "to", "of", "in", "on", "for", "and", "or",
            "me", "my", "i", "you", "your", "can", "do", "does", "please", "it", "this", "that", "with"));

    // Tokenization -> lowercase -> remove punctuation -> remove stopwords -> simple stemming
    static List<String> preprocess(String text) {
        List<String> out = new ArrayList<>();
        for (String w : text.toLowerCase().replaceAll("[^a-z0-9 ]", " ").split("\\s+")) {
            if (w.isEmpty() || STOPWORDS.contains(w)) continue;
            out.add(stem(w));
        }
        return out;
    }

    static String stem(String w) {
        w = w.toLowerCase();
        if (w.length() > 5 && w.endsWith("ing")) return w.substring(0, w.length() - 3);
        if (w.length() > 4 && w.endsWith("ed")) return w.substring(0, w.length() - 2);
        if (w.length() > 3 && w.endsWith("es")) return w.substring(0, w.length() - 2);
        if (w.length() > 3 && w.endsWith("s")) return w.substring(0, w.length() - 1);
        return w;
    }

    List<Intent> intents = new ArrayList<>();
    Random rnd = new Random();

    void trainBot() {
        intents.add(new Intent("greeting",
                new String[]{"hello", "hi", "hey", "morning", "evening"},
                new String[]{"Hello! How can I help you today?", "Hi there! Ask me anything."}));
        intents.add(new Intent("name",
                new String[]{"name", "who", "called"},
                new String[]{"I'm JavaBot, a chatbot built in Java for the CodeAlpha internship."}));
        intents.add(new Intent("how_are_you",
                new String[]{"how", "doing", "feeling"},
                new String[]{"I'm doing great, thanks for asking!"}));
        intents.add(new Intent("hours",
                new String[]{"time", "hour", "open", "close", "timing"},
                new String[]{"We are open Monday to Saturday, 9 AM to 6 PM."}));
        intents.add(new Intent("contact",
                new String[]{"contact", "email", "phone", "whatsapp", "reach"},
                new String[]{"Email: services@codealpha.tech | Website: www.codealpha.tech"}));
        intents.add(new Intent("internship",
                new String[]{"internship", "certificate", "task", "submit", "submission"},
                new String[]{"Complete at least 2 or 3 tasks and submit them via the form shared in your WhatsApp group to get your certificate."}));
        intents.add(new Intent("java",
                new String[]{"java", "oop", "programming", "code"},
                new String[]{"Java is an object-oriented language. Key OOP ideas: encapsulation, inheritance, polymorphism, abstraction."}));
        intents.add(new Intent("ai",
                new String[]{"ai", "artificial", "intelligence", "machine", "learning", "nlp"},
                new String[]{"AI lets machines mimic human intelligence. NLP is the part that helps them understand language, like I'm doing now!"}));
        intents.add(new Intent("thanks",
                new String[]{"thank", "thanks", "great", "awesome"},
                new String[]{"You're welcome!", "Happy to help!"}));
        intents.add(new Intent("bye",
                new String[]{"bye", "goodbye", "exit", "quit", "later"},
                new String[]{"Goodbye! Have a great day!"}));
    }

    String respond(String input) {
        List<String> tokens = preprocess(input);
        if (tokens.isEmpty()) return "Please type something so I can help.";
        Intent best = null;
        int bestScore = 0;
        for (Intent in : intents) {
            int score = 0;
            for (String t : tokens) if (in.keywords.contains(t)) score++;
            if (score > bestScore) { bestScore = score; best = in; }
        }
        if (best == null) return "Sorry, I didn't understand that. Try asking about the internship, Java, AI, or contact details.";
        return best.responses.get(rnd.nextInt(best.responses.size()));
    }

    // ---------- GUI ----------
    JTextArea chatArea = new JTextArea();
    JTextField inputField = new JTextField();

    Task3_AIChatbot() {
        super("JavaBot - AI Chatbot");
        trainBot();

        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JButton send = new JButton("Send");
        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.add(inputField, BorderLayout.CENTER);
        bottom.add(send, BorderLayout.EAST);
        bottom.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(new JScrollPane(chatArea), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        ActionListener action = e -> {
            String msg = inputField.getText().trim();
            if (msg.isEmpty()) return;
            chatArea.append("You: " + msg + "\n");
            String reply = respond(msg);
            chatArea.append("Bot: " + reply + "\n\n");
            inputField.setText("");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        };
        send.addActionListener(action);
        inputField.addActionListener(action);

        chatArea.append("Bot: Hi! I'm JavaBot. Ask me anything!\n\n");
        setSize(450, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Task3_AIChatbot::new);
    }
}