/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.ctx.UCtx;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.IDLog;
import pasa.cbentley.core.src4.logging.ITechLvl;
import pasa.cbentley.core.src4.memory.IMemFreeable;
import pasa.cbentley.core.src4.memory.IMemory;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.framework.coredraw.src4.interfaces.IGraphics;
import pasa.cbentley.framework.coredraw.src4.interfaces.IImage;
import pasa.cbentley.framework.coredraw.src4.interfaces.IImageFactory;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.ITechCtxSettingsDrwx;
import pasa.cbentley.framework.drawx.src4.interfaces.IRgbLoader;
import pasa.cbentley.framework.drawx.src4.tech.ITechRgbImage;

/**
 * Background class used for creating {@link RgbImage}s. 
 * 
 * The Programmer does not use it directly. <br>
 * The goal is for the programmer to tag an Image as cachable, because he knows
 * the exact same image dimension is reused constantly in a short period of
 * time. The overhead is that the programmer must manually dispose of the image.
 * <br>
 * 
 * <br>
 * <br>
 * By default, an image is controlled by the JVM garbage collector. This has
 * drawbacks and advantages of both systems: <br>
 * <b>JVM garbage collector</b> <br>
 * <li><font color=#0C8F00 size=3><b>+</b></font> Image data is kept in memory
 * as long as the live referenced
 * <li><font color=#0C8F00 size=3><b>+</b></font> Free references are garbaged
 * collector.
 * <li><font color=#FF0C00 size=3><b>-</b></font> No control on memory reuse.
 * <br>
 * <br>
 * <b>RgbCache</b> <br>
 * <li><font color=#0C8F00 size=3><b>+</b></font> RgbImage acts like a soft
 * reference.Image data can be garbaged and later retrived with changing the
 * reference.
 * <li><font color=#FF0C00 size=3><b>-</b></font> Memory leak. Otherwise seemgly
 * free references on the surface are pinned into memory. They must be manually
 * tagged as disposable. <br>
 * Simple Cache with one on one relationship between a memory array and a
 * RgbImage <br>
 * The cache keeps in memory all the RgbImages until a Soft Garbage call is
 * made. <br>
 * When a RgbImage is disposed, the memory area associated with it becomes
 * elligible for reuse or soft garbage collection. <br>
 * The soft garbage collection process is initiated by the MemKing when a memory
 * area creation fails. <br>
 * <br>
 * A memory area may grow <br>
 * <br>
 * <b>Animations</b><br>
 * 
 * Animation takes the RgbImage. Its mode {@link GraphicsX#MODE_1_IMAGE} Rgb
 * Mode to : <br>
 * <li><b>true</b> if rgb pixel buffer manipulation is needed (e.g. for
 * AlphaChange).
 * <li><b>false</b> because drawing an Image is faster for simple Move
 * animations. <br>
 * <br>
 * AlphaTrail will use a ImmutableCache and a MutableCache used by all
 * AlphaChange animations <br>
 * <br>
 * <b>Background on JavaSemantics</b> <br>
 * <br>
 * <b>Soft Reference</b> : <br>
 * An object is softly reachable if it is not strongly reachable and there is a
 * path to it with no weak or phantom references but one or more soft
 * references. <br>
 * The garbage collector might or might not reclaim a softly reachable object
 * depending on how recently the object was created or accessed but is required
 * to clear all soft references before throwing an OutOfMemoryError. <br>
 * If heap memory is running low the garbage collector may at its own discretion
 * find softly reachable objects that have not been accessed in the longest time
 * and clear them (set their reference field to null). <br>
 * <b>Weak Reference</b>: <br>
 * An object is weakly reachable when the garbage collector finds no strong or
 * soft references but at least one path to the object with a weak reference.
 * <br>
 * Weakly reachable objects are finalized some time after their weak references
 * have been cleared. <br>
 * The only real difference between a soft reference and a weak reference is
 * that the garbage collector uses algorithms to decide whether or not to
 * reclaim a softly reachable object but always reclaims a weakly reachable
 * object.
 * 
 * @author Charles-Philip Bentley
 *
 */
public class RgbCache implements IMemFreeable {
   /**
    * Use of the {@link ITechRgbImage#FLAG_09_USED} to know which Image may be
    * garbaged collected. This require strict programming to avoid memory
    * leaks.
    */
   public static final int TYPE_FULL   = 2;

   /**
    * No cache is done at all. RgbCache class simply creates. No caching
    * whatsoever.
    */
   public static final int TYPE_NONE   = 0;

   /**
    * Image with source enabled reloads can have their data array released from
    * memory by the {@link MemKing} memory management. <br>
    * Those image are centralized in {@link RgbCache#rgbImages} array cache.
    */
   public static final int TYPE_SOURCE = 1;

   public static final String debugType(int type) {
      switch (type) {
         case TYPE_NONE:
            return "None";
         case TYPE_SOURCE:
            return "Source";
         case TYPE_FULL:
            return "Full";
         default:
            throw new IllegalArgumentException();
      }
   }

   /**
    * Tracks the RgbImage index for that memory area. <br>
    * When value is set to -1, that means the memory area is not used by any
    * RgbImage. <br>
    * Regions are not indexed. <br>
    * Used when Cache is enabled.
    */
   private int[]         areaToRgb     = new int[4];

   /**
   * 
   */
   private int           cacheType     = 0;

   private final DrwCtx  drc;

   /**
    * Tracks if the memory area is filled with a single identical value/color
    * When an image is created
    */
   private boolean[]     fillingColors = new boolean[0];

   /**
    * Matches String with RgbImages. <br>
    * Enables images caching with the client config setting flag. <br>
    * 
    * Null if caching is disabled
    * 
    */
   protected Hashtable   imagesByName;

   /**
    * 1 to 1 relationship to rgbImage : memoryArea[rgbImageID] = int[] array of
    * RgbImage. <br>
    * How does the Cache know if a memory area is used? <br>
    * It reads RgbImage using {@link RgbCache#areaToRgb} linker and check Flags
    * <br>
    * Used when Cache is enabled.
    */
   private int[][]       memoryAreas   = new int[4][];

