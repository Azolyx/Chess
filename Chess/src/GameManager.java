public class GameManager {
    public boolean running = false;
    Thread thread;

    Board board = new Board();

    public final static int fps = 60;
    public long deltaTime = 0;
    public long frameStart = System.currentTimeMillis();

    public void init(WindowManager windowManager) {
        thread = new Thread(() -> start(windowManager));
        thread.start();
    }

    public void start(WindowManager windowManager) {
        running = true;
        windowManager.init("Chess", 500, 500);

        board.setBoard();

        while (running) {
            long waitTime = (long) ((1d / fps * 1000) - deltaTime);
            if (waitTime < 0) { waitTime = 0; }
            frameStart = System.currentTimeMillis();

            tick(windowManager);

            deltaTime = System.currentTimeMillis() - frameStart;
            try { Thread.sleep(waitTime); }
            catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    public void stop() {
        running = false;
        thread.interrupt();
    }

    public void tick(WindowManager windowManager) {
        board.tick(windowManager);
    }
}
