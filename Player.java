public class Player {
    private final int id;
    private final String username;
    private final String name;
    private int levelCleared; // new field

    public Player(int id, String username, String name, int levelCleared) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.levelCleared = levelCleared;
    }

    // getters and setters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public int getLevelCleared() { return levelCleared; }
    public void setLevelCleared(int levelCleared) { this.levelCleared = levelCleared; }
}
