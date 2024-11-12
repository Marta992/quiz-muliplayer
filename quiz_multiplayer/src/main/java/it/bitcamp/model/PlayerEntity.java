package it.bitcamp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PLAYER")
public class PlayerEntity {

    @Id
    @Column(name = "NICKNAME")
    private String nickname;
    
    @Column(name = "PASSWORD")
    private String password;
    
    @Column(name = "ADMIN")
    private boolean admin;

    @Column(name = "TOTAL_SCORE")  // Campo per memorizzare il punteggio totale
    private int totalScore;

    public PlayerEntity() {}

    public PlayerEntity(String nickname, String password, boolean admin, int totalScore) {
        this.nickname = nickname;
        this.password = password;
        this.admin = admin;
        this.totalScore = totalScore;
    }

    // Getters and Setters
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public void addToTotalScore(int score) {
        this.totalScore += score;  // Aggiunge il nuovo punteggio al punteggio totale
    }
}

