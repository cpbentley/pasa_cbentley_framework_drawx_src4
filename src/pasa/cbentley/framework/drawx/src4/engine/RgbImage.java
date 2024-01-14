/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.engine;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.objects.color.BlendOp;
import pasa.cbentley.core.src4.ctx.IToStringFlags;
import pasa.cbentley.core.src4.ctx.ToStringStaticUc;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.IDLog;
import pasa.cbentley.core.src4.logging.IStringable;
import pasa.cbentley.core.src4.logging.ITechLvl;
import pasa.cbentley.core.src4.structs.IntToStrings;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.core.src4.utils.RgbUtils;
import pasa.cbentley.core.src4.utils.TransformUtils;
import pasa.cbentley.framework.coredraw.src4.interfaces.IGraphics;
import pasa.cbentley.framework.coredraw.src4.interfaces.IImage;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechGraphics;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IFlagsToStringDrw;
import pasa.cbentley.framework.drawx.src4.ctx.ToStringStaticDrawx;
import pasa.cbentley.framework.drawx.src4.interfaces.IRgbLoader;
import pasa.cbentley.framework.drawx.src4.tech.ITechGraphicsX;
import pasa.cbentley.framework.drawx.src4.tech.ITechRgbImage;

/**
 * Framework {@link IImage} encapuslation class. It provides services:<br>
 * <li>acts as a weak reference for image data 
 * <li>automatically reload its source data when needed
 * <li>creates regions linked to their parent
 * <li>encapsulate an image transformation
 * <li>read and write locks
 * <li>performance optimization with IgnoreAlpha, Virgin, BackgroundColor RGB 
 * <br>
 * <br>
 * <b>Image Sharing</b>: <br>
 * What happens when several piece of code want to work on the same base image and share between themsevles their computation?
 * <br>
 * <br>
 * Uses {@link RgbCache} for managing memory issues. Most of the time, the developer will not interact with {@link RgbCache} class.
 * <br>
 * <br>
 * <b>Duty of the programmer</b> :
 * <li>It is the responsability of the programmer to release RgbImage from memory with the dispose method
 * <br>
 * Immutable image will throw an {@link IllegalStateException} for method trying to modify its content.
 * RgbImage uses an Immutable image for that purpose.
 * <br>
 * <br>
 * A {@link RgbImage} may have its own caching policy.?? However it will never be stronger than the RgbCache's policy.
 * So if {@link RgbCache#TYPE_NONE}, all {@link RgbImage} will not be cached.
 * <br>
 * <br>
 * See {@link RgbImage#acquireLock(int)} for detailed discussion about image sharing and locks.
 * <br>
 * <br>
 * 
 * Because SE mobile phones implement J2ME brilliantly (Nokia?), the RgbCache tries to handles different video RAM areas
 * <br>
 * As said in SonyEricson book, JP-7 (W850i) JP-6 (W810i)
 * <br>
 * <br>
 * Graphics memory areas are used in the following order (JP-6). If one area is full or an image to large to fit in the free space of one area, the next one is used instead.
 * 1. One area of fast video RAM dedicated for graphics storage. <br>
 * 2. Another video RAM area, with somewhat slower access. <br>
 * 3. The general heap area of the phone is used for images when it is not possible to use the two video RAM areas. <br>
 * 4. Swapping of images to the phone flash memory is supported (not in JP-7 and later phones).<br>
 * <br>
 * The developer should always try to fit commonly used images into the fastest RAM area and use the
 * slower areas for more seldomly used images. This is done by making the MIDlet <b> fetch the commonly used
 * images first </b> and make sure that they fit into the 80 kb of fast video RAM.
 * THe RgbImages may use the RgbCache to keep ImageData
 * 
 * In all cases, the developer must flag RgbImages as disposed in the code.
 * 
 * {@link RgbImage#dispose()}
 * 
 * That is especially true of temporary images. If not done and the cache is enabled, it will lead to a blatant memory leak
 * 
 */
public class RgbImage implements IStringable, ITechRgbImage {

   /**
    * When a RGB Image is created, it is initialized with a background color.
    * Default is fully transparent black. RgbImage keeps track with the flag
    * {@linke RgbImage#FLAGX_8VIRGIN} if its RgbArray is filled with this background color
    */
   int             backgroundColor;

   RgbCache        cache;

   /**
    * ID of the int array in the cache:  {@link RgbCache#areaToRgb} <br>
    *
    * -1 means not used or Primitive mode <br>
    * Entirely private to the package. may change during garbage collection
    */
   int             cacheIntID    = -1;

   /**
    * ID of the RgbImage in the cache
    * Entirely private to the package. may change during garbage collection
    */
   int             cacheRgbIndex = -1;

   private DrwCtx  drc;

   /**
    * By default, Image is in Primitive mode, No Locks, Alpha Mode, Not a Region, Not a Link
    * 
    */
   int             flags;

   /**
    * The graphics object on which primitives are drawn
    */
   GraphicsX       graphicsX;

   int             height;

   /**
    * Mutable or Immutable image object
    * Sometimes, when Image data is read from disk, RgbImage will start 
    * 
    * If not null, rgb mode is false
    */
   IImage          img;

   RgbImage        linkImage;

   IRgbLoader      loader;

   int             m;

   protected int   memoryState;

   int             n;

   private String  name          = "";

   /**
    * Start of start in int array.
    * <br>
    * usually 0, unless the Image is being read in a big shared array
    */
   int             offset;

   /**
    * {@link RgbImage} of which this Image is a region.
    * <br>
    * When flag {@link ITechRgbImage#FLAG_03_LINKING} is true, this field is not null.
    */
   public RgbImage parentImage;

   /**
    * image data. managed by RgbCache
    * If not null, rgb mode is true.
    * 
    */
   int[]           rgbData;

   private int     shareCount;

   /**
    * Int value used by the Cache when an incoming Request for linking
    * arrives.
    */
   int             signature;

   /**
    * In the case of cache object, this is the IDrawable.
    */
   Object          source;

   /**
    * Location on Disk/Internet of the Image data.
    * <br>
    * <br>
    * Path to the ID of a ByteObject that build the image (usually a Figure or a Style) /$ID;
    * where ID is the reference ID in the ByteObject pool
    * This value may also be used for Transformation Linking
    */
   String          sourceLocator;

   /**
    * Image transformation to apply on root image data
    * getWidth and getHeight method are impacted by the transformation
    */
   int             transform;

   int             width;

   /**
    * Empty shell created by {@link RgbCache}.
    */
   RgbImage(DrwCtx drc, RgbCache cache) {
      this.drc = drc;
      this.cache = cache;
   }

   RgbImage(DrwCtx drc, RgbCache cache, IImage img) {
      this.drc = drc;
      this.cache = cache;
      this.img = img;
      this.width = img.getWidth();
      this.height = img.getHeight();
   }

   /**
    * 
    * @param data
    * @param 
    * @param w
    * @param h
    */
   RgbImage(DrwCtx drc, RgbCache cache, int[] data, int offset, int w, int h) {
      this.drc = drc;
      this.cache = cache;
      rgbData = data;
      this.offset = offset;
      width = w;
      height = h;
   }

