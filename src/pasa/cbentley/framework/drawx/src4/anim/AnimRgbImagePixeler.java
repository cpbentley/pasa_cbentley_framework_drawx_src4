package pasa.cbentley.framework.drawx.src4.anim;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.objects.function.Function;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;

/**
 * <li>Draws an image buffer.
 * 
 * <li>Copy of original whose alpha values are changed.
 * 
 * 
 * In appearing mode, buffer starts with 0 alpha and each turn some pixels are given their original alpha.
 * Could be a function.
 * Must work on a fixed copy. Is not compatible with {@link SizeMod}.
 * Compatible with {@link AnimRgbImageMove}.
 * 
 * @author Charles-Philip Bentley
 *
 */
public class AnimRgbImagePixeler extends AnimRgbImage {

   /**
    * Stores the original alpha, so it can be put back at the end of the animation
    */
   byte[]      alphaArray;

   /**
    * When all alpha are the same use this value
    */
   private int alphaSingle;

   /**
    * buffer height
    */
   int         bh;

   /**
    * Copy of {@link RgbImage} pixels.
    * Animation thread must synch on this
    */
   int[]       buffer;

   /**
    * buffer width
    */
   int         bw;

   int         incr = 0;

   byte[]      switches;

   /**
    * Turn is infinite
    * @param d
    * @param f
    */
   public AnimRgbImagePixeler(DrwCtx gc, ByteObject def, Function f) {
      super(gc, def);
      if (f != null) {
         stepFunction = f;
      }
      init();
   }

   public int getState() {
      // TODO Auto-generated method stub
      return 0;
   }

   public boolean hasRunFlag(int flag) {
      // TODO Auto-generated method stub
      return false;
   }

   private void init() {
      setAnimFlag(ANIM_02_REVERSABLE, false);
   }

   /**
    * Method called just before the animation starts.
    * <br>
    * <br>
    * Watch out in which thread the cache is generated!!
    * <br>
    * <br>
    * Getting the cache gets automatically an updated version of the Drawable.
    * <br>
    * Start method also acquires a {@link RgbImage#FLAG_06_WRITE_LOCK} on the cache {@link RgbImage}
    */
   public void lifeStart() {
      //running super
      //send worker to pool thread
      //TODO : globalize the thread start init for all animations
      // d.getDevice().processRunnable(this);
      //at each turn, we actively check if cache is invalidated (size of drawable has changed).
   }

   /**
    * Uses a counter step function.
    * <br>
    * <br>
    * We don't care. Last may still not be finished. When animation finishes, we cannot assume the Drawable will be repainted.
    * 
    * there is always the implicit repaint of drawable in normal state.
    * <br>
    * <br>
    * Animation thread may clean the buffer and send it to the Drawable so it has a ready to use
    * cache :)
    * <br>
    * that would only be for non image drawable such TableView with lots of gradients.
    * Different pixelaization.
    * <br>
    * <br>
    *  Appearance and disappearance
    * <li>RGB function up or down from original. all pixels together or randomized 
    * <li>every pixel is appearing one at a time.
    * <li>on each column, pixels appear according to a function, growing pixels.
    * <li>falling pixels. double pixels may be used and 1x1, 1x2, 2x2
    * <li>Increment: First each out of every 3, then shift 1, until
    * <br>
    * <br>
    */
   public void nextTurnSub() {
      int start = 0; //0,1
      //diminish to get to 1 where all pixels will be made opaque
      int incr = stepFunction.fxInv();

      if (incr <= 0) {
         setAnimFlag(ANIM_13_STATE_FINISHED, true);
      } else {
         if (alphaArray == null) {
            for (int i = start; i < buffer.length; i += incr) {
               int buffVal = buffer[i];
               buffer[i] = (buffVal & 0xFFFFFF) + (alphaSingle << 24);
            }
         } else {
            for (int i = start; i < buffer.length; i += incr) {
               int alphac = alphaArray[i];
               buffer[i] = (buffer[i] & 0xFFFFFF) + (alphac << 24);
            }
         }
      }
   }

   /**
    * Called in the Render Thread.
    */
   public void paint(GraphicsX g) {
      g.drawRGB(buffer, 0, bw, x, y, bw, bh, true);
   }

   /**
    * Run in Worker during start up
    */
   public void run() {
   }



   public void runStartBuffer() {
      bw = img.getWidth();
      bh = img.getHeight();

      buffer = img.getRgbCopy();
      //set all alpha to null
      switches = new byte[buffer.length];
      alphaArray = new byte[buffer.length];
      for (int i = 0; i < buffer.length; i++) {
         alphaArray[i] = (byte) ((buffer[i] >> 24) & 0xFF);
         buffer[i] = (buffer[i] & 0xFFFFFF);
      }
      incr = 10;
   }

   public void setState(int state) {
      // TODO Auto-generated method stub

   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, AnimRgbImagePixeler.class, 210);
      toStringPrivate(dc);
      super.toString(dc.sup());
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, AnimRgbImagePixeler.class, 210);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   private void toStringPrivate(Dctx dc) {

   }
   //#enddebug

}
