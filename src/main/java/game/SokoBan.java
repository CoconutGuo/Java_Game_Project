package game;

import graphics.G;
import graphics.Window;
import graphics.G.V;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public class SokoBan extends Window {
    public static Board board = new Board();
    public static G.V LEFT = new G.V(-1, 0), RIGHT = new G.V(1, 0);
    public static G.V UP = new G.V(0, -1), DOWN = new G.V(0, 1);

    public SokoBan() {
      super("SokoBan", 1000, 700);
      board.loadStringArray(puzz1);
    }

    public static void main(String[] args) {
    (PANEL = new SokoBan()).launch();
  }

    public void paintComponent(Graphics g) {
        G.clear(g);  // clear background
  //    g.getColor(Color.red);
  //    g.fillRect(100,100,100,100);
        board.show(g);
        if (board.done()) {
          g.setColor(Color.black);
          g.drawString("You Won!!",20,20);
        }
  }
  public void keyPressed(KeyEvent ke) {
      int vk = ke.getKeyCode();  //
      if (vk == KeyEvent.VK_LEFT) {
        board.go(LEFT);
      }
      if (vk == KeyEvent.VK_RIGHT) {
        board.go(RIGHT);
      }
      if (vk == KeyEvent.VK_UP) {
        board.go(UP);
      }
      if (vk == KeyEvent.VK_DOWN) {
        board.go(DOWN);
      }
      if (vk == KeyEvent.VK_SPACE) {
        board.clear();
        board.loadStringArray(puzz1);
      }
      repaint();
  }
  //------------------ Board ---------------------//
    public static class Board {
      public static final int N = 25;
      public static String boardStates = " WPCGgE";  // Space, Wall, Player, Container, Goal, Error
      public static final int xM = 50, yM = 50, W = 40;
      public static Color[] colors = {Color.white, Color.darkGray, Color.green, Color.orange,
                                      Color.cyan, Color.blue, Color.red};
      public char[][] b = new char[N][N];

      public G.V player = new V(0, 0);
      public G.V dest = new G.V(0, 0);

      public boolean onGoal = false;  // determined if it is moving to the right direction

      public Board() {clear();}

    /**
     * make the board all white
     */
    public void clear() {
        for (int r = 0; r < N; r++) {
          for (int c = 0; c < N; c++) {
              b[r][c]= ' ';  // the space color is white
          }
        }
        player.set(0, 0);
        onGoal = false;
      }

    /**
     * draw out all the colors for the board
     * @param g
     */
    public void show(Graphics g) {
      for (int r = 0; r < N; r++) {
        for (int c = 0; c < N; c++) {
            int ndx = boardStates.indexOf(b[r][c]);  // ndx is the index of Array b
            g.setColor(colors[ndx]);
            g.fillRect(xM + c * W, yM + r * W, W, W);  // the space color is white
        }
      }
    }

    /**
     * getter class
     * @param v
     * @return
     */
    public char ch(G.V v) {
      return b[v.y] [v.x];
    }

    /**
     * setter class
     * @param v y is the row
     * @param c x is the column
     */
    public void set(G.V v, char c) {b[v.y] [v.x] = c;}
    public void movePerson() {
      boolean res = ch(dest) == 'G';  // Goal state
      set(player, onGoal ? 'G' : ' ');  // set value for space where the player left
      set(dest, 'P');  // where the player go
      player.set(dest);  // player goes to destination
      onGoal = res;
    }

    public void go(G.V v) {
      System.out.println("player" + player.x + " " + player.y);
      dest.set(player);  // copy
      dest.add(v);  // actual destination
      System.out.println("dest" + dest.x + " " + dest.y);
      if (ch(dest) == 'W' || ch(dest) == 'E') {return;}  // Blocked
      if (ch(dest) == ' ' || ch(dest) == 'G') {movePerson(); return;}  // able to move
      if (ch(dest) == 'C' || ch(dest) == 'g') {  // if block is orange or blue able to push the box
        dest.add(v);
        if (ch(dest) != ' ' && ch(dest) != 'G') {return;}  // can't move
        set(dest, (ch(dest) == 'G') ? 'g' : 'C');
        dest.set(player);
        dest.add(v);
        set(dest, (ch(dest) == 'g') ? 'G' : ' ');  // sets it to space where the box started
        movePerson();
      }
    }

    /**
     * length is a member of the Array class
     * @param a
     */
    public void loadStringArray(String[] a) {
      for (int r = 0; r < a.length; r++) {
        String s = a[r];
        for (int c = 0; c < s.length(); c++) {  // length is a function of the String class
          char ch = s.charAt(c);  // charAt: get the char value at index c
          b[r][c] = (boardStates.indexOf(ch) > -1) ? ch : 'E';  // ch：the char object at board E: empty
          if (ch == 'P') {
            player.x = c;
            player.y = r;
            }
          }
        }
      }
      public boolean done() {
      for (int r = 0; r < N; r++) {
          for (int c = 0; c < N; c++) {
            if (b[r][c] == 'C') {
              return false;  // if the board still has container then the game is not done
            }
        }
      }
      return true;
      }
    }
    public static String[] puzz1 = {
        "  WWWWW",
        "WWW   W",
        "WGPC  W",
        "WWW CGW",
        "WGWWC W",
        "W W G WW",
        "WC g CCGW",
        "W    G  W",
        "WWWWWWWWW"
    };

    public static String[] puzz2 = {
        "WWWWWWWWWWWW",  // 12
        "WGG  W     WWW",
        "WGG  W C  C  W",
        "WGG  WCWWWW  W",
        "WGG    P WW  W",
        "WGG  W W  C WW",
        "WWWWWW WWC C W",
        "  W C  C C C W",
        "  W    W     W",
        "  WWWWWWWWWWWW"
    };
}