   /**
    * Returns the instance on which READ and/or WRITE operation will be done.  
    * <br>
    * <br>
    * Concurrent reads may happen on the same instance.<br>
    * In all other cases, the RgbImage is cloned.
    * <br>
    * When a Write lock is asked and Image is already shared, image will be cloned.
    * <br>
    * When a Write lock is required, is {@link RgbImage} cloned automatically? Nope.
    * When this is the only use for that image, there is no point cloning, since the original will never be used
    * <br>
    * <br>
    * <b>Example</b>: An Image is used as the basis for several animations.
    * <br>
    * Animation A wants to modify the content (int array) of the Image. It must acquire a {@link ITechRgbImage#FLAG_06_WRITE_LOCK}.
    * <br>
    * Animation B only read the original content.  the Image via a READ_LOCK. 
    * <br>
    * <br>
    * <br>
    * <b>Example</b>: Drawable's cache is used to WRITE.
    * <br>
    * Share Count
    * <br>
    * <b>Example</b> 2:
    * <br>
    * User code explicitely asked image content not to be modifiable, with {@link ITechRgbImage#FLAG_04_IMMUTABLE}, a copy of the Image is created<br>
    * <br>
    * @param lock Flag value of the Lock
    * @return
    */
   public RgbImage acquireLock(int lock) {
      boolean clone = false;
      if (lock == ITechRgbImage.FLAG_06_WRITE_LOCK) {
         if (hasFlag(ITechRgbImage.FLAG_06_WRITE_LOCK) || hasFlag(ITechRgbImage.FLAG_07_READ_LOCK)) {
            clone = true;
         }
      } else if (lock == ITechRgbImage.FLAG_07_READ_LOCK) {
         if (hasFlag(ITechRgbImage.FLAG_06_WRITE_LOCK)) {
            clone = true;
         }
      }
      RgbImage ri = this;
      if (clone) {
         ri = cloneImg();
      }
      ri.setFlag(lock, true);
      return ri;
   }

   /**
    * Blends Image (Source) with this {@link RgbImage} (Destination) at x,y.
    * <br>
    * <br>
    * @param bo {@link BlendOp}
    * @param x coordinate relative to TOP LEFT coordinate of RgbImage
    * @param y coordinate relative to TOP LEFT coordinate of RgbImage
    * @param img blender image is read into a int[] array.
    */
   public void blend(BlendOp bo, int x, int y, IImage img) {
      int[] sec = drc.getUCtx().getGeo2dUtils().getIntersectionDest(0, 0, getWidth(), getHeight(), x, y, img.getWidth(), img.getHeight());
      int ix = sec[0];
      int iy = sec[1];
      int iw = sec[2];
      int ih = sec[3];
      //SystemLog.printDraw(debugBlending(img, this, x, y, ix, iy, iw, ih));
      //this extracts pseudo transparent colors from the Image.
      int[] layerRgb = cache.getImageData(img, ix, iy, iw, ih);
      //SystemLog.printDraw(RgbImage.debugAlphas(layerRgb, iw, ih));
      blend(bo, x, y, layerRgb, 0, iw, 0, 0, iw, ih);
   }

   /**
    * 
    * @param bo
    * @param x
    * @param y
    * @param srcRGB must be the size of this {@link RgbImage}.
    */
   public synchronized void blend(BlendOp bo, int[] srcRGB) {
      if (srcRGB.length != this.width * this.height) {
         throw new IllegalArgumentException();
      }
      int srcScan = this.width;
      int srcW = this.width;
      int srcH = this.height;
      blend(bo, 0, 0, srcRGB, 0, srcScan, 0, 0, srcW, srcH);
   }

   /**
    * Blends the rgb array into the RgbImage.
    * <br>
    * <br>
    * <li>If RgbImage is in Rgb mode, operation is straightforward.
    * <li>If RgbImage is in Primitive mode, Rgb is switched to Rgb 
    * <br>
    * <br>
    * All blend methods call this method eventually.
    * <br>
    * <br>
    * Beofre a blends, RgbImage is flushed, finalized.
    * <br>
    * After a blends, RgbImage is in RGB mode.
    * @param bo {@link BlendOp}, {@link BlendOp#}
    * @param x coordinate relative to TOP LEFT coordinate of RgbImage
    * @param y coordinate relative to TOP LEFT coordinate of RgbImage
    * @param srcRGB the pixel data to blend into the current image
    * @param srcOffset starting offset when reading rgb int[] array
    * @param srcScan 'width' of the scan in rgb int[] array
    * @param srcM the x coordinate in the srcRGB array 
    * @param srcN the y coordinate in the srcRGB array
    * @param srcW the width of the source
    * @param srcH the height of the source
    * @throws IllegalArgumentException when x or y are negative
    */
   public synchronized void blend(BlendOp bo, int x, int y, int[] srcRGB, int srcOffset, int srcScan, int srcM, int srcN, int srcW, int srcH) {
      //case of Image with pending graphicsx.
      //we cannot call getRgbData because code will loop if a merge happens during the flush
      //SystemLog.printDraw("#Blend in RgbImage "+ this.toString("\t\n"));
      if (x < 0 || y < 0)
         throw new IllegalArgumentException("Negative x,y = " + x + ":" + y);
      if (hasFlag(ITechRgbImage.FLAG_19_BLENDING)) {
         return;
      }
      setFlag(ITechRgbImage.FLAG_19_BLENDING, true);
      setFlag(ITechRgbImage.FLAG_05_IGNORE_ALPHA, false);

      int[] rgbData = getRgbData();

      int destM = getM();
      int destN = getN();
      int destScan = getScanLength();
      int destOff = getOffset();
      int destIndex = destOff + destM + x + (destScan * (destN + y));

      //#debug
      String msg = "destIndex=" + destIndex + "\t destMN=" + destM + "," + destN + " destScan=" + destScan + " destOff=" + destOff + " destLength=" + getLength();
      //#debug
      toDLog().pDraw(msg, this, RgbImage.class, "blend", ITechLvl.LVL_05_FINE, true);

      int srcIndex = srcOffset + srcM + (srcScan * srcN);
      int minDest = destScan - srcW;
      int minSrc = srcScan - srcW;

      //#debug
      String msg2 = "blend srcOffset=" + srcOffset + "\t srcScan=" + srcScan + " srcMN=" + srcM + "," + srcN + " srcWH=" + srcW + "," + srcH + " x=" + x + " y=" + y;
      //#debug
      toDLog().pDraw(msg2, this, RgbImage.class, "blend", ITechLvl.LVL_05_FINE, true);

      try {
         for (int i = 0; i < srcH; i++) {
            for (int j = 0; j < srcW; j++) {
               int basePix = rgbData[destIndex];
               int blendPix = srcRGB[srcIndex];
               //only blend if pixel is not virgin
               //if (rgbData[destIndex] != VIRGIN_PIXEL) {
               rgbData[destIndex] = bo.blendPixel(basePix, blendPix);
               //} else {
               // rgbData[destIndex] = rgb[srcIndex];
               //}
               destIndex++;
               srcIndex++;
            }
            destIndex += minDest;
            srcIndex += minSrc;
         }
      } catch (ArrayIndexOutOfBoundsException e) {
         //#debug
         String msg3 = "Array Index Error for " + rgbData.length + "=" + destIndex + " " + srcRGB.length + "=" + srcIndex + " srcWH=" + srcW + "," + srcH;
         //#debug
         toDLog().pDraw(msg3, this, RgbImage.class, "blend", ITechLvl.LVL_05_FINE, true);
         //#debug
         e.printStackTrace();
      }
      setFlag(ITechRgbImage.FLAG_19_BLENDING, false);
   }