   /**
    * The null image is a special image with zero width and heigth.
    */
   public final RgbImage NULL_IMAGE;

   /**
    * No null instance. Once created, a RgbImage is diposed with flag
    * {@link ITechRgbImage#FLAG_12_DISPOSED} Instance are reused
    */
   private RgbImage[]    rgbImages     = new RgbImage[0];

   private int           statCacheFound;

   private int           statCacheMiss;

   private int           statConvertPrimitive;

   private int           statConvertRgb;

   private int           statImageCreation;

   private DrwCtx        dc;

   /**
    * Constructor creates non negotiable objects.
    * 
    * {@link RgbCache#applySettings()} objects whose existence depends on the code ctx settings.
    * 
    * @param drc
    */
   public RgbCache(DrwCtx drc) {
      if(drc == null) {
         throw new NullPointerException();
      }
      this.drc = drc;
      NULL_IMAGE = new RgbImage(drc, this, new int[0], 0, 0, 0);
      NULL_IMAGE.setFlag(ITechRgbImage.FLAG_22_NOT_DRAWABLE, true);
   }

   /**
    * 
    */
   public void applySettings(ByteObject settingsNew) {
      if (settingsNew.hasFlag(ITechCtxSettingsDrwx.CTX_DRW_OFFSET_01_FLAG1, ITechCtxSettingsDrwx.CTX_DRW_FLAG_1_USER_IMAGE_CACHE)) {
         imagesByName = new Hashtable();
      } else {
         imagesByName = null;
      }
   }

   public DrwCtx getDC() {
      return dc;
   }

   private void addImgToCache(String str, RgbImage rgbImage) {
      if (imagesByName != null) {
         imagesByName.put(str, rgbImage);
      }
   }

   public void cacheRemove(String name) {
      if (imagesByName != null) {
         imagesByName.remove(name);
      }
   }

   /**
    * Sets the color in all the array
    * 
    * @param array
    * @param color
    */
   private void cleanArray(int[] array, int color) {
      int len = array.length;
      for (int i = 0; i < len; i++) {
         array[i] = color;
      }
   }

   /**
    * Clone the Image on the pixel data, datasource, signatures <br>
    * <br>
    * When cloning an immutable image {@link ITechRgbImage#FLAG_04_IMMUTABLE},
    * opaque with bg color, then use
    * 
    * @param rgbImage
    * @return
    */
   public RgbImage cloneImg(RgbImage rgbImage) {
      RgbImage clone = null;
      if (rgbImage.isRgb()) {
         clone = createImage(rgbImage.getWidth(), rgbImage.getHeight(), 0);
         int srcOffset = rgbImage.getOffset();
         int[] srcData = rgbImage.getRgbData();
         System.arraycopy(srcData, srcOffset, clone.rgbData, 0, rgbImage.getLength());
      } else {
         //
         clone = fetchFreeRgbImage();
         IImage src = rgbImage.img;
         if (src == null) {
            if (!rgbImage.hasFlag(ITechRgbImage.FLAG_20_RELOADABLE)) {
               // throw new RuntimeException("Implemention miss");
            }
            rgbImage.reload();
            src = rgbImage.img;
         }
         IImage cli = drc.getImageFactory().createImage(rgbImage.img);
         clone.img = cli;
      }
      clone.flags = rgbImage.flags;
      clone.signature = rgbImage.signature;
      clone.sourceLocator = rgbImage.sourceLocator;
      clone.width = rgbImage.width;
      clone.height = rgbImage.height;
      clone.setFlag(ITechRgbImage.FLAG_11_CLONED, true);
      return clone;
   }

   /**
    * Converts the RgbImage from an int[] array representation to an Image
    * representation. <br>
    * The Image is mutable. A pseudo trans color When converting a Region to
    * primitive mode, it means falling back on the parent image If the parent
    * image is in primitive mode, If the parent image is in rgb mode, the image
    * is modded and is no more a region
    * 
    * @param rgbImage
    */
   void convertToPrimitve(RgbImage rgbImage) {
      statConvertPrimitive++;
      if (rgbImage.rgbData == null) {
         throw new IllegalArgumentException("null rgbdata. impossible to convert to primitive mode");
      }

      if (rgbImage.isRegion()) {
         if (rgbImage.parentImage.isRgb()) {

         } else {

         }
      }
      // creates immutable image
      IImage img = rgbImage.getImage(false);
      rgbImage.img = img;
      rgbImage.rgbData = null;
      if (cacheType == TYPE_FULL && rgbImage.cacheIntID != -1) {
         areaToRgb[rgbImage.cacheIntID] = -1;
      }
      rgbImage.cacheIntID = -1;
      rgbImage.setFlag(ITechRgbImage.FLAG_13_RGB, false);

   }

