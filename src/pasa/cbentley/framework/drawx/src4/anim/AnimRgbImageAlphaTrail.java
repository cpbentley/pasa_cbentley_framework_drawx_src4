package pasa.cbentley.framework.drawx.src4.anim;

import java.util.Vector;

import pasa.cbentley.byteobjects.src4.objects.function.Function;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;

/**
 * Draws an alpha trail to a moving {@link Drawable}
 * <br>
 * <br>
 * Composite animation of {@link AnimRgbImageMove} and {@link AnimRgbImageAlpha}.
 * <br>
 * <br>
 * At each Move frames, a new {@link AnimRgbImageAlpha} animation is created.
 * <br>
 * <br>
 * {@link AnimRgbImageAlphaTrail} precomputes the alpha frames and all {@link AnimRgbImageAlpha} simply draws<br>
 * If not enough memory is available, {@link AnimRgbImageAlpha} will compute on its own image copy<br>
 * <br>
 * <br>
 * <b>Implementation</b>:
 * <br>
 * <li>Host Drawable draws normally its full state
 * <li>creates a new DLayer with a Drawable hosting each fading image.
 * <br>
 * {@link AnimRgbImageAlphaTrail} controls the removal from {@link MasterCanvas}
 * <br>
 * @author Charles-Philip Bentley
 *
 */
public class AnimRgbImageAlphaTrail extends AnimRgbImage {

   private Vector   af = new Vector();

   private Function alphaFunction;

   private RgbImage img;

   private AnimRgbImageMove     move;

   public AnimRgbImageAlphaTrail(DrwCtx gc, Function f) {
      super(gc, f);
   }

   public void addXY(int x, int y) {

   }

   public int nextTurn() {
      move.nextTurn();
      //when data is shared, no need to use the AlphaChangeRgb class
      //TODO just keep everything in this class 
      //give shared move so anim Draws an index in the image cache
      //AlphaChangeRgb af = new AlphaChangeRgb(img, move.x, move.y, alphaFunction);
      //this.af.addElement(af);
      for (int i = 0; i < this.af.size(); i++) {
         AnimRgbImageAlpha afa = (AnimRgbImageAlpha) this.af.elementAt(i);
         afa.nextTurn();
      }
      for (int i = this.af.size() - 1; i >= 0; i--) {
         AnimRgbImageAlpha afa = (AnimRgbImageAlpha) this.af.elementAt(i);
         if (afa.hasAnimFlag(ANIM_13_STATE_FINISHED)) {
            this.af.removeElement(afa);
         }
      }
      if (move.isFinished() && this.af.size() == 0) {
         return -1;
      }
      return 0;
   }

   public void paint(GraphicsX g) {
      move.paint(g);
      for (int i = 0; i < this.af.size(); i++) {
         AnimRgbImageAlpha afa = (AnimRgbImageAlpha) this.af.elementAt(i);
         afa.paint(g);
      }
   }
   
   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, "AlphaTrail");
      toStringPrivate(dc);
      super.toString(dc.sup());
   }

   private void toStringPrivate(Dctx dc) {
      
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, "AlphaTrail");
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   //#enddebug
   


}
