package pasa.cbentley.framework.drawx.src4.tech;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.interfaces.ITech;
import pasa.cbentley.core.src4.utils.ColorUtils;
import pasa.cbentley.framework.coredraw.src4.interfaces.IGraphics;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;

public interface ITechGraphicsX extends ITech {

   public static final int ANCHOR                        = IGraphics.TOP | IGraphics.LEFT;

   /**
    * Uses mostly as most of the time, parent drawable clipping context supersedes children.
    */
   public static final int CLIP_DIRECTIVE_0_INTERSECTION = 0;

   /**
    * It is rarely override, since a Drawable cannot assume he knows in which context it is drawn.
    */
   public static final int CLIP_DIRECTIVE_1_OVERRIDE     = 1;

   public static final int CLIP_INDEX_INCREMENT          = 6;

   /**
    * The GraphicsX Object is a simple adapter to the {@link IGraphics} object of the {@link ICanvasHost}. <br>
    * Only mode which doesn't have a RgbImage attached.<br>
    * Default Mode.<br>
    * <li>No Rotations
    * <li>No Pixel Queries
    * <li>No Erase
    * <li>No Blending Modes
    * <li>Yes Opaque Background
    * <li>Yes Alpha Primitive with shadow RGB_IMAGE mode.
    * <li>Yes Keep Old. Cheap advantage of this mode. What was drawn in the last painting cycle is not erased automatically
    * <br>
    * <br>
    * Animation Status: The whole painting cycle is redrawn at each animation repaint.<br>
    * Drawable caches may allievate the drawing overhead <br>
    * <br>
    * Transition to Image and RgbImage is possible<br>
    */
   public static final int MODE_0_SCREEN                 = 0;

   /**
    * Draws everything on an intermediary Image object. The {@link RgbImage} is not used. 
    * <br>
    * <br>
    * <li>Yes Rotations
    * <li>Yes Keep Old => everything must be repaint on intermediary layer at each paint cycle
    * <li>Yes Pixel Queries
    * <br>
    * <br>
    * Uses:<br>
    * <li>when a snapshot of the screen is needed.
    * <li>when a screen rotation is used
    * <br>
    * <br>
    * The {@link GraphicsX} of a mutable white image will start with this mode. 
    * <br>
    * <br>
    * With white as pseudo transparent, when an Alpha color is set, a RGB background layer is created and mode goes to RGB_IMAGE
    */
   public static final int MODE_1_IMAGE                  = 1;

   /**
    * {@link RgbImage} and a Layer to draw on RgbImage int array.
    * <br>
    * <br>
    * This is the most complete mode.
    * <br>
    * Image Layer is used to capture primitives. Primitives are merged with the RGB array using the current blending Mode.
    * This mode makes semi-transparent primitives possible.
    * <br>
    * Alpha mode is enabled by default
    * <br>
    * When the Paint Cycle doesn't know at the start if alpha primitives will be needed. SCREEN mode is fine. A switch to
    * a shadow RGB_IMAGE mode will be made
    */
   public static final int MODE_2_RGB_IMAGE              = 2;

   /**
    * Mode that starts only with a RGB array layer. Code uses this mode when only RGB operations are foreseen.
    * <br>
    * <br>
    * It requires less memory as no Layer object is created. 
    * <br>
    * <br>
    * When a call to a primitive method is made, GraphicsX goes to RGB_IMAGE mode automatically.
    * <br>
    */
   public static final int MODE_3_RGB                    = 3;

   public static final int MODE_4_NULL                   = 4;

   public static final int MODE_5_MAX_MODULO             = 5;

   /**
    * 
    */
   public static final int OPTION_1_PASS_TRHOUGH         = 1;

   public static final int PRIMITIVE_COLOR_ALT           = ColorUtils.FULLY_OPAQUE_CYAN;

   public static final int PRIMITIVE_COLOR_BASE          = ColorUtils.FULLY_OPAQUE_PURPLE;

   public static final int STROKE_0_FILL                 = 0;

   public static final int STROKE_1_DOTTED               = 1;

   /**
    * Temporarily disable the Gradients on figures drawn by
    * 
    * {@link GraphicsX#drawFigure(ByteObject, int, int, int, int)}
    */
   public static final int SWITCHOFF_1_GRADIENT          = 1;

   public static final int SWITCHOFF_2_TEXT_EFFECTS      = 1 << 1;

   /**
    * OVER blending operator when CYAN is the pseudo color.
    * It will merge the top layer to the RGB array ignoring CYAN pixels in the top layer.
    * <br>
    * Does not solve the semi alpha transparencies?
    */
   //public BlendOpGraphicsX bgOpCyan;

   /**
    * OVER blending operator when PURPLE is the pseudo color.
    * It will merge the top layer to the RGB array ignoring WHITE pixels in the top layer
    */
   //public BlendOpGraphicsX bgOpPurple;

   public static final int SWITCHOFF_3_TEXT_EFFECTS      = 1 << 2;

   /**
    * Blending mode decides how pixels from the top layer will be merged with the pixels of the RGB array.
    * Blending operation occurs during merges using a blend operator.
    * <br>
    * TODO for the most commonly used and default blending mode, use optimized loop.
    */
   //int                     blendingMode      = BlendOp.BLENDING_00_OVER;

   /**
    * Blending for the primitive who are impacted by the setColor method.
    * <br>
    * Starts with Pink exclude.
    * <br>
    * When blending the primitive with the RGB array, the pixel with the exclude color are ignored.
    */
   //private BlendOpGX    blendOpForPrimitive;

   /**
    * allows the caller of a method to make initialize work and force the early return of a method
    * Method implementator decides what to do
    */
   public static final int SWITCHOFF_4_BUSINESS          = 1 << 3;

   public static final int SWITCHOFF_5_PAINT             = 1 << 4;
}
