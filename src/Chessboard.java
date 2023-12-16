import java.io.Serializable;

public class Chessboard implements Serializable {
    private int[][] board;
    private int size; // 棋盘大小

    private int currentPlayer = 1; // 当前玩家编号

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Chessboard(int size) {
        this.size = size;
        this.board = new int[size][size];
        // 初始化棋盘，0 表示空格
        initializeBoard();
    }

    // 初始化棋盘
    private void initializeBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = 0;
            }
        }
    }

    // 落子操作
    public void placeStone(int x, int y, int playerNumber) {
        board[x][y] = playerNumber;
    }

    // 获取棋盘状态
    public int[][] getBoard() {
        return board;
    }

    // 保存局面
    public int[][] saveBoardState() {
        int[][] savedBoard = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(board[i], 0, savedBoard[i], 0, size);
        }
        return savedBoard;
    }

    // 读取局面
    public void loadBoardState(int[][] savedBoard) {
        if (savedBoard.length == size && savedBoard[0].length == size) {
            for (int i = 0; i < size; i++) {
                System.arraycopy(savedBoard[i], 0, board[i], 0, size);
            }
        } else {
            // 处理尺寸不匹配的情况
            System.out.println("Saved board size does not match the current board size.");
        }
    }

    // 打印棋盘状态
    public void printBoard() {
        for (int[] row : board) {
            for (int cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    // 获取棋盘大小
    public int getSize() {
        return size;
    }

    //复制棋盘
    public Chessboard copy() {
        Chessboard copy = new Chessboard(size);
        copy.loadBoardState(board);
        return copy;
    }
}

