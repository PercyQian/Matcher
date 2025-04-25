package matchle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.stream.Collectors;
import matchle.util.UIUtils;

/**
 * A graphical user interface implementation of the Matchle word guessing game.
 * This class provides a Swing-based GUI for players to interact with the game,
 * offering features such as:
 * <ul>
 *   <li>Interactive word guessing with immediate feedback</li>
 *   <li>Visual representation of guess matches</li>
 *   <li>Difficulty level selection</li>
 *   <li>Dark mode appearance option</li>
 *   <li>Game state saving and loading</li>
 *   <li>Keyboard shortcuts for common actions</li>
 * </ul>
 * The class relies on GameLogic for the core game mechanics while handling
 * all UI rendering and user interaction.
 */
public class MatchleGUI extends JFrame {

    /** Original corpus of words loaded at game start */
    private Corpus corpus;
    
    /** Game logic engine that handles core mechanics */
    private GameLogic gameLogic;

    // UI controls
    /** Text field for entering guesses */
    private JTextField guessField;
    
    /** Text area displaying game feedback and history */
    private JTextArea feedbackArea;
    
    /** Label showing number of remaining candidate words */
    private JLabel candidateLabel;
    
    /** Label showing the best guess suggestion */
    private JLabel bestGuessLabel;
    
    /** Button to submit a guess */
    private JButton submitButton;
    
    /** Button to start a new game */
    private JButton newGameButton;
    
    /** Dropdown for selecting difficulty level */
    private JComboBox<String> difficultySelector;
    
    /** Toggle button for dark/light mode */
    private JToggleButton darkModeToggle;
    
    /** Button to save the current game */
    private JButton saveGameButton;
    
    /** Button to load a saved game */
    private JButton loadGameButton;

    // Color constants
    /** Color used for correctly placed letters */
    private static final Color COLOR_CORRECT = new Color(0, 204, 0); // Green
    
    /** Color used for correct letters in wrong position */
    private static final Color COLOR_MISPLACED = new Color(255, 204, 0); // Yellow
    
    /** Color used for letters not in the target word */
    private static final Color COLOR_ABSENT = new Color(102, 102, 102); // Gray
    
    /** Background color for light mode */
    private static final Color COLOR_BACKGROUND_LIGHT = Color.WHITE;
    
    /** Background color for dark mode */
    private static final Color COLOR_BACKGROUND_DARK = new Color(50, 50, 50);
    
    /** Text color for light mode */
    private static final Color COLOR_TEXT_LIGHT = Color.BLACK;
    
    /** Text color for dark mode */
    private static final Color COLOR_TEXT_DARK = Color.WHITE;
    
    /** Current theme state (dark mode enabled/disabled) */
    private boolean darkModeEnabled = false;

    // Panel instance variables
    /** Top panel containing controls */
    private JPanel topPanel;
    
    /** Panel containing feedback text area */
    private JPanel feedbackPanel;
    
    /** Scrollable panel in the center of the layout */
    private JScrollPane centerPanel;
    
    /** Bottom panel containing status information */
    private JPanel bottomPanel;