   /**
    * Blends input image into {@link RgbImage}.
    * Automatically sets by precaution {@link ITechRgbImage#FLAG_05_IGNORE_ALPHA} to false.
    * @param bo
    * @param x
    * @param y
    * @param img
    */
   public void blend(BlendOp bo, int x, int y, RgbImage img) {
      if (img.isRgb()) {
         //blend the image. the image might be a region of another image
         blend(bo, x, y, img.rgbData, img.getOffset(), img.getScanLength(), img.getM(), img.getN(), img.getWidth(), img.getHeight());
      } else {
         blend(bo, x, y, img.img);
      }
   }

   /**
    * Change the Dimension of the Image.
    * <br>
    * <br>
    * If Image is not in RgbMode, mode is set to True for the change
    * <br>
    * @param w
    * @param h
    * @throws IllegalStateException parent is not officially linked
    */
   public void changeDimension(int w, int h) {
      if (getWidth() == w && getHeight() == h)
         return;
      if (hasFlag(ITechRgbImage.FLAG_03_LINKING)) {
         linkImage.changeDimension(w, h);
         return;
      }
      //to change the dimensions of the image, we must go to Rgb mode
      setRgbMode(true);

      int total = w * h;
      int current = width * height;
      int diff = total - current;
      cache.memChange(this, current, diff);
      width = w;
      height = h;
   }

   private void checkReload() {
      if (hasFlag(ITechRgbImage.FLAG_10_NO_DATA)) {
         reload();
      }
   }

   /**
    * Clone the {@link RgbImage} giving it its own rgb int[] array. 
    * 
    * <br>
    * <br>
    * This will require twice the memory.
    * <br>
    * Both Images will be identical pixel wise in the rgb space.
    * However if this is a region, the cloned image will not.
    * Control flags are reset
    * @return
    */
   public RgbImage cloneImg() {
      return cache.cloneImg(this);
   }

   /**
    * Copy an area of RgbImage. Does Alpha Blending if necessary
    * @param x_src
    * @param y_src
    * @param width
    * @param height
    * @param x_dest
    * @param y_dest
    * @param anchor
    */
   public void copyArea(int x_src, int y_src, int width, int height, int x_dest, int y_dest, int anchor) {

   }

   /**
    * Convenience method for the {@link RgbImage#getRegion(int, int, int, int)}
    * with the added service that it checks and bounds the region so that no exception is thrown
    * <br>
    * People know about the crop
    * <br>
    * 
    * @param w
    * @param h
    * @return
    */
   public RgbImage crop(int x, int y, int w, int h) {
      if (this == cache.NULL_IMAGE)
         return this;
      if (x < 0) {
         x = 0;
      }
      if (y < 0) {
         y = 0;
      }
      if (x >= w || y >= h) {
         return this;
      }
      if (x + w > getWidth()) {
         w = getWidth() - x;
      }
      if (y + h > getHeight()) {
         h = getHeight() - y;
      }

      try {
         return getRegion(x, y, w, h);
      } catch (Exception e) {
         return this;
      }
   }

   /**
    * Returns the smallest rectangle removing external blocks of color
    * @param img
    * @param color
    * @return
    */
   public RgbImage crop(int color) {
      int[] rgb = this.getRgbData();
      int w = this.getWidth();
      int h = this.getHeight();
      int[] vals = RgbUtils.cropTBLRDistances(rgb, w, h, color);
      int minLeftCount = vals[2];
      int minRightCount = vals[3];
      int minTopCount = vals[0];
      int minBotCount = vals[1];
      int newW = w - minLeftCount - minRightCount;
      int newH = h - minTopCount - minBotCount;
      RgbImage nimg = this.getRgbCache().create(newW, newH);
      GraphicsX g = nimg.getGraphicsX();
      g.drawRgbImage(this, minLeftCount, minTopCount, newW, newH, IImage.TRANSFORM_0_NONE, 0, 0);
      return nimg;
   }

   public void decrementShareCount() {
      shareCount--;
   }

   /**
    * Call on the temporary images to release the memory used by it.
    * Calling this on Source Image will only free the memory used by
    * If it is a region, the root data is not disposed
    */
   public void dispose() {
      cache.dispose(this);

   }

   /**
    * nullify {@link GraphicsX}.
    */
   public void disposeGraphics() {
      flush();
      graphicsX = null;
   }

   /**
    * Check all pixels for an alpha Value different from 255.if there is
    */
   public void doUpdateIgnoreAlphaFlag() {
      if (isRgb()) {
         int m = getM();
         int n = getN();
         int w = getWidth();
         int h = getHeight();
         int scanlength = getScanLength();
         int offset = getOffset();
         int[] data = getRgbData();
         for (int i = 0; i < h; i++) {
            int index = offset + m + (scanlength * (n + i));
            for (int j = 0; j < w; j++) {
               if (((data[index] >> 24) & 0xFF) != 255) {
                  setFlag(ITechRgbImage.FLAG_05_IGNORE_ALPHA, false);
                  return;
               }
               index++;
            }
         }
         setFlag(ITechRgbImage.FLAG_05_IGNORE_ALPHA, true);
      } else {
         if (img.isMutable()) {
            setFlag(ITechRgbImage.FLAG_05_IGNORE_ALPHA, true);
         } else {
            int[] rgb = cache.getImageData(img);
            int len = rgb.length;
            for (int i = 0; i < len; i++) {
               if (((rgb[i] >> 24) & 0xFF) != 255) {
                  setFlag(ITechRgbImage.FLAG_05_IGNORE_ALPHA, false);
                  return;
               }
            }
            setFlag(ITechRgbImage.FLAG_05_IGNORE_ALPHA, true);
         }
      }
      //in image mode ?
   }

   /**
    * Draws RgbImage content on {@link IGraphics}. {@link RgbImage#transform} is applied.<br>
    * If both {@link IImage} and rgb array are null, a time consuming reload will happen, provided the source locator is valid
    * @param g
    * @param x
    * @param y
    */
   public void draw(GraphicsX g, int x, int y) {
      draw(g, getM(), getN(), getWidth(), getHeight(), x, y, transform);
   }

   /**
    * Draws the image according to its m,n, width values etc.
    * @param g
    * @param x
    * @param y
    * @param anchor
    */
   public void draw(GraphicsX g, int x, int y, int anchor) {
      draw(g, getM(), getN(), getWidth(), getHeight(), x, y, transform, anchor);
   }

   public void draw(GraphicsX g, int m, int n, int w, int h, int x, int y) {
      draw(g, m, n, w, h, x, y, transform);
   }

   private void draw(GraphicsX g, int m, int n, int w, int h, int x, int y, int trans) {
      draw(g, m, n, w, h, x, y, trans, GraphicsX.ANCHOR);
   }

   /**
    * Do a Flush
    * @param g
    * @param m x coord of region
    * @param n y coord of region
    * @param w width of area
    * @param h height of area
    * @param x destination x coordinate in g
    * @param y destination y coordinate in g
    * @param trans
    * @param anchor anchoring in g relative to x,y 
    */
   void draw(GraphicsX g, int m, int n, int w, int h, int x, int y, int trans, int anchor) {
      flushReload();
      if (img != null) {
         //case of Primitive Mode. Image does not have a RGB array
         g.drawRegion(img, m, n, w, h, trans, x, y, anchor);
      } else {
         if (this == cache.NULL_IMAGE) {
            return;
         }
         if (!isRegion() && trans == 0) {
            if ((anchor & ITechGraphics.HCENTER) == ITechGraphics.HCENTER) {
               x = x + w / 2;
            }
            if ((anchor & ITechGraphics.VCENTER) == ITechGraphics.VCENTER) {
               y = y + h / 2;
            }
            if ((anchor & ITechGraphics.BOTTOM) == ITechGraphics.BOTTOM) {
               y = y - h;
            }
            if ((anchor & ITechGraphics.RIGHT) == ITechGraphics.RIGHT) {
               x = x - w;
            }
            g.drawRGB(rgbData, 0, w, x, y, w, h, !hasFlag(ITechRgbImage.FLAG_05_IGNORE_ALPHA));
         } else {
            //case Image is a region of another Image.
            IImage img = getImage();
            g.drawRegion(img, 0, 0, img.getWidth(), img.getHeight(), trans, x, y, anchor);
         }
      }
   }

