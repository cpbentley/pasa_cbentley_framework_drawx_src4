package pasa.cbentley.framework.drawx.src4.factories.interfaces;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.core.interfaces.IByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesDrw;
import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;

public interface IBOFigure extends IByteObject {

   /**
    * 1 byte for fig type
    * 1 byte for existence flag
    * 1 byte for perf flag
    * 4 bytes for root color
    * 1 byte for flagx
    */
   public static final int FIG__BASIC_SIZE               = A_OBJECT_BASIC_SIZE + 11;

   /**
    * Type of figure (circle, losange, line, etc)
    */
   public static final int FIG__OFFSET_01_TYPE1          = A_OBJECT_BASIC_SIZE;

   /**
    * Flag shared by all figures. Existence Flag for 
    */
   public static final int FIG__OFFSET_02_FLAG           = A_OBJECT_BASIC_SIZE + 1;

   /**
    * Perf flag for internal use
    */
   public static final int FIG__OFFSET_03_FLAGP          = A_OBJECT_BASIC_SIZE + 2;

   /**
    * 
    */
   public static final int FIG__OFFSET_04_FLAGX          = A_OBJECT_BASIC_SIZE + 3;

   /**
    * <b>4 bits</b> for dir type def
    * <li> {@link C#DIR_0_TOP}
    * <li> {@link C#DIR_1_BOTTOM}
    * <li> {@link C#DIR_2_LEFT}
    * <li> {@link C#DIR_3_RIGHT}
    * <li> {@link C#DIR_4_TopLeft}
    * <li> {@link C#DIR_5_TopRight}
    * <li> {@link C#DIR_6_BotLeft}
    * <li> {@link C#DIR_7_BotRight}
    * <br>
    * <br>
    * 
    * <b>4 bits</b> for 
    * Different type of figure direction <br>
    * <li>Undefined. Nothing can be said [0] full value.
    * <li>Neutral [1] Figure can be flipped without modifying it
    * <li>TBLR -> T[0] B[1] L[2] R[3] - [2]
    * <li>bi TBLR -> TL[0] TR[1] BL[2] BR[3] - [3]
    * <li>Vector: Horizontal/Vertical [4]
    * <br>
    * <br>
    * Those last bits tells the cache engine about the nature of the figure.
    * <br>
    * <br>
    * {@link IBOTypesDrw#TYPE_059_GRADIENT} often modifies Figure's perceptual direction.
    * <br>
    * If Gradient exists, engine must ask the gradient definition sheet for gradient direction
    * For the Rectangle figure, the only neutral gradient is {@link GRADIENT_TYPE_RECT_0SQUARE}.
    * <br>
    * <br>
    * <b>Parametrized Drawing</b> 
    * <br>
    * Draw a figure
    * Call the dynamic direction in the {@link ByteObjectFig#paintFigure(GraphicsX, int, int, int, int, DrwParam)} call.
    * Tag figure as being a transformation of a cached figure.
    * <br>
    * TL is drawn and flag ask to cache it.
    * TR is drawn with flag cache seek. get cache and draw with transform
    * Same with BL and BR. Provided of course dimension allow for the transform to happen.
    */
   public static final int FIG__OFFSET_05_DIR1           = A_OBJECT_BASIC_SIZE + 4;

   /**
    * offset of figure's main color or pointer to color serie
    */
   public static final int FIG__OFFSET_06_COLOR4         = A_OBJECT_BASIC_SIZE + 5;

   /**
    * Blend
    */
   public static final int FIG__OFFSET_07_BLENDER1       = A_OBJECT_BASIC_SIZE + 9;

   public static final int FIG__OFFSET_08_BLENDER_ALPHA1 = A_OBJECT_BASIC_SIZE + 10;

   /**
    * Set for 32bits anchor definition
    * Anchor defines position of figure relative position FILL,CENTER,BOTTOM,LEFT etc))
    * In addition to the intuitive semantics of "Anchor",
    * our anchor definition has a dimension (width and height)
    * Default anchor of a figure is an implicit FILL. That is the Figure definition will only be drawn with
    * an absolute xywh area. The Box Model will provide those values. So a Figure with a 32Bits anchor only has meaning
    * within a Box.
    * The Box may ask the figure for its preferred dimension. 0 is returned unless anchor has a W or H definition
    * When the Box
    */
   public static final int FIG_FLAG_1_ANCHOR             = 1 << 0;

   public static final int FIG_FLAG_2_GRADIENT           = 1 << 1;

   /**
    * Set this flag to true if the figure should be cached for performant
    * repetitive drawing. If not set, figure is not cached
    */
   public static final int FIG_FLAG_3_COLOR_ARRAY        = 1 << 2;

   /**
    */
   public static final int FIG_FLAG_4_MASK               = 1 << 3;

