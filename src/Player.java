import java.io.Serializable;

public class Player implements Serializable {
    private String name;
    private int playerNumber; // 1 or 2
    private int score;

    private int regretTimes = 0;

    public int getRegretTimes() {
        return regretTimes;
    }

    public void addRegretTimes() {
        regretTimes++;
    }

    public void resetRegretTimes() {
        regretTimes = 0;
    }

    public Player(String name, int playerNumber) {
        this.name = name;
        this.playerNumber = playerNumber;
        this.score = 0;
    }

    // 获取玩家姓名
    public String getName() {
        return name;
    }

    // 获取玩家编号（1 or 2）
    public int getPlayerNumber() {
        return playerNumber;
    }

    // 获取玩家分数
    public int getScore() {
        return score;
    }

    // 更新玩家分数
    public void updateScore(int points) {
        score += points;
    }

    // 重置玩家分数
    public void resetScore() {
        score = 0;
    }

    // 可以根据需要添加其他玩家相关的方法和属性
}