CREATE DATABASE quiz_multiplayer;
USE quiz_multiplayer;

CREATE TABLE QUESTION (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    QUESTION_TEXT VARCHAR(255) NOT NULL,
    CORRECT_OPTION VARCHAR(100) NOT NULL,
    OTHER_OPTION_1 VARCHAR(100) NOT NULL,
    OTHER_OPTION_2 VARCHAR(100) NOT NULL,
    OTHER_OPTION_3 VARCHAR(100) NOT NULL
);

CREATE TABLE PLAYER (
    NICKNAME VARCHAR(20) PRIMARY KEY,
    PASSWORD_HASH VARCHAR(255) NOT NULL,
    IS_ADMIN BOOLEAN NOT NULL,
    TOTAL_SCORE INT DEFAULT 0
);

CREATE TABLE GAME_MATCH (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    NICKNAME VARCHAR(20) NOT NULL,
    SCORE INT NOT NULL,
    DATE_PLAYED TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (NICKNAME) REFERENCES PLAYER(NICKNAME)
);

INSERT INTO PLAYER (NICKNAME, PASSWORD_HASH, IS_ADMIN, TOTAL_SCORE) VALUES
("Marta", "$2a$10$cZt8jNSCTDRLStsVWX86vuuYnSQTktqWy9NmtytD3d6/hLzJUdjGW", 1, 29),
("Paolo", "$2a$10$BwovJklOxaloflhAqPSVPuK8Qz9nPEMwmOYYyhvccp9xNvpe3TaJG", 0, 30);

INSERT INTO QUESTION (QUESTION_TEXT, CORRECT_OPTION, OTHER_OPTION_1, OTHER_OPTION_2, OTHER_OPTION_3) VALUES
('Chi è il fondatore della casa di Grifondoro?', 'Godric Grifondoro', 'Severus Piton', 'Salazar Serpeverde', 'Helga Tassorosso'),
('Qual è il nome del gioco che si gioca su scope nel mondo di Harry Potter?', 'Quidditch', 'Quodpot', 'Scacchi magici', 'Calcio volanti'),
('Cosa regala Hagrid a Harry per il suo undicesimo compleanno?', 'Un gufo', 'Una scopa', 'Un libro', 'Un cane'),
('Qual è il vero nome di Lord Voldemort?', 'Tom Riddle', 'Salazar Serpeverde', 'Sirius Black', 'Albus Silente'),
('Qual è il nome della scuola frequentata da Harry Potter?', 'Hogwarts', 'Beauxbatons', 'Durmstrang', 'Ilvermorny'),
('Qual è l''animale che rappresenta la casa di Corvonero?', 'Un''aquila', 'Un leone', 'Un cervo', 'Un cane'),
('Qual è il nome del negozio di bacchette magiche di Diagon Alley?', 'Olivanders', 'Fred & George', 'Weasleys'' Wizard Wheezes', 'La bottega delle pozioni'),
('Chi ha ucciso Sirius Black?', 'Bellatrix Lestrange', 'Lucius Malfoy', 'Severus Piton', 'Nagini'),
('Qual è il nome dell''incantesimo che fa levitare gli oggetti?', 'Wingardium Leviosa', 'Lumos', 'Accio', 'Petrificus Totalus'),
('Chi è il professor di Difesa contro le Arti Oscure nel quinto anno di Harry?', 'Dolores Umbridge', 'Gilderoy Lockhart', 'Severus Piton', 'Remus Lupin');

INSERT INTO GAME_MATCH (NICKNAME, SCORE, DATE_PLAYED) VALUES
('Paolo',8,'2024-11-11 12:13:50'),
('Marta',6,'2024-11-11 13:41:15'),
('Marta',5,'2024-11-11 11:00:28'),
('Paolo',7,'2024-11-12 09:33:20'),
('Paolo',4,'2024-11-12 12:12:11'),
('Paolo',10,'2024-11-12 22:29:20'),
('Marta',9,'2024-11-13 12:22:54'),
('Marta',9,'2024-11-13 17:54:23'),
('Paolo',6,'2024-11-13 23:58:12');