package matchle;

import java.io.Serializable;

/**
 * 表示游戏状态的类，用于保存和加载游戏
 */
public class GameState implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final NGram secretKey;
    private final Corpus candidateCorpus;
    private final Filter accumulatedFilter;
    
    public GameState(NGram secretKey, Corpus candidateCorpus, Filter accumulatedFilter) {
        this.secretKey = secretKey;
        this.candidateCorpus = candidateCorpus;
        this.accumulatedFilter = accumulatedFilter;
    }
    
    public NGram getSecretKey() {
        return secretKey;
    }
    
    public Corpus getCandidateCorpus() {
        return candidateCorpus;
    }
    
    public Filter getAccumulatedFilter() {
        return accumulatedFilter;
    }
} 