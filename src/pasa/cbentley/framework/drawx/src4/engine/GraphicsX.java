/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.engine;

import java.util.Random;
import java.util.Vector;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.objects.color.BlendOp;
import pasa.cbentley.byteobjects.src4.objects.color.IColorSettable;
import pasa.cbentley.byteobjects.src4.objects.color.ITechBlend;
import pasa.cbentley.core.src4.ctx.ToStringStaticUc;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.core.src4.logging.IStringable;
import pasa.cbentley.core.src4.logging.ITechLvl;
import pasa.cbentley.core.src4.structs.IntBuffer;
import pasa.cbentley.core.src4.utils.BitUtils;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.core.src4.utils.Geo2dUtils;
import pasa.cbentley.core.src4.utils.IntUtils;
import pasa.cbentley.core.src4.utils.TransformUtils;
import pasa.cbentley.framework.coredraw.src4.ctx.CoreDrawCtx;
import pasa.cbentley.framework.coredraw.src4.interfaces.IGraphics;
import pasa.cbentley.framework.coredraw.src4.interfaces.IImage;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechFeaturesDraw;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechGraphics;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IFlagsToStringDrw;
import pasa.cbentley.framework.drawx.src4.ctx.ObjectDrw;
import pasa.cbentley.framework.drawx.src4.ctx.ToStringStaticDrawx;
import pasa.cbentley.framework.drawx.src4.factories.FigureOperator;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOAnchor;
import pasa.cbentley.framework.drawx.src4.tech.ITechGraphicsX;
import pasa.cbentley.framework.drawx.src4.tech.ITechRgbImage;
import pasa.cbentley.framework.drawx.src4.tech.ITechStyles;

/**
 * Custom MIDP 3.0 Graphical Layer over the MIDP 2.0 Graphics class. <br>
 * Provide Rgb/Primitive dual layer for drawing into {@link RgbImage}s and screen device.
 * <br>
 * Framework encapsulates {@link IGraphics} into a {@link GraphicsX} when {@link ICanvasAppli#paint(IGraphics)} is called.
 * <br> 
 * <br> 
 * For {@link RgbImage}s, 
 * <li>a mutable {@link IImage} does not need the rgb layer, unless it wants to access buffer data.
 * <li>the rgb array will need top imageLayer for drawing primitives.
 * <br>
 * <br>
 * <b>Services:</b> <br>
 * <li>Enable mutable transparent images <br>
 * <li>Direct access to {@link RgbImage} buffer<br>
 * <li>Time measure. Ticking the milliseconds and primtive count taken for drawing a figure/drawable.
 * <li>Blending
 * <li>Pixel reading
 * <li>One Color region tracking.
 * <li>Postpone {@link ByteObject} drawings.
 * <br>
 * <br>
 * <b>Properties</b> : <br>
 * <li>Backward compatible with {@link IGraphics}.
 * <li>For performance, PseudoTColor is disabled when the whole Graphics have been set with an opaque color. 
 * 
 * <br>
 * 
 * When Cache's drawable has a fully opaque background and then a semi-transparent figure<br>
 * 
 * <br>
 * <b>Duplicates</b> : <br>
 * <li>Because in RGB paint mode the {@link IGraphics} is null, clip is duplicated in this class.
 * <li>Translation x and y
 * <br>
 * <br>
 * <b>Timing and Ticking</b> : <br>
 * {@link GraphicsX#tick()}. <br>
 * A scrollbar bar's top figure worth 50 milliseconds is nice to cache and draw a 180 rotation for the bottom button.
 * So the engine keeps a cache of the normal and selected top figure (grey is not usefull) and register
 * it with a signature. the same signature is used by the bottom figure to fetch the cache. Bottom figure knows the image
 * transformation it needs from root Top. <br>
 * {@link GraphicsX} may be tipped to cache it.
 * <br>
 * <br>
 * <b>One Color Regions</b> : <br>
 * <li>
 * When a rectangular fill is done, record it. This allows efficient figure masking.
 * <li> {@link GraphicsX#fillRect(int, int, int, int)}
 * 
 * Must be explicitely recorded with a flag. No records is done when creating gradients.
 * Drawing an masked figure over a OneColor region, code does not have to use an RgbImage layer and blend it with
 * main layer.
 * <br>
 * <br>
 * <b>How to create a transparent background? </b>
 *   <br>
 *   <code> 
 *   RgbImage img = RgbImage.create(w, h, 0);
 *   GraphicsX gx = img.getGraphicsX(GraphicsX.MODE_2_RGB_IMAGE);
 *      </code>
 * <br>
 * <br>
 * @author Charles-Philip Bentley
 * @see RgbImage
 * @see BlendOp
 * @see IGraphics
 * 
 */
public class GraphicsX extends ObjectDrw implements IStringable, ITechGraphicsX, IColorSettable {

   /**
    * Graphics wide value that applies to all primitive operations.
    */
   int               alpha             = 255;

   private int       bgColor;

   /**
    * Blend operator for images and areas that don't have a pseudo transparent color
    */
   private BlendOp   blendOpImages;

   /**
    * Variable tracking the Buffer image that was create with the method {@link #createBuffer(int, int, boolean)}
    */
   private RgbImage  bufferRegion      = null;

   private RgbCache  cache;

   /**
    * 
    */
   private int       clipDirective;

   /**
    * Minimum height size of Layer and RgbImage
    */
   private int       clipH             = 0;

   private IntBuffer clipHistory;

   /**
    * Index to the last active clip.
    */
   private int       clipIndex;

   /**
    * Gives access to the last clips.
    * <br>
    * Stores translation values at the time the clip was requested and which were computed in clipx and clipy
    * <br>
    * <br>
    */
   private int[]     clipList          = new int[5 * CLIP_INDEX_INCREMENT];

   /**
    * Minimum width size of Layer and RgbImage.
    * <br>
    * <br>
    * Initialized with Canvas width
    */
   private int       clipW             = 0;

   /**
    * Tracks clip x coordinate in destination graphics coordinate system.
    * <br>
    * <br>
    * Value is relative to the source, so it includes translation
    * <br>
    * To get value {@link GraphicsX#getClipX()} returns translated value.
    * <br>
    * When clipping {@link IGraphics}, we set it to the root (0,0).
    * <br>
    * <br>
    *  
    */
   private int       clipX             = 0;

   /**
    * Tracks clip y coordinate
    */
   private int       clipY             = 0;

   private int       excludeColor;

   /**
    * Graphics Object of the Image Layer.
    * <br>
    * <br>
    * 
    * null <=> paintMode is {@link GraphicsX#MODE_3_RGB}
    * or
    * null <=> paintMode is {@link GraphicsX#MODE_4_NULL}
    * 
    */
   IGraphics         g;

   /**
    * Flag telling alpha or alphaColor is relevant
    * <br>
    * Cannot be set to True in Screen PaintMode
    */
   boolean           hasAlpha          = false;

   private int       imageDraws;

   /**
    * May be null. If not null. Graphics object belongs to that Image
    */
   IImage            imageLayer;

   /**
    * The pixel data of our {@link GraphicsX}.
    * <br>
    * <br>
    * Reference is null for modes:
    * <li>{@link GraphicsX#MODE_0_SCREEN}
    * <li>{@link GraphicsX#MODE_1_IMAGE}
    * <br>
    * <br>
    * Also we have  <code>null <=> alpha == false</code>
    * <br>
    * But it is a hollow shell
    * 
    */
   RgbImage          imageRgbData;

   /**
    * Switch for completely turn off alpha support. Bring back MIDP 2.0 behavior
    */
   boolean           isAlphaMode       = false;

   boolean           isFillMode        = true;

   private boolean   isIgnoreClip      = false;

   /**
    * System Flag. This is not a user flag.
    * Automatically set to pseudo mode when RgbImage(background layer) has transparent pixels 
    * If image is filled by construction with opaque pixels, there is no need for a pseudo transparent color.
    * When RgbImage is filled with opaque pixels, the flag will be turned off.
    * When a subsequent SRC blending occurs with alpha value, the flag is turned back on.
    */
   boolean           isPseudoColorMode = false;

   /**
    * Count the number of times this GraphicsX has called {@link GraphicsX#merge()} for merging
    * the top primitive {@link GraphicsX#imageLayer} with the RGB array background layer {@link GraphicsX#imageRgbData}.
    * <br>
    * Never reset.
    */
   int               mergeCount        = 0;

   private int       mycolor;

   /**
    * Counts the number of pixels that were merged
    */
   int               numPixelsMerged   = 0;

   /**
    * Statistic about the number of calls to {@link GraphicsX#drawRGB(int[], int, int, int, int, int, int, boolean)}.
    */
   int               numRGBDrawCalls   = 0;

   /**
    * Communicates Flag.
    * 
    */
   private int       optionsFlags;

   /**
    * <li> {@link GraphicsX#MODE_0_SCREEN}
    * <li> {@link GraphicsX#MODE_1_IMAGE}
    * <li> {@link GraphicsX#MODE_2_RGB_IMAGE}
    * <li> {@link GraphicsX#MODE_3_RGB}
    * 
    */
   private int       paintMode;

   /**
    * Count the number of times some primitives have been drawn on the top {@link GraphicsX#imageLayer}
    * since the last merge.
    */
   int               pOpaqueLayerCount = 0;

   private Vector    postpones;

   /**
    * Counts all the primitive calls during a paint cycle
    */
   int               primitiveTally    = 0;

   /**
    * Tracks the number of times pseudo color switched from White to Cyan and vice versa.
    */
   private int       pseudoSwapCount;

   /**
    * Flags from .
    * Enable flagging of 
    * <li>Painting for screen
    * <li>Painting on a cache
    * <li>Painting for Image
    * <li>Repaint type
    */
   private int       repaintFlags;

   /**
    * Counts the number of RGB method calls
    */
   int               rgbCount          = 0;