   /**
    * When set, the figure has a color filter in the ByteObject array
    */
   public static final int FIG_FLAG_5_FILTER             = 1 << 4;

   /**
    * Flag set when the figure defines one or several Animation {@link ByteObject} of type {@link IBOTypesDrw#TYPE_ANIMATION}.
    * 
    */
   public static final int FIG_FLAG_6_ANIMATED           = 1 << 5;

   /**
    * Flag telling sub figures parameters are defined after the optional anchor definition. <br>
    * <br>
    * Sub figures are defined and will be drawn along specified anchors. <br>
    */
   public static final int FIG_FLAG_7_SUB_FIGURE         = 1 << 6;

   /**
    * Flag if this figure definition supports Directional drawing.
    * <li> {@link ITechFigure#FIG_TYPE_12_ARROW}
    * <li> {@link ITechFigure#FIG_TYPE_03_TRIANGLE}
    * <br>
    * <br>
    * Direction offset is always the first offset after root + flag.
    * <br>
    * <br>
    *  TODO to be decided
    * {@link IBOFigTriangle#FIG_TRIANGLE_OFFSET_03_ANGLE2}
    * 
    */
   public static final int FIG_FLAG_8_TBLR_DIR           = 1 << 7;

   /**
    * If set, the figure creates blank int[] array and build itself on it with no primitives.
    * <br>
    * If not set, the figure is assumed to use primitive functions of the GraphicsX context
    * <br>
    * <b>Why use it</b>? Because {@link GraphicsX} may optimize by taking the int[] array directly from the destination.
    * <br>Thus saving the creation of a buffer.
    */
   int FIG_FLAGP_1RGB                     = 1;

   /**
    * Set when figure has extra boundary pixels. <br>
    * <br>
    * Thus figure draws around the rectangle area. This is an exception to the tacit rule a figure should
    * stay within its designated rectangle area.
    * <br>
    * <br>
    * <b>Examples of out of boundary figures</b> : <br>
    * <li> the outer border
    * <li> smoke screen
    * <li> losange with excessive overstep and no clipping
    * <br>
    * <br>
    * Those figures imply an overhead to the painting cycle in partial repaints cases.
    */
   int FIG_FLAGP_2EXTRA_BOUNDARY          = 2;

   /**
    * Set this flag when the figure will cover the whole rectangle area with opaque pixels.
    * This flag allows to draw the figure once and then copy area to another position without jeopardizing
    * the GraphicsX.
    * Typically a triangle or ellipse will never have this flag set
    */
   int FIG_FLAGP_3OPAQUE                  = 4;

   /**
    * Flags this figure definition as being a subfigure of another
    */
   int FIG_FLAGP_4SUBFIGURE               = 8;

   /**
    * When this flag is set, figure does not have any alpha channel data. <br>
    * Concerning colors, figure is opaque.is ignored from figure color.
    * <br>
    * From gradient?
    * Opaqueness depends on color and shape.
    */
   int FIG_FLAGP_5IGNORE_ALPHA            = 16;

   /**
    * Some figure are unable to fit an area. It computes the closest fitting area
    * by reducing, correctly aligns it and draws itself.
    * Those figure may have a ByteObject anchor or else they are centered
    * They must be able to compute their dimension based given w/h
    */
   int FIG_FLAGP_6NO_FIT                  = 32;

   /**
    * The figure is able to set the area for translucent pixels.<br>
    * A Square shaped border is not opaque but can be made to fill 
    * its center area with transparent pixels.
    */
   int FIG_FLAGP_7TRANS_FIG               = 64;

   /**
    * Figure will be postponed when drawn within the Drawable framework 
    */
   int FIG_FLAGP_8POSTPONE                = 128;

   /**
    * Function that modifies the area given the paint method of the figure.
    */
   int FIG_FLAGX_1AREA_FUNCTION           = 1;

   /**
    * When set, paint method of figure clips on the figure area during the figure's drawing process.
    * <br>
    * Extra boundary pixels will thus not be drawn.
    */
   int FIG_FLAGX_2CLIP                    = 2;

   /**
    * Genetic flag set when figure's shape covers the whole area
    */
   int FIG_FLAGX_3OPAQUE_SHAPE            = 4;

   /**
    * When colors from figure and gradient all drawn opaque.
    */
   int FIG_FLAGX_4OPAQUE_COLORS           = 8;

   /**
    * When the figure is loaded with a {@link IBOTypesDrw#TYPE_055_SCALE} object.
    * <br>
    * <br>
    * 
    */
   int FIG_FLAGX_5_SCALER                 = 1 << 4;

   /**
    * When set, enables hardware anti aliasing in {@link ITechHostDrawer#HOST_FLAGX_1_ANTI_ALIAS}
    * <br>
    * Why setting a specific implementation setting here? TODO
    */
   int FIG_FLAGX_6_HARDWARE_OP            = 1 << 5;

}
