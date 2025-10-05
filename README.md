# JAVA_Project

# STEPS TO RUN THE APPLICATION
# javac -cp ".;mysql-connector-j-9.4.0.jar" *.java
# java -cp ".;mysql-connector-j-9.4.0.jar" GameLauncher

<!-- 
git add .
git commit -m "anything"
git push origin main

git reset --hard HEAD

git status


DATABASE CREATION
CREATE DATABASE DevilLevelGame;
USE DevilLevelGame;
CREATE TABLE Players (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL
);
CREATE TABLE GameScores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    player_id INT,
    score INT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES Players(id) ON DELETE CASCADE
);

SHOW TABLES;
INSERT INTO Players (username, password) VALUES ('testplayer', '1234');
INSERT INTO GameScores (player_id, level, score) VALUES (1, 2, 500);

select * from players;
-->