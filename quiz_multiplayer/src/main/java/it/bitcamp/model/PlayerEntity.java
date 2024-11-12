package it.bitcamp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PLAYER")
public class PlayerEntity {

    @Id
    @Column(name = "NICKNAME")
    private String nickname;
    
    @Column(name = "PASSWORD_HASH")
    private String passwordHash;
    
    @Column(name = "ADMIN")
    private boolean admin;

    @Column(name = "TOTAL_SCORE")
    private int totalScore;

    public PlayerEntity() {}

    public PlayerEntity(String nickname, String passwordHash, boolean admin, int totalScore) {
        this.nickname = nickname;
        this.passwordHash = passwordHash;
        this.admin = admin;
        this.totalScore = totalScore;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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
}

