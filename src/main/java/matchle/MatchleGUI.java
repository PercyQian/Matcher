package matchle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Queue;
import java.util.LinkedList;
import matchle.util.UIUtils;

/**
 * A simple Swing GUI version of Matchle game
 */
public class MatchleGUI extends JFrame {

    // Game related data
    private Corpus corpus;             // Original corpus
    private NGram secretKey;           // Randomly selected secret key
    private Corpus candidateCorpus;    // Current candidate corpus (continuously narrowing)
    private Filter accumulatedFilter;  // Accumulated feedback filter (initially null)

    // UI controls
    private JTextField guessField;
    private JTextArea feedbackArea;
    private JLabel candidateLabel;
    private JLabel bestGuessLabel;
    private JButton submitButton;
    private JButton newGameButton;
    private JComboBox<String> difficultySelector;
    private JToggleButton darkModeToggle;
    private JButton saveGameButton;
    private JButton loadGameButton;

    // Color constants
    private static final Color COLOR_CORRECT = new Color(0, 204, 0); // Green
    private static final Color COLOR_MISPLACED = new Color(255, 204, 0); // Yellow
    private static final Color COLOR_ABSENT = new Color(102, 102, 102); // Gray
    private static final Color COLOR_BACKGROUND_LIGHT = Color.WHITE;
    private static final Color COLOR_BACKGROUND_DARK = new Color(50, 50, 50);
    private static final Color COLOR_TEXT_LIGHT = Color.BLACK;
    private static final Color COLOR_TEXT_DARK = Color.WHITE;
    
    // Theme state
    private boolean darkModeEnabled = false;

    // Panel instance variables
    private JPanel topPanel;
    private JPanel feedbackPanel;
    private JScrollPane centerPanel; // Using JScrollPane for direct addition
    private JPanel bottomPanel;

