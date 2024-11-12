package quiz_multiplayer;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int scoreId; // ID univoco per il punteggio
    private int playerId; // ID del giocatore
    private int score; // Punteggio ottenuto dal giocatore
    private Timestamp createdAt; // Data e ora della registrazione del punteggio

    // Costruttore
    public Score() {
    }

    public Score(int playerId, int score, Timestamp createdAt) {
        this.playerId = playerId;
        this.score = score;
        this.createdAt = createdAt;
    }

    // Getters e Setters
    public int getScoreId() {
        return scoreId;
    }

    public void setScoreId(int scoreId) {
        this.scoreId = scoreId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}

