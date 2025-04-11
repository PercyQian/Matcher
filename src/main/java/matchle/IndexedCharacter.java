package matchle;

import java.io.Serializable;

public record IndexedCharacter(int index, Character character) implements Serializable {
    private static final long serialVersionUID = 1L;
}
