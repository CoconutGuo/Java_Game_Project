package graphics;

import javax.xml.crypto.dsig.Transform;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class G {
    public static Random RANDOM = new Random();
    public static int rnd(int max){ //gives a random number from 0-max argument. returns an int
        return RANDOM.nextInt(max);
    }
    public static G.V LEFT = new G.V(-1,0), RIGHT = new G.V(1, 0);
    public static G.V UP = new G.V(0,-1), DOWN = new G.V(0, 1);
    public static Color rndColor(){
        return new Color(rnd(256),rnd(256), rnd(256));
    }
    public static VS backRect = new VS(0,0,5000,5000); //background rect to help clear the background


    public static void clear(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0,0,5000,5000);
    }

    //-------------------V-------------------// (vector)
    public static class V implements Serializable {
        public static Transform T = new Transform();
        public int x, y;
        public V(){} // lets build something that we can change later
        public V(int x, int y){set(x, y);} // lets build something with mouse coordinates (x,y)
        public V(V v){set(v.x, v.y);} // lets make a copy of an already existing one

        public void set(int x, int y) {this.x = x;this.y=y;}
        public void set(V v) {x = v.x;y=v.y;}

        public void add(V v){x += v.x; y += v.y;}
        public void setT(V v){set(v.tx(), v.ty());}
        public int tx(){return x *T.n / T.d + T.dx;}
        public int ty(){return y *T.n / T.d + T.dy;}

        public void blend (V v, int k) {set((k * x + v.x)/(k+1), (k*y + v.y)/(k+1));}
    }

    //--------Transform--------//
    public static class Transform{
        public int dx, dy, n, d;
        public void setScale(int oW, int oH, int nW, int nH){
            n = (nW >nH)? nW: nH;
            d = (oW >oH)? oW :oH;
        }
//        public void set(VS oVS, VS nVS){
//            setScale(oVS.size.x, oVS.size.y,nVS.size.x, nVS.size.y);
//            dx = of;
//            dy - offSet
//        }
//        public void set(BBox ob, VS nVS){
//            setScale(oB.h);
//        }
    }

    //-------------------VS-------------------// (vector / size) //need this for rectangle
    public static class VS {
        public V loc = new V(), size = new V(); // there is a vector to denote location and size
        public VS(int x, int y, int w, int h){loc.set(x,y);size.set(w,h);}
        public void fill(Graphics g, Color c){
            g.setColor(c);
            g.fillRect(loc.x, loc.y, size.x, size.y);
        }
        public void draw(Graphics g, Color c){
            g.setColor(c);
            g.drawRect(loc.x, loc.y, size.x, size.y);
        }
        public boolean hit(int x, int y) {
            return (x > loc.x && y > loc.y && x < loc.x + size.x && y < loc.y + size.y);
        }
    }

    //-------------------BUTTON-------------------//
    public static abstract class Button {
        // why abstract?
        //      This is responsible for drawing the button, detecting the mouse, visual feedback...
        //      but it doesn't have any idea what is supposed to happen when you press the button...
        //      something else determines the action

        public abstract void act();
        public boolean enabled = true, bordered = true;
        public String text = "";
        public VS vs = new VS(0,0,0,0);
        public LookAndFeel lnf = new LookAndFeel();

        public Button(Button.List list, String str){
            if (list != null) {list.add(this);}
            text = str;
        }

        // what should a button do???
        public void show(Graphics g) {
            if (vs.size.x == 0) { setSize(g);} // <-- text in the button demands resizing
            vs.fill(g, lnf.back);
            if (bordered) {vs.draw(g, lnf.border);}
            g.setColor(enabled? lnf.enabled : lnf.disabled);
            g.drawString(text, vs.loc.x + lnf.m.x, vs.loc.y + lnf.dyText);
        }

        public void setSize(Graphics g) {
            FontMetrics fm = g.getFontMetrics(); // fm object which fetches stuff
            vs.size.set(2 * lnf.m.x + fm.stringWidth(text), 2 * lnf.m.y + fm.getHeight());
            lnf.dyText = fm.getAscent() + lnf.m.y; // how far the text is from the baseline
        }

        public void set(int x, int y){ vs.loc.set(x, y); }
        public boolean hit(int x, int y) { return vs.hit(x, y); }
        public void click() {if (enabled) {act();}}


        //-------------------LOOK AND FEEL-------------------//
        public static class LookAndFeel {
            public Color back = Color.WHITE;
            public Color border = Color.BLUE;
            public Color enabled = Color.BLACK;
            public Color disabled = Color.GRAY;
            public V m = new V(5, 3);
            public int dyText = 0;
        }

        //-------------------List-------------------//
        public static class List extends ArrayList<Button> {
            public Button hit(int x, int y) {
                for(Button b: this) {
                    if (b.hit(x, y)) {return b;}
                } return null;
            }

            public boolean clicked(int x, int y) {
                // return true if button was clicked
                Button b = hit(x, y);
                if(b == null) { return false;}
                b.click();
                return true;
            }

            public void show(Graphics g) { for(Button b: this) { b.show(g); }}
        }


    }




}









