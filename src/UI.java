import java.util.Scanner;

public class UI {
    private GameEngine gameEngine;
    private Scanner scanner;

    private boolean isHide = false;

    public UI(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        this.scanner = new Scanner(System.in);
    }

    // 开始游戏
    public void startGame() {

        System.out.println("欢迎开始游戏！");
        while (true) {
            displayBoard();
            Player currentPlayer = gameEngine.getCurrentPlayer();
            System.out.println(currentPlayer.getName() + "，请输入落子位置（格式：行 列），或输入指令");
            if (!isHide) {
                System.out.println("save: 保存游戏");
                System.out.println("load: 读取游戏");
                System.out.println("restart: 重新开始游戏");
                System.out.println("exit: 退出游戏");
                System.out.println("regret: 悔棋");
                System.out.println("surrender: 认负");
                System.out.println("skip: 不落子");
                System.out.println("end: 双方决定不落子 结束游戏");
                System.out.println("hide: 隐藏操作提示");
            } else {
                System.out.println("输入 show 显示操作提示");
            }

            String input = scanner.nextLine();
            input = input.trim().toLowerCase();

            if (input.equals("exit")) {
                gameEngine.setLastOpIsRegret(false);
                System.out.println("游戏结束。");
                break;
            } else if (input.equals("save")) {
                gameEngine.saveGame();
                System.out.println("游戏已保存。");
                continue;
            } else if (input.equals("load")) {
                System.out.println("读取游戏。");
                gameEngine.loadGame();
                continue;
            } else if (input.equals("restart")) {
                gameEngine.setLastOpIsRegret(false);
                System.out.println("重新开始游戏。");
                gameEngine.restart();
                continue;
            } else if (input.equals("regret")) {
                if (gameEngine.getLastOpIsRegret()) {
                    System.out.println("请勿连续悔棋！");
                    continue;
                }
                System.out.println("悔棋。");
                gameEngine.regret();
                continue;
            } else if (input.equals("surrender")) {
                gameEngine.setLastOpIsRegret(false);
                System.out.println("认输。");
                gameEngine.surrender();
                continue;
            } else if (input.equals("skip")) {
                if (gameEngine.getRulesType() == 1) {
                    System.out.println("五子棋不允许不落子。");
                    continue;
                }
                System.out.println("不落子。");
                gameEngine.switchPlayer();
                continue;
            } else if (input.equals("end")) {
                gameEngine.setLastOpIsRegret(false);
                System.out.println("双方决定不落子 结束游戏。");
                gameEngine.endGame();
                continue;
            } else if (input.equals("hide")) {
                System.out.println("隐藏操作提示。");
                isHide = true;
                continue;
            } else if (input.equals("show")) {
                System.out.println("显示操作提示。");
                isHide = false;
                continue;
            }
            try {
                String[] coordinates = input.split(" ");
                int x = Integer.parseInt(coordinates[0]);
                int y = Integer.parseInt(coordinates[1]);

                boolean isValidMove = gameEngine.makeMove(x, y);

                if (!isValidMove) {
                    System.out.println("非法移动，请重新输入。");
                } else {
                    gameEngine.setLastOpIsRegret(false);
                }
            } catch (Exception e) {
                System.out.println("输入格式错误，请重新输入。");
            }
        }

        scanner.close();
    }

    // 显示当前棋盘状态
    private void displayBoard() {
        System.out.println("当前棋盘状态：");
        gameEngine.getCurrentBoard().printBoard();
    }

    public void init() {
        System.out.println("请选择游戏模式：1. 五子棋 2. 围棋");
        int mode = scanner.nextInt();
        // 创建规则管理器
        RulesManager rulesManager = new RulesManager(new GoRules());
        if (mode == 1) {
            rulesManager = new RulesManager(new GomokuRules());
        }
        System.out.println("请输入棋盘大小：（最小值8，最大值19）");
        int boardSize = scanner.nextInt();
        while (boardSize < 8 || boardSize > 19) {
            System.out.println("输入棋盘大小错误，请重新输入：");
            boardSize = scanner.nextInt();
        }
        // 创建两名玩家
        Player player1 = new Player("黑棋手", 1);
        Player player2 = new Player("白棋手", 2);

        // 创建游戏引擎和用户交互类
        gameEngine = new GameEngine(boardSize, player1, player2, rulesManager);
    }
}

// 使用示例
class GameMain {
    public static void main(String[] args) {
        UI ui = new UI(null);
        // 创建游戏引擎和用户交互类
        ui.init();
        // 开始游戏
        ui.startGame();
    }
}