   private BlendOp   saved;

   private int       switches;

   private long      tickTime;

   /**
    * Class own translate x component.
    */
   private int       translateX        = 0;

   /**
    * Class own translate y component
    */
   private int       translateY        = 0;

   /**
    * Use this constructor for paint(Graphics g) of MasterCanvas
    * The principle is G is the screen when PseudoColor support is not needed
    * Otherwise, G is the graphics object of a RgbImage.
    * PaintMode used by this constructor is always SCREEN
    * @param g
    */
   public GraphicsX(DrwCtx drc) {
      super(drc);
      this.cache = drc.getCache();
      paintMode = MODE_0_SCREEN;
      aInit();
   }

   GraphicsX(DrwCtx drc, RgbCache rc, RgbImage rgbImg, boolean isNull) {
      super(drc);
      if (isNull) {
         this.cache = rc;
         this.imageRgbData = rgbImg;
         paintMode = MODE_4_NULL;
         aInit();
      } else {
         throw new RuntimeException();
      }
   }

   /**
    * Defines a mode for write operations over the whole RgbImage
    * The clip area is the area of the image.
    * @param rgbImg image cannot be null
    * @param paintingMode
    */
   GraphicsX(DrwCtx drc, RgbCache rc, RgbImage rgbImg, int paintingMode) {
      this(drc, rc, rgbImg, paintingMode, 0, 0, rgbImg.getWidth(), rgbImg.getHeight());
   }

   /**
    * Defines a clipping area and mode for write operations over the RgbImage.
    * 
    * Uses the {@link RgbImage#getGraphicsX(int, int, int, int, int)} method
    * 
    * Sets the graphics of {@link RgbImage} 
    * @param rgbImg underlying image
    * @param paintingMode TODO rephase it elsewhere if RgbImage is in RgbMode, painting mode cannot be IMAGE and is automatically
    * set to RGB_IMAGE. That is because RgbImage cannot be put back to Primitive mode with transparent pixels
    * @param x clip of drawing operations
    * @param y
    * @param w
    * @param h
    */
   GraphicsX(DrwCtx drc, RgbCache rc, RgbImage rgbImg, int paintingMode, int x, int y, int w, int h) {
      super(drc);
      if (paintingMode == MODE_0_SCREEN)
         throw new IllegalArgumentException("Cannot have SCREEN mode with a RgbImage");
      this.cache = rc;
      aInit();
      this.imageRgbData = rgbImg;
      if (imageRgbData.isRgb() && paintingMode == MODE_1_IMAGE) {
         paintingMode = MODE_2_RGB_IMAGE;
      }
      this.paintMode = paintingMode;
      initializeClip(x, y, w, h);
      constructLayers(rgbImg);
      //not needed
      //imageRgbData.graphicsX = this;
   }

   private void aInit() {
      clipHistory = new IntBuffer(drc.getUC());
      this.excludeColor = GraphicsX.PRIMITIVE_COLOR_BASE;
      //bgOpCyan = new BlendOpGraphicsX(dd, BlendOp.BLENDING_00_OVER, DrawUtilz.FULLY_OPAQUE_CYAN);
      //bgOpPurple = new BlendOpGraphicsX(dd, BlendOp.BLENDING_00_OVER, DrawUtilz.FULLY_OPAQUE_PURPLE);

      //blendOpForPrimitive = new BlendOpGX(dd, this);
      blendOpImages = new BlendOp(drc.getBOC(), ITechBlend.BLENDING_00_OVER);
   }

   /**
    * Changes the dimension of destination Graphics. 
    * <br>
    * <br>
    * TODO If clipped
    * @param w
    * @param h
    */
   public void changeDimension(int w, int h) {
      //#debug
      toDLog().pDraw("w=" + w + " h=" + h, this, GraphicsX.class, "changeDimension", ITechLvl.LVL_05_FINE, true);
      //in all cases do a merge
      merge();
      switch (paintMode) {
         case MODE_0_SCREEN:
            break;
         case MODE_1_IMAGE:
            changeDimensionLayer(w, h);
            break;
         default:
            imageRgbData.changeDimension(w, h);
            changeDimensionLayer(w, h);
            break;
      }
   }

   private void changeDimensionLayer(int w, int h) {
      if (imageLayer.getWidth() != w || imageLayer.getHeight() != h) {
         imageLayer = drc.getImageFactory().createImage(w, h);
         g = imageLayer.getGraphics();
      }
   }

   /**
    * Clear statistics and flag area as virgin.
    * <br>
    * <br>
    * @param x
    * @param y
    * @param w
    * @param h
    */
   public void clear(int x, int y, int w, int h) {
      primitiveTally = 0;
      rgbCount = 0;
      setVirgin(x, y, w, h);
   }

   public void clear(int color, int x, int y, int w, int h) {
      bgColor = color;
      g.setColor(color);
      g.fillRect(x, y, w, h);
      primitiveTally = 0;
      rgbCount = 0;
      setVirgin(x, y, w, h);
   }

   /**
    * Since our {@link IGraphics} is never translated
    */
   private void clearLayer() {
      int oldColor = g.getColor();
      g.setColor(excludeColor);
      //clear layer
      g.fillRect(0, 0, imageLayer.getWidth(), imageLayer.getHeight());
      g.setColor(oldColor);
      pOpaqueLayerCount = 0;
   }

   /**
    * Using current clip, fill draw area with background color
    */
   public void clearWithBgColor() {
      clearLayer();
   }

   /**
    * Intersects the current clip with the specified rectangle.
    * <br>
    * <br>
    * The resulting clipping area is the intersection of the current clipping area and the specified rectangle.
    * This method can only be used to make the current clip smaller. 
    * To set the current clip larger, use the setClip method. 
    * Rendering operations have no effect outside of the clipping area.
    * <br>
    * <br>
    * Equals to {@link GraphicsX#clipSet(int, int, int, int, int)} with the intersect directive.
    * <br>
    * <br>
    * @param x
    * @param y
    * @param width
    * @param height
    */
   public void clipRect(int x, int y, int width, int height) {
      int[] sec = getGeo2dUtils().getIntersection(clipX, clipY, clipW, clipH, x, y, width, height);
      clipSet(sec[0], sec[1], sec[2], sec[3], CLIP_DIRECTIVE_0_INTERSECTION);
   }

   /**
    * Removes current clip rectangle and sets the previous one. down 1 step in the clip sequence.
    * <br>
    * <br>
    * Sets back the clip on {@link IGraphics}.
    * <br>
    * {@link IGraphics} cannot be translated between a reset clip unlike it is translated back.
    * <br>
    * <br>
    * Resets to the previous clip configuration. Takes into account any translation that occured ?
    * Reset the translation?
    * <br>
    * When no clip was made. do nothing
    */
   public synchronized void clipReset() {
      if (isIgnoreClip)
         return;
      //else no clip so reset has no effect
      if (clipIndex > 0) {
         clipIndex -= CLIP_INDEX_INCREMENT;
         int x = clipList[clipIndex];
         int y = clipList[clipIndex + 1];
         int width = clipList[clipIndex + 2];
         int height = clipList[clipIndex + 3];
         setMyClip(x, y, width, height, true);
      } else {
         // do nothing since no clip was set. keep the current clip
         //throw new IllegalStateException("Cannot Call Reset Clip When Clip Was not Set Previously");
      }
   }

   /**
    * Sets the current clip to the rectangle specified by the given coordinates.
    * <br>
    * <br>
    * When {@link GraphicsX} has been translated, the value is translated inside.
    * <br>
    * 
    * @param x x coordinate of clip region
    * @param y y coordinate of clip region
    * @param width
    * @param height
    */
   public synchronized void clipSet(int x, int y, int width, int height) {
      clipSet(x, y, width, height, CLIP_DIRECTIVE_0_INTERSECTION);
   }

   /**
    * Most of the time, parent container will set the clip directive to intersection.
    * <br>
    * <br>
    * The translation state is stored along with the clip state.
    * <br>
    * <br>
    * The clip coordinates are relative to the current coordinate system. (translation).
    * <br>
    * <br>
    * Operations on the coordinate system, such as translate(), do not modify the clip. 
    * <br>
    * So the clip values are kept intact until the clip is reset.
    * <br>
    * {@link GraphicsX#getClipX()} returns the clip value?
    * 
    * @param x
    * @param y
    * @param width
    * @param height
    * @param directive
    */
   public synchronized void clipSet(int x, int y, int width, int height, int directive) {
      if (isIgnoreClip) {
         return;
      }

      //add previous current to cliplist history

      clipList[clipIndex] = clipX;
      clipList[clipIndex + 1] = clipY;
      clipList[clipIndex + 2] = clipW;
      clipList[clipIndex + 3] = clipH;
      clipList[clipIndex + 4] = translateX;
      clipList[clipIndex + 5] = translateY;

      clipIndex += CLIP_INDEX_INCREMENT;
      if (clipIndex >= clipList.length) {
         clipList = drc.getMem().increaseCapacity(clipList, CLIP_INDEX_INCREMENT);
         //#debug
         String msg = "extending clipList to " + clipList.length + " clip area [" + x + "," + y + " " + width + "," + height + "]";
         //#debug
         toDLog().pDraw(msg, this, GraphicsX.class, "clipSet", ITechLvl.LVL_05_FINE, true);
      }
      //take into account the translation
      x += translateX;
      y += translateY;
      if (directive == CLIP_DIRECTIVE_0_INTERSECTION) {
         int[] sec = getGeo2dUtils().getIntersection(clipX, clipY, clipW, clipH, x, y, width, height);
         if (sec != null) {
            x = sec[0];
            y = sec[1];
            width = sec[2];
            height = sec[3];
         }
      }
      setMyClip(x, y, width, height, false);
   }

