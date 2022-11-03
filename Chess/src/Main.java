public class Main {
    WindowManager window = new WindowManager();
    GameManager game = new GameManager();

    public Main() {
        game.init(window);
    }

    public static void main(String[] args) {
        Main main = new Main();
    }
}
