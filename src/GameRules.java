import java.awt.print.Printable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// 游戏规则接口
public interface GameRules {

    boolean isValidMove(int x, int y, int[][] board);

    int isGameWon(int x, int y, int[][] board, boolean earlyStop);

    // 检查并移除无气的棋子
    void checkAndRemoveDeadStones(int x, int y, int playerNumber, int[][] board);

    int getType();
}

// 五子棋规则实现类
class GomokuRules implements GameRules, Serializable {
    private static final int TYPE = 1;
    private static final int WINNING_STONES = 5;
    private static final int EMPTY = 0;

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public boolean isValidMove(int x, int y, int[][] board) {
        // 检查是否在棋盘范围内，且该位置为空
        return x >= 0 && x < board.length && y >= 0 && y < board[0].length && board[x][y] == EMPTY;
    }

    @Override
    public int isGameWon(int x, int y, int[][] board, boolean earlyStop) {
        int currentPlayer = board[x][y];
        // 检查水平、垂直、左上到右下、右上到左下四个方向是否有五子相连
        boolean isWon = checkDirection(x, y, 1, 0, currentPlayer, board) || // 水平
                checkDirection(x, y, 0, 1, currentPlayer, board) || // 垂直
                checkDirection(x, y, 1, 1, currentPlayer, board) || // 左上到右下
                checkDirection(x, y, 1, -1, currentPlayer, board); // 右上到左下
        if (isWon) return currentPlayer;
        return -1; // 棋局尚未结束
    }

    @Override
    public void checkAndRemoveDeadStones(int x, int y, int playerNumber, int[][] board) {
        return; // 五子棋不需要检查无气的棋子
    }

    private boolean checkDirection(int x, int y, int dx, int dy, int player, int[][] board) {
        int count = 1; // 当前位置已经有一个棋子
        for (int i = 1; i < WINNING_STONES; i++) {
            int newX = x + i * dx;
            int newY = y + i * dy;
            if (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length &&
                    board[newX][newY] == player) {
                count++;
            } else {
                break;
            }
        }

        // 检查反方向
        for (int i = 1; i < WINNING_STONES; i++) {
            int newX = x - i * dx;
            int newY = y - i * dy;
            if (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length &&
                    board[newX][newY] == player) {
                count++;
            } else {
                break;
            }
        }
        return count >= WINNING_STONES;
    }
}

class GoRules implements GameRules, Serializable {
    private static final int EMPTY = 0;
    private static final int[] DIRECTION = {-1, 0, 1, 0, -1, 1, 1, -1};
    private static final int BLACK_STONE = 1;
    private static final int WHITE_STONE = 2;
    private static final int TYPE = 2;
    private int[] dx = {1, -1, 0, 0};
    private int[] dy = {0, 0, 1, -1};

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public boolean isValidMove(int x, int y, int[][] board) {
        // 检查是否在棋盘范围内，且该位置为空
        boolean isValid = x >= 0 && x < board.length && y >= 0 && y < board[0].length && board[x][y] == EMPTY;
        if (!isValid) return false;
        // 判断是否有气
        if (!hasLiberties(x, y, board)) {
            return false; // 不能在没有气的位置下子
        }
        return true; // 合法的下子位置
    }