   /**
    * Transform {@link IImage} of {@link RgbImage} into an int rgb array. <br>
    * The {@link RgbImage} is flushed.
    * 
    * @param rgbImage
    * @param img
    */
   void convertToRbg(RgbImage rgbImage) {
      if (rgbImage == NULL_IMAGE) {
         return;
      }
      if (rgbImage.img == null) {
         throw new IllegalArgumentException("null Image");
      }
      statConvertRgb++;
      IImage img = rgbImage.img;
      int len = img.getWidth() * img.getHeight();
      int[] imgData = null;
      if (cacheType == TYPE_FULL) {
         int id = findMemoryAreaIndex(len);
         imgData = memoryAreas[id];
         rgbImage.cacheIntID = id;
         areaToRgb[id] = rgbImage.cacheRgbIndex;
      } else {
         imgData = drc.getMem().createIntArray(len);
      }
      img.getRGB(imgData, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
      rgbImage.setFlag(ITechRgbImage.FLAG_13_RGB, true);
      rgbImage.rgbData = imgData; // set the data
      rgbImage.img = null; // null the reference for it to be GCed
   }

   /**
    * Creates a {@link RgbImage} in mode rgb with fully transparent blacks
    * pixel (0s)
    * 
    * @param w
    * @param h
    * @return
    * @throws IllegalArgumentException
    *             when w or h is <= 0
    */
   public RgbImage create(int w, int h) {
      return create(w, h, 0, true);
   }

   /**
    * Create Rgb Image filled with the given color. <br>
    * <br>
    * Transparency is supported. <br>
    * <br>
    * But code chooses the most adequate RgbImage mode. If the color is fully
    * opaque the RgbImage will be in Primitive Mode. If the color is
    * transparent the RgbImage will be in RGB Mode. <br>
    * <br>
    * 
    * @param w
    * @param h
    * @param color
    *            when color has a non opaque alpha channel, {@link RgbImage}
    *            will be in RGB mode.
    * @return
    */
   public RgbImage create(int w, int h, int color) {
      if (((color >> 24) & 0xFF) == 255) {
         return createPrimitiveRgb(w, h, color);
      } else {
         return createRGB(w, h, color);
      }
   }

   /**
    * Create Rgb Image filled with the given color. <br>
    * When rgb mode is false, the transparency component is ignored. <br>
    * 
    * @param w
    * @param h
    * @param color
    * @param rgb
    * @return
    */
   public RgbImage create(int w, int h, int color, boolean rgb) {
      if (rgb) {
         return createRGB(w, h, color);
      } else {
         return createPrimitiveRgb(w, h, color);
      }
   }

   public RgbImage create(RgbImage img) {
      return img.cloneImg();
   }

   public RgbImage create(String locator) {
      return createImage(locator);
   }

   public RgbImage createImage(byte[] data, String url) {
      RgbImage image = getCachedImage(url);
      if (image == null) {
         IImage img = drc.getImageFactory().createImage(data, 0, data.length);
         image = createImage(img, url);
      }
      return image;
   }

   /**
    * Create an {@link RgbImage} based on a {@link IImage}. A non null locator
    * allows the {@link RgbCache} to reload the image if purged from the cache.
    * <br>
    * 
    * @param img
    * @param locator
    * @return
    */
   public RgbImage createImage(IImage img, String url) {
      RgbImage image = getCachedImage(url);
      if (image == null) {
         image = new RgbImage(drc, this, img);
         image.sourceLocator = url;
         if (imagesByName != null) {
            imagesByName.put(url, image);
         }
      }
      return image;
   }

   /**
    * Create a Rgb Image (Mutable) Method looks for a free memory area. If none
    * is found, it creates a new one
    * 
    * @param w
    *            > 0
    * @param h
    *            > 0
    * @return The content of int array is fully transparent black
    */
   public RgbImage createImage(int w, int h) {
      return createImage(w, h, 0);
   }

   /**
    * Creates a RGB image with the given background color
    * 
    * @param w
    * @param h
    * @param color
    * @return
    * @throws IllegalArgumentException
    *             when w or h is smaller or equal to zero
    */
   public RgbImage createImage(int w, int h, int color) {

      if (w <= 0 || h <= 0) {
         throw new IllegalArgumentException("w=" + w + " h= " + h);
      }
      int len = w * h;
      // fetch a disposed RgbImage shell. RgbImage flags are set
      RgbImage ri = fetchFreeRgbImage();
      int[] ar = null;
      if (cacheType == TYPE_FULL) {
         // find a free memory area. may call GC
         int id = findMemoryAreaIndex(len, color);
         ar = memoryAreas[id];
         areaToRgb[id] = ri.cacheRgbIndex;
         ri.cacheIntID = id;
      } else {
         IMemory mem = drc.getMem();
         ar = mem.createIntArray(len);
      }
      if (color != 0) {
         cleanArray(ar, color);
      }
      ri.reset(ar, 0, w, h);
      ri.setFlag(ITechRgbImage.FLAG_13_RGB, true);
      if (((color >> 24) & 0xFF) == 255) {
         // fully opaque background
         ri.setFlag(ITechRgbImage.FLAG_15_KNOWN_ALPHA, true);
         ri.setFlag(ITechRgbImage.FLAG_05_IGNORE_ALPHA, true);
      }
      ri.backgroundColor = color;
      return ri;
   }

   /**
    * Create a new image from the data provided
    * 
    * @param data
    *            the int array is not in the cache? TODO?
    * @param w
    * @param h
    * @return
    */
   public RgbImage createImage(int[] data, int w, int h) {
      if (w <= 0 || h <= 0) {
         throw new IllegalArgumentException("w=" + w + " h=" + h);
      }
      if (data.length < w * h) {
         throw new IllegalArgumentException("Bad Length");
      }
      RgbImage ri = new RgbImage(drc, this, data, 0, w, h);
      ri.setFlag(ITechRgbImage.FLAG_09_USED, true);

      return ri;
   }

   /**
    * TODO
    * 
    * @param loader
    * @param load
    * @return
    */
   public RgbImage createImage(IRgbLoader loader, boolean load) {
      String locator = loader.getLoaderStrID();
      RgbImage image = getCachedImage(locator);
      if (image == null) {
         // not found create one
         RgbImage ri = fetchFreeRgbImage();
         if (load) {
            ri = loader.loadImage();
         }
         if (imagesByName != null) {
            imagesByName.put(locator, ri);
         }
         ri.setFlag(ITechRgbImage.FLAG_20_RELOADABLE, true);
         ri.loader = loader;
         ri.sourceLocator = locator;
         return ri;
      } else {
         return image;
      }

   }

   public RgbImage createImage(String locator) {
      return createImage(locator, true);
   }

   /**
    * Creates a {@link RgbImage} with a String locator. <br>
    * First looks in the cache if it hasn't already been loaded in the cache
    * memory. <br. Images with a string locator are be modified. They must be
    * cloned mutable and then modified. <br>
    * 
    * The images with a locator don't need a the USED flag because they can
    * reload themselves
    * 
    * @param locator
    *            String to physical image or path to a ByteObject figure: in
    *            memory, in jar or in rms Starts with /$m/{repository_id} for a
    *            memory adress. /$f/{path_to_file} .
    *            /$r/{STORE_NAME}/{STORE_ID}
    * @return
    */
   public RgbImage createImage(String locator, boolean load) {
      RgbImage image = getCachedImage(locator);
      if (image == null) {
         // not found create one
         RgbImage ri = fetchFreeRgbImage();
         if (load) {
            IImage img = loadImageI(locator);
            ri.setFlag(ITechRgbImage.FLAG_16_VIRGIN, false);
            ri.img = img;
            ri.sourceLocator = locator;
            ri.width = img.getWidth();
            ri.height = img.getHeight();
         }
         if (imagesByName != null) {
            imagesByName.put(locator, ri);
         }
         return ri;
      } else {
         return image;
      }
   }

   public RgbImage createImage(String locator, int w, int h) {
      // TODO
      return null;
   }

   int[] createImageArray(int len) {
      if (cacheType == TYPE_FULL) {
         int id = findMemoryAreaIndex(len);
         areaToRgb[id] = -1;
         return memoryAreas[id];
      } else {
         return drc.getMem().createIntArray(len);
      }

   }

   /**
    * Looks up
    * 
    * @param sig
    * @param transform
    * @return null if no image signed with the signature have been found
    */
   public RgbImage createImageLink(int sig, int transform) {
      int rootID = -1;
      for (int i = 0; i < rgbImages.length; i++) {
         RgbImage check = rgbImages[i];
         // sig match and this is the root
         if (check.signature == sig) {
            if (check.hasFlag(ITechRgbImage.FLAG_03_LINKING)) {
               if (check.transform == transform)
                  return check;
            } else {
               rootID = i;
            }
         }

      }
      if (rootID != -1) {
         return createImageLink(rgbImages[rootID], transform);
      } else {
         return null;
      }
   }

   /**
    * Create an image that is linked by transformation to other RgbImage
    * instances. So when you create a Triangle with an RgbFilter, the Figure
    * user may decide to load it as Print. So when next figure when to draw the
    * same figure, it just reuses the RgbImage instance and possibly apply a
    * transform
    * 
    * @param w
    * @param h
    * @param sig
    *            figure id + filter sig
    * @param transform
    * @return
    */
   public RgbImage createImageLink(int w, int h, int sig) {
      RgbImage img = createImage(w, h);
      img.signature = sig;
      return img;
   }

   /**
    * Looks up a match <br>
    * <br>
    * This method cache a figure DrwParam The goal is to save drawing of
    * similar figures The Figure returns the transform. None if transform is
    * not a possibility <br>
    * For example in the skewbox design, an Image is used and then transformed.
    * <br>
    * The Image signature is a mean to retrieve an Image symbol. for example we
    * wante the scroll button <br>
    * Is'nt it easier to just cache it using a name locator instead of a sig
    * ID! such #baseScrollButton. Then a ImageLink is created for BOTTOM, LEFT
    * AND RIGHT which is a transformation but only if w and h matches.
    * 
    * @param w
    * @param h
    * @param sig
    *            hashcode of the image
    * @return Caller expect the root image data to be present. What is this
    *         data is not loaded? Is caller able to create the image from
    *         scratch?
    */
   public RgbImage createImageLink(int w, int h, int sig, int transform) {
      for (int i = 0; i < rgbImages.length; i++) {
         RgbImage check = rgbImages[i];
         if (check.width == w && check.height == h) {
            if (check.signature == sig) {
               return createImageLink(check, transform);
            }
         }
      }
      // throw an exception
      throw new IllegalArgumentException("Not implemented yet");
   }

   /**
    * Create an image that is linked by transformation to another RgbImage
    * instance.
    * 
    * @param rgb
    *            This image will be Locked with a Link
    */
   public RgbImage createImageLink(RgbImage rgb, int trans) {
      RgbImage img = getImageLink(rgb.signature, trans);
      if (img == null) {
         img = fetchFreeRgbImage();
         img.linkImage = rgb;
         img.rgbData = rgb.rgbData;
         img.signature = rgb.signature;
         img.transform = trans;
         img.setFlag(ITechRgbImage.FLAG_03_LINKING, true);
      }
      return img;
   }

   /**
    * Does not load the image yet
    * 
    * @param locator
    * @return
    */
   public RgbImage createImageRef(String locator) {
      return createImage(locator, false);
   }

   public RgbImage[] createImages(int w, int h, int num) {
      RgbImage[] ar = new RgbImage[num];
      for (int i = 0; i < ar.length; i++) {
         ar[i] = createImage(w, h);
      }
      return ar;
   }

   /**
    * Same as {@link RgbCache#create(int, int)} but return Null Image
    * 
    * @param w
    * @param h
    * @return
    */
   public RgbImage createNonNull(int w, int h) {
      return createNonNull(w, h, ColorUtils.FULLY_TRANSPARENT_BLACK);
   }

   /**
    * When color is fully opaque, create 
    * @param w
    * @param h
    * @param color
    * @return {@link RgbCache#NULL_IMAGE} when w or h is 0 or negative
    * 
    * @see RgbCache#create(int, int, int)
    */
   public RgbImage createNonNull(int w, int h, int color) {
      if (w <= 0 || h <= 0) {
         return NULL_IMAGE;
      }
      return create(w, h, color);
   }

   /**
    * A Non null image
    * 
    * @param w
    * @param h
    * @param color
    * @return
    */
   public IImage createPrimitiveColor(int w, int h, int color) {
      IImage img = drc.getImageFactory().createImage(w, h, color);
      if (img == null) {
         throw new NullPointerException("Null for width height " + w + " " + h + " Color=" + color);
      }
      return img;
   }

   public IImage createPrimitiveImmutable(int[] data, int w, int h, boolean processAlpha) {
      if (w <= 0 || h <= 0) {
         throw new IllegalArgumentException("w=" + w + " h=" + h);
      }
      IImage ri = drc.getImageFactory().createRGBImage(data, w, h, processAlpha);
      return ri;
   }

   /**
    * Creates non RGB mutable image with the color is located
    * 
    * @param w
    * @param h
    * @param color
    * @return
    */
   public RgbImage createPrimitiveRgb(int w, int h, int color) {
      if (w <= 0 || h <= 0) {
         throw new IllegalArgumentException("w=" + w + " h=" + h);
      }
      RgbImage ri = fetchFreeRgbImage();
      ri.img = createPrimitiveWhite(w, h);
      IGraphics g = ri.img.getGraphics();
      g.setColor(color);
      g.fillRect(0, 0, w, h);

      ri.backgroundColor = color;
      ri.width = w;
      ri.height = h;

      ri.setFlag(ITechRgbImage.FLAG_13_RGB, false);
      ri.setFlag(ITechRgbImage.FLAG_15_KNOWN_ALPHA, true);
      ri.setFlag(ITechRgbImage.FLAG_05_IGNORE_ALPHA, true);

      return ri;
   }

   /**
    * Simple creates a mutable white image with no strings attached
    * 
    * @param width
    * @param height
    * @return
    */
   public IImage createPrimitiveWhite(int width, int height) {
      IImageFactory imageFactory = drc.getImageFactory();
      return imageFactory.createImage(width, height);
   }

   public RgbImage createPrimviteRgbImmutable(int[] data, int w, int h, boolean processAlpha) {
      if (w <= 0 || h <= 0) {
         throw new IllegalArgumentException("w=" + w + " h=" + h);
      }
      IImage ri = drc.getImageFactory().createRGBImage(data, w, h, processAlpha);
      RgbImage img = new RgbImage(drc, this, ri);
      return img;
   }

   /**
    * Return the rgb array of the region of {@link RgbImage} that interesect with
    * with RgbImage
    * @param rgbImage
    * @param m
    * @param n
    * @param w
    * @param h
    * @return
    */
   public int[] getRegion(RgbImage rgbImage, int m, int n, int w, int h) {
      if (w <= 0 || h <= 0) {
         throw new IllegalArgumentException("w=" + w + " h=" + h);
      }
      RgbImage r = fetchFreeRgbImage();
      if (rgbImage.isRgb()) {
         r.rgbData = rgbImage.rgbData;
         r.cacheIntID = rgbImage.cacheIntID;
         r.offset = rgbImage.getOffset();
      } else {
         r.img = rgbImage.img;
      }
      r.setFlag(ITechRgbImage.FLAG_16_VIRGIN, false);
      r.setFlag(ITechRgbImage.FLAG_01_REGION, true);
      r.setFlag(ITechRgbImage.FLAG_05_IGNORE_ALPHA, rgbImage.hasFlag(ITechRgbImage.FLAG_05_IGNORE_ALPHA));
      r.setFlag(ITechRgbImage.FLAG_13_RGB, rgbImage.hasFlag(ITechRgbImage.FLAG_13_RGB));
      r.width = w;
      r.height = h;
      r.sourceLocator = rgbImage.sourceLocator;
      r.parentImage = rgbImage;
      r.cacheIntID = rgbImage.cacheIntID;
      r.transform = rgbImage.transform;
      r.m = rgbImage.getM() + m;
      r.n = rgbImage.getN() + n;
      int[] rdata = rgbImage.getRgbData();
      if (m < 0)
         m = 0;
      if (n < 0)
         n = 0;
      int nw = w;
      int nh = h;
      int imgW = rgbImage.getWidth();
      if (m + nw > imgW) {
         nw = imgW - m;
      }
      if (n + nh > rgbImage.getHeight()) {
         nw = rgbImage.getHeight() - n;
      }
      int[] rgb = new int[nw * nh];
      int count = 0;
      for (int i = 0; i < nh; i++) {
         int index = m + imgW * i;
         for (int j = 0; j < nw; j++) {
            rgb[count] = rdata[index];
            index++;
            count++;
         }
      }
      return rgb;
   }
   
   /**
    * 
    * @param rgbImage
    * @param m
    *            x coordinate inside the image
    * @param n
    * @param w
    *            width of the region
    * @param h
    * @return
    */
   public RgbImage createRegion(RgbImage rgbImage, int m, int n, int w, int h) {
      if (w <= 0 || h <= 0) {
         throw new IllegalArgumentException("w=" + w + " h=" + h);
      }
      RgbImage r = fetchFreeRgbImage();
      if (rgbImage.isRgb()) {
         r.rgbData = rgbImage.rgbData;
         r.cacheIntID = rgbImage.cacheIntID;
         r.offset = rgbImage.getOffset();
      } else {
         r.img = rgbImage.img;
      }
      r.setFlag(ITechRgbImage.FLAG_16_VIRGIN, false);
      r.setFlag(ITechRgbImage.FLAG_01_REGION, true);
      r.setFlag(ITechRgbImage.FLAG_05_IGNORE_ALPHA, rgbImage.hasFlag(ITechRgbImage.FLAG_05_IGNORE_ALPHA));
      r.setFlag(ITechRgbImage.FLAG_13_RGB, rgbImage.hasFlag(ITechRgbImage.FLAG_13_RGB));
      r.width = w;
      r.height = h;
      r.sourceLocator = rgbImage.sourceLocator;
      r.parentImage = rgbImage;
      r.cacheIntID = rgbImage.cacheIntID;
      r.transform = rgbImage.transform;
      r.m = rgbImage.getM() + m;
      r.n = rgbImage.getN() + n;
      int[] rdata = rgbImage.getRgbData();
      if (m < 0)
         m = 0;
      if (n < 0)
         n = 0;
      int nw = w;
      int nh = h;
      int imgW = rgbImage.getWidth();
      if (m + nw > imgW) {
         nw = imgW - m;
      }
      if (n + nh > rgbImage.getHeight()) {
         nw = rgbImage.getHeight() - n;
      }
      int[] rgb = new int[nw * nh];
      int count = 0;
      for (int i = 0; i < nh; i++) {
         //TODO fix and test
         int index = m + imgW * i;
         for (int j = 0; j < nw; j++) {
            rgb[count] = rdata[index];
            index++;
            count++;
         }
      }
      return createImage(rgb, nw, nh);
   }

   /**
    * Create an image whose source is a DrwParam. If the figure is in the
    * repository. clone it out of the repository
    * 
    * @param w
    * @param h
    * @param color
    * @param fig
    * @return
    */
   public RgbImage createRGB(GraphicsX g, int w, int h, int color, ByteObject fig) {
      boolean isWhiteOpaque = false;
      RgbImage ri = drc.getFigureOperator().getFigImage(fig, w, h, false, isWhiteOpaque, 0);
      ri.source = fig;
      return ri;
   }

   public RgbImage createRGB(int w, int h, int color) {
      return createImage(w, h, color);
   }

   public boolean deleteImageFromCache(String name) {
      if (imagesByName != null) {
         IImage img = (IImage) imagesByName.remove(name);
         if (img != null) {
            return true;
         }
      }
      return false;
   }

   /**
    * When an Image is disposed, all fields are reset. <br>
    * The only exception is the rgbImageID which links to the Cache main array
    * <br>
    * This call has no effect if cache has been disable globally or on the
    * {@link RgbImage}.
    * 
    * @param rgbImage
    */
   public void dispose(RgbImage rgbImage) {
      if (cacheType != TYPE_FULL) {
         return;
      }
      if (rgbImage.cacheIntID >= areaToRgb.length) {
         throw new IllegalArgumentException(rgbImage.cacheIntID + ">=" + areaToRgb.length);
      }
      if (!rgbImage.isRegion()) {
         if (rgbImage.cacheIntID != -1) {
            areaToRgb[rgbImage.cacheIntID] = -1;
         }
      }
      rgbImage.releaseFreeMemory();
      if (rgbImage.sourceLocator != null) {

      } else {
         rgbImage.cacheIntID = -1;
         // rgbImage.cacheRgbIndex = -1;
         rgbImage.sourceLocator = null;
         rgbImage.transform = 0;
         rgbImage.flags = 0;
         rgbImage.width = 0;
         rgbImage.height = 0;
         rgbImage.m = 0;
         rgbImage.n = 0;
         rgbImage.graphicsX = null;
         rgbImage.setFlag(ITechRgbImage.FLAG_12_DISPOSED, true);
      }
   }

   /**
    * Looks for an instance of {@link RgbImage} that is disposed. <br>
    * <br>
    * If none is found, create a new one. <br>
    * <li>{@link ITechRgbImage#FLAG_12_DISPOSED} false
    * <li>{@link ITechRgbImage#FLAG_16_VIRGIN} true
    * <li>{@link ITechRgbImage#FLAG_09_USED} true <br>
    * <br>
    * 
    * @param id
    *            The Cache ID to use
    * @return
    */
   private RgbImage fetchFreeRgbImage() {
      statImageCreation++;
      if (cacheType == TYPE_NONE) {
         return new RgbImage(drc, this);
      }
      RgbImage img = null;
      for (int i = 0; i < rgbImages.length; i++) {
         if (rgbImages[i].hasFlag(ITechRgbImage.FLAG_12_DISPOSED)) {
            img = rgbImages[i];
            break;
         }
      }
      if (img == null) {
         // else create a new one
         RgbImage[] old = rgbImages;
         rgbImages = new RgbImage[old.length + 1];
         for (int i = 0; i < old.length; i++) {
            rgbImages[i] = old[i];
         }
         rgbImages[old.length] = new RgbImage(drc, this);
         rgbImages[old.length].cacheRgbIndex = old.length;
         img = rgbImages[old.length];
      }
      img.setFlag(ITechRgbImage.FLAG_12_DISPOSED, false);
      img.setFlag(ITechRgbImage.FLAG_16_VIRGIN, true);
      img.setFlag(ITechRgbImage.FLAG_09_USED, true);
      return img;
   }

   public int findMemoryAreaIndex(int len) {
      return findMemoryAreaIndex(len, 0);
   }


   /**
    * Look for an unused memory area with at least len of capacity
    * 
    * @param len
    *            The number of pixels needed.
    * @param color
    *            memory area is filled with that color
    * @return The index of the memory area where it is possible to store len
    *         pixels.
    */
   public int findMemoryAreaIndex(int len, int color) {
      int nullIndex = -1;
      for (int i = 0; i < memoryAreas.length; i++) {
         if (memoryAreas[i] != null) {
            if (areaToRgb[i] == -1) {
               if (memoryAreas[i].length >= len) {
                  statCacheFound++;
                  return i;
               }
            }
         } else {
            nullIndex = i;
         }
      }
      statCacheMiss++;
      // this code may call freeMemory; make sure it does not interfere with
      // state of Cache.
      int[] data = drc.getMem().createIntArray(len);
      int id = 0;
      // when no null areas
      if (nullIndex == -1) {
         // we could not find a free memory area with enough capacity.
         memoryAreas = drc.getMem().increaseCapacityNonEmpty(memoryAreas, 1, 0);
         areaToRgb = drc.getMem().increaseCapacity(areaToRgb, 1);
         id = memoryAreas.length - 1;
      } else {
         id = nullIndex;
      }
      //#debug
      //toDLog().printDraw("#RgbCache#findMemoryAreaIndex " + id + " " + nullIndex);
      memoryAreas[id] = data;
      return id;
   }

   /**
    * Method sets to null int[] array references for memory areas flagged as
    * not used Image with a source path are freed
    */
   public void freeMemory() {
      if (imagesByName != null) {
         imagesByName.clear();
      }
      // clear cache memory
      for (int i = 0; i < rgbImages.length; i++) {
         if (rgbImages[i] != null) {
            RgbImage ri = rgbImages[i];
            if (!ri.hasFlag(ITechRgbImage.FLAG_09_USED)) {
               ri.releaseFreeMemory();
               if (ri.cacheIntID != -1)
                  memoryAreas[ri.cacheIntID] = null;
               if (ri.sourceLocator == null) {
                  // no source locator. so dispose it
                  ri.setFlag(ITechRgbImage.FLAG_12_DISPOSED, true);
               }
               ri.cacheIntID = -1;
               ri.cacheRgbIndex = -1;
            }
            // an image with a source locator may always been reclaimed
            // todo if the reference to
            if (ri.sourceLocator != null) {
               ri.releaseFreeMemory();
            }

         }
      }
      for (int i = 0; i < areaToRgb.length; i++) {
         if (areaToRgb[i] == -1) {
            memoryAreas[i] = null;
         }
      }
      //#debug
      //toDLog().printDraw("#RgbCache#freeMemory After:" + this);
   }

   // public void freeMemory(int byteSize) {
   // int yet = 0;
   // for (int i = 0; i < areaToRgb.length; i++) {
   // if (areaToRgb[i] == -1) {
   // if (memoryAreas[i] != null) {
   // yet += memoryAreas[i].length * 4;
   // memoryAreas[i] = null;
   // if (yet >= byteSize) {
   // return;
   // }
   // }
   // }
   // }
   // }
   //
   // public void freeMemoryNotUsed() {
   // for (int i = 0; i < areaToRgb.length; i++) {
   // if (areaToRgb[i] == -1) {
   // if (memoryAreas[i] != null) {
   // memoryAreas[i] = null;
   // }
   // }
   // }
   // }

   /**
    * Free all arrays and set the RgbImages to empty. Used images will have to
    * be recreate when needed from sourceLocator or drawing on Graphics context
    */
   public void freeMemoryAll() {
      for (int i = 0; i < rgbImages.length; i++) {
         if (rgbImages[i] != null) {
            RgbImage ri = rgbImages[i];
            ri.rgbData = null;
            ri.img = null;
            ri.setFlag(ITechRgbImage.FLAG_10_NO_DATA, true);
         }
      }
      for (int i = 0; i < memoryAreas.length; i++) {
         memoryAreas[i] = null;
      }
   }

   /**
    * New array with {@link RgbImage} without the
    * {@link ITechRgbImage#FLAG_12_DISPOSED} flag that are managed by the cache
    * instance.
    * 
    * @return
    */
   public synchronized RgbImage[] getActiveImages() {
      int count = 0;
      for (int i = 0; i < rgbImages.length; i++) {
         if (!rgbImages[i].hasFlag(ITechRgbImage.FLAG_12_DISPOSED)) {
            count++;
         }
      }
      RgbImage[] ar = new RgbImage[count];
      count = 0;
      for (int i = 0; i < rgbImages.length; i++) {
         if (!rgbImages[i].hasFlag(ITechRgbImage.FLAG_12_DISPOSED)) {
            ar[count] = rgbImages[i];
            count++;
         }
      }
      return ar;
   }

   protected RgbImage getCachedImage(String locator) {
      RgbImage image = null;
      if (imagesByName != null) {
         image = (RgbImage) imagesByName.get(locator);
      }
      return image;
   }

   public int getCacheType() {
      return cacheType;
   }

   /**
    * 
    * @param string
    * @return never null. returns {@link RgbCache#NULL_IMAGE} when image is not
    *         found
    */
   public RgbImage getImage(String string) {
      return getImage(string, false);
   }

   /**
    * 
    * @param name
    * @param cache
    *            when true, stores the image data in a cache.
    * @return
    * 
    * 		<br>
    *         To delete image from cache. {@link RgbCache#dele}
    */
   public RgbImage getImage(String name, boolean cache) {
      if (name.charAt(0) != '/') {
         // #debug
         System.out.println("Path must starts with a / for Image " + name);
      }
      if (imagesByName != null) {
         RgbImage image = getCachedImage(name);
         if (image != null) {
            return image;
         }
      }
      RgbImage img = this.loadImage(name);
      if (cache) {
         if (imagesByName != null) {
            imagesByName.put(name, img);
         }
      }
      return img;
   }

   public int[] getImageData(IImage img) {
      return getImageData(img, 0, 0, img.getWidth(), img.getHeight());
   }

   /**
    * @see RgbCache#getImageIntersection(IImage, int, int, int, int) to avoid
    *      those Exceptions
    * @param img
    * @param x
    *            coordinate inside the image. Must be 0
    * @param y
    * @param w
    *            width of the rectangle
    * @param h
    * @return
    * @throws ArrayIndexOutOfBoundsException
    *             if region does not fall into the image
    * @throws IllegalArgumentException
    *             x < 0, y < 0
    */
   public int[] getImageData(IImage img, int x, int y, int w, int h) {
      if (x < 0 || y < 0) {
         throw new IllegalArgumentException();
      }
      if (x + w > img.getWidth() || y + h > img.getHeight()) {
         throw new IllegalArgumentException();
      }
      int rw = w;
      int rh = h;
      int[] imgData = createImageArray(rw * rh);
      img.getRGB(imgData, 0, rw, x, y, rw, rh);
      return imgData;
   }

   /**
    * 
    * @param img
    * @param x
    * @param y
    * @param w
    * @param h
    * @return
    */
   public int[] getImageIntersection(IImage img, int x, int y, int w, int h) {
      throw new RuntimeException();
   }

   /**
    * Return an Image that is the transformation of RgbImage with the given
    * signature. <br>
    * 
    * @param sig
    * @param transform
    * @return
    */
   public RgbImage getImageLink(int sig, int transform) {
      for (int i = 0; i < rgbImages.length; i++) {
         RgbImage check = rgbImages[i];
         // sig match and this is the root
         if (check.signature == sig && check.transform == transform) {
            return check;
         }
      }
      return null;
   }

   /**
    * Linear search for array index position Adds array if not in the
    * registered memory areas
    * 
    * @param rgbData
    * @return -1 if not found
    */
   private int getMemoryAreaIndex(int[] rgbData) {
      int cid = -1;
      for (int i = 0; i < memoryAreas.length; i++) {
         if (memoryAreas[i] == rgbData) {
            cid = i;
         }
      }
      return cid;
   }

   /**
    * Grow the image area and position the image data so top pixels separate
    * image from the top, bot pixels seperate image from Update Image getWidth
    * and getHeight
    * 
    * @param top
    * @param bot
    * @param left
    * @param right
    */
   public void grow(RgbImage rgb, int top, int bot, int left, int right) {
      throw new RuntimeException();
   }

   public boolean isImageTransparent(IImage img) {
      int[] d = getImageData(img);
      for (int i = 0; i < d.length; i++) {
         if (((d[i] >> 24) & 0xFF) != 255)
            return true;
      }
      return false;
   }

   /**
    * 
    * @param s
    * @return
    */
   public RgbImage loadImage(String s) {
      InputStream is = null;
      try {
         // TODO understand the job of the preverifier
         // in some cases, the next line line generates an error
         is = RgbCache.class.getResourceAsStream(s);
         if (is != null) {
            IImage i = drc.getImageFactory().createImage(is);
            return new RgbImage(drc, this, i);
         } else {
            // #debug
            System.out.println("BIP:765 Could not open image " + s);
         }
      } catch (Exception e) {
         // #debug
         System.out.println(s + " could not be loaded " + e.getMessage());
         // #debug
         e.printStackTrace();
      } finally {
         if (is != null)
            try {
               is.close();
            } catch (IOException e) {
               // #debug
               e.printStackTrace();
            }
      }

      return NULL_IMAGE;

   }

   /**
    * Architecture dependant.
    * 
    * @param s
    * @return
    */
   private IImage loadImageI(String s) {
      InputStream is = null;
      try {
         // understand the job of the preverifier. it refuses to
         // to make static class reference ImgLib.class
         is = this.getClass().getResourceAsStream(s);
         if (is != null) {
            return drc.getImageFactory().createImage(is);
         } else {
            //#debug
            toDLog().pInit(s + " could not be loaded ", this, RgbCache.class, "loadImageI", ITechLvl.LVL_10_SEVERE, true);
            return null;
         }
      } catch (Exception e) {
         //#debug
         toDLog().pEx(s + " could not be loaded ", this, RgbCache.class, "loadImageI", e);
         return null;
      } finally {
         if (is != null)
            try {
               is.close();
            } catch (IOException e) {
               // #debug
               e.printStackTrace();
            }
      }
   }

   /**
    * Used by the ChangeDimension method
    * 
    * @param rgbImage
    * @param currentLen
    * @param memChange
    */
   public void memChange(RgbImage rgbImage, int currentLen, int memChange) {
      if (memChange > 0) {
         int[] ar = drc.getMem().createIntArray(currentLen + memChange);
         if (cacheType == TYPE_FULL) {
            // increase of memory is needed.
            int areaId = rgbImage.cacheIntID;
            if (memoryAreas[areaId].length < currentLen + memChange) {
               memoryAreas[areaId] = ar;
               rgbImage.rgbData = memoryAreas[areaId];
               // if the image is linked, it will have a reference
            }
         } else {
            rgbImage.rgbData = ar;
         }
      }
   }

   /**
    * The number of bytes consumed by all the cached RgbImages
    * 
    * @return
    */
   public int memConsumed() {
      int total = 0;
      for (int i = 0; i < memoryAreas.length; i++) {
         if (memoryAreas[i] != null) {
            total += memoryAreas[i].length * 4;
         }
      }
      if (imagesByName != null) {
         Enumeration en = imagesByName.elements();
         while (en.hasMoreElements()) {
            RgbImage img = (RgbImage) en.nextElement();
            // only add if image is in Primitive mode
            if (img.img != null) {
               total += (img.getHeight() * img.getWidth() * 4);
            }
         }
      }
      return total;
   }

   /**
    * Reloads the {@link RgbImage} data into the RGB array. Updates width and
    * height? <br>
    * 
    * Special code for reloading regions. Create full image <br>
    * <br>
    * 
    * @param rgbImage
    */
   public void reload(RgbImage rgbImage) {
      if (rgbImage.sourceLocator != null) {
         if (rgbImage.isRegion()) {
            // look for parent image
            RgbImage p = getCachedImage(rgbImage.sourceLocator);
            if (p == null) {
               p = createImage(rgbImage.sourceLocator);
               addImgToCache(rgbImage.sourceLocator, p);
            }
            rgbImage.parentImage = p;
            rgbImage.img = p.getImage();
            rgbImage.cacheIntID = rgbImage.cacheIntID;
         } else {
            rgbImage.img = loadImageI(rgbImage.sourceLocator);
            addImgToCache(rgbImage.sourceLocator, rgbImage);
         }
         rgbImage.setFlag(ITechRgbImage.FLAG_10_NO_DATA, false);
      }
   }

   /**
    * Automatically
    * 
    * @param type
    */
   public void setCacheType(int type) {
      this.cacheType = type;
   }

   //#mdebug

   public String toString() {
      return Dctx.toString(this);
   }

   public IDLog toDLog() {
      return drc.toDLog();
   }

   public void toString(Dctx dc) {
      dc.root(this, "RgbCache");
      dc.append(" MemConsumed=" + (memConsumed() / 1000) + " kb");
      dc.nl();
      dc.append("cacheType=" + debugType(cacheType));
      if (statCacheMiss != 0)
         dc.append(" statCacheMiss=" + statCacheMiss);
      if (statCacheFound != 0)
         dc.append(" statCacheFound=" + statCacheFound);
      if (statConvertPrimitive != 0)
         dc.append(" statConvertPrimitive=" + statConvertPrimitive);
      if (statConvertRgb != 0)
         dc.append(" statConvertRgb=" + statConvertRgb);
      if (statImageCreation != 0)
         dc.append(" statImageCreation=" + statImageCreation);

      dc.nl();
      dc.append("#Memory Areas");
      for (int i = 0; i < memoryAreas.length; i++) {
         dc.nl();
         dc.append("CID = " + i);
         if (memoryAreas[i] != null) {
            dc.append(" Len=" + memoryAreas[i].length);
            dc.append(" areaToRgb=" + areaToRgb[i]);
         } else {
            dc.append(" null");
         }
      }
      dc.nl();
      if (imagesByName != null) {
         dc.append("#RgbImages in Cache :");
         Enumeration en = imagesByName.keys();
         while (en.hasMoreElements()) {
            String k = (String) en.nextElement();
            dc.append(k);
            dc.append(" ");
         }
      } else {
         dc.append("No Cache");
      }
      dc.nl();
      dc.append("#RgbImages");
      for (int i = 0; i < rgbImages.length; i++) {
         dc.append("#" + (i + 1));
         dc.append('\t');
         if (rgbImages[i] == null) {
            dc.append(" null [ERROR]");
         } else {
            rgbImages[i].toString1Line(dc);
         }
         dc.nl();
      }
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, "RgbCache");
   }

   public String toString1Line() {
      return Dctx.toString1Line(this);
   }

   public UCtx toStringGetUCtx() {
      return drc.getUC();
   }

   //#enddebug

}
