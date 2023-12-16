import java.io.*;

public class GameEngine implements Serializable {
    private Chessboard chessboard;
    private Chessboard last_blackChessboard;
    private Chessboard last_whiteChessboard;
    private RulesManager rulesManager;
    private Player currentPlayer;
    private Player blackPlayer;
    private Player whitePlayer;
    private boolean lastOpIsRegret = false;
    private boolean lastOpIsSkip = false;
    private int maxRegretTimes = 3;

    public GameEngine(int boardSize, Player blackPlayer, Player whitePlayer, RulesManager rulesManager) {
        this.chessboard = new Chessboard(boardSize);
        this.last_blackChessboard = null;
        this.last_whiteChessboard = null;
        this.rulesManager = rulesManager;
        this.blackPlayer = blackPlayer;
        this.whitePlayer = whitePlayer;
        this.currentPlayer = blackPlayer;
    }

    // 处理玩家落子
    public boolean makeMove(int x, int y) {
        int playerNumber = currentPlayer.getPlayerNumber();
        chessboard.setCurrentPlayer(playerNumber);
        boolean isValidMove = rulesManager.isValidMove(x, y, chessboard.getBoard());
        if (isValidMove) {
            if (currentPlayer.getPlayerNumber() == blackPlayer.getPlayerNumber()) {
                last_blackChessboard = chessboard.copy();
            } else {
                last_whiteChessboard = chessboard.copy();
            }
            // 落子
            chessboard.placeStone(x, y, playerNumber);
            // 检查并移除无气棋子
            rulesManager.checkAndRemoveDeadStones(x, y, playerNumber, chessboard.getBoard());
            int isWon = rulesManager.isGameWon(x, y, chessboard.getBoard(), false);
            if (isWon != -1) {
                outputResult(isWon);
                System.out.println("棋盘终局状态：");
                chessboard.printBoard();
                resetGame();
            } else {
                switchPlayer(); // 切换至下一位玩家
            }
        }
        return isValidMove;
    }

    // 切换当前玩家
    public void switchPlayer() {
        currentPlayer = (currentPlayer == blackPlayer) ? whitePlayer : blackPlayer;
    }

    // 重新开始游戏
    private void resetGame() {
        chessboard = new Chessboard(chessboard.getSize());
        currentPlayer = blackPlayer;
    }

    // 获取当前棋盘状态
    public Chessboard getCurrentBoard() {
        return chessboard;
    }

    // 保存当前游戏状态
    public void saveGame() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("chessboard.obj"))) {
            outputStream.writeObject(chessboard);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("gameEngine.obj"))) {
            outputStream.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 读取游戏状态
    public void loadGame() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("chessboard.obj"))) {
            chessboard = (Chessboard) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("gameEngine.obj"))) {
            GameEngine gameEngine = (GameEngine) inputStream.readObject();
            currentPlayer = gameEngine.getCurrentPlayer();
            blackPlayer = gameEngine.getblackPlayer();
            whitePlayer = gameEngine.getwhitePlayer();
            last_blackChessboard = gameEngine.last_blackChessboard;
            last_whiteChessboard = gameEngine.last_whiteChessboard;
            lastOpIsRegret = gameEngine.lastOpIsRegret;
            lastOpIsSkip = gameEngine.lastOpIsSkip;
            maxRegretTimes = gameEngine.maxRegretTimes;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 获取当前玩家
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    // 获取玩家1
    public Player getblackPlayer() {
        return blackPlayer;
    }

    // 获取玩家2
    public Player getwhitePlayer() {
        return whitePlayer;
    }

    public void restart() {
        resetGame();
        blackPlayer.resetScore();
        blackPlayer.resetRegretTimes();
        whitePlayer.resetRegretTimes();
        whitePlayer.resetScore();
    }

    public boolean getLastOpIsRegret() {
        return lastOpIsRegret;
    }

    public boolean setLastOpIsRegret(boolean lastOpIsRegret) {
        return this.lastOpIsRegret = lastOpIsRegret;
    }

    public void regret() {
        if (currentPlayer.getRegretTimes() > maxRegretTimes) {
            System.out.println("悔棋次数已达上限！");
            return;
        }
        //检查是否有子可悔
        if (currentPlayer.getPlayerNumber() == blackPlayer.getPlayerNumber() && last_blackChessboard == null) {
            System.out.println("无子可悔！");
            return;
        } else if (currentPlayer.getPlayerNumber() == whitePlayer.getPlayerNumber() && last_whiteChessboard == null) {
            System.out.println("无子可悔！");
            return;
        }
        //悔棋
        currentPlayer.addRegretTimes();
        lastOpIsRegret = true;
        if (currentPlayer.getPlayerNumber() == blackPlayer.getPlayerNumber()) {
            chessboard = last_blackChessboard.copy();
        } else {
            chessboard = last_whiteChessboard.copy();
        }
    }

    public void surrender() {
        if (currentPlayer == blackPlayer) System.out.println("白方获胜！");
        else System.out.println("黑方获胜！");
        restart();
    }

    public int getRulesType() {
        return rulesManager.getType();
    }

    public void endGame() {
        //双方决定不落子，判断胜负，结束游戏
        int isWon = rulesManager.isGameWon(0, 0, chessboard.getBoard(), true);
        if (isWon != -1) {
            outputResult(isWon);
        } else {
            System.out.println("双方决定不落子，平局！");
        }
        restart();
    }

    private void outputResult(int isWon) {
        if (isWon == 0) {
            System.out.println("平局！");
        } else if (isWon == 1) {
            System.out.println("黑方获胜！");
            blackPlayer.updateScore(1); // 玩家获胜，更新分数
        } else {
            System.out.println("白方获胜！");
            whitePlayer.updateScore(1); // 玩家获胜，更新分数
        }
    }
}