   /**
    * Update rgbImage based on painting mode.
    * @param rgbImage
    */
   private void constructLayers(RgbImage rgbImage) {
      isPseudoColorMode = false;
      isAlphaMode = false;
      switch (paintMode) {
         case MODE_1_IMAGE:
            //set image to primitive mode and mutable
            rgbImage.setRgbMode(false);
            imageLayer = rgbImage.getImage(true);
            break;
         case MODE_3_RGB:
            //starts with only rgb. if a primitive call is made, switch will be made
            rgbImage.setRgbMode(true);
            imageLayer = null;
            break;
         case MODE_2_RGB_IMAGE:
            imageLayer = createImageLayer(clipW, clipH);
            isAlphaMode = true;
            break;
         default:
            //other modes are not compatible with RGB Image editing
            break;
      }
      //TODO how do we paint in RGB?
      if (paintMode != MODE_3_RGB) {
         g = imageLayer.getGraphics();
      }
      pOpaqueLayerCount = 0;
      primitiveTally = 0;
   }

   /**
    * Create a RgbImage buffer of size (w,h).
    * For rgb = false => RgbImage is a mutable white Image.
    * For rgb = true => RgbImage is RGB int with transparent black pixels.
    * @param w
    * @param h
    * @param rgb 
    * @return
    */
   private RgbImage createBuffer(int w, int h, boolean rgb) {
      if (rgb)
         return cache.create(w, h);
      else
         return cache.createPrimitiveRgb(w, h, excludeColor);
   }

   /**
    * Cannot return null.
    * What about implementation details?
    * @param w
    * @param h
    * @return
    */
   private IImage createImageLayer(int w, int h) {
      IImage img = null;
      if (drc.hasFeatureSupport(ITechFeaturesDraw.SUP_ID_10_TRANSPARENT_BACKGROUND)) {
         //alpha suppport which means the background is transparent by default
         img = cache.createPrimitiveColor(w, h, 0);
      } else {
         //j2me is not able to create an empty transparent image
         img = cache.createPrimitiveColor(w, h, excludeColor);
         isPseudoColorMode = true;
      }
      return img;
   }

