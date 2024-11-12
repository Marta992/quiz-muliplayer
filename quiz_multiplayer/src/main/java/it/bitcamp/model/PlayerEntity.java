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

	public PlayerEntity() {
	}
	
	public PlayerEntity(String nickname, String password, boolean admin) {
		super();
		this.nickname = nickname;
		this.password = password;
		this.admin = admin;
	}

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
	
}
