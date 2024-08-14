package pasa.cbentley.framework.drawx.src4.anim;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.objects.function.Function;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.utils.RgbUtils;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;

/**
 * Applies a changing alpha filter to the pixels of an image.
 * <br>
 * <br>
 * TODO Must listen to cache updates.
 * 
 * <b>Image Work</b> :
 * <li>Takes an image copy and work on it without
 * <li>Works on a shared {@link RgbImage}. Each turn it modifies the alpha value of that image.
 * <li>Updates image at each turn from source drawable.
 * <br>
 * <br>
 * Includes changes in image size change.
 * 
 * <b>Example</b> :  Animation to enter/exit a Drawable with a growing moving appearing image.
 * That animation is a mix of
 * <li> {@link AnimRgbImageMove}
 * <li> {@link SizeMod}
 * <li> {@link AnimRgbImageAlpha}
 * <br>
 * Move is easy as it only modifies and Alpha share the same instance of the Drawable's cache. Move modifies Drawable's coordinate.
 * {@link SizeMod} modifies the size.<br>
 * <br>
 * When Mixed with {@link AnimRgbImageShiftLines} though, both animations uses the cache data.{@link AnimRgbImageShiftLines} create a buffer 
 * AlphaChange modifies alpha of ShiftLines buffer that is based Drawable.
 * <br>
 * Order and caching issues are managed by {@link AnimAggregate}
 * <br>
 * <br>
 * @author Charles-Philip Bentley
 *
 */
public class AnimRgbImageAlpha extends AnimRgbImage {

   public static int[] aValues = new int[] { 0, 32, 64, 96, 128, 160, 192, 224, 255 };

   /**
    * Cache the last alpha
    */
   private int         nextAlpha;

 
   /**
    * Uses the default function
    * @param d
    * @param asc
    */
   public AnimRgbImageAlpha(DrwCtx gc, boolean asc) {
      this(gc, aValues, asc);
   }

   public AnimRgbImageAlpha(DrwCtx gc, ByteObject def) {
      super(gc, def);
   }

   /**
    * 
    * @param d
    * @param f cannot be null
    */
   public AnimRgbImageAlpha(DrwCtx gc, Function f) {
      super(gc, f);
   }

   /**
    * Alpha change for a Drawable. Will follow coordinate of Drawable
    * @param d
    * @param values
    * @param asc
    */
   public AnimRgbImageAlpha(DrwCtx gc, int[] values, boolean asc) {
      super(gc, gc.getBOC().getFunctionFactory().createFunction(values, asc));
   }

   /**
    * Work to be done when the animation is finished.
    * Set back the original alpha values?
    */
   public void lifeEnd() {
      if (img != null) {
         img.releaseLock(RgbImage.FLAG_06_WRITE_LOCK);
      }
      super.lifeEnd();
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
    * run in the Anim Thread. Returns Quickly
    */
   public void lifeStart() {
      super.lifeStart();
      //running super
      //send worker to pool thread
      //Controller.getMe().runWorkerInPool(this);
      //at each turn, we actively check if cache is invalidated (size of drawable has changed).
   }

   /**
    * Produce the next alpha value and apply it to all pixels of img.
    * <br>
    * actively checking for dimension change is feasible but it does not take into account content change
    * or style changes.
    * <br>
    * Could there be a Drawable version change between the calls
    * <li> {@link IAnimable#nextTurn()}
    * <li> {@link IAnimable#paint(GraphicsX)}
    * <br>
    * <br>
    * @see IAnimable#nextTurn()
    */
   public void nextTurnSub() {
      //check the time of the turn and create a cache for each alpha value but only when needed. that is for repetitive
      //animations that are dragged
      nextAlpha = stepFunction.fx(nextAlpha);

      //#debug
      String msg = "newalpha=" + nextAlpha + " " + ((img != null) ? img.toString1Line() : "Image is null?!");
      //#debug
      toDLog().pAnim(msg, this, AnimRgbImageAlpha.class, "nextTurn", LVL_05_FINE, true);

      //System.out.println("nextAlpha=" + nextAlpha);
      //TODO check if root img cache content has been invalidated.. in which case, Drawable image data must be reloaded
      //? use a listener? instead of active polling
      if (img != null) {
         synchronized (img) {
            RgbUtils.setAlpha(img.getRgbData(), nextAlpha);
         }
      }

   }

   /**
    * Will paint from turn 1
    * It may work on the Cache data or on a copy.
    * Animation may paint or may not if it is only worker
    */
   public void paint(GraphicsX g) {
      //#debug
      toDLog().pAnim("Turn=" + turn + " nextAlpha=" + nextAlpha, this, AnimRgbImageAlpha.class, "paint", LVL_05_FINE, true);
      //must manage transparent pixels from the drawable
      if (img != null) {
         synchronized (img) {
            img.draw(g, x, y);
         }
      }
   }

   /**
    * Overrides. Might be called in AnimThread of Workerthread.
    * The flag {@link IAnimable#ANIM_11_STATE_RUNNING} is false.
    * <br>
    * @see IAnimable#ANIM_26_HEAVY_LOADING
    */
   public void loadHeavyResourcesSub() {
      //#mdebug
      toDLog().pAnim("Loading Drawable Cache " + Thread.currentThread(), this, AnimRgbImageAlpha.class, "loadHeavyResourcesSub", LVL_05_FINE, true);
      if (hasAnimFlag(ANIM_11_STATE_RUNNING)) {
         //throw new IllegalStateException();
      }
      //#enddebug

      img.acquireLock(RgbImage.FLAG_06_WRITE_LOCK);
      img.incrementShareCount();

   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, AnimRgbImageAlpha.class, 220);
      toStringPrivate(dc);
      super.toString(dc.sup());
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, AnimRgbImageAlpha.class, 212);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   private void toStringPrivate(Dctx dc) {

   }
   //#enddebug

}