   /**
    * Draws the outline of a circular or elliptical arc covering the specified rectangle, using the current color and stroke style.
    * @param x
    * @param y
    * @param width
    * @param height
    * @param startAngle
    * @param arcAngle
    */
   public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
      if (!prePrimitiveWork()) {
         return;
      }
      x += translateX;
      y += translateY;
      g.drawArc(x, y, width, height, startAngle, arcAngle);
      postPrimitiveWork();
   }

   /**
    * Draws the specified character using the current font and color.
    * @param character
    * @param x
    * @param y
    * @param anchor
    */
   public void drawChar(char character, int x, int y, int anchor) {
      if (!prePrimitiveWork()) {
         return;
      }
      x += translateX;
      y += translateY;
      g.drawChar(character, x, y, anchor);
      postPrimitiveWork();
   }

   /**
    * Draws the specified characters using the current font and color.
    * @param data
    * @param offset
    * @param length
    * @param x
    * @param y
    * @param anchor
    */
   public void drawChars(char[] data, int offset, int length, int x, int y, int anchor) {
      if (!prePrimitiveWork()) {
         return;
      }
      x += translateX;
      y += translateY;
      g.drawChars(data, offset, length, x, y, anchor);
      postPrimitiveWork();
   }

   public void drawFigure(ByteObject fig, int x, int y, int w, int h) {
      FigureOperator figureOperator = drc.getFigureOperator();
      figureOperator.paintFigure(this, x, y, w, h, fig);
   }

   /**
    * Draws
    * @param gi
    */
   public void drawGraphics(GraphicsX gi, int x, int y) {
      RgbImage r = gi.getRgbImage();
      this.drawRgbImage(r, x, y);
   }

   /**
    * Draws the specified image by using the anchor point.
    * @param img
    * @param x
    * @param y
    * @param anchor
    * GraphicsX:
    * If Image has transparent pixels and GraphicsX is in pseudo Trans mode,
    * Primitive are merge to the RGB
    */
   public void drawImage(IImage img, int x, int y, int anchor) {
      this.drawRegion(img, 0, 0, img.getWidth(), img.getHeight(), IImage.TRANSFORM_0_NONE, x, y, anchor);
   }

   public void drawImage(IImage img, int x, int y, int anchor, int trans) {
      this.drawRegion(img, 0, 0, img.getWidth(), img.getHeight(), trans, x, y, anchor);
   }

   /**
    * Convert Image to RGB, replace pixel color by color replace.
    * <br>
    * Draw using alpha channels.
    * @param img
    * @param x
    * @param y
    * @param anchor
    * @param color
    * @param replace
    */
   public void drawImage(IImage img, int x, int y, int anchor, int color, int replace) {
      throw new RuntimeException();
   }

   public void drawImage(RgbImage img, int x, int y) {
      drawImage(img, x, y, ANCHOR);
   }

   /**
    * 
    * @param img
    * @param x
    * @param y
    * @param anchor
    */
   public void drawImage(RgbImage img, int x, int y, int anchor) {
      if (!img.hasFlag(ITechRgbImage.FLAG_22_NOT_DRAWABLE)) {
         this.drawRgbImage(img, x, y, anchor);
      }
   }

   /**
    * 
    * @param img
    * @param x
    * @param y
    * @param anchor
    * @param trans
    */
   public void drawImage(RgbImage img, int x, int y, int anchor, int trans) {
      if (!img.hasFlag(ITechRgbImage.FLAG_22_NOT_DRAWABLE)) {
         IImage i = img.getImage();
         this.drawImage(i, x, y, anchor, trans);
      }
   }

   /**
    * Draws a line between the coordinates (x1,y1) and (x2,y2) using the current color and stroke style.
    * @param x1
    * @param y1
    * @param x2
    * @param y2
    */
   public void drawLine(int x1, int y1, int x2, int y2) {
      if (!prePrimitiveWork()) {
         return;
      }
      x1 += translateX;
      y1 += translateY;
      x2 += translateX;
      y2 += translateY;
      g.drawLine(x1, y1, x2, y2);
      postPrimitiveWork();
   }

   /**
    * Draw figures stored in the postpone vector.
    * <br>
    * Animation method or object methods.
    * <br>
    * Primitives are postpones using
    * {@link GraphicsX#postpone(int, int, int, int, ByteObject)}
    * <br>
    * This allows to draw an outer border.... but how do we time the postpone?
    * The call is called after each layer.
    */
   public void drawPostponed() {
      if (paintMode == MODE_4_NULL || hasSwitchOff(SWITCHOFF_5_PAINT)) {
         return;
      }
      if (postpones != null && postpones.size() != 0) {
         int size = postpones.size();
         for (int i = 0; i < size; i += 2) {
            ByteObject fig = (ByteObject) postpones.elementAt(i);
            int[] ar = (int[]) postpones.elementAt(i + 1);
            drc.getFigureOperator().paintFigure(this, ar[0], ar[1], ar[2], ar[3], fig);
         }
         postpones.removeAllElements();
      }
   }

   /**
    * Draws the outline of the specified rectangle using the current color and stroke style.
    * @param x
    * @param y
    * @param width
    * @param height
    */
   public void drawRect(int x, int y, int width, int height) {
      if (!prePrimitiveWork()) {
         return;
      }
      x += translateX;
      y += translateY;
      g.drawRect(x, y, width, height);
      postPrimitiveWork();
   }

   /**
    * Copies a region of the specified source image <code>src</code> to a location within the destination.
    * <br>
    * <br>
    * Possibly transforming (rotating and reflecting) the image data using the chosen <code>transform</code> function.
    * <br>
    * <br>
    * Called by all methods that draw {@link IImage} objects.
    * <br>
    * <br>
    * @param src source {@link IImage}, if underlying is {@link RgbImage}
    * @param x_src the x coordinate of the upper left corner of the region within the source image to copy
    * @param y_src the y coordinate of the upper left corner of the region within the source image to copy
    * @param width the width of the region to copy
    * @param height the height of the region to copy
    * @param transform
    * @param x the x coordinate of the anchor point in the destination drawing area
    * @param y the y coordinate of the anchor point in the destination drawing area
    * @param anchor flag {@link ITechGraphics#LEFT}
    * GraphicsX. This call requires a merge
    * @throws   NullPointerException - if src is null 
    * @throws   IllegalArgumentException - if src is the same image as the destination of this Graphics object 
    * @throws   IllegalArgumentException - if transform is invalid 
    * @throws   IllegalArgumentException - if anchor is invalid 
    * @throws   IllegalArgumentException - if the region to be copied exceeds the bounds of the source image
    */
   public void drawRegion(IImage src, int x_src, int y_src, int width, int height, int transform, int x, int y, int anchor) {
      int sw = src.getWidth();
      int sh = src.getHeight();
      if (x_src < 0 || y_src < 0 || x_src + width > sw || y_src + height > sh) {
         throw new IllegalArgumentException(sw + ":" + sh + " x=" + x + " y=" + y + " width=" + width + " height" + height);
      }
      if (transform < 0 || transform > IImage.TRANSFORM_MAX) {
         throw new IllegalArgumentException("Transform not valid " + transform);
      }
      if (!prePrimitiveWork()) {
         return;
      }
      x += translateX;
      y += translateY;
      imageDraws++;
      if (paintMode == MODE_2_RGB_IMAGE) {
         //complex stuff
         //#debug
         //#debug
         toDLog().pDraw("x y=" + x + "," + y, this, GraphicsX.class, "drawRegion", ITechLvl.LVL_05_FINE, true);
         //pixel blending. we don't know the alpha
         int refX = x + x_src;
         int refY = y + y_src;
         //1st : take the intersection of image region and underlying image based on x and y
         //         int[] sec1 = ColorUtils.getIntersectionDest(0, 0, imageRgbData.getWidth(), imageRgbData.getHeight(), refX, refY, width, height);
         //         if (sec1 != null) {
         //            toLog().pDraw("Intersection between Image Region and Image Destination is " + MUtils.debugAlone(sec1, ","), GraphicsX.class);
         //         }
         //---------------------
         //2nd take intersection between source Image and underlying clipped area
         //clipx and clipy are not translated because x_dest is not translated so effect is null for the purpose of computing intersection
         //int[] sec = ColorUtils.getIntersectionDest(getClipXTranslated(), getClipYTranslated(), clipW, clipH, x_dest, y_dest, src.getWidth(), src.getHeight());
         //coordinates are in the referential of the Image src. to be used only for reading pixel data 
         int[] sec = getGeo2dUtils().getIntersectionDest(clipX, clipY, clipW, clipH, refX, refY, width, height);

         //3rd if an intersection exists, get that portion into a pixel int[] array and draw/blends it with data.
         if (sec != null) {
            //fetch intersection
            int[] data = drc.getCache().getImageData(src, sec[0], sec[1], sec[2], sec[3]);
            //draw intersected image data into our root pixel data.
            //check if there are transparent pixels
            //Image img = cache.createImageImmutable(data, sec[2], sec[3], true);
            //half transparent pixels not shown correctly
            //g.drawRegion(img, x_src, y_src, width, height, transform, x, y, anchor);

            //#debug
            String msg = "Trans=" + ToStringStaticUc.toStringTrans(transform) + " Src xy=[" + x_src + "," + y_src + ":" + width + "-" + height + "] at position " + x + ";" + y;
            //#debug
            toDLog().pDraw(msg, this, GraphicsX.class, "drawRegion", ITechLvl.LVL_05_FINE, true);
            //#debug
            toDLog().pDraw("Trans=" + ToStringStaticUc.toStringTrans(transform) + " w=" + sec[2] + " h=" + sec[3] + " Clipped To " + getIntUtils().debugString(sec), this, GraphicsX.class, "drawRegion", ITechLvl.LVL_05_FINE, true);

            data = TransformUtils.transform(data, sec[2], sec[3], transform);
            BlendOp op = blendOpImages;
            //get intersection in GraphicsX referencetial
            sec = getGeo2dUtils().getIntersection(clipX, clipY, clipW, clipH, refX, refY, width, height);
            //toLog().pDraw("drawRegion data=" + data.length + " should be = " + (sec[2] * sec[3]) + " Drawing at " + MUtils.debugString(sec), GraphicsX.class);
            imageRgbData.blend(op, sec[0], sec[1], data, 0, sec[2], 0, 0, sec[2], sec[3]);
            //NOTE: translation of x_dest and y_dest will occur inside drawRGB method
         }
      } else {
         g.drawRegion(src, x_src, y_src, width, height, transform, x, y, anchor);
      }
      postPrimitiveWork();
   }

   /**
    * Renders a series of device-independent RGBa values in a specified region.
    * <br>
    * <br>
    * Uses {@link IGraphics#drawRGB(int[], int, int, int, int, int, int, boolean)} for SCREEN and Image modes.
    * <br>
    * @param rgbData array of pixel data
    * @param offset the array index of the first ARGB value. to define a region, offset is equal to m + scanle * n
    * @param scanlength the relative array offset between the corresponding pixels in consecutive rows in the rgbData array
    * i.e. the width of the region to copy
    * @param x the horizontal location of the region to be rendered
    * @param y the vertical location of the region to be rendered
    * @param width the width of the region to be rendered
    * @param height the height of the region to be rendered
    * @param processAlpha
    * GraphicsX: It is performant to set processAlpha to false when it known by construction that there is 
    * no transparency in the rgbData
    */
   public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {
      rgbCount++;
      switch (paintMode) {
         case MODE_0_SCREEN:
         case MODE_1_IMAGE:
            x += translateX;
            y += translateY;
            g.drawRGB(rgbData, offset, scanlength, x, y, width, height, processAlpha);
            pOpaqueLayerCount++;
            primitiveTally++;
            break;
         case MODE_4_NULL:
            return;
         default:

            //merge layer and do the alpha blending with the RGB
            mergeAndClear();
            //get intersection with RgbImage or with Layer? and copy over it normalize if graphics have been translated
            //intersection between the current clip and the position of the image data
            //int[] sec = ColorUtils.getIntersectionDest(getClipXTranslated(), getClipYTranslated(), clipW, clipH, x, y, width, height);
            x += translateX;
            y += translateY;
            //offset=m + scanlength * n
            //scanlength is the width of the region to copy
            int[] sec = getGeo2dUtils().getIntersectionDest(clipX, clipY, clipW, clipH, x, y, width, height);
            if (sec != null) {
               //#mdebug
               String msg1 = "intersection clip=" + clipX + "," + clipY + " : " + clipW + "," + clipH + " and " + " and [" + x + "," + y + " : " + width + "," + height + "]";
               toDLog().pDraw(msg1, this, GraphicsX.class, "drawRGB", ITechLvl.LVL_05_FINE, true);
               String msg2 = "data=" + rgbData.length + " at x,y [" + x + "," + y + "] Drawing at " + getIntUtils().debugString(sec);
               toDLog().pDraw(msg2, this, GraphicsX.class, "drawRGB", ITechLvl.LVL_05_FINE, true);
               //#enddebug
               //update the rgbdata values based on the intersection. actually only the offset changes. the scan stays the same
               int newscan = scanlength;
               int newoffset = offset + (sec[1] * scanlength) + sec[0];
               BlendOp op = blendOpImages;
               if (!processAlpha) {
                  //without alpha, all pixels go to destination.
                  op = new BlendOp(drc.getBOC(), blendOpImages.getMode(), ITechBlend.ALPHA_2_255);
               }
               sec = getGeo2dUtils().getIntersection(clipX, clipY, clipW, clipH, x, y, width, height);

               //#debug
               toDLog().pDraw("newoffset=" + newoffset + " newscan=" + newscan + " Drawing at " + getIntUtils().debugString(sec), this, GraphicsX.class, "drawRGB", ITechLvl.LVL_05_FINE, true);
               imageRgbData.blend(op, sec[0], sec[1], rgbData, newoffset, scanlength, 0, 0, sec[2], sec[3]);
            }
      }
   }

   //   /**
   //    * Fill the whole area with the color.
   //    * <br>
   //    * <br>
   //    * Performant for clearing with an opaque color.
   //    * If color is pseudo transparent
   //    * @param
   //    */
   //   public void fill(int color) {
   //      if (((color >> 24) & 0xFF) == 255) {
   //         pOpaqueLayerCount = 0;
   //         primitiveTally++;
   //         if (isPseudo && color == pseudoTColor) {
   //            whiteSwap();
   //         }
   //         if (imageLayer != null) {
   //            g.setColor(color);
   //            g.fillRect(0, 0, imageLayer.getWidth(), imageLayer.getHeight());
   //         }
   //         if (imageRgbData != null) {
   //            imageRgbData.fill(color);
   //         }
   //      } else {
   //         //do a normal fill
   //         this.setColor(color);
   //         this.fillRect(0, 0, imageLayer.getWidth(), imageLayer.getHeight());
   //      }
   //   }

   /**
    * Draws the {@link RgbImage}. Flush any data on the {@link GraphicsX} object of this image.
    * <br>
    * Nothing happens if RgbImage source is equal to RgbImage of GraphicsX.
    * <br>
    * <br>
    * @param data null => nothing happens
    * @param x
    * @param y
    * GraphicsX:
    */
   public void drawRgbImage(RgbImage data, int x, int y) {
      drawRgbImage(data, x, y, ANCHOR);
   }

   /**
    * Draws the {@link RgbImage} on the {@link GraphicsX}
    * @param data
    * @param x
    * @param y
    * @param anchor
    */
   public void drawRgbImage(RgbImage data, int x, int y, int anchor) {
      if (data == null) {
         throw new NullPointerException("Null RgbImage");
      }
      switch (paintMode) {
         case MODE_0_SCREEN:
         case MODE_1_IMAGE:
            //draw data Image onto the GraphicsX object
            data.draw(this, x, y, anchor);
            break;
         case MODE_3_RGB:
         case MODE_2_RGB_IMAGE:
            if (data.isMutable()) {
               GraphicsX gi = data.getGraphicsX();
               if (gi != null) {
                  primitiveTally += gi.primitiveTally;
               }
            }
            //mode has a RGB Image 
            if (data.isRgb() && data.getRgbData() == imageRgbData.getRgbData()) {
               //data was merged by user code. It is the responsability of that code to blend with the blending mode
               //of GraphicsX and use the correct x,y translation

            } else {
               data.draw(this, x, y, anchor);
            }
            break;
         case MODE_4_NULL:
            return;
         default:
      }
   }

   /**
    * Draws a Region of the Rgb Image
    * @param data
    * @param m
    * @param n
    * @param w
    * @param h
    * @param trans
    * @param x
    * @param y
    */
   public void drawRgbImage(RgbImage data, int m, int n, int w, int h, int trans, int x, int y) {
      data.draw(this, m, n, w, h, x, y);
   }

   public void drawRgbImageT(RgbImage data, int x, int y, int trans) {
      data.drawT(this, x, y, ANCHOR, trans);
   }

   /**
    * Draws the outline of the specified rounded corner rectangle using the current color and stroke style.
    * @param x
    * @param y
    * @param width
    * @param height
    * @param arcWidth
    * @param arcHeight
    */
   public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
      if (!prePrimitiveWork()) {
         return;
      }
      x += translateX;
      y += translateY;
      g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
      postPrimitiveWork();
   }

   public void drawString(String str, int x, int y) {
      this.drawString(str, x, y, IBOAnchor.ANCHOR_G_TOP_LEFT);
   }

   /**
    * Draws the specified String using the current font and color.
    * @param str
    * @param x
    * @param y
    * @param anchor
    */
   public void drawString(String str, int x, int y, int anchor) {
      if (!prePrimitiveWork()) {
         return;
      }
      x += translateX;
      y += translateY;
      g.drawString(str, x, y, anchor);
      postPrimitiveWork();
   }

   /**
    * Draws the specified String using the current font and color.
    * @param str
    * @param offset
    * @param len
    * @param x
    * @param y
    * @param anchor
    */
   public void drawSubstring(String str, int offset, int len, int x, int y, int anchor) {
      if (!prePrimitiveWork()) {
         return;
      }
      x += translateX;
      y += translateY;
      g.drawSubstring(str, offset, len, x, y, anchor);
      postPrimitiveWork();

   }

   public void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
      if (!prePrimitiveWork()) {
         return;
      }
      g.drawLine(x1 + translateX, y1 + translateY, x2 + translateX, y2 + translateY);
      g.drawLine(x2 + translateX, y2 + translateY, x3 + translateX, y3 + translateY);
      g.drawLine(x1 + translateX, y1 + translateY, x3 + translateX, y3 + translateY);
      postPrimitiveWork();

   }

   /**
    * Swaps the pseudo color White<->Cyan
    */
   void excludeColorSwap() {
      if (excludeColor == GraphicsX.PRIMITIVE_COLOR_BASE) {
         excludeColor = GraphicsX.PRIMITIVE_COLOR_ALT;
      } else {
         excludeColor = GraphicsX.PRIMITIVE_COLOR_BASE;
      }

      //#debug
      toDLog().pDraw(ToStringStaticDrawx.toStringColor(excludeColor), this, GraphicsX.class, "excludeColorSwap", ITechLvl.LVL_05_FINE, true);
   }

   /**
    * Adapts for Arc or Fill mode. slightly modifies for Arc.
    * 
    * @param x
    * @param y
    * @param width
    * @param height
    * @param startAngle
    * @param arcAngle
    */
   public void fiArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
      if (isFillMode) {
         fillArc(x, y, width, height, startAngle, arcAngle);
      } else {
         drawArc(x, y, width - 1, height - 1, startAngle, arcAngle);
      }
   }

   /**
    * Fills a circular or elliptical arc covering the specified rectangle.
    * @param x the x coordinate of the upper-left corner of the arc to be filled.
    * @param y the y coordinate of the upper-left corner of the arc to be filled.
    * @param width the width of the arc to be filled
    * @param height the height of the arc to be filled
    * @param startAngle the beginning angle.
    * @param arcAngle the angular extent of the arc, relative to the start angle.
    * @throws NullPointerException if painting mode is {@link #MODE_3_RGB}
    * Using the {@link #setColor(int)} method will switch automatically
    */
   public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
      if (!prePrimitiveWork()) {
         return;
      }
      x += translateX;
      y += translateY;
      g.fillArc(x, y, width, height, startAngle, arcAngle);
      postPrimitiveWork();
   }

   /**
    * Fills the specified rectangle with the current color.
    * @param x
    * @param y
    * @param width
    * @param height
    */
   public void fillRect(int x, int y, int width, int height) {
      if (!prePrimitiveWork()) {
         return;
      }
      x += translateX;
      y += translateY;
      g.fillRect(x, y, width, height);
      postPrimitiveWork();
   }

   /**
    * Fills the specified rounded corner rectangle with the current color.
    * @param x
    * @param y
    * @param width
    * @param height
    * @param arcWidth
    * @param arcHeight
    */
   public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
      if (!prePrimitiveWork()) {
         return;
      }
      x += translateX;
      y += translateY;
      g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
      postPrimitiveWork();
   }

   /**
    * Fills the specified triangle will the current color.
    * @param x1
    * @param y1
    * @param x2
    * @param y2
    * @param x3
    * @param y3
    */
   public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
      if (!prePrimitiveWork()) {
         return;
      }
      g.fillTriangle(x1 + translateX, y1 + translateY, x2 + translateX, y2 + translateY, x3 + translateX, y3 + translateY);
      postPrimitiveWork();
   }

   /**
    * Flush data from the top layer to the Rgb array
    * If rgb int array is null, nothing happens.
    */
   public void flush() {
      merge();
   }

   public int getAlpha() {
      return alpha;
   }

   public int getBgColor() {
      return bgColor;
   }

   public BlendOp getBlendOp() {
      return blendOpImages;
   }

   /**
    * Gets the blue component of the current color.
    * @return
    */
   public int getBlueComponent() {
      return g.getBlueComponent();
   }

   /**
    * Same as {@link GraphicsX#getBufferRegion(int, int, int, int, boolean)} with false as rgb mode.
    * @param x
    * @param y
    * @param w
    * @param h
    * @return
    */
   public RgbImage getBufferRegion(int x, int y, int w, int h) {
      return getBufferRegion(x, y, w, h, false);
   }

   /**
    * Returns a {@link RgbImage} pinned to that area of that GraphicsX. This method exists for performance reasons in some cases.
    * <br>
    * <br>
    * General contract of this method is to give direct access to the pixels of an area for write modifications.
    * <br>
    * <br>
    * The pixels of the RgbImage are filled with fully transparent black. 
    * <br>
    * <br>
    * <b>Uses</b>: <br>
    * A {@link ByteObject} figure modifies pixels in an area of GraphicsX. Figure's code does not know how is represented the GraphicsX.
    * It could have RGB array or it may be on the Screen or on a Mutable {@link IImage}.
    * 
    * <br>
    * The method always returns an RgbImage in Rgb Mode. getGraphicsX method on this image will generate a Image Layer.
    * <br>
    * <br>
    * When the specified region matches a virgin area of underlying this GraphicsX, no extra buffer is created.<br>
    * Instead a RgbImage will be returned as a Region of the underlying Image. Pixels are modified directly
    * <br>
    * <br>
    * RgbImage is clipped to match GraphicsX clipping area. 
    * <br>
    * <br>
    * @param x
    * @param y
    * @param w
    * @param h
    * @param rgb
    * The buffer will be merged every time another call is made in the Graphics object
    * @return An empty RgbImage buffer
    * <br>
    * This method does not usually give you access to image data. <br>
    * In order to get access to pixels data of an {@link IImage}, use the {@link GraphicsX#getPixels(int, int, int, int)} method that returns a copy
    * 
    * @see GraphicsX#getPixels(int, int, int, int)
    */
   public RgbImage getBufferRegion(int x, int y, int w, int h, boolean rgb) {
      //account for translation
      x += translateX;
      y += translateY;
      if (paintMode == MODE_0_SCREEN || paintMode == MODE_1_IMAGE) {
         bufferRegion = createBuffer(w, h, rgb);
      } else {
         if (imageRgbData.hasFlag(ITechRgbImage.FLAG_16_VIRGIN) || imageRgbData.isEmpty(x, y, w, h)) {
            //Go straight to the buffer and draw on it with implicit SRC blending
            bufferRegion = imageRgbData.getRegion(x, y, w, h);
            bufferRegion.setFlag(ITechRgbImage.FLAG_16_VIRGIN, true);
         } else {
            bufferRegion = createBuffer(w, h, rgb);
         }
      }
      return bufferRegion;
   }

   public int getClipDirective() {
      return clipDirective;
   }

   /**
    * Gets the height of the current clipping area.
    * @return
    */
   public int getClipHeight() {
      return clipH;
   }

   /**
    * Gets the width of the current clipping area.
    * @return
    */
   public int getClipWidth() {
      return clipW;
   }

   /**
    * Gets the X offset of the current clipping area, relative to the coordinate system origin of this graphics context.
    * <br>
    * <br>
    * Includes translation
    * @return
    */
   public int getClipX() {
      return clipX;
   }

   /**
    * Gets the X offset of the clip in the current coordinate system
    * @return
    */
   public int getClipXRelative() {
      return clipX + translateX;
   }

   /**
    * Gets the Y offset of the current clipping area, relative to the coordinate system origin of this graphics context.
    * @return
    */
   public int getClipY() {
      return clipY;
   }

   /**
    * Gets the Y offset of the current clipping area, relative to the coordinate system origin of this graphics context.
    * @return
    */
   public int getClipYRelative() {
      return clipY + translateY;
   }

   /**
    * Gets the current color.
    * @return
    */
   public int getColor() {
      return g.getColor();
   }

   /**
    * Gets the color that will be displayed if the specified color is requested.
    * @param color
    * @return
    */
   public int getDisplayColor(int color) {
      return g.getDisplayColor(color);
   }

   /**
    * Gets the current font.
    * @return
    */
   public IMFont getFont() {
      return g.getFont();
   }

   public IMFont getFont(int face, int style, int size) {
      return g.getFont(face, style, size);
   }

   public IMFont getFontD() {
      return (IMFont) g.getFont();
   }

   private Geo2dUtils getGeo2dUtils() {
      return drc.getUC().getGeo2dUtils();
   }

   /**
    * Returns the internal handle to the {@link IGraphics}, the {@link CoreDrawCtx}
    * which implements drawing primities.
    * @return
    */
   public IGraphics getGraphics() {
      return g;
   }

   /**
    * Gets the current grayscale value of the color being used for rendering operations.
    * @return
    */
   public int getGrayScale() {
      return g.getGrayScale();
   }

   /**
    * Gets the green component of the current color.
    * @return
    */
   public int getGreenComponent() {
      return g.getGreenComponent();
   }

   public IntUtils getIntUtils() {
      return drc.getUC().getIU();
   }

   /**
    * The current paint mode
    * <li> {@link GraphicsX#MODE_0_SCREEN}
    * <li> {@link GraphicsX#MODE_1_IMAGE}
    * <li> {@link GraphicsX#MODE_2_RGB_IMAGE}
    * <li> {@link GraphicsX#MODE_3_RGB}
    * @return
    */
   public int getPaintMode() {
      return paintMode;
   }

   /**
    * Return the pixel value for reading purposes. <br>
    * <br>
    * Pixel queries are not possible in {@link GraphicsX#MODE_0_SCREEN}. <br>
    * @param x
    * @param y
    * @return
    * @throws IllegalArgumentException when mode is {@link GraphicsX#MODE_0_SCREEN}
    */
   public int getPixel(int x, int y) {
      mergeAndClear();
      switch (paintMode) {
         case MODE_0_SCREEN:
            throw new IllegalArgumentException();
         case MODE_1_IMAGE:
            int[] ar = new int[1];
            imageLayer.getRGB(ar, 0, 1, x, y, 1, 1);
            return ar[0];
         default:
            return imageRgbData.getPixel(x, y);
      }
   }

   /**
    * Get a copy of the pixels for reading purposes. <br>
    * Though one can modify returned {@link RgbImage} and draw it again on same {@link GraphicsX}.
    * <br>
    * @param x
    * @param y
    * @param w
    * @param h
    * @return a copy of pixels in the given area
    * @throws IllegalArgumentException when mode is {@link GraphicsX#MODE_0_SCREEN}
    */
   public RgbImage getPixels(int x, int y, int w, int h) {
      mergeAndClear();
      RgbImage pixels = null;
      switch (paintMode) {
         case MODE_0_SCREEN:
            throw new IllegalArgumentException();
         case MODE_1_IMAGE:
            pixels = drc.getCache().create(w, h);
            int[] rgb = pixels.getRgbData();
            int offset = pixels.getOffset();
            imageLayer.getRGB(rgb, offset, w, x, y, w, h);
            break;
         default:
            RgbImage ri = imageRgbData.getRegion(x, y, w, h);
            pixels = ri.cloneImg();
      }
      return pixels;
   }

   public int getPrimitiveCount() {
      return pOpaqueLayerCount;
   }

   public int getPrimitivetTally() {
      return primitiveTally;
   }

   public Random getRandom() {
      return drc.getRandom();
   }

   /**
    * Gets the red component of the current color.
    * @return
    */
   public int getRedComponent() {
      return g.getRedComponent();
   }

   /**
    * Get Default Font
    * @return
    */
   public IMFont getRefFont() {
      return drc.getFontFactory().getDefaultFont();
   }

   /**
    * Counts the number of times {@link GraphicsX#drawRGB(int[], int, int, int, int, int, int, boolean)} has been called
    * @return
    */
   public int getRgbCount() {
      return rgbCount;
   }

   /**
    * Get the RgbImage underlying the Graphics Object.
    * If the Graphics Object is the Device Screen's object
    * @return
    */
   public RgbImage getRgbImage() {
      if (paintMode == MODE_0_SCREEN || paintMode == MODE_1_IMAGE) {
         throw new IllegalArgumentException("No RgbImage in Mode " + ToStringStaticDrawx.toStringPaintMode(paintMode));
      }
      mergeAndClear();
      return imageRgbData;
   }

   /**
    * Gets the stroke style used for drawing operations.
    * @return
    */
   public int getStrokeStyle() {
      return g.getStrokeStyle();
   }

   /**
    * Gets the X coordinate of the translated origin of this graphics context.
    * @return
    */
   public int getTranslateX() {
      return translateX;
   }

   /**
    * Gets the Y coordinate of the translated origin of this graphics context.
    * @return
    */
   public int getTranslateY() {
      return translateY;
   }

   /**
    * Null if no virgin buffer in the area. This allows to modifies directly the pixel data
    * of the underlying image
    * @param x
    * @param y
    * @param w
    * @param h
    * @return
    */
   public RgbImage getVirginBuffer(int x, int y, int w, int h) {
      if (imageRgbData != null) {
         x += translateX;
         y += translateY;
         if (imageRgbData.hasFlag(ITechRgbImage.FLAG_16_VIRGIN) || imageRgbData.isEmpty(x, y, w, h)) {
            bufferRegion = imageRgbData.getRegion(x, y, w, h);
            bufferRegion.setFlag(ITechRgbImage.FLAG_16_VIRGIN, true);
         }
      }
      return null;
   }

   public boolean hasAlphaBlending() {
      return isAlphaMode;
   }

   public boolean hasFlagOptions(int flag) {
      return BitUtils.hasFlag(optionsFlags, flag);
   }

   /**
    * We want to check whether to use gradients at all or not
    * <br>
    * 
    * @return
    */
   public boolean hasGradient() {
      if (hasSwitchOff(SWITCHOFF_1_GRADIENT)) {
         return false;
      } else if (drc.hasStyleFlag(ITechStyles.STYLES_1_GRADIENT_DISABLED)) {
         return false;
      }
      return true;
   }

   /**
    * Bit flag are decided externally.
    * <br>
    * Default implementation of MasterCanvas.
    * <li> IMaster.SCREENSHOT for example
    * <br>
    * TODO move this method GraphicsXD
    * @param ctx
    * @return
    */
   public boolean hasPaintCtx(int ctx) {
      return BitUtils.hasFlag(repaintFlags, ctx);
   }

   /**
    * 
    * @param flag
    * @return
    */
   public boolean hasSwitchOff(int flag) {
      return BitUtils.hasFlag(switches, flag);
   }

   /**
    * Switch to RGB with TOP primitive layer
    * RGB Image is put to RGB mode and top layer is kept
    */
   public void imageToRgbImageSwitch() {
      if (paintMode != MODE_1_IMAGE) {
         throw new IllegalStateException();
      }
      paintMode = MODE_2_RGB_IMAGE;
   }

   /**
    * Initialize clip information when {@link GraphicsX} is created.
    * @param x
    * @param y
    * @param w
    * @param h
    */
   private void initializeClip(int x, int y, int w, int h) {
      //get intersection between image and given clip
      int[] sec = getGeo2dUtils().getIntersection(0, 0, imageRgbData.getWidth(), imageRgbData.getHeight(), x, y, w, h);
      //#mdebug
      if (sec == null) {
         //#debug
         String msg = "x=" + x + " y=" + y + " w=" + w + " h=" + h;
         //#debug
         toDLog().pDraw(msg, this, GraphicsX.class, "initializeClip", ITechLvl.LVL_05_FINE, true);
         throw new IllegalArgumentException("Null Intersection");
      }
      //#enddebug
      setMyClip(sec[0], sec[1], sec[2], sec[3], false);
   }

   /**
    * True when GraphicsX object is full of a single color.
    * @return
    */
   public boolean isVirgin() {
      return false;
   }

   /**
    * Screen Mode might be able to answer true if no primitive were drawn since the last erase with the background color
    * @param x
    * @param y
    * @param w
    * @param h
    * @return
    */
   public boolean isVirgin(int x, int y, int w, int h) {
      if (paintMode == MODE_0_SCREEN && primitiveTally == 0) {
         return true;
      }
      return false;
   }

   /**
    * Merge the mask if necessary.
    * For other case unclip clip
    */
   public void maskEnd() {
      // TODO Auto-generated method stub

   }

   /**
    * Start mask process over that area.
    * If that area is OneColor region, creates an intermediary layer. Clips automatically.
    * Else clip it.
    * Automatically sets the mask color
    * @param x
    * @param y
    * @param w
    * @param h
    */
   public GraphicsX maskStart(int x, int y, int w, int h) {
      return this;
   }

   /**
    * Only merge if opaque primitives were drawn on Image Layer.
    * <br>
    * <br>
    * Alpha primitives are always merged in the postWork method of the primitive method call.
    * <br>
    * Use the boundary compute to ease the work of the merge
    */
   private boolean merge() {
      if (pOpaqueLayerCount != 0 && imageRgbData != null && imageLayer != null && !imageRgbData.isNullImage()) {
         if (imageRgbData.rgbData != null) {

            //#debug
            String msg = "pOpaqueLayerCount=" + pOpaqueLayerCount + " TColor=" + ToStringStaticDrawx.toStringColor(excludeColor) + " " + ToStringStaticDrawx.toStringPaintMode(paintMode);
            //#debug
            toDLog().pDraw(msg, this, GraphicsX.class, "merge", ITechLvl.LVL_05_FINE, true);

            int srcH = imageRgbData.getHeight();
            int srcW = imageRgbData.getWidth();
            int[] layerRgb = cache.getImageData(imageLayer, 0, 0, srcW, srcH);
            int[] rgbData = imageRgbData.rgbData; //cannot be null?
            //merge only on the clip
            int srcIndex = 0;
            for (int i = 0; i < srcH; i++) {
               for (int j = 0; j < srcW; j++) {
                  int basePix = rgbData[srcIndex];
                  int blendPix = layerRgb[srcIndex];
                  int newPixel = 0;
                  if (isPseudoColorMode && excludeColor == blendPix) {
                     newPixel = basePix;
                  } else {
                     //full support of alpha
                     newPixel = blendOpImages.blendPixel(basePix, blendPix);
                  }
                  rgbData[srcIndex] = newPixel;
                  srcIndex++;
               }
            }
            pOpaqueLayerCount = 0;
            mergeCount++;
            return true;
         }
      }
      return false;
   }

   /**
    * Do a merge {@link GraphicsX#merge()}. <br>
    * If merging occured, clear the top {@link GraphicsX#imageLayer} with the pseudo transparent color
    */
   private void mergeAndClear() {
      if (merge()) {
         clearLayer();
      }
   }

   /**
    * Draw the GraphicsX content to the Screen Graphics using the given TRANSFORMATION (Flip, Rotation)
    * @param screenGraphics
    * @param trans
    */
   public void paintToScreen(IGraphics screenGraphics, int trans) {
      //either the layer or
      switch (paintMode) {
         case MODE_0_SCREEN:
            break;
         case MODE_1_IMAGE:
            screenGraphics.drawRegion(imageLayer, 0, 0, imageLayer.getWidth(), imageLayer.getHeight(), trans, 0, 0, GraphicsX.ANCHOR);
            break;
         default:
            IImage img = imageRgbData.getImage();
            screenGraphics.drawRegion(img, 0, 0, img.getWidth(), img.getHeight(), trans, 0, 0, GraphicsX.ANCHOR);
            break;
      }
   }

   /**
    * Postpone drawing of figure until call drawPostponed is called
    * @param x
    * @param y
    * @param w
    * @param h
    * @param figure
    */
   public void postpone(int x, int y, int w, int h, ByteObject figure) {
      if (postpones == null) {
         postpones = new Vector(2);
      }
      postpones.addElement(figure);
      postpones.addElement(new int[] { x, y, w, h });
   }

   /**
    * If last primitive draw was using an alpha value or color, it must be merged with the underlying
    * RgbImage. Alpha drawings never stay on the intermediayr layer
    */
   private void postPrimitiveWork() {
      //in all case, it is counted as an opaque primitive
      pOpaqueLayerCount++;
      primitiveTally++;
      if (hasAlpha) {
         //merges the primitive that was drawn with an opaque color using the current blending
         //and alpha value
         mergeAndClear();
      }
   }

   /**
    * If drawing with alpha and opaque primitive is on the layer
    */
   private boolean prePrimitiveWork() {
      if (paintMode == MODE_4_NULL || hasSwitchOff(SWITCHOFF_5_PAINT)) {
         return false;
      }
      if (hasAlpha && pOpaqueLayerCount != 0) {
         //
         mergeAndClear();
      }
      if (paintMode == MODE_3_RGB) {
         setPaintMode(MODE_2_RGB_IMAGE, clipW, clipH);
      }
      return true;
   }

   /**
    * Reset the Graphics g.
    * Change the reference as well because the Graphics object may have changed. When resize of canvas.
    * <br>
    * <br>
    * Called by Constructor
    * @param g
    */
   public void reset(IGraphics g) {
      this.g = g;
      clipX = g.getClipX();
      clipY = g.getClipY();
      clipW = g.getClipWidth();
      clipH = g.getClipHeight();
      clipIndex = 0;
      translateX = g.getTranslateX();
      translateY = g.getTranslateY();
      clipHistory.clear();
   }

   /**
    * Sets the alpha modifiers for all blending operations. 
    * <br>
    * It updates the blender alpha mode. setting a fixed value
    * <br>
    * If in {@link GraphicsX#MODE_0_SCREEN} paint mode, a hidden RGB_IMAGE mode is set up. 
    * If in {@link GraphicsX#MODE_1_IMAGE} paint mode, mode is switched to {@link GraphicsX#MODE_2_RGB_IMAGE}.
    * <br>
    * <br>
    * The next primitive operation is done a RGB/Image buffer and merged immediately with a drawRGB method
    * <br>
    * <br>
    * @param alpha
    */
   public void setAlpha(int alpha) {
      this.alpha = alpha & 0xFF;
      if (this.alpha != 255) {
         hasAlpha = true;
         if (paintMode == MODE_1_IMAGE) {
            setPaintMode(MODE_2_RGB_IMAGE, clipW, clipH);
         }

      } else {
      }
   }

   /**
    * This flag is used in order to temporarily disable/enable alpha settings of the {@link GraphicsX}. 
    * <br>
    * <br>
    * When set to False, overrides painting Mode.
    * <br>
    * When true, the blending uses
    * When false, {@link ITechBlend#ALPHA_2_255} is set
    * @param v
    */
   public void setAlphaBlending(boolean v) {
      isAlphaMode = v;
      if (v) {

      } else {
         blendOpImages.setAlphaMode(ITechBlend.ALPHA_2_255);
      }
   }

   /**
    * Alpha state of GraphicX object is modified to use the alpha channel of the color value. <br>
    * {@link GraphicsX#setAlpha(int)} and {@link GraphicsX#setColor(int)}
    * @param aRGB
    */
   public void setAlphaColor(int aRGB) {
      int cAlpha = (aRGB >> 24) & 0xFF;
      setAlpha(cAlpha);
      this.setColor(aRGB);
   }

   public void setAlphaColor(int a, int r, int g, int b) {
      setAlpha(a);
      setColor(r, g, b);
   }

   /**
    * Sets the {@link BlendOp} for the method {@link GraphicsX#drawRGB(int[], int, int, int, int, int, int, boolean)}
    * in {@link GraphicsX#MODE_2_RGB_IMAGE}.
    * <br>
    * Sets the blending mode for all operations.
    * <br>
    * Drawing primitive with Blend mode {@link ITechBlend#BLENDING_00_OVER}
    * @param mode
    */
   public void setBlendingModeRGB(int mode) {
      blendOpImages = new BlendOp(drc.getBOC(), mode);
   }

   public void setBlendOp(BlendOp bo) {
      if (bo != null) {
         blendOpImages = bo;
      }
   }

   /**
    * Sets the current color to the specified RGB values. <br>
    * Alpha state of graphics object is not changed <br>
    * @param RGB value is set to <b>screen</b> color and then to <b>fully opaque</b>
    * 
    */
   public void setColor(int RGB) {
      this.mycolor = RGB;
      //#debug
      //toLog().pDraw("setColor " + ColorConstants.debugColor(RGB) + " DisplayColor = " + ColorConstants.debugColor(g.getDisplayColor(RGB)), GraphicsX.class);
      if (g == null) {
         if (paintMode == MODE_4_NULL) {
            return;
         }
         setPaintMode(MODE_2_RGB_IMAGE, clipW, clipH);
      }
      //There may be a disparity between SET color and display color. For example if RGB is 255,254,254,254. 
      //This color will not match WHITE pseudoT color now but once drawn to the display it will match 255,255,255,255
      //Because for the Screen 255,254,254,254 is equal to 255,255,255,255.
      //WHY this line of code? Read the comment above.
      RGB = g.getDisplayColor(RGB);
      RGB = (255 << 24) + (RGB & 0xFFFFFF);
      //check if the color is not the pseudo transparent color
      if (isPseudoColorMode && RGB == excludeColor) {
         //change the pseudo color so that the primitive will not be confused with transparent background.
         setColorChangePseudoColor();
      }
      g.setColor(RGB);
   }

   /**
    * Sets the current color to the specified RGB values.
    * @param red
    * @param green
    * @param blue
    * 
    */
   public void setColor(int red, int green, int blue) {
      this.setColor(ColorUtils.getRGBInt(red, green, blue));
   }

   /**
    * 
    */
   private void setColorChangePseudoColor() {
      //#debug
      toDLog().pDraw("", this, GraphicsX.class, "setColorChangePseudoColor");

      pseudoSwapCount++;
      //if mode is Graphics. throw an exception
      switch (paintMode) {
         case MODE_1_IMAGE:
            //switch to RGB_IMAGE_ALPHA
            setPaintMode(MODE_2_RGB_IMAGE, imageLayer.getWidth(), imageLayer.getHeight());
            break;
         case MODE_3_RGB:
         case MODE_2_RGB_IMAGE:
            break;
         case MODE_0_SCREEN:
            throw new IllegalArgumentException("Cannot changePseudoColor in this Screen Mode");
         default:
            throw new IllegalArgumentException("Cannot changePseudoColor in this mode");
      }
      merge();
      excludeColorSwap();
      //clears the whole image layer by filling with new transparent color
      clearLayer();

   }

   public void setFillMode(boolean mode) {
      isFillMode = mode;
   }

   public void setFlagOptions(int flag, boolean v) {
      optionsFlags = BitUtils.setFlag(optionsFlags, flag, v);
   }

   /**
    * Sets the font for all subsequent text rendering operations.
    * @param font
    */
   public void setFont(IMFont font) {
      g.setFont(font);
   }

   /**
    * Resets Primitive count.
    * 
    * @param g
    */
   public void setGraphics(IGraphics g) {
      this.g = g;
      pOpaqueLayerCount = 0;
      primitiveTally = 0;
   }

   /**
    * Sets the current grayscale to be used for all subsequent rendering operations.
    * @param value
    */
   public void setGrayScale(int value) {
      g.setGrayScale(value);
   }

   /**
    * Related to 
    *  
    * @param isIgnore
    */
   public void setIgnoreClip(boolean isIgnore) {
      if (isIgnoreClip != isIgnore) {
         isIgnoreClip = isIgnore;
      }
   }

   /**
    * Sets cache values and sets {@link IGraphics} clip.
    * <br>
    * When doing a reset, change the 
    * <br>
    * Sets the clip
    * <br>
    * <br>
    * @param x value has already been translated
    * @param y
    * @param width
    * @param height
    */
   private void setMyClip(int x, int y, int width, int height, boolean isReset) {

      clipX = x;
      clipY = y;
      clipW = width;
      clipH = height;

      clipHistory.addInt(x);
      clipHistory.addInt(y);
      clipHistory.addInt(width);
      clipHistory.addInt(height);

      if (g != null) {
         g.setClip(x, y, width, height);
         //         SystemLog.pDraw("#Graphics " + debugName + " clip is " + ((isReset) ? "reset" : "set  ") + " to \t[" + g.getClipX() + "," + g.getClipY() + " " + g.getClipWidth() + "," + g.getClipHeight()
         //             + "] cached=" + toStringClip());
      }
   }

   public void setPaintCtx(int paintCtx) {
      repaintFlags = paintCtx;
   }

   public void setPaintCtxFlag(int flag, boolean b) {
      repaintFlags = BitUtils.setFlag(repaintFlags, flag, b);
   }

   /**
    * Handles the transition from a paint mode to another. TODO transfer the old data to the new mode
    * <br>
    * <br>
    * <li> {@link GraphicsX#MODE_0_SCREEN}
    * 
    * Must not be null if mode is SCREEN, ignored for other modes
    * <br>
    * <br>
    * @param mode the new mode for the GraphicsX
    * @param w the width of the canvas. Used for rotated screens.
    * @param h the height of the canvas. Used for rotated screens.
    * @param screenGraphics Not null when {@link GraphicsX#MODE_0_SCREEN}, null otherwise.
    */
   public void setPaintMode(int mode, int w, int h) {
      if (mode != paintMode) {
         switch (mode) {
            case MODE_0_SCREEN:
               setPaintModeScreen();
               break;
            case MODE_1_IMAGE:
               setPaintModeImage(w, h);
               break;
            case MODE_3_RGB:
               setPaintModeRGB();
               break;
            case MODE_2_RGB_IMAGE:
               setPaintModeRgbImage(w, h);
               break;
            default:
               break;
         }
         paintMode = mode;
      }
   }

   private void setPaintModeImage(int w, int h) {
      if (imageLayer == null || imageLayer.getHeight() != h || imageLayer.getWidth() != w) {
         imageLayer = drc.getImageFactory().createImage(w, h);
      }
      if (imageRgbData != null) {
         IGraphics gl = imageLayer.getGraphics();
         IImage img = imageRgbData.getImage();
         gl.drawImage(img, 0, 0, ANCHOR);
         imageRgbData.dispose();
         imageRgbData = null;
      }
      g = imageLayer.getGraphics();
      g.translate(translateX, translateY);
      isAlphaMode = false;
      isPseudoColorMode = false;
   }

   private void setPaintModeRGB() {
      if (imageLayer != null) {
         //TODO
         //rgbImage = RgbImage.createRgb(layer);
      } else {

      }
      imageLayer = null;
      g = null;
   }

   private void setPaintModeRgbImage(int w, int h) {
      //make sure we have a RgbImage of Canvas Size
      if (imageRgbData == null) {
         imageRgbData = drc.getCache().create(w, h);
      } else {
         imageRgbData.changeDimension(w, h);
      }
      if (imageLayer == null) {
         imageLayer = createImageLayer(w, h);
      }
      g = imageLayer.getGraphics();
      g.translate(translateX, translateY);
      isAlphaMode = true;
   }

   /**
    * The {@link IGraphics} object is set with the reset
    * each time a reference is process for rendering
    * {@link GraphicsX#reset(IGraphics)}
    */
   private void setPaintModeScreen() {
      if (imageRgbData != null) {
         imageRgbData.dispose();
      }
      imageLayer = null;
      //update the translation
      g.translate(translateX, translateY);
      isAlphaMode = false;
      isPseudoColorMode = false;
   }

   /**
    * Sets the stroke style used for drawing lines, arcs, rectangles, and rounded rectangles.
    * <li>
    * @param style
    */
   public void setStrokeStyle(int style) {
      g.setStrokeStyle(style);
   }

   public void setSwitchOff(int flag, boolean v) {
      switches = BitUtils.setFlag(switches, flag, v);
   }

   /**
    * Force the translation by setting values
    * @param x
    * @param y
    */
   public void setTranslationForce(int x, int y) {
      this.translateX = x;
      this.translateY = y;
   }

   /**
    * Translates the origin of the graphics context to the point (x, y) in the current coordinate system.
    * RGB operation
    * <br>
    * <br>
    * Modifies existing cached clip info? No. All clip info is relative to origin of graphics destination, i.e RgbImage.
    * @param x
    * @param y
    */
   public void setTranslationShift(int x, int y) {
      //#debug
      toDLog().pFlow("Before trX=" + translateX + "(" + x + ") trY=" + translateY + " (" + y + ")" + ToStringStaticDrawx.toStringPaintMode(paintMode), this, GraphicsX.class, "setTranslationShift", LVL_05_FINE, true);
      translateX += x;
      translateY += y;
      //modify the clip roots ? why?

      //SystemLog.pDraw("GraphicsX#translate After trX=" + trX + "(" + x + ") trY=" + trY + " (" + y + ")" + debugPaintMode(paintMode));
      //      if (g != null) {
      //         //SystemLog.pDraw("Translation Before x=" + g.getTranslateX() + " y=" + g.getTranslateY());
      //         g.translate(x, y);
      //         //SystemLog.pDraw("Translation After x=" + g.getTranslateX() + " y=" + g.getTranslateY());
      //      }
   }

   public void setVirgin(int x, int y, int w, int h) {
      // TODO Auto-generated method stub

   }

   /**
    * Starts a tick count session to help code see if the figure takes lots of time and primitive calls.
    * <br>
    * The code may decide it is worth caching the figure.
    * <br>
    * A tick is cleared when the clear method is called
    */
   public void tickStart() {
      tickTime = System.currentTimeMillis();
   }

   public long tickTime() {
      return System.currentTimeMillis() - tickTime;
   }

   public void toggleBlendBack() {
      blendOpImages = saved;
   }

   public void toggleBlendTo(BlendOp beo) {
      saved = blendOpImages;
      blendOpImages = beo;
   }

   /**
    * Returns the new value of the flag
    * 
    * <li> {@link ITechGraphicsX#SWITCHOFF_1_GRADIENT}
    * <li> {@link ITechGraphicsX#SWITCHOFF_2_TEXT_EFFECTS}
    * <li> {@link ITechGraphicsX#SWITCHOFF_3_TEXT_EFFECTS}
    * <li> {@link ITechGraphicsX#SWITCHOFF_4_BUSINESS}
    * <li> {@link ITechGraphicsX#SWITCHOFF_5_PAINT}
    * 
    * @param flag
    * @return
    */
   public boolean toggleSwitchOff(int flag) {
      boolean v = !BitUtils.hasFlag(switches, flag);
      switches = BitUtils.setFlag(switches, flag, v);
      return v;
   }

   public void toString(Dctx dc) {
      dc.root(this, GraphicsX.class, 2390);
      toStringPrivate(dc);
      super.toString(dc.sup());

      dc.appendVarWithSpace("isPseudoColorMode", isPseudoColorMode);
      dc.appendVarWithSpace("isAlphaMode", isAlphaMode);
      dc.appendVarWithSpace("isFillMode", isFillMode);
      dc.appendVarWithSpace("isIgnoreClip", isIgnoreClip);

      dc.line();

      dc.appendVarWithSpace("hasAlpha", hasAlpha);
      dc.appendVarWithSpace("alpha", alpha);
      dc.appendColorWithSpace("ActiveColor", mycolor);

      dc.line();
      dc.appendVarWithSpace("pseudoSwapCount", pseudoSwapCount);
      dc.appendVarWithSpace("mergeCount", mergeCount);
      dc.appendVarWithSpace("pOpaqueLayerCount", pOpaqueLayerCount);
      dc.appendVarWithSpace("primitiveTally", primitiveTally);
      dc.appendVarWithSpace("rgbCount", rgbCount);

      dc.line();
      toStringClipInfo(dc);

      dc.line();
      toStringTranslate(dc);

      dc.nlLvl(blendOpImages, "blendOpImages");
      dc.nlLvl(imageLayer, "ImageLayer");
      dc.nlLvl(imageRgbData, "imageRgbData");

      //we don't want font stuff here
      dc.setFlagData(drc, IFlagsToStringDrw.D_FLAG_28_IGNORE_FONT, true);
      if (dc.hasFlagData(drc, IFlagsToStringDrw.D_FLAG_25_IGNORE_IGRAPHICS)) {
         dc.line();
         dc.append(" [IGraphics Ignored]");
      } else {
         dc.nlLvl(g, "IGraphics");
      }
   }

   public void toString1Line(Dctx dc) {
      dc.root1Line(this, GraphicsX.class);
      toStringPrivate(dc);
      super.toString1Line(dc.sup1Line());
   }

   public String toStringClip() {
      Dctx dc = new Dctx(toStringGetUCtx());
      toStringClip(dc);
      return dc.toString();
   }

   public void toStringClip(Dctx dc) {
      dc.append("[");
      dc.append(clipX);
      dc.append(",");
      dc.append(clipY);
      dc.append(" ");
      dc.append(clipW);
      dc.append(",");
      dc.append(clipH);
      dc.append("]");
      dc.appendVarWithSpace("Index", clipIndex);
   }

   public void toStringClipInfo(Dctx dc) {
      dc.append("CurrentClip");
      toStringClip(dc);

      int numClips = (clipIndex / 4);
      dc.append(" Clip#" + numClips);
      int v = clipIndex;
      for (int i = 0; i < numClips; i++) {
         dc.tab();
         int newH = clipList[v - 1];
         int newW = clipList[v - 2];
         int newY = clipList[v - 3];
         int newX = clipList[v - 4];
         dc.line();
         dc.append("#");
         dc.append(i + 1);
         dc.append("[");
         dc.append(newX);
         dc.append(",");
         dc.append(newY);
         dc.append(" ");
         dc.append(newW);
         dc.append(",");
         dc.append(newH);
         dc.append("]");
         v -= 4;
         dc.tabRemove();
      }
   }

   private void toStringPrivate(Dctx dc) {
      dc.appendVarWithSpace("paintMode", ToStringStaticDrawx.toStringPaintMode(paintMode));
   }

   /**
    * Normal that Graphics values differ.
    * @return
    */
   public String toStringTranslate() {
      Dctx dc = new Dctx(toStringGetUCtx());
      toStringTranslate(dc);
      return dc.toString();
   }

   public String toStringColor() {
      return g.toStringGetUCtx().getColorU().toStringColor(mycolor);
   }

   public void toStringTranslate(Dctx dc) {
      dc.append("Translate On GraphicsX ");
      dc.appendVarWithSpace("translateX", translateX);
      dc.appendVarWithSpace("translateY", translateY);
      if (g != null) {
         dc.append("Translation on IGraphics [");
         dc.append(g.getTranslateX());
         dc.append(",");
         dc.append(g.getTranslateY());
         dc.append("]");
      }
   }
   //#enddebug

}
