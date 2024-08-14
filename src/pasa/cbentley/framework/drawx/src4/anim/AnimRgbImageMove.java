package pasa.cbentley.framework.drawx.src4.anim;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.objects.move.FunctionMove;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;
import pasa.cbentley.framework.drawx.src4.string.CharOpt;
import pasa.cbentley.framework.drawx.src4.utils.RgbImageRotateUtils;

/**
 * Move animation on a 2D plane. {@link FunctionMove} decides the path.
 * <br>
 * <br>
 * Function could use a retrace like a ball rebounding
 * <br>
 * <br>
 * If the whole screen is repainted, no micro optimizations are made on bounds.
 * <br>
 * <br>
 * <b>Implementation</b>:
 * <br>
 * <br>
 * <br>
 * <br>
 * <b>Use Cases </b>
 * <ol>
 * <li>Move a small object across the screen on the top DLayer of {@link MasterCanvas}.
 * <li>Appearance or Transition: Move main {@link Drawable} to his defined position from a screen corner
 * <li>Moves content of a {@link ViewPane} when the user activate the scrollbar <br>
 * <ol>
 * <li>asks to draw ViewPort content on main Image<br>
 * <li>ask to draw ViewPort addition<br>
 * <li>adds a trail<br>
 * </ol>
 * </ol>
 * 
 * Tracks trail increments in memory to deal with further moves<br>
 * <br>
 * <br>
 * @author Charles-Philip Bentley
 *
 */
public class AnimRgbImageMove extends AnimRgbImage {

   /**
    * if 25 pixels separate origin from dest
    * 8 + 5 + 3 + 2 + 1 + 1= 20
    * 25 - 8 = 17;
    * 17 - 5 = 12
    * 12 - 5 = 7
    * 7 - 3 = 4;
    * 4 - 2 = 2;
    * 1 - 1 =1;
    * 1 - 1 = 0;
    */
   public static int[]     fib                    = new int[] { 1, 1, 2, 3, 5, 8, 13, 21 };

   public static final int MOVE_FLAG_1_USE_CLIP   = 1;

   /**
    * In the case of Drawable, will it inherit the destination coordinate?
    * once the animation is finished or cut short
    */
   public static final int MOVE_FLAG_2_EFFECTIVE  = 1 << 1;

   public static final int MOVE_FLAG_3_DEBUG_TIME = 1 << 2;

   public static final int TRAIL_0_UP             = 0;

   public static final int TRAIL_1_DOWN           = 1;

   public static final int TRAIL_2_LEFT           = 2;

   public static final int TRAIL_3_RIGHT          = 3;

   private int[]           boundsRepaint          = new int[5];

   private int             cliph;

   private int             clipw;

   private int             clipx;

   private int             clipy;

   private int             flagsMove;

   private int             olddiff;

   /**
    * <li> {@link AnimRgbImageMove#TRAIL_0_UP} Default
    * <li> {@link AnimRgbImageMove#TRAIL_1_DOWN}
    * <li> {@link AnimRgbImageMove#TRAIL_2_LEFT}
    * <li> {@link AnimRgbImageMove#TRAIL_3_RIGHT}
    */
   private int             tblrTrail;

   public void setTrail(int trail) {
      tblrTrail = trail;
   }

   private long       time;

   /**
    * Draws those images next to each other starting from the main image
    * TrailDir defines the direction of the trail.
    * All images in the trail
    */
   private RgbImage[] trail;

   /**
    * Move of a Drawable 
    * @param d
    * @param f
    */
   public AnimRgbImageMove(DrwCtx gc, FunctionMove f) {
      super(gc, f);
   }

   public AnimRgbImageMove(DrwCtx gc, ByteObject def) {
      super(gc, def);
   }

   public void addTrail(RgbImage trailDrawable) {
      if (trail == null) {
         trail = new RgbImage[1];
         trail[0] = trailDrawable;
      } else {
         int index = trail.length;
         trail = RgbImageRotateUtils.ensureCapacity(trail, trail.length);
         trail[index] = trailDrawable;
      }
   }

   protected void doDebugTime(GraphicsX g, int x, int y) {
      if (hasFlagMove(MOVE_FLAG_3_DEBUG_TIME)) {
         long cur = System.currentTimeMillis();
         int diff = (int) (cur - time);
         if (Math.abs(olddiff - diff) <= 1) {
            diff = olddiff;
         }
         olddiff = diff;
         CharOpt.draw(g, diff, x, y, g.getRefFont(), 0, -1);
         time = cur;
      }
   }