   /**
    * 
    * @param g
    * @param x
    * @param y
    * @param anchor
    * @param trans
    */
   public void drawT(GraphicsX g, int x, int y, int anchor, int trans) {
      //verbose but faster to step over in debug
      int m = getM();
      int n = getN();
      int width = getWidth();
      int height = getHeight();
      draw(g, m, n, width, height, x, y, trans, anchor);
   }

   /**
    * this method modifies pixels values from an index model. existing pixels are ignored
    * @param rgbs colors values
    * @param indexes array with 
    * 
    * Apply with given blend mode, default is OVER
    */
   public void applyModel(int[] rgbs, int[] indexes) {

   }

   /**
    * Fill the image with
    * Mode is not changed
    * <br>
    * TODO : Clears all data on {@link GraphicsX}
    * @param color
    * @throws IllegalStateException when RgbImage is not Mutable
    */
   public void fill(int color) {
      if (hasFlag(ITechRgbImage.FLAG_16_VIRGIN) && backgroundColor == color)
         return;
      if (isRgb()) {
         int w = getWidth();
         int h = getHeight();
         int index = getOffset() + getM() + (getScanLength() * getN());
         int add = getScanLength() - w;
         for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
               rgbData[index] = color;
               index++;
            }
            index += add;
         }
      } else {
         IGraphics g = img.getGraphics();
         g.setColor(color);
         g.fillRect(0, 0, img.getWidth(), img.getHeight());
      }
      backgroundColor = color;
      setFlag(ITechRgbImage.FLAG_15_KNOWN_ALPHA, true);
      setFlag(ITechRgbImage.FLAG_16_VIRGIN, true);
   }

   /**
    * Flush any undone data to the image from the GraphicsX object
    * <br>
    * This is necessary when in RgbMode and {@link GraphicsX} is in {@link GraphicsX#MODE_2_RGB_IMAGE}.
    * Image data on the primitive layer has to be merged to the rgb array.
    * <br>
    * Called by any method that require access to RgbImage data.
    */
   private void flush() {
      if (graphicsX != null) {
         //make sure all drawings on graphicsX layer are written to the rgb array
         if (rgbData != null) {
            //TODO clean this. buggy. the point is a Merge is not necessary when the RgbImage is Primitive
            //and the GraphicsX is just a shell
            graphicsX.flush();
         } else {
            graphicsX.flush();
         }
      }
   }

   private void flushReload() {
      if (hasFlag(ITechRgbImage.FLAG_10_NO_DATA)) {
         reload();
      }
      flush();
   }

   /**
    * Returns a {@link GraphicsX} object to write to this {@link RgbImage}. <br>
    * Mode is {@link GraphicsX#MODE_2_RGB_IMAGE}. <br>
    * 
    * @return a {@link GraphicsX} 
    * @throws IllegalStateException If the image is immutable
    */
   public GraphicsX getGraphicsX() {
      return getGraphicsXSmart();
   }

   /**
    * Returns a {@link GraphicsX} object to write to this {@link RgbImage}. <br>
    * <br>
    * <br>
    * Blending mode is set on the returned Object
    * <br>
    * <li>{@link ITechGraphicsX#MODE_0_SCREEN}
    * <li>{@link ITechGraphicsX#MODE_1_IMAGE}
    * <li>{@link ITechGraphicsX#MODE_2_RGB_IMAGE}
    * <li>{@link ITechGraphicsX#MODE_3_RGB}
    * <li>{@link ITechGraphicsX#MODE_4_NULL}
    * <br>
    * 
    * @param paintingMode
    * @return a {@link GraphicsX}  
    * @throws IllegalStateException if RgbImage is not mutable. {@link RgbImage#isMutable()}
    */
   public GraphicsX getGraphicsX(int paintingMode) {
      if (!isMutable()) {
         throw new IllegalStateException("Cannot get GraphicX on an Immutable Image");
      }
      if (this == cache.NULL_IMAGE) {
         //special graphics that doesn't draw anything
         graphicsX = new GraphicsX(drc, cache, this, true);
         //#debug
         graphicsX.toStringSetName("RgbImage_" + ToStringStaticDrawx.toStringPaintMode(paintingMode));
         return graphicsX;
      }
      if (graphicsX != null) {
         //change the paint mode.
         if (graphicsX.getPaintMode() != paintingMode) {
            graphicsX.setPaintMode(paintingMode, getWidth(), getHeight());
         }
      } else {
         graphicsX = new GraphicsX(drc, cache, this, paintingMode);
         //#debug
         graphicsX.toStringSetName("RgbImage_" + ToStringStaticDrawx.toStringPaintMode(paintingMode));
      }
      return graphicsX;
   }

   /**
    * GraphicsX whose area over the RgbImage is clipped to the given rectangle
    * @param paintingMode
    * @param x
    * @param y
    * @param w
    * @param h
    * @return
    * Uses: When the need to draw a small Drawable over a known area of the RgbImage, it is more efficient
    * to create a smaller GraphicsX object. It consumes less memory.
    * TODO Can you mix two GraphicsX object? 
    */
   public GraphicsX getGraphicsX(int paintingMode, int x, int y, int w, int h) {
      if (this == cache.NULL_IMAGE) {
         //special graphics that doesn't draw anything
         graphicsX = new GraphicsX(drc, cache, this, true);
         graphicsX.toStringSetName("NullImage");
         return graphicsX;
      }
      graphicsX = new GraphicsX(drc, cache, this, paintingMode, x, y, w, h);
      graphicsX.toStringSetName("RgbImage_" + w + "_" + h);
      return graphicsX;
   }

   /**
    * Returns a {@link GraphicsX} object to write to this {@link RgbImage}. <br>
    * 
    * Automatically choose the most adequate painting mode for the GraphicsX object.
    * 
    * If the RgbImage is a Image mutable, the painting mode is IMAGE and the image is used as the top layer.
    * 
    * White is still considered pseudo alpha.
    * If in the future pseudo alpha is needed, a RGB int[] array is created
    * => {@link GraphicsX#MODE_1_IMAGE}
    * 
    * For RGB Mode => {@link GraphicsX#MODE_2_RGB_IMAGE}
    * Creates an Image layer to draw primitives on.
    * 
    * @return a {@link GraphicsX} 
    * @throws IllegalStateException If the image is immutable
    */
   public GraphicsX getGraphicsXSmart() {
      int mode = GraphicsX.MODE_1_IMAGE;
      if (isRgb()) {
         mode = GraphicsX.MODE_2_RGB_IMAGE;
      }
      return getGraphicsX(mode);
   }

   /**
    * Retrieves the height of this image
    * <br>
    * Loads the image if it was never loaded.
    * @return the height of this image
    */
   public int getHeight() {
      if (hasFlag(ITechRgbImage.FLAG_03_LINKING)) {
         if (transform >= IImage.TRANSFORM_4_MIRROR_ROT270) {
            return linkImage.getWidth();
         } else {
            return linkImage.getHeight();
         }
      }
      checkReload();
      return this.height;
   }

   /**
    * Simply returns the Immutable image of this RgbImage
    * <br>
    * <br>
    * Transparency is preserved.
    * @return
    */
   public IImage getImage() {
      return getImage(false);
   }

   /**
    * Create a cloned {@link IImage} from this {@link RgbImage}. 
    * <br>
    * <br>
    * 
    * If RgbImage is in primitive mode
    * IMPOSSIBLE: rgb array with transparent pixels. You cannot draw primitives on it.
    * 
    * @param mutable true if the getGraphicsX method is used to change the content of the image. Transparent pixels will show white.
    * @return a non null {@link IImage}
    */
   public IImage getImage(boolean mutable) {
      if (img == null) {
         int[] data = rgbData;
         if (isRegion() || getOffset() != 0) {
            //we must take a copy because a region does not uses all the pixels 
            data = getRgbCopy();
         }
         if (mutable) {
            //Every pixel within a mutable image is always fully opaque
            IImage mi = drc.getImageFactory().createImage(getWidth(), getHeight());
            IGraphics g = mi.getGraphics();
            //problem : if image has transparent pixels, it will go on the background color of the mutable image
            g.setColor(backgroundColor);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.drawRGB(data, 0, getWidth(), 0, 0, getWidth(), getHeight(), !hasFlag(ITechRgbImage.FLAG_05_IGNORE_ALPHA));
            return mi;
         } else {
            return cache.createPrimitiveImmutable(data, getWidth(), getHeight(), !hasFlag(ITechRgbImage.FLAG_05_IGNORE_ALPHA));
         }
      }
      return img;
   }

   /**
    * The number of pixels used by this image
    * @return
    */
   public int getLength() {
      return getWidth() * getHeight();
   }

   /**
    * Region x coordinate
    * @return
    */
   public int getM() {
      if (parentImage != null) {
         return parentImage.getM() + m;
      }
      return m;
   }

   /**
    * Region y coordinate in the rgb array
    * @return
    */
   public int getN() {
      if (parentImage != null) {
         return parentImage.getN() + n;
      }
      return n;
   }

   /**
    * Name for the Image
    * Tries to build a short readable image name (not the Path)
    */
   public String getName() {
      return name;
   }

   /**
    * Return the first index of rgb data for that image
    * For a region, return the root index of the root image
    * a region first pixel is offset + m + (n*scanlength)
    * @return
    */
   public int getOffset() {
      if (parentImage != null) {
         return parentImage.getOffset();
      }
      return offset;
   }

   public int getPixel(int x, int y) {
      flushReload();
      int index = getOffset() + getM() + x + (getScanLength() * (getN() + y));
      return rgbData[index];
   }

   /**
    * Create a new RgbImage instance for the region defined by the rectangle w,h located at x,y in the image coordinate 
    * <br>
    * An intersection is computed for rectangle laying outside the Image.
    * TODO
    * @param x must be positive
    * @param y must be positive
    * @param w
    * @param h
    * @return
    */
   public RgbImage getRegion(int x, int y, int w, int h) {
      flushReload();
      if (x < 0 || y < 0)
         throw new IllegalArgumentException("x=" + x + " y=" + y);
      return cache.createRegion(this, x, y, w, h);
   }

   public RgbCache getRgbCache() {
      return cache;
   }

   /**
    * Gets a clean cut array with the Image/Region inside
    * <br>
    * <br>
    * @return
    */
   public int[] getRgbCopy() {
      //auto check load with getLen
      int len = getLength();
      int[] fullData = cache.createImageArray(len);
      int[] rgbData = getRgbData();
      if (isRegion()) {
         int copyIndex = 0;
         int rw = getWidth();
         int rh = getHeight();
         int m = getM();
         int n = getN();
         int scan = getScanLength();
         for (int i = 0; i < rh; i++) {
            int start = getOffset() + m + (scan * (n + i));
            System.arraycopy(rgbData, start, fullData, copyIndex, rw);
            copyIndex += rw;
         }
      } else {
         System.arraycopy(rgbData, offset, fullData, 0, getLength());
      }
      return fullData;
   }

   /**
    * Return the reference to the rgb int array of this image. 
    * <br>
    * <br>
    * <b>If mode is primitive</b>. <br>
    * <li> automatically switch {@link RgbImage} to mode RGB. <br>
    * <li> flushes any {@link GraphicsX} pending operations.
    * <br>
    * <br>
    * <b>If mode is rgb</b> <br>
    * RgbImage has a {@link GraphicsX} object with pending changes, all changes are flushed to the Image.
    * <br>
    * The flag {@link ITechRgbImage#FLAG_16_VIRGIN} is set to false
    * <br>
    * <br>
    * 
    * @return int array
    * @throws NullPointerException if not enough memory to host rgb array
    */
   public int[] getRgbData() {
      flushReload();
      if (rgbData == null) {
         setRgbMode(true);
      }
      if (rgbData == null) {
         //setting the mode to Rgb may call softGC. An animation fetching the RgbData of a huge image
         //will get here
         throw new NullPointerException("RgbImage Data has been nullified by Garbage Collector");
      }
      //assume it will be modified
      setFlag(ITechRgbImage.FLAG_16_VIRGIN, false);
      return this.rgbData;
   }

   public RgbImage getRootImage() {
      if (parentImage != null)
         return parentImage.getRootImage();
      return this;
   }

   /**
    * The scanning length of the Image in the Rgb Array.
    * 
    * @return
    */
   public int getScanLength() {
      if (parentImage != null)
         return parentImage.getScanLength();
      return width;
   }

   /**
    * 
    * @param trans {@link IImage#TRANSFORM_1_FLIP_H_MIRROR_ROT180} etc.
    * @return
    */
   public RgbImage getTransform(int trans) {
      if (this == cache.NULL_IMAGE) {
         return this;
      }
      RgbImage img = null;
      int w = getWidth();
      int h = getHeight();
      if (trans == IImage.TRANSFORM_4_MIRROR_ROT270 || trans == IImage.TRANSFORM_5_ROT_90 || trans == IImage.TRANSFORM_6_ROT_270 || trans == IImage.TRANSFORM_7_MIRROR_ROT90) {
         int t = w;
         w = h;
         h = t;
      }
      if (isRgb()) {
         flush();
         int[] rgb = TransformUtils.transform(getRgbData(), getWidth(), getHeight(), trans);
         img = cache.createImage(rgb, w, h);
      } else {
         //img = cache.createImage(w, h, 0);
         img = cache.createPrimitiveRgb(w, h, ColorUtils.FULLY_OPAQUE_BLACK);
         GraphicsX gx = img.getGraphicsX();
         gx.drawImage(this, 0, 0, GraphicsX.ANCHOR, trans);
         gx.flush();
      }
      return img;
   }

   /**
    * Retrieves the width of this image
    * 
    * @return the width of this image
    */
   public int getWidth() {
      if (hasFlag(ITechRgbImage.FLAG_03_LINKING)) {
         if (transform >= IImage.TRANSFORM_4_MIRROR_ROT270) {
            return linkImage.getHeight();
         } else {
            return linkImage.getWidth();
         }
      }
      checkReload();
      return this.width;
   }

   /**
    * For all flags
    * @param flag
    * @return
    */
   public boolean hasFlag(int flag) {
      return (flags & flag) == flag;
   }

   /**
    * 
    * @return
    */
   public boolean hasTransparentPixels() {
      //check caching flag
      return false;
   }

   /**
    * Increment counter for those sharing this {@link RgbImage}.
    * Calling
    */
   public void incrementShareCount() {
      shareCount++;
   }

   /**
    * True if the area is filled with background color. 
    * <br>
    * False otherwise.
    * <br>
    * <br>
    * Set {@link RgbImage} to RGB mode 
    * <br>
    * x,y are taken relative to 0,0 in the image.
    * <br>
    * When there is no intersection, returns false.
    * True if null image.
    * @param x coordinate base on Image root coordinate
    * @param y
    * @param w
    * @param h
    * @return
    */
   public boolean isEmpty(int x, int y, int w, int h) {
      if (this == cache.NULL_IMAGE) {
         return true;
      }
      setRgbMode(true);
      int[] sec = drc.getUCtx().getGeo2dUtils().getIntersection(0, 0, getWidth(), getHeight(), x, y, w, h);
      if (sec == null) {
         return false;
      }
      x = sec[0];
      y = sec[1];
      w = sec[2];
      h = sec[3];
      int[] rgbData = this.rgbData;
      int m = getM();
      int n = getN();
      int scan = getScanLength();
      int off = getOffset();
      int index = off + m + x + (scan * (n + y));
      int minDest = scan - w;

      //#debug
      toDLog().pState("", this, RgbImage.class, "isEmpty", ITechLvl.LVL_05_FINE, true);

      //#debug
      String msg = "x=" + x + " y=" + y + " w=" + w + " h=" + h + " off=" + off + " scan=" + scan + " m=" + m + " n=" + n;
      //#debug
      toDLog().pDraw(msg, this, RgbImage.class, "isEmpty", ITechLvl.LVL_05_FINE, true);

      for (int i = 0; i < h; i++) {
         for (int j = 0; j < w; j++) {
            if (rgbData[index] != backgroundColor) {
               return false;
            }
            index++;
         }
         index += minDest;
      }
      return true;
   }

   public boolean isMutable() {
      if (img != null) {
         return img.isMutable();
      }
      return true;
   }

   public boolean isNullImage() {
      return this == cache.NULL_IMAGE;
   }

   public boolean isRegion() {
      return hasFlag(ITechRgbImage.FLAG_01_REGION);
   }

   /**
    * Tells the mode RGB [True] or Primitive [False]
    * <br>
    * <br>
    * @return
    */
   public boolean isRgb() {
      return hasFlag(ITechRgbImage.FLAG_13_RGB);
   }

   /**
    * Set int[] array and Image object to null
    */
   public void releaseFreeMemory() {
      rgbData = null;
      img = null;
      linkImage = null;
      setFlag(ITechRgbImage.FLAG_10_NO_DATA, true);
   }

   /**
    * Releases the lock for that Object and its type
    * @param lock
    */
   public void releaseLock(int lock) {
      setFlag(lock, false);
   }

   /**
    * Reloading works only with supported source image.
    * 
    */
   void reload() {
      if (img == null && rgbData == null) {
         //the image data has been wiped out from memory by the controller
         //try to reload the image data if possible
         cache.reload(this);
         //#debug
         toDLog().pInit("Asking RgbCache to load Image Data", this, RgbImage.class, "reload");
         if (img == null && rgbData == null) {
            //TODO how to smoothly go out of htis?
            //#debug
            toDLog().pInit("Both Image and RGB array are null", this, RgbImage.class, "reload");
            throw new IllegalArgumentException("No Data for Image");
         }
      }
   }

   void reset(int[] rgb, int offset, int w, int h) {
      rgbData = rgb;
      this.offset = offset;
      width = w;
      m = 0;
      n = 0;
      height = h;
      flags = 0;
   }

   public RgbImage scaleImage(int newWidth, int newHeight, ByteObject scaler) {
      return drc.getRgbImageOperator().scaleRgbImage(this, newWidth, newHeight, scaler);
   }

   public void setFlag(int flag, boolean v) {
      flags = BitUtils.setFlag(flags, flag, v);
   }

   /**
    * At X,Y, set a rectangle of data, reading from pixels
    * @param x
    * @param y
    * @param width
    * @param height
    * @param pixels
    */
   public void setRGB(int x, int y, int width, int height, int[] pixels) {
      int index = x + y * getWidth();
      int count = 0;
      for (int i = 0; i < height; i++) {
         for (int k = 0; k < width; k++) {
            rgbData[index] = pixels[count];
            index++;
            count++;
         }
      }
   }

   /**
    * Change the {@link RgbImage} mode. Flush {@link GraphicsX} if any.
    * Mode: <br>
    * <li>In Rgb Mode, the image data is represented with an int array.
    * If cache is enabled, the cache manages the int rgb array.
    * <li> In Primitive Mode, the image data is represented with the {@link IImage} class.
    * <br>
    * The mode is either rgb or primtive. <br>
    * <br>
    * <b>Notes</b>: <br>
    * <li>Settings primitive mode with a LineDef image is not very memory efficient
    * <br>
    * @param rgb
    */
   public void setRgbMode(boolean rgb) {
      if (rgb == hasFlag(ITechRgbImage.FLAG_13_RGB)) {
         //no changes of mode
         //SystemLog.printDraw("No Change of Mode");
         return;
      }
      flushReload();
      if (rgb) {
         //primitive -> rgb
         cache.convertToRbg(this);
      } else {
         //rgb -> primitive
         cache.convertToPrimitve(this);
      }
   }

   /**
    * Transforms this image. Makes it automatically mutable
    * Creates a root link image
    * If it was a source image, this image is removed from cache
    * and will have to be reloaded
    * @param trans
    */
   public void setTransform(int trans) {
      if (transform == 0 && trans != 0) {
         //explicit modification
         setFlag(ITechRgbImage.FLAG_02_ROOT_LINK, false);

      }
      transform = trans;
   }

   void setWidth(int width) {
      this.width = width;
   }

   /**
    * 
    * @param x0 top left x
    * @param y0 top left y
    * @param x1
    * @param y1
    * @param x2
    * @param y2
    * @param x3
    * @param y3
    * @param skewer
    * @return
    */
   public RgbImage skew(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3, ByteObject skewer) {
      return drc.getRgbImageOperator().skewImage(this, skewer, x0, y0, x1, y1, x2, y2, x3, y3);
   }

   //#mdebug
   public IDLog toDLog() {
      return drc.toDLog();
   }

   // private void drawRgb(GraphicsX g, int m, int n, int w, int h, int x, int y) {
   //	 int newM = m;
   //	 int newN = n;
   //	 int scanlength = getScanLength();
   //	 int[] data = getRgbData();
   //	 int dy = y + h;
   //	 int offset = getOffset() + newM + (newN * scanlength);
   //	 for (int i = 0; i < h; i++) {
   //	    g.drawRGB(data, offset, 0, x, dy, w, 1, !hasFlag(FLAG_5IGNORE_ALPHA));
   //	    dy++;
   //	    offset += scanlength;
   //	 }
   //   }
   //
   //   /**
   //    * 
   //    * @param g
   //    * @param m
   //    * @param n
   //    * @param w
   //    * @param h
   //    * @param x
   //    * @param y
   //    */
   //   private void drawRgbFlipH(GraphicsX g, int m, int n, int w, int h, int x, int y) {
   //	 int newM = m;
   //	 int newN = n;
   //	 int scanlength = getScanLength();
   //	 int[] data = getRgbData();
   //	 int dy = y + h;
   //	 int offset = getOffset() + newM + newN * scanlength;
   //	 for (int i = 0; i < h; i++) {
   //	    g.drawRGB(data, offset, w, x, dy, w, 1, !hasFlag(FLAG_5IGNORE_ALPHA));
   //	    dy--;
   //	    offset += scanlength;
   //	 }
   //   }
   //
   //   /**
   //    * Same as Rot90
   //    * @param g
   //    * @param m
   //    * @param n
   //    * @param w
   //    * @param h
   //    * @param x
   //    * @param y
   //    */
   //   private void drawRgbRot270(GraphicsX g, int m, int n, int w, int h, int x, int y) {
   //	 //very similar to above
   //	 int newM = m + getM();
   //	 int newN = n + getN();
   //
   //	 //first row of pixels is last column
   //	 //last row of pixels is first column
   //	 int[] workingLine = RgbCache.getCache().getWorkingLine(h);
   //	 int scanlength = getScanLength();
   //	 int[] data = getRgbData();
   //	 int dy = y + w;
   //	 int index = 0;
   //	 int count = 0;
   //	 for (int i = 0; i < w; i++) {
   //	    //fill the working line 
   //	    index = offset + newM + i + (newN * scanlength) + scanlength;
   //	    for (int j = 0; j < h; j++) {
   //		  workingLine[count] = data[index];
   //		  index += scanlength;
   //		  count++;
   //	    }
   //	    g.drawRGB(workingLine, 0, h, x, dy, h, 1, !hasFlag(FLAG_5IGNORE_ALPHA));
   //	    dy--;
   //	    count = 0;
   //	 }
   //   }
   //
   //   /**
   //    * Must be clipped externally for efficient drawing.
   //    * @param g
   //    * @param m m position in rgb array from offset
   //    * @param n
   //    * @param w
   //    * @param h
   //    * @param x
   //    * @param y
   //    */
   //   private void drawRgbRot90(GraphicsX g, int m, int n, int w, int h, int x, int y) {
   //	 int newM = m;
   //	 int newN = n;
   //
   //	 //first row of pixels is last column
   //	 //last row of pixels is first column
   //	 int[] workingLine = RgbCache.getCache().getWorkingLine(h);
   //	 int scanlength = getScanLength();
   //	 int[] data = getRgbData();
   //	 int dy = y;
   //	 int index = 0;
   //	 int count = 0;
   //	 for (int i = 0; i < w; i++) {
   //	    //fill the working line 
   //	    index = offset + newM + i + (newN * scanlength) + scanlength;
   //	    for (int j = 0; j < h; j++) {
   //		  workingLine[count] = data[index];
   //		  index += scanlength;
   //		  count++;
   //	    }
   //	    g.drawRGB(workingLine, 0, h, x, dy, h, 1, !hasFlag(FLAG_5IGNORE_ALPHA));
   //	    dy++;
   //	    count = 0;
   //	 }
   //   }

   public String toString() {
      return Dctx.toString(this);
   }

   public void toString(Dctx dc) {
      dc.root(this, RgbImage.class, 1601);
      if (hasFlag(ITechRgbImage.FLAG_03_LINKING)) {
         linkImage.toString(dc);
         return;
      }
      dc.append(" [");
      dc.append(width);
      dc.append(",");
      dc.append(height);
      dc.append("]");
      dc.append(" size=");
      dc.append((width * height * 4 / 1000));
      dc.append("kb");
      dc.append(' ');
      dc.append("Mode=");
      if (isRgb()) {
         dc.append("Rgb");
      } else {
         dc.append("Primitive");
      }
      if (dc.hasFlagData(drc, IToStringFlags.FLAG_DATA_06_SHOW_NULLS)) {
         if (img == null) {
            dc.append(" Img=null");
         }
         if (rgbData == null) {
            dc.append(" Rgb=null");
         }
      }
      dc.appendVarWithSpace("m",getM());
      dc.appendVarWithSpace("n",getN());
      dc.appendVarWithSpace("offset",getOffset());
      dc.appendVarWithSpace("scanlength",getScanLength());
      dc.append(getScanLength());
      dc.appendVarWithSpace("bgcolor",ToStringStaticDrawx.toStringColor(backgroundColor));
      dc.appendVarWithSpace("transform",ToStringStaticUc.toStringTransform(transform));

      dc.nl();
      IntToStrings flags = new IntToStrings(toStringGetUCtx());
      flags.add(ITechRgbImage.FLAG_09_USED, "Used");
      flags.add(ITechRgbImage.FLAG_10_NO_DATA, "NoData");
      flags.add(ITechRgbImage.FLAG_11_CLONED, "Cloned");
      flags.add(ITechRgbImage.FLAG_12_DISPOSED, "Disposed");
      flags.add(ITechRgbImage.FLAG_13_RGB, "RGB");
      flags.add(ITechRgbImage.FLAG_15_KNOWN_ALPHA, "KnownAlpha");
      flags.add(ITechRgbImage.FLAG_14_MODIFIED, "Modified");
      flags.add(ITechRgbImage.FLAG_01_REGION, "Region");
      flags.add(ITechRgbImage.FLAG_02_ROOT_LINK, "Root");
      flags.add(ITechRgbImage.FLAG_03_LINKING, "Link");
      flags.add(ITechRgbImage.FLAG_05_IGNORE_ALPHA, "NoAlpha");
      flags.add(ITechRgbImage.FLAG_06_WRITE_LOCK, "WriteLock");
      flags.add(ITechRgbImage.FLAG_07_READ_LOCK, "ReadLock");
      flags.add(ITechRgbImage.FLAG_16_VIRGIN, "Virgin");

      dc.appendFlagsPositive(this.flags, "RGBFlags", flags);

      dc.nlLvlO(sourceLocator, "Locator");

      if (dc.hasFlagData(drc, IFlagsToStringDrw.DATA_FLAG_20_HIDE_CACHE)) {
         dc.append(" Cache Ignored ");
      } else {
         dc.nl();
         dc.appendVar("cacheIntID", cacheIntID);
         dc.appendVar("cacheRgbIndex", cacheRgbIndex);
      }
      if (dc.hasFlagData(drc, IFlagsToStringDrw.DATA_FLAG_22_HIDE_GRAPHICS)) {
         dc.append(" GraphicsX Ignored ");
      } else {
         dc.nlLvl(graphicsX, "graphicsX");
      }

      dc.nlLvl(img, "img");
   }

   public String toString1Line() {
      return Dctx.toString1Line(this);
   }

   public void toString1Line(Dctx sb) {
      sb.root1Line(this, "RgbImage");
      sb.append(cacheRgbIndex);
      sb.append(" CID=");
      sb.append(cacheIntID);
      if (getM() != 0)
         sb.append(" m=" + getM());
      if (getN() != 0)
         sb.append(" n=" + getN());
      sb.append(" [");
      sb.append(width);
      sb.append(",");
      sb.append(height);
      sb.append("]\t");
      if (getOffset() != 0) {
         sb.append(" offset=" + getOffset());
      }
      if (getScanLength() != getWidth()) {
         sb.append(" scanlength=" + getScanLength());
      }

      if (img == null) {
         sb.append(" img=null");
      } else {
         sb.append(" img mutable=" + img.isMutable());
      }
      if (rgbData == null) {
         sb.append(" rgbdata=null");
      } else {
         sb.append(" rgbdata = " + rgbData.length + " " + rgbData);
      }
      toStringFlag(sb, ITechRgbImage.FLAG_09_USED, " Used");
      toStringFlag(sb, ITechRgbImage.FLAG_10_NO_DATA, " NoData");
      toStringFlag(sb, ITechRgbImage.FLAG_11_CLONED, " Cloned");
      toStringFlag(sb, ITechRgbImage.FLAG_12_DISPOSED, " Disposed");
      toStringFlag(sb, ITechRgbImage.FLAG_13_RGB, " Rgb");
      toStringFlag(sb, ITechRgbImage.FLAG_14_MODIFIED, " Modified");
      toStringFlag(sb, ITechRgbImage.FLAG_15_KNOWN_ALPHA, " KnownAlpha");
      toStringFlag(sb, ITechRgbImage.FLAG_01_REGION, " Region");
      toStringFlag(sb, ITechRgbImage.FLAG_02_ROOT_LINK, " Root");
      toStringFlag(sb, ITechRgbImage.FLAG_03_LINKING, " Link");
      toStringFlag(sb, ITechRgbImage.FLAG_05_IGNORE_ALPHA, " NoAlpha");
      toStringFlag(sb, ITechRgbImage.FLAG_06_WRITE_LOCK, " WriteLock");
      toStringFlag(sb, ITechRgbImage.FLAG_07_READ_LOCK, " ReadLock");
      toStringFlag(sb, ITechRgbImage.FLAG_16_VIRGIN, " Virgin");
      if (graphicsX != null) {
         sb.append(" g=" + ToStringStaticDrawx.toStringPaintMode(graphicsX.getPaintMode()));
      }
      if (backgroundColor != 0) {
         sb.append(" c=" + ToStringStaticDrawx.toStringColor(backgroundColor));
      }
      if (sourceLocator != null) {
         sb.append(" ");
         sb.append(sourceLocator);
      }
   }

   public void toStringAlpha(Dctx sb) {
      int m = getM();
      int destN = getN();
      int destScan = getScanLength();
      int destOff = getOffset();
      int w = getWidth();
      int h = getHeight();
      int min = destScan - w;
      int[] rgb = getRgbData();
      int destIndex = destOff + m + (destScan * (destN));
      for (int i = 0; i < h; i++) {
         for (int j = 0; j < w; j++) {
            int p = rgb[destIndex];
            int val = ((p >> 24) & 0xFF);
            sb.append(val);
            if (val < 100) {
               sb.append('\t');
            }
            sb.append('\t');
            destIndex++;
         }
         sb.append("\n");
         destIndex += min;
      }
   }

   public void toStringAlphas(Dctx sb, int[] ar, int w, int h) {
      int destIndex = 0;
      for (int i = 0; i < h; i++) {
         for (int j = 0; j < w; j++) {
            int val = ((ar[destIndex] >> 24) & 0xFF);
            sb.append(val);
            if (val < 100) {
               sb.append('\t');
            }
            sb.append('\t');
            destIndex++;
         }
         sb.nl();
      }
   }

   public void toStringBlending(Dctx sb, IImage img, RgbImage dest, int x, int y, int ix, int iy, int iw, int ih) {
      sb.append("#RgbImage.blends");
      sb.append(" srcImage=[" + img.getWidth() + ", " + img.getHeight() + "]");
      sb.append(" into [" + dest.getWidth() + "," + dest.getHeight() + "]");
      sb.append(" at " + x + "," + y);
      sb.append(" intersection=[" + ix + "," + iy + " " + iw + "," + ih + "]");
   }

   public void toStringColors(Dctx sb) {
      int m = getM();
      int destN = getN();
      int destScan = getScanLength();
      int destOff = getOffset();
      int w = getWidth();
      int h = getHeight();
      int min = destScan - w;
      int[] rgb = getRgbData();
      int destIndex = destOff + m + (destScan * (destN));
      ColorUtils cu = toStringGetUCtx().getColorU();
      for (int i = 0; i < h; i++) {
         for (int j = 0; j < w; j++) {
            cu.toStringColor(sb, rgb[destIndex]);
            sb.append('\t');
            destIndex++;
         }
         sb.append("\n");
         destIndex += min;
      }
   }

   public void toStringColors(Dctx sb, int[] ar, int w, int h) {
      int destIndex = 0;
      ColorUtils cu = toStringGetUCtx().getColorU();
      for (int i = 0; i < h; i++) {
         for (int j = 0; j < w; j++) {
            cu.toStringColor(sb, ar[destIndex]);
            sb.append('\t');
            destIndex++;
         }
         sb.nl();
         ;
      }
   }

   private void toStringFlag(Dctx sb, int flag, String str) {
      if (hasFlag(flag)) {
         sb.append(str);
      }
   }

   public String toStringFull(Dctx sb) {
      if (hasFlag(ITechRgbImage.FLAG_03_LINKING)) {
         sb.append(linkImage.toString());
         return sb.toString();
      }
      sb.append("#RgbImage");
      sb.nl();
      if (getM() != 0)
         sb.append(" m=" + getM());
      if (getN() != 0)
         sb.append(" n=" + getN());
      sb.append(" w=");
      sb.append(width);
      sb.append(" h=");
      sb.append(height);
      sb.append(" ");
      sb.append((width * height * 4 / 1000));
      sb.append("kb");
      sb.nl();
      if (img == null) {
         sb.append(" img=null");
      } else {
         sb.append(" Image mutable=" + img.isMutable());
      }
      if (rgbData == null) {
         sb.append(" rgbdata=null");
      } else {
         sb.append(" rgbdata : lenght=" + rgbData.length + " " + rgbData);
      }
      if (getOffset() != 0) {
         sb.append("offset=");
         sb.append(getOffset());
      }
      if (getScanLength() != getWidth()) {
         sb.append("scanlength=");
         sb.append(getScanLength());
      }
      sb.append(" cacheIntID=");
      sb.append(cacheIntID);
      sb.append(" cacheRgbIndex=");
      sb.append(cacheRgbIndex);
      sb.append(" ");
      sb.append("backgroundColor=" + ToStringStaticDrawx.toStringColor(backgroundColor));
      sb.nl();
      sb.append(" Transform=");
      sb.append(ToStringStaticUc.toStringTransform(transform));
      sb.nl();
      sb.append(" FlagX=");
      toStringFlag(sb, ITechRgbImage.FLAG_09_USED, " Used");
      toStringFlag(sb, ITechRgbImage.FLAG_10_NO_DATA, " NoData");
      toStringFlag(sb, ITechRgbImage.FLAG_11_CLONED, " Cloned");
      toStringFlag(sb, ITechRgbImage.FLAG_12_DISPOSED, " Disposed");
      toStringFlag(sb, ITechRgbImage.FLAG_13_RGB, " Rgb");
      toStringFlag(sb, ITechRgbImage.FLAG_14_MODIFIED, " Modified");
      toStringFlag(sb, ITechRgbImage.FLAG_15_KNOWN_ALPHA, " KnownAlpha");
      toStringFlag(sb, ITechRgbImage.FLAG_01_REGION, " Region");
      toStringFlag(sb, ITechRgbImage.FLAG_02_ROOT_LINK, " Root");
      toStringFlag(sb, ITechRgbImage.FLAG_03_LINKING, " Link");
      toStringFlag(sb, ITechRgbImage.FLAG_05_IGNORE_ALPHA, " NoAlpha");
      toStringFlag(sb, ITechRgbImage.FLAG_06_WRITE_LOCK, " WriteLock");
      toStringFlag(sb, ITechRgbImage.FLAG_07_READ_LOCK, " ReadLock");
      toStringFlag(sb, ITechRgbImage.FLAG_16_VIRGIN, " Virgin");
      sb.nl();
      if (sourceLocator != null) {
         sb.append(" ");
         sb.append(sourceLocator);
      } else {
         sb.append("sourceLocator = null");
      }

      sb.nlLvl("", graphicsX);
      return sb.toString();
   }

   //#enddebug

   public UCtx toStringGetUCtx() {
      return drc.getUCtx();
   }
}
