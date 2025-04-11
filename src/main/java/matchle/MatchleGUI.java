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
 * 一个简单的 Swing GUI 版 Matchle 游戏
 */
public class MatchleGUI extends JFrame {

    // 游戏相关数据
    private Corpus corpus;             // 原始词库
    private NGram secretKey;           // 随机选出的密钥
    private Corpus candidateCorpus;    // 当前候选词库（不断缩小）
    private Filter accumulatedFilter;  // 累积的反馈过滤器（初始为 null）

    // UI 控件
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

    // 颜色常量
    private static final Color COLOR_CORRECT = new Color(0, 204, 0); // 绿色
    private static final Color COLOR_MISPLACED = new Color(255, 204, 0); // 黄色
    private static final Color COLOR_ABSENT = new Color(102, 102, 102); // 灰色
    private static final Color COLOR_BACKGROUND_LIGHT = Color.WHITE;
    private static final Color COLOR_BACKGROUND_DARK = new Color(50, 50, 50);
    private static final Color COLOR_TEXT_LIGHT = Color.BLACK;
    private static final Color COLOR_TEXT_DARK = Color.WHITE;
    
    // 主题状态
    private boolean darkModeEnabled = false;

    // 添加面板实例变量
    private JPanel topPanel;
    private JPanel feedbackPanel;
    private JScrollPane centerPanel; // 改用JScrollPane以便直接添加
    private JPanel bottomPanel;

    public MatchleGUI() {
        super("Matchle Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        initUI();
        startNewGame();
    }

    private void initUI() {
        // 拆分为多个方法减少复杂度
        initTopPanel();
        initCenterPanel();
        initBottomPanel();
        setupLayout();
        registerEventHandlers();
        registerKeyboardShortcuts();
    }

    private void initTopPanel() {
        // 只包含顶部面板的初始化逻辑
        JPanel topPanel = new JPanel(new FlowLayout());
        
        // 初始化难度选择器
        JLabel difficultyLabel = new JLabel("难度: ");
        String[] difficulties = {"简单", "中等", "困难"};
        difficultySelector = new JComboBox<>(difficulties);
        
        // 初始化其他控件
        darkModeToggle = new JToggleButton("暗黑模式");
        guessField = new JTextField(10);
        submitButton = new JButton("提交猜测");
        newGameButton = new JButton("新游戏");
        saveGameButton = new JButton("保存游戏");
        loadGameButton = new JButton("加载游戏");
        
        // 添加到面板
        topPanel.add(difficultyLabel);
        topPanel.add(difficultySelector);
        topPanel.add(darkModeToggle);
        topPanel.add(guessField);
        topPanel.add(submitButton);
        topPanel.add(newGameButton);
        topPanel.add(saveGameButton);
        topPanel.add(loadGameButton);
        
        // 保存面板引用
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
        // 只包含底部面板的初始化逻辑
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        candidateLabel = new JLabel("Remaining candidates: ");
        bestGuessLabel = new JLabel("Best guess suggestion: ");
        bottomPanel.add(candidateLabel);
        bottomPanel.add(bestGuessLabel);
        
        // 保存面板引用
        this.bottomPanel = bottomPanel;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void registerEventHandlers() {
        // 按钮事件绑定
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

        // 新的事件处理
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
        
        // 更新所有组件的颜色
        updateComponentColors(this, bg, fg);
        
        // 刷新UI
        SwingUtilities.updateComponentTreeUI(this);
    }

    /**
     * 优化递归组件颜色更新，减少复杂度
     */
    private void updateComponentColors(Container container, Color bg, Color fg) {
        // 使用BFS而不是递归来降低复杂度
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
        // 添加回车键提交猜测
        guessField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "submit");
        guessField.getActionMap().put("submit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitGuess();
            }
        });
        
        // 添加Ctrl+N快捷键创建新游戏
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
        // 根据难度调整游戏参数
        if ("简单".equals(difficulty)) {
            // 简单模式：提供更多提示
            bestGuessLabel.setVisible(true);
        } else if ("中等".equals(difficulty)) {
            // 中等模式：隐藏最优猜测
            bestGuessLabel.setVisible(false);
        } else if ("困难".equals(difficulty)) {
            // 困难模式：使用更大的词库，更少的提示
            bestGuessLabel.setVisible(false);
            // 加载更复杂的词库，可以在这里实现
        }
    }

    /**
     * 优化游戏状态管理，统一保存和加载操作
     */
    private void handleGameState(boolean isSaving) {
        try {
            if (isSaving) {
                GameState state = new GameState(secretKey, candidateCorpus, accumulatedFilter);
                GameStateManager.saveGame(state, "savedgame.dat");
                UIUtils.showInfoMessage(this, "游戏已保存", "保存成功");
            } else {
                GameState state = GameStateManager.loadGame("savedgame.dat");
                applyGameState(state);
                UIUtils.showInfoMessage(this, "游戏已加载", "加载成功");
            }
        } catch (Exception ex) {
            String operation = isSaving ? "保存" : "加载";
            UIUtils.showErrorMessage(this, operation + "游戏失败: " + ex.getMessage(), 
                    operation + "错误");
        }
    }

    /**
     * 应用载入的游戏状态
     */
    private void applyGameState(GameState state) {
        secretKey = state.getSecretKey();
        candidateCorpus = state.getCandidateCorpus();
        accumulatedFilter = state.getAccumulatedFilter();
        updateLabels();
    }

    // 使用优化后的方法
    private void saveGame() {
        handleGameState(true);
    }

    private void loadGame() {
        handleGameState(false);
    }

    /**
     * 开始一局新游戏：重置词库、随机选择密钥、清空累积过滤器等
     */
    private void startNewGame() {
        // load the corpus
        // 使用 CorpusLoader 加载 5 个字母的词库
        corpus = CorpusLoader.loadEnglishWords(5);
        if (corpus == null || corpus.size() == 0) {
            JOptionPane.showMessageDialog(this, "Failed to load corpus. Using default corpus instead.", "Warning", JOptionPane.WARNING_MESSAGE);
            // 使用默认词库作为后备
            // use the default corpus as a backup
            corpus = Corpus.Builder.of()
                    .add(NGram.from("rebus"))
                    .add(NGram.from("redux"))
                    .add(NGram.from("route"))
                    .add(NGram.from("hello"))
                    .build();
        }

        // choose a secret key from the corpus
        // 从 corpus 随机选取一个密钥
        List<NGram> keyList = new ArrayList<>(corpus.corpus());
        Collections.shuffle(keyList);
        secretKey = keyList.get(0);
        // 重置候选词库和累积过滤器
        candidateCorpus = corpus;
        accumulatedFilter = null;
        feedbackArea.setText("");
        candidateLabel.setText("Remaining candidates: " + candidateCorpus.size());
        bestGuessLabel.setText("Best guess suggestion: " + candidateCorpus.bestWorstCaseGuess());
        guessField.setText("");
        submitButton.setEnabled(true);
        // 为了调试，可以在这里打印密钥，实际游戏中可隐藏
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MatchleGUI gui = new MatchleGUI();
            gui.setVisible(true);
        });
    }
}
