# JAVA_Project

# STEPS TO RUN THE APPLICATION
# javac -cp ".;mysql-connector-j-9.4.0.jar" *.java
# java -cp ".;mysql-connector-j-9.4.0.jar" GameLauncher

<!-- 
git add .
git commit -m "anything"
git push origin main

git reset --hard HEAD
git pull
git status


DATABASE CREATION:

CREATE DATABASE DevilLevelGame;

USE DevilLevelGame;

CREATE TABLE players (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(255),
    name VARCHAR(100),
    level_cleared DECIMAL(10,0)
);

CREATE TABLE gamescores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    player_id INT,
    score INT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES players(id)
        ON UPDATE RESTRICT
        ON DELETE CASCADE
);

SHOW TABLES;
select * from players;
select * from gamescores;
-->
