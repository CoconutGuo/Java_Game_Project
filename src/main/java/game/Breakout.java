package game;

import game.Breakout.Paddle.Ball;
import graphics.G;
import graphics.Window;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.Timer;

public class Breakout extends Window implements ActionListener {

  public static final int H = 16, BW = 60, PW = 70, N_BRICK = 13,
      LEFT = 100, RIGHT = LEFT + N_BRICK * BW,
      TOP = 50, BOT = 700;

  public static final int GAP = 3 * H;

  public static Paddle paddle = new Paddle();

  public static Ball ball = new Ball();

  public static final int MAX_LIFE = 3;

  public static int life = MAX_LIFE, score = 0;

  public static int row_count = 1;
  public static Timer timer;

  public Breakout() {
    super("Breakout", 1000, 800);
    timer = new Timer(30, this);
    timer.start();
    startGame();
  }

  public void startGame() {
    life = 3;
    score = 0;
    row_count = 0;
    startNewRow();
  }

  public static void startNewRow() {
    row_count++;
    Bricks.ALL.clear();
    Bricks.newBrickRows(row_count);
    ball.init();
  }

  public void paintComponent(Graphics g) {
    G.clear(g);
    g.setColor(Color.BLACK);
    g.fillRect(LEFT, TOP, RIGHT - LEFT, BOT - TOP);
    paddle.show(g);
    ball.show(g);
    showText(g);
    Bricks.ALL.show(g);
  }

  private void showText(Graphics g) {
    g.setColor(Color.BLACK);
    g.drawString("Lives: " + life, LEFT + 20, 30 );
    g.drawString("Score: " + score, RIGHT - 80, 30 );
  }

  public void keyPressed(KeyEvent ke) {
    int vk = ke.getKeyCode();
    if (vk == KeyEvent.VK_LEFT) {paddle.left();}
    if (vk == KeyEvent.VK_RIGHT) {paddle.right();}
    if (ke.getKeyChar() == ' ') {
      unStick();}
    repaint();
  }

  private void unStick() {
    ball.init();
    paddle.dxS = -1;
  }

  public static void main(String[] args) {
    (PANEL = new Breakout()).launch();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    ball.move();
    repaint();
  }

  //------------- Paddle ----------------//
  public static class Paddle extends G.VS {

    public Color color = Color.YELLOW;

    public int dxS = 10;
    public static final int DX = 20;  // speed for the paddle

    public Paddle() {
      super(LEFT, BOT - H, PW, H);
    }

    public void left() {
      loc.x += -DX;
      limitX();
    }

    public void right() {
      loc.x += DX;
      limitX();

    }

    public void limitX() {
      if (dxS >= 0) {
        ball.loc.set(loc.x + dxS, BOT - 2 * H);
      }
      if (loc.x < LEFT) {
        loc.x = LEFT;
      }
      if (loc.x > RIGHT - PW) {
        loc.x = RIGHT - PW;
      }
    }

    public void show(Graphics g) {
      fill(g, color);
    }

    // --------------Ball-------------//
    public static class Ball extends G.VS {

      public Color color = Color.WHITE;
      private static final int dy_start = -5;
      public int dx = 3, dy = dy_start;

      public Ball() {
        super(LEFT, BOT - 2 * H, H, H);
        init();
      }

      public void init() {
        paddle.dxS = PW / 2 - H / 2;  // center of the paddle
        loc.set(paddle.loc.x + paddle.dxS, BOT - 2 * H);
        dy = dy_start;
        dx = 0;
      }

      public void show(Graphics g) {
        fill(g, color);
      }

      public void move() {
        if (paddle.dxS < 0) {
          loc.x += dx;
          loc.y += dy;
          wallBounce();
          Bricks.list.ballHitBrick();
        }
      }

      private void wallBounce() {
        if (loc.x < LEFT) {
          loc.x = LEFT;
          dx = -dx;
        }
        if (loc.x + H > RIGHT) {
          loc.x = RIGHT - H;
          dx = -dx;
        }

        if (loc.y < TOP) {
          loc.y = TOP;
          dy = -dy;
        }
        if (loc.y > BOT - 2 * H) {
          paddle.hitsBall();
        }
      }
    }

    private void hitsBall() {
      if (ball.loc.x < loc.x || ball.loc.x > loc.x + PW) {
        life--;
        ball.init();
      } else {  // hit the paddle
        ball.dy = - ball.dy;
        ball.dx += paddle.boost();
      }
    }

    private int boost() {
      int cp = loc.x + PW / 2;
      return (ball.loc.x + H /2 - cp) / 10;
    }
  }

  //--------- Bricks -----------//
  public static class Bricks extends G.VS {

    public static Color[] colors = {Color.red, Color.blue, Color.CYAN, Color.MAGENTA, Color.green, Color.gray};
    public static list ALL = new list();
    public Color color;

    public Bricks(int x, int y) {
      super(x, y, BW, H);
      this.color = colors[G.rnd(colors.length)];
      ALL.add(this);
    }


    private void show(Graphics g) {
      fill(g, color);
      draw(g, Color.BLACK);
    }

    public static void newBrickRows(int n) {
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < N_BRICK; j++) {
          new Bricks(LEFT + j * BW, TOP + GAP + i * H);
        }
      }
    }
    public boolean hit(int x, int y) {
      return x < loc.x + BW && (x + H) > loc.x && y > loc.y && y < (loc.y + H);
    }

    private void destroy() {
      ball.dy = -ball.dy;
      Bricks.ALL.remove(this);
      score += 17;
      if (Bricks.ALL.isEmpty()) {
        startNewRow();
      }

    }


    //---------------------List----------------//
    public static class list extends ArrayList<Bricks> {
      public void show(Graphics g) {
        for (Bricks b : this) {
          b.show(g);
        }
      }
      public static void ballHitBrick() {
        int x = ball.loc.x, y = ball.loc.y;
        for (Bricks b : ALL) {
          if (b.hit(x, y)) {
            b.destroy();
            return;
          }
        }
      }
    }
  }
}
