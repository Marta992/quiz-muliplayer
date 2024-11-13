package it.bitcamp.model;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "GAME_MATCH")
public class GameMatchEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "NICKNAME")
    private String nickname; 

    @Column(name = "SCORE")
    private int score; 
    
    @CreationTimestamp
    @Column(name = "DATE_PLAYED")
    private LocalDateTime datePlayed; 

    public GameMatchEntity() {}

	public GameMatchEntity(int id, String nickname, int score, LocalDateTime datePlayed) {
		super();
		this.id = id;
		this.nickname = nickname;
		this.score = score;
		this.datePlayed = datePlayed;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public LocalDateTime getDatePlayed() {
		return datePlayed;
	}

	public void setDatePlayed(LocalDateTime datePlayed) {
		this.datePlayed = datePlayed;
	}
    
}