   public int[] getBounds() {
      return boundsRepaint;
   }

   public boolean hasFlagMove(int flag) {
      return BitUtils.hasFlag(flagsMove, flag);
   }

   /**
    * If move is effective, automatically sets the {@link IDrawable} coordinates.
    */
   public void lifeEnd() {
      FunctionMove mf = (FunctionMove) stepFunction;
      mf.finish();
      super.lifeEnd();
   }

   public void lifeStart() {
      setAnimFlag(ANIM_24_OVERRIDE_DRAW, true);
      setAnimFlag(ANIM_02_REVERSABLE, true);
   }

   public int nextTurn() {


      //      MoveFunction mf = ((MoveFunction) stepFunction);
      //      mf.fx();
      //      int x = mf.getX();
      //      int y = mf.getY();
      //      d.setXY(x, y);
      return 0;
   }

   /**
    * Paint the Img or the Drawable
    * 
    * @param g {@link GraphicsX}
    */
   public void paint(GraphicsX g) {
      FunctionMove mf = ((FunctionMove) stepFunction);
      mf.fx();
      int x = mf.getX();
      int y = mf.getY();
      //System.out.println("Drawing Move at " + x + ":" + y + " " + mf.toStringDebug());
      if (hasFlagMove(MOVE_FLAG_1_USE_CLIP)) {
         g.clipSet(clipx, clipy, clipw, cliph);
      }
      if (hasFlagMove(MOVE_FLAG_1_USE_CLIP)) {
         g.clipReset();
      }
      doDebugTime(g, x, y);
   }

   protected void paintTrail(GraphicsX g, int x, int y) {
      int dx = x;
      int dy = y;
      int uh = 0;
      int uw = 0;
      if (trail != null) {
         for (int i = 0; i < trail.length; i++) {
            switch (tblrTrail) {
               case TRAIL_0_UP:
                  dy -= uh;
                  break;
               case TRAIL_1_DOWN:
                  dy += uh;
                  break;
               case TRAIL_2_LEFT:
                  dx -= uw;
                  break;
               case TRAIL_3_RIGHT:
                  dx += uw;
                  break;
            }
         }
      }
   }



   /**
    * the Base class {@link DrawableAnim} will call resetCounter on the MoveFunction
    * {@link FunctionMove#resetCounter()}
    */
   public void reset() {
   }

   /**
    * Sets a specific clip when drawing the move animation
    * @param x
    * @param y
    * @param w
    * @param h
    */
   public void setClip(int x, int y, int w, int h) {
      setFlagMove(MOVE_FLAG_1_USE_CLIP, true);
      clipx = x;
      clipy = y;
      clipw = w;
      cliph = h;
   }

   public void setFlagMove(int flag, boolean v) {
      flagsMove = BitUtils.setFlag(flagsMove, flag, v);
   }

   /**
    * Some classes may instanciate a Move animation and run it. They will keep a reference and reuse it to dynamically set
    * a new destination.
    * <br>
    * <br>
    * Method asks the {@link FunctionMove} to move x,y in distance
    * 2 cases. 
    * <br>
    * <li>the call is made when the move is ongoing => A move is compute from the current position to the new offset destination.<br>
    * <li>the call is made when the move is finished => #2 New origin, new destination. 
    * <br>
    * <br>
    * @param xoffset
    * @param yoffset
    * 
    */
   public void setNewDestOffset(int xoffset, int yoffset) {
      FunctionMove mf = (FunctionMove) stepFunction;
      int dx = xoffset - mf.getDestX();
      int dy = yoffset - mf.getDestY();
      mf.shiftDest(dx, dy);
   }

   public void setShiftDestOffset(int dx, int dy) {
      FunctionMove mf = (FunctionMove) stepFunction;
      mf.shiftDest(dx, dy);
   }

   
   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, AnimRgbImageMove.class, 325);
      toStringPrivate(dc);
      super.toString(dc.sup());
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, AnimRgbImageMove.class, 325);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
      
      FunctionMove mf = (FunctionMove) stepFunction;
      dc.nlLvl(mf);
      boolean isUseClip = (hasFlagMove(MOVE_FLAG_1_USE_CLIP));
      dc.append("isClipping=" + isUseClip + " clip=" + clipx + "," + clipy + "-" + clipw + "," + cliph);
 
   }

   private void toStringPrivate(Dctx dc) {
      
   }
   //#enddebug

}