    /**
     * Constructs a new MatchleGUI window.
     * Sets up the window properties, initializes the game logic,
     * and prepares the UI components.
     */
    public MatchleGUI() {
        super("Matchle Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        gameLogic = new GameLogic();
        initUI();
        startNewGame();
    }

    /**
     * Initializes all UI components by calling specialized
     * initialization methods for each section of the interface.
     */
    private void initUI() {
        // Split into multiple methods to reduce complexity
        initTopPanel();
        initCenterPanel();
        initBottomPanel();
        setupLayout();
        registerEventHandlers();
        registerKeyboardShortcuts();
    }

    /**
     * Initializes the top panel containing game controls.
     * This includes difficulty selector, dark mode toggle,
     * text field for guesses, and action buttons.
     */
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

    /**
     * Initializes the center panel containing the feedback area.
     * The feedback area displays game history and current game state.
     */
    private void initCenterPanel() {
        feedbackArea = new JTextArea(10, 50);
        feedbackArea.setEditable(false);
        
        JPanel feedbackPanel = new JPanel();
        feedbackPanel.setLayout(new BoxLayout(feedbackPanel, BoxLayout.Y_AXIS));
        feedbackPanel.add(feedbackArea);
        
        this.feedbackPanel = feedbackPanel;
        this.centerPanel = new JScrollPane(feedbackPanel);
    }

    /**
     * Initializes the bottom panel containing game status information.
     * This includes labels for remaining candidates and best guess suggestions.
     */
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

    /**
     * Sets up the main layout of the window by adding
     * the three panels to the appropriate border locations.
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Registers action listeners for all interactive UI components.
     * This sets up the event handling for buttons and other controls.
     */
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

    /**
     * Toggles between dark and light mode appearance.
     * This method is called when the dark mode toggle button is clicked.
     */
    private void toggleDarkMode() {
        darkModeEnabled = darkModeToggle.isSelected();
        updateTheme();
    }

    /**
     * Updates the visual theme of all components based on
     * the current dark/light mode setting.
     */
    private void updateTheme() {
        Color bg = darkModeEnabled ? COLOR_BACKGROUND_DARK : COLOR_BACKGROUND_LIGHT;
        Color fg = darkModeEnabled ? COLOR_TEXT_DARK : COLOR_TEXT_LIGHT;
        
        // Update colors of all components
        updateComponentColors(this, bg, fg);
        
        // Refresh UI
        SwingUtilities.updateComponentTreeUI(this);
    }

    /**
     * Updates the colors of all components in a container using breadth-first search.
     * This optimizes the recursive component color update to reduce complexity.
     *
     * @param container The root container to update
     * @param bg The background color to apply
     * @param fg The foreground color to apply
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

    /**
     * Registers keyboard shortcuts for common actions.
     * This includes Enter to submit a guess and Ctrl+N for new game.
     */
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

    /**
     * Adjusts game parameters based on the selected difficulty level.
     * Different difficulty levels provide different levels of assistance
     * and challenge to the player.
     */
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
     * Handles game state saving and loading operations.
     * This method provides a unified approach to both save and load
     * functionality with appropriate error handling.
     *
     * @param isSaving true if saving game, false if loading game
     */
    private void handleGameState(boolean isSaving) {
        try {
            if (isSaving) {
                GameState state = gameLogic.createGameState();
                GameStateManager.saveGame(state, "savedgame.dat");
                UIUtils.showInfoMessage(this, "Game Saved", "Save Successful");
            } else {
                GameState state = GameStateManager.loadGame("savedgame.dat");
                gameLogic.loadState(state);
                updateLabels();
                displayCandidates();
                UIUtils.showInfoMessage(this, "Game Loaded", "Load Successful");
            }
        } catch (Exception ex) {
            String operation = isSaving ? "Save" : "Load";
            UIUtils.showErrorMessage(this, operation + " Game Failed: " + ex.getMessage(), 
                    operation + " Error");
        }
    }

    /**
     * Saves the current game state to a file.
     * This uses the optimized handleGameState method with the save flag.
     */
    private void saveGame() {
        handleGameState(true);
    }
    
    /**
     * Loads a previously saved game state from a file.
     * This uses the optimized handleGameState method with the load flag.
     */
    private void loadGame() {
        handleGameState(false);
    }

    /**
     * Starts a new game by resetting the corpus, selecting a new secret key,
     * and resetting the UI state. This method is called when the user clicks
     * the New Game button or uses the Ctrl+N shortcut.
     */
    private void startNewGame() {
        // load the corpus
        // Use CorpusLoader to load a 5-letter corpus
        corpus = CorpusLoader.loadEnglishWords(5);
        if (corpus == null || corpus.size() == 0) {
            JOptionPane.showMessageDialog(this, "Failed to load corpus. Using default corpus instead.", "Warning", JOptionPane.WARNING_MESSAGE);
            // Use default corpus as backup
            corpus = gameLogic.createDefaultCorpus();
        }

        // Initialize game logic with corpus
        gameLogic.initialize(corpus);

        // reset the UI
        feedbackArea.setText("");
        updateLabels();
        submitButton.setEnabled(true);
        // For debugging, you can print the key here, it can be hidden in actual game
        System.out.println("Secret key: " + gameLogic.getSecretKey());
    }

    /**
     * Handles the action when the user submits a guess.
     * This validates the guess, processes it, and updates the game state.
     */
    private void submitGuess() {
        String guessStr = validateAndGetGuess();
        if (guessStr == null) return;
        
        NGram guess = NGram.from(guessStr);
        displayGuess(guessStr);

        if (checkForWin(guess)) {
            return;
        }

        updateGameState(guess);
    }

    /**
     * Validates the user's input and returns the processed guess string.
     * This method checks if the input has the correct length and formats it.
     *
     * @return The validated guess string, or null if invalid
     */
    private String validateAndGetGuess() {
        String guessStr = guessField.getText().trim().toLowerCase();
        
        if (guessStr.length() != gameLogic.getSecretKey().size()) {
            UIUtils.showErrorMessage(this, 
                    "Guess must be " + gameLogic.getSecretKey().size() + " letters long", "Invalid Guess");
            return null;
        }
        
        return guessStr;
    }

    /**
     * Displays the current guess in the feedback area.
     *
     * @param guessStr The guess string to display
     */
    private void displayGuess(String guessStr) {
        feedbackArea.append("Guess: " + guessStr + "\n");
    }

    /**
     * Checks if the current guess matches the secret key (win condition).
     * If the guess is correct, updates the UI to indicate a win.
     *
     * @param guess The NGram to check against the secret key
     * @return true if the guess is correct, false otherwise
     */
    private boolean checkForWin(NGram guess) {
        if (gameLogic.isCorrectGuess(guess)) {
            feedbackArea.append("Correct! You found the secret key: " + gameLogic.getSecretKey() + "\n");
            submitButton.setEnabled(false);
            return true;
        }
        return false;
    }

    /**
     * Updates the game state based on the player's guess.
     * 
     * @param guess The player's guess
     */
    private void updateGameState(NGram guess) {
        Filter roundFilter = gameLogic.processGuess(guess);
        feedbackArea.append("Round filter pattern: " + roundFilter.toString() + "\n");
        
        // Update the GUI
        displayCandidates();
        updateLabels();
        
        // Check if game has terminated (single candidate or no candidates)
        Corpus candidateCorpus = gameLogic.getCandidateCorpus();
        int candidateSize = candidateCorpus.size();
        if (candidateSize == 1) {
            NGram remaining = candidateCorpus.corpus().iterator().next();
            feedbackArea.append("Candidate corpus reduced to one: " + remaining + "\n");
            if (remaining.equals(gameLogic.getSecretKey())) {
                feedbackArea.append("Found key: " + gameLogic.getSecretKey() + "\n");
            } else {
                feedbackArea.append("Remaining candidate does not match key. Key was: " + gameLogic.getSecretKey() + "\n");
            }
            guessField.setEnabled(false);
            submitButton.setEnabled(false);
        } else if (candidateSize == 0) {
            feedbackArea.append("No candidates remain. The key was: " + gameLogic.getSecretKey() + "\n");
            guessField.setEnabled(false);
            submitButton.setEnabled(false);
        }
    }

    /**
     * Displays the current candidate words in the feedback area.
     * This provides the player with information about the remaining
     * possible words based on their guesses so far.
     */
    private void displayCandidates() {
        Corpus candidateCorpus = gameLogic.getCandidateCorpus();
        if (candidateCorpus == null) return;
        
        String candidates = candidateCorpus.corpus().stream()
            .map(NGram::toString)
            .collect(Collectors.joining(", "));
        feedbackArea.append("Candidates: [" + candidates + "]\n\n");
    }

    /**
     * Updates the UI labels with the current game state.
     * This includes updating the candidate count and best guess suggestion.
     */
    private void updateLabels() {
        Corpus candidateCorpus = gameLogic.getCandidateCorpus();
        if (candidateCorpus != null) {
            candidateLabel.setText("Remaining candidates: " + candidateCorpus.size());
            bestGuessLabel.setText("Best guess suggestion: " + gameLogic.getBestGuess());
        }
        guessField.setText("");
    }

    /**
     * Main entry point for the GUI version of the Matchle game.
     * Creates and displays the game window.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MatchleGUI gui = new MatchleGUI();
            gui.setVisible(true);
        });
    }
}
