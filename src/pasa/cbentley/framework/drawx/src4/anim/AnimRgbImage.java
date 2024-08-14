package pasa.cbentley.framework.drawx.src4.anim;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.objects.anim.AnimAbstract;
import pasa.cbentley.byteobjects.src4.objects.function.Function;
import pasa.cbentley.core.src4.event.BusEvent;
import pasa.cbentley.core.src4.event.IEventConsumer;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;

/**
 * Animations that work on a {@link RgbImage} cache instance of a {@link IDrawable}.
 * <br>
 * <br>
 * Based on 
 * <li> {@link RgbImage}
 * <li> {@link IDrawable}
 * <li> {@link ByteObject} figure.
 * <br>
 * <br>
 * <b>Entry Animation</b>  
 * <br>
 * start: keep the animHide behavior flag true<br>
 * end: set the animHide behavior flag to false<br>
 * <br>
 * <b>Main Animation</b>
 * <br>
 * start: set the animHide behavior flag to true<br>
 * end: set the animHide behavior flag to false<br>
 * <br>
 * <b>Exit Animation</b>
 * <br>
 * start = set the animHide behavior flag to true<br>
 * end = -
 * <br>
 * <br>
 * When given an {@link IDrawable} in constructor,
 * <br>
 * <br>
 * Since the {@link RgbImage} may be an indirection from the real source of data, class must listen to cache updates.
 * <li>Takes an image copy and work on it without any updates.
 * <li>Works on a shared {@link RgbImage}. Each turn it modifies the alpha value of that image.
 * <li>Updates image at each turn from source drawable if source drawable has had his state changed
 * <br>
 * <br>
 * <br>
 * @author Charles-Philip Bentley
 * @see DrawableAnim
 * @see RgbDrawable
 */
public abstract class AnimRgbImage extends AnimAbstract implements IEventConsumer {

   //   public static RgbDrawable getRgbImage(ByteObject fig, int x, int y, int w, int h) {
   //      RgbImage img = DrwParamFig.getFigImage(fig, w, h);
   //      RgbDrawable rd = new RgbDrawable(img, x, y);
   //      return rd;
   //   }

   protected int          x;

   protected int          y;

   /**
    * Stores the original alpha, so it can be put back at the end of the animation
    */
   protected byte[]       alphaArray;

   /**
    * buffer height
    */
   protected int          bh;

   /**
    * buffer width
    */
   protected int          bw;

   /**
    * Animation may work on different {@link IDrawable}'s cache levels.
    * 
    * When flag {@link IAnimable#ANIM_08_CACHE_WORKING} is not set
    * 
    * <li> {@link ITechDrawable#IMAGE_0_ALL}
    * <li> {@link ITechDrawable#IMAGE_1_CONTENT}
    * <li> {@link ITechDrawable#IMAGE_2_BG}
    * <li> {@link ITechDrawable#IMAGE_3_FG}
    * <li> {@link ITechDrawable#IMAGE_4_CONTENT_BG}
    * <li> {@link ITechDrawable#IMAGE_5_CONTENT_FG}
    * <br>
    * <br>
    * When flag is set
    * <li> {@link ITechDrawable#CACHE_1_CONTENT}
    * <li> {@link ITechDrawable#CACHE_2_FULL}
    * <li> {@link ITechDrawable#CACHE_3_BG_DECO}
    * 
    */
   protected int          imageType;

   /**
    * Updated with {@link IDrawable#getRgbImage(int)}
    * <li> {@link ITechDrawable#IMAGE_0_ALL}
    * <br>
    * Image may have to be initialized on demand when object is an {@link IDrawable}.
    * <br>
    * Where image type is
    * <br>
    * Become an IRgbImageListener
    * <br>
    * <br>
    * May be null when only {@link IDrawable} is used.
    */
   protected RgbImage     img;

   /**
    * Copy of {@link RgbImage} pixels.
    */
   protected int[]        workBuffer;

   protected final DrwCtx drc;

   /**
    * Creates an image from a figure
    * @param fig
    * @param w
    * @param h
    * @param f
    */
   //   public ImgAnimable(ByteObject fig, int x, int y, int w, int h, Function f) {
   //      super(getRgbImage(fig, x, y, w, h), f);
   //      this.img = d.getRgbImage(IDrawable.IMAGE_0_ALL);
   //   }

   public AnimRgbImage(DrwCtx drc, ByteObject def) {
      super(drc.getBOC(), def);
      this.drc = drc;
   }

   /**
    * Take {@link IDrawable} as an Image and animate pixels. 
    * <br>
    * <br>
    * When the Drawable has not been initialized, image cannot be created
    * @param d
    * @param f
    */
   public AnimRgbImage(DrwCtx drc, Function f) {
      super(drc.getBOC(), f);
      this.drc = drc;
   }

   /**
    * Image change
    * <br>
    * Is img a copy or a reference to the original cache?
    * <br>
    * When Drawable is not cached, animation runs, Drawable is invalidated but animation will not recieve event
    * {@link AnimRgbImage} must recieve changes of state {@link ITechDrawable#STATE_07_CACHE_INVALIDATED}.
    * or
    * Drawable must check if a cache is being used and update it, then send event through listeners.
    * <br>
    * If it takes too much time, animation must race as soon as it gets an event that invalidate data
    * 
    * <li>Animation does its thing
    * <li>Drawable get event and ask for repaint
    * <li>In draw method, cache is updated, event cache update to RgbImage. This is the new root data
    * <li>RgbImage notifies animation listener.
    * <li>Animation update reference to img, and if necessary to buffers it uses to draw itself. Animation.
    * <li>If cache update costs more than 100ms, animation is raced/ended. otherwise, speed of animation is increased
    * <br>
    * <br>
    */
   public void consumeEvent(BusEvent e) {
      if (img != null && e.getProducer() == img) {
         //update content
      }
   }

   public boolean hasTrans() {
      if (img != null) {
         return !img.hasFlag(RgbImage.FLAG_05_IGNORE_ALPHA);
      }
      return false;
   }

   public void lifeEnd() {
      super.lifeEnd();
   }

   /**
    * Start method loads an RGB version of the {@link IDrawable} asynchronously.
    * <br>
    * <br>
    * Animator thread checks back or will be notified when work has finished.
    * <br>
    * <br>
    * If start work take more than the time limit threshold, the animation is canceled and start worker thread
    * is interrupted.
    * <br>
    * Usually it must take less than 100 ms. otherwise, it is interrupted and canceled.
    * <br>
    */
   public void lifeStart() {
      super.lifeStart();
   }

   public void setImageType(int type) {
      imageType = type;
   }

   //#mdebug
   public void toString(Dctx dc) {
      dc.root(this, AnimRgbImage.class, 207);
      toStringPrivate(dc);
      super.toString(dc.sup());
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, AnimRgbImage.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   private void toStringPrivate(Dctx dc) {

   }
   //#enddebug

}