    // 判断棋局是否已死
    public boolean isBoardDead(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == EMPTY && hasLiberties(i, j, board)) {
                    return false; // 存在空位置有气，棋局尚未死
                }
            }
        }
        return true; // 所有空位置都没有气，棋局已死
    }

    @Override
    public int isGameWon(int x, int y, int[][] board, boolean earlyStop) {
        // 检查是否有棋局死亡
        if (!isBoardDead(board) && !earlyStop) {
            return -1; // 平局
        }
        int komi = (int) (board.length * board.length / 361 * 7.5); // 贴目

        int blackScore = calculateTerritory(BLACK_STONE, board);
        int whiteScore = calculateTerritory(WHITE_STONE, board) + komi;

        if (blackScore > whiteScore) {
            return BLACK_STONE;
        } else if (blackScore < whiteScore) {
            return WHITE_STONE;
        } else {
            return 0; // 平局
        }
    }

    // 计算某一方的数子
    private int calculateTerritory(int player, int[][] board) {
        int territory = 0;
        boolean[][] visited = new boolean[board.length][board[0].length];

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (!visited[i][j] && board[i][j] == EMPTY) {
                    List<int[]> emptyPoints = new ArrayList<>();
                    boolean isEye = true;

                    calculateEmptyPoints(i, j, player, board, visited, emptyPoints);

                    for (int[] emptyPoint : emptyPoints) {
                        int emptyX = emptyPoint[0];
                        int emptyY = emptyPoint[1];

                        for (int k = 0; k < 4; k++) {
                            int newX = emptyX + dx[k];
                            int newY = emptyY + dy[k];

                            if (isValidPosition(newX, newY, board.length, board[0].length)) {
                                int neighbor = board[newX][newY];
                                if (neighbor != player && neighbor != EMPTY) {
                                    isEye = false;
                                    break;
                                }
                            }
                        }
                    }

                    if (isEye) {
                        territory += emptyPoints.size();
                    }
                }
            }
        }

        return territory;
    }

    private void calculateEmptyPoints(int x, int y, int player, int[][] board, boolean[][] visited, List<int[]> emptyPoints) {
        if (!isValidPosition(x, y, board.length, board[0].length) || visited[x][y]) {
            return;
        }

        visited[x][y] = true;

        if (board[x][y] == EMPTY) {
            emptyPoints.add(new int[]{x, y});
        } else if (board[x][y] == player) {
            for (int i = 0; i < 4; i++) {
                int newX = x + dx[i];
                int newY = y + dy[i];

                calculateEmptyPoints(newX, newY, player, board, visited, emptyPoints);
            }
        }
    }

    @Override
    // 检查并移除无气的棋子
    public void checkAndRemoveDeadStones(int x, int y, int playerNumber, int[][] board) {
        List<int[]> stonesToRemove = new ArrayList<>();

        // 在围棋规则中，可以根据实际情况调整是否需要检查双方无气的棋子
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] != playerNumber && board[i][j] != EMPTY) {
                    if (!hasLiberties(i, j, board)) {
                        // 该棋子无气，需要提子
                        stonesToRemove.add(new int[]{i, j});
                    }
                }
            }
        }

        // 移除无气的棋子
        for (int[] stone : stonesToRemove) {
            board[stone[0]][stone[1]] = EMPTY;
        }
    }

    // 判断棋子是否有气
    public boolean hasLiberties(int x, int y, int[][] board) {
        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        for (int i = 0; i < 4; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];

            if (isValidPosition(newX, newY, board.length, board[0].length) && board[newX][newY] == EMPTY) {
                return true; // 发现相邻的空位，即有气
            }
        }

        return false; // 没有找到相邻的空位，即没有气
    }

    // 判断位置是否在棋盘范围内
    private boolean isValidPosition(int x, int y, int rows, int cols) {
        return x >= 0 && x < rows && y >= 0 && y < cols;
    }

}


// 规则管理类
class RulesManager implements Serializable {
    private GameRules gameRules;

    public RulesManager(GameRules gameRules) {
        this.gameRules = gameRules;
    }

    public boolean isValidMove(int x, int y, int[][] board) {
        return gameRules.isValidMove(x, y, board);
    }

    public int isGameWon(int x, int y, int[][] board, boolean earlyStop) {
        return gameRules.isGameWon(x, y, board, earlyStop);
    }

    public void checkAndRemoveDeadStones(int x, int y, int playerNumber, int[][] board) {
        gameRules.checkAndRemoveDeadStones(x, y, playerNumber, board);
    }

    public int getType() {
        return gameRules.getType();
    }
}

