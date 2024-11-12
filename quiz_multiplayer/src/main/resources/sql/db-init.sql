CREATE DATABASE quiz_multiplayer;
USE quiz_multiplayer;

CREATE TABLE questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    question_text VARCHAR(255) NOT NULL,
    option_1 VARCHAR(100) NOT NULL,
    option_2 VARCHAR(100) NOT NULL,
    option_3 VARCHAR(100) NOT NULL,
    option_4 VARCHAR(100) NOT NULL,
    correct_option TINYINT NOT NULL,
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE player (
    nickname VARCHAR(20) PRIMARY KEY,
    password_hash VARCHAR(20) NOT NULL, -- Store hashed passwords for security
    admin INT
);

CREATE TABLE scores (
    score_id INT AUTO_INCREMENT PRIMARY KEY,
    player_id VARCHAR(20),
    score INT NOT NULL,
    date_played TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES player(nickname)
);