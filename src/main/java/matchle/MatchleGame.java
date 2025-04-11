package matchle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MatchleGame {
    private Corpus corpus;
    private NGram key;
    private Corpus candidateCorpus;
    private Optional<Filter> accumulatedFilter;
    private int maxRounds = 10;
    
    public static void main(String[] args) {
        MatchleGame game = new MatchleGame();
        game.initialize();
        game.play();
    }
    
    private void initialize() {
        loadCorpus();
        selectRandomKey();
        initializeGameState();
    }
    
    private void loadCorpus() {
        corpus = CorpusLoader.loadEnglishWords(5);
        if (corpus == null || corpus.size() == 0) {
            System.out.println("语料库为空或无效。");
            corpus = createDefaultCorpus();
        }
    }
    
    private Corpus createDefaultCorpus() {
        return Corpus.Builder.of()
                .add(NGram.from("rebus"))
                .add(NGram.from("redux"))
                .add(NGram.from("route"))
                .add(NGram.from("hello"))
                .build();
    }
    
    private void selectRandomKey() {
        List<NGram> keys = new ArrayList<>(corpus.corpus());
        Collections.shuffle(keys);
        key = keys.get(0);
        System.out.println("密钥（隐藏）: " + key);
    }
    
    private void initializeGameState() {
        accumulatedFilter = Optional.empty();
        candidateCorpus = corpus;
    }
    
    private void play() {
        for (int round = 1; round <= maxRounds; round++) {
            System.out.println("==== 第 " + round + " 轮 ====");
            if (playRound(round)) {
                return; // 游戏结束
            }
        }
        System.out.println("已达到最大轮次。密钥是: " + key);
    }
    
    private boolean playRound(int round) {
        NGram guess = makeGuess();
        
        if (isCorrectGuess(guess)) {
            return true; // 游戏结束
        }
        
        updateGameState(guess);
        
        return checkGameTermination();
    }
    
    private NGram makeGuess() {
        NGram guess = candidateCorpus.bestWorstCaseGuess();
        System.out.println("最佳猜测: " + guess);
        return guess;
    }
    
    private boolean isCorrectGuess(NGram guess) {
        if (guess.equals(key)) {
            System.out.println("猜测正确！密钥是: " + key);
            return true;
        }
        return false;
    }
    
    private void updateGameState(NGram guess) {
        Filter roundFilter = NGramMatcher.of(key, guess).match();
        System.out.println("本轮过滤器: " + roundFilter);
        
        updateAccumulatedFilter(roundFilter);
        updateCandidateCorpus();
    }
    
    private void updateAccumulatedFilter(Filter roundFilter) {
        if (accumulatedFilter.isPresent()) {
            accumulatedFilter = Optional.of(accumulatedFilter.get().and(Optional.of(roundFilter)));
        } else {
            accumulatedFilter = Optional.of(roundFilter);
        }
    }
    
    private void updateCandidateCorpus() {
        Corpus newCorpus = Corpus.Builder.of(candidateCorpus)
                .filter(accumulatedFilter.get())
                .build();
        
        if (newCorpus == null) {
            System.out.println("没有有效的候选词。密钥是: " + key);
            candidateCorpus = Corpus.Builder.of().build(); // 空语料库
        } else {
            candidateCorpus = newCorpus;
        }
        
        System.out.println("剩余候选词数量: " + candidateCorpus.size());
    }
    
    private boolean checkGameTermination() {
        // 检查是否只剩一个候选词
        if (candidateCorpus.size() == 1) {
            NGram remaining = candidateCorpus.corpus().iterator().next();
            System.out.println("候选词库缩减为一个: " + remaining);
            if (remaining.equals(key)) {
                System.out.println("找到密钥: " + key);
            } else {
                System.out.println("剩余候选词与密钥不匹配。密钥是: " + key);
            }
            return true;
        }
        
        // 检查候选词库是否为空
        if (candidateCorpus.size() == 0) {
            System.out.println("没有剩余候选词。密钥是: " + key);
            return true;
        }
        
        return false;
    }
}