    public MatchleGUI() {
        super("Matchle Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        initUI();
        startNewGame();
    }

    private void initUI() {
        // Split into multiple methods to reduce complexity
        initTopPanel();
        initCenterPanel();
        initBottomPanel();
        setupLayout();
        registerEventHandlers();
        registerKeyboardShortcuts();
    }

    private void initTopPanel() {
        // Only contains top panel initialization logic
        JPanel topPanel = new JPanel(new FlowLayout());
        
        // Initialize difficulty selector
        JLabel difficultyLabel = new JLabel("Difficulty: ");
        String[] difficulties = {"Easy", "Medium", "Hard"};
        difficultySelector = new JComboBox<>(difficulties);
        
        // Initialize other controls
        darkModeToggle = new JToggleButton("Dark Mode");
        guessField = new JTextField(10);
        submitButton = new JButton("Submit Guess");
        newGameButton = new JButton("New Game");
        saveGameButton = new JButton("Save Game");
        loadGameButton = new JButton("Load Game");
        
        // Add to panel
        topPanel.add(difficultyLabel);
        topPanel.add(difficultySelector);
        topPanel.add(darkModeToggle);
        topPanel.add(guessField);
        topPanel.add(submitButton);
        topPanel.add(newGameButton);
        topPanel.add(saveGameButton);
        topPanel.add(loadGameButton);
        
        // Save panel reference
        this.topPanel = topPanel;
    }

    private void initCenterPanel() {
        feedbackArea = new JTextArea(10, 50);
        feedbackArea.setEditable(false);
        
        JPanel feedbackPanel = new JPanel();
        feedbackPanel.setLayout(new BoxLayout(feedbackPanel, BoxLayout.Y_AXIS));
        feedbackPanel.add(feedbackArea);
        
        this.feedbackPanel = feedbackPanel;
        this.centerPanel = new JScrollPane(feedbackPanel);
    }

    private void initBottomPanel() {
        // Only contains bottom panel initialization logic
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        candidateLabel = new JLabel("Remaining candidates: ");
        bestGuessLabel = new JLabel("Best guess suggestion: ");
        bottomPanel.add(candidateLabel);
        bottomPanel.add(bestGuessLabel);
        
        // Save panel reference
        this.bottomPanel = bottomPanel;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void registerEventHandlers() {
        // Button event binding
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitGuess();
            }
        });
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startNewGame();
            }
        });
        saveGameButton.addActionListener(e -> saveGame());
        loadGameButton.addActionListener(e -> loadGame());

        // New event handling
        darkModeToggle.addActionListener(e -> toggleDarkMode());
        difficultySelector.addActionListener(e -> adjustDifficulty());
    }

    private void toggleDarkMode() {
        darkModeEnabled = darkModeToggle.isSelected();
        updateTheme();
    }

    private void updateTheme() {
        Color bg = darkModeEnabled ? COLOR_BACKGROUND_DARK : COLOR_BACKGROUND_LIGHT;
        Color fg = darkModeEnabled ? COLOR_TEXT_DARK : COLOR_TEXT_LIGHT;
        
        // Update colors of all components
        updateComponentColors(this, bg, fg);
        
        // Refresh UI
        SwingUtilities.updateComponentTreeUI(this);
    }

    /**
     * Optimize recursive component color update, reduce complexity
     */
    private void updateComponentColors(Container container, Color bg, Color fg) {
        // Use BFS instead of recursion to reduce complexity
        Queue<Container> queue = new LinkedList<>();
        queue.add(container);
        
        while (!queue.isEmpty()) {
            Container current = queue.poll();
            current.setBackground(bg);
            current.setForeground(fg);
            
            for (Component comp : current.getComponents()) {
                comp.setBackground(bg);
                comp.setForeground(fg);
                
                if (comp instanceof Container) {
                    queue.add((Container) comp);
                }
            }
        }
    }

    private void registerKeyboardShortcuts() {
        // Add enter key to submit guess
        guessField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "submit");
        guessField.getActionMap().put("submit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitGuess();
            }
        });
        
        // Add Ctrl+N shortcut to create new game
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("control N"), "newGame");
        getRootPane().getActionMap().put("newGame", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startNewGame();
            }
        });
    }

    private void adjustDifficulty() {
        String difficulty = (String) difficultySelector.getSelectedItem();
        // Adjust game parameters based on difficulty
        if ("Easy".equals(difficulty)) {
            // Easy mode: provide more hints
            bestGuessLabel.setVisible(true);
        } else if ("Medium".equals(difficulty)) {
            // Medium mode: hide best guess
            bestGuessLabel.setVisible(false);
        } else if ("Hard".equals(difficulty)) {
            // Hard mode: use larger corpus, fewer hints
            bestGuessLabel.setVisible(false);
            // Load more complex corpus, can be implemented here
        }
    }

    /**
     * Optimize game state management, unify save and load operations
     */
    private void handleGameState(boolean isSaving) {
        try {
            if (isSaving) {
                GameState state = new GameState(secretKey, candidateCorpus, accumulatedFilter);
                GameStateManager.saveGame(state, "savedgame.dat");
                UIUtils.showInfoMessage(this, "Game Saved", "Save Successful");
            } else {
                GameState state = GameStateManager.loadGame("savedgame.dat");
                applyGameState(state);
                UIUtils.showInfoMessage(this, "Game Loaded", "Load Successful");
            }
        } catch (Exception ex) {
            String operation = isSaving ? "Save" : "Load";
            UIUtils.showErrorMessage(this, operation + " Game Failed: " + ex.getMessage(), 
                    operation + " Error");
        }
    }

    /**
     * Apply loaded game state
     */
    private void applyGameState(GameState state) {
        secretKey = state.getSecretKey();
        candidateCorpus = state.getCandidateCorpus();
        accumulatedFilter = state.getAccumulatedFilter();
        updateLabels();
    }

    // Use optimized method
    private void saveGame() {
        handleGameState(true);
    }

    private void loadGame() {
        handleGameState(false);
    }

    /**
     * Start a new game: reset corpus, randomly select secret key, clear accumulated filter, etc.
     */
    private void startNewGame() {
        // load the corpus
        // Use CorpusLoader to load a 5-letter corpus
        corpus = CorpusLoader.loadEnglishWords(5);
        if (corpus == null || corpus.size() == 0) {
            JOptionPane.showMessageDialog(this, "Failed to load corpus. Using default corpus instead.", "Warning", JOptionPane.WARNING_MESSAGE);
            // Use default corpus as backup
            // use the default corpus as a backup
            corpus = Corpus.Builder.of()
                    .add(NGram.from("rebus"))
                    .add(NGram.from("redux"))
                    .add(NGram.from("route"))
                    .add(NGram.from("hello"))
                    .build();
        }

        // choose a secret key from the corpus
        // Randomly select a key from the corpus
        List<NGram> keyList = new ArrayList<>(corpus.corpus());
        Collections.shuffle(keyList);
        secretKey = keyList.get(0);
        // Reset candidate corpus and accumulated filter
        candidateCorpus = corpus;
        accumulatedFilter = null;
        feedbackArea.setText("");
        candidateLabel.setText("Remaining candidates: " + candidateCorpus.size());
        bestGuessLabel.setText("Best guess suggestion: " + candidateCorpus.bestWorstCaseGuess());
        guessField.setText("");
        submitButton.setEnabled(true);
        // For debugging, you can print the key here, it can be hidden in actual game
        System.out.println("Secret key: " + secretKey);
    }

    /**
     * Handles the logic when a guess is submitted.
     */
    private void submitGuess() {
        String guessStr = validateAndGetGuess();
        if (guessStr == null) return;
        
        NGram guess = NGram.from(guessStr);
        displayGuess(guessStr);
        
        if (checkForWin(guess)) return;
        
        updateGameState(guess);
    }

    // validate the guess
    private String validateAndGetGuess() {
        String guessStr = guessField.getText().trim().toLowerCase();
        if (guessStr.length() != 5) {
            JOptionPane.showMessageDialog(this, "Please enter a 5-letter word.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return guessStr;
    }

    // display the guess
    private void displayGuess(String guessStr) {
        feedbackArea.append("Your guess: " + guessStr + "\n");
    }

    // check if the guess is the secret key
    private boolean checkForWin(NGram guess) {
        if (guess.equals(secretKey)) {
            feedbackArea.append("Congratulations! You guessed the key: " + secretKey.toString() + "\n");
            submitButton.setEnabled(false);
            return true;
        }
        return false;
    }

    /**
     * Updates the game state with the new guess
     */
    private void updateGameState(NGram guess) {
        Filter roundFilter = generateRoundFilter(guess);
        updateAccumulatedFilter(roundFilter);
        updateCandidateCorpus();
        displayCandidates();
        updateLabels();
    }

    /**
     * Generates a filter based on the current guess and secret key
     */
    private Filter generateRoundFilter(NGram guess) {
        Filter roundFilter = NGramMatcher.of(secretKey, guess).match();
        feedbackArea.append("Round filter pattern: " + roundFilter.toString() + "\n");
        return roundFilter;
    }

    /**
     * Updates the accumulated filter with the new round filter
     */
    private void updateAccumulatedFilter(Filter roundFilter) {
        if (accumulatedFilter != null) {
            accumulatedFilter = accumulatedFilter.and(Optional.of(roundFilter));
        } else {
            accumulatedFilter = roundFilter;
        }
    }

    /**
     * Updates the candidate corpus based on the accumulated filter
     */
    private void updateCandidateCorpus() {
        candidateCorpus = Corpus.Builder.of(candidateCorpus).filter(accumulatedFilter).build();
        if (candidateCorpus == null || candidateCorpus.size() == 0) {
            feedbackArea.append("No candidates remain. The key was: " + secretKey.toString() + "\n");
            submitButton.setEnabled(false);
        }
    }

    /**
     * Displays the current candidates in the feedback area
     */
    private void displayCandidates() {
        if (candidateCorpus == null) return;
        String candidates = candidateCorpus.corpus().stream()
            .map(NGram::toString)
            .collect(Collectors.joining(", "));
        feedbackArea.append("Candidates: [" + candidates + "]\n\n");
    }

    /**
     * Updates the UI labels with current game state
     */
    private void updateLabels() {
        if (candidateCorpus != null) {
            candidateLabel.setText("Remaining candidates: " + candidateCorpus.size());
            bestGuessLabel.setText("Best guess suggestion: " + candidateCorpus.bestWorstCaseGuess());
        }
        guessField.setText("");
    }

    private boolean checkGameTermination() {
        return handleSingleCandidate() || handleEmptyCorpus();
    }

    private boolean handleSingleCandidate() {
        if (candidateCorpus.size() != 1) {
            return false;
        }
        
        NGram remaining = candidateCorpus.corpus().iterator().next();
        System.out.println("Candidate corpus reduced to one: " + remaining);
        
        if (remaining.equals(secretKey)) {
            System.out.println("Found key: " + secretKey);
        } else {
            System.out.println("Remaining candidates do not match the key. Key is: " + secretKey);
        }
        
        return true;
    }

    private boolean handleEmptyCorpus() {
        if (candidateCorpus.size() == 0) {
            System.out.println("No remaining candidates. Key is: " + secretKey);
            return true;
        }
        
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MatchleGUI gui = new MatchleGUI();
            gui.setVisible(true);
        });
    }
}
