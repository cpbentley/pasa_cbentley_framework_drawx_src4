package pasa.cbentley.framework.drawx.src4.factories.interfaces;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.core.interfaces.IByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.byteobjects.src4.objects.color.IBOBlend;
import pasa.cbentley.byteobjects.src4.objects.color.IBOFilter;
import pasa.cbentley.byteobjects.src4.objects.color.ITechGradient;
import pasa.cbentley.byteobjects.src4.objects.pointer.IBOMergeMask;
import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.framework.coredraw.src4.interfaces.IGraphics;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechFeaturesDraw;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.factories.FigureOperator;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
import pasa.cbentley.framework.drawx.src4.tech.ITechMergeMaskFigure;

public interface IBOFigure extends IByteObject {

   /**
    * 1 byte for fig type
    * 1 byte for existence flag
    * 1 byte for perf flag
    * 4 bytes for root color
    * 1 byte for flagx
    */
   public static final int FIG__BASIC_SIZE             = A_OBJECT_BASIC_SIZE + 10;

   /**
    * Type of figure (circle, losange, line, etc)
    * 
    * <li> {@link ITechFigure#FIG_TYPE_01_RECTANGLE}
    * <li> {@link ITechFigure#FIG_TYPE_02_BORDER}
    * <li> {@link ITechFigure#FIG_TYPE_03_TRIANGLE}
    * <li> {@link ITechFigure#FIG_TYPE_04_CHAR}
    * <li> {@link ITechFigure#FIG_TYPE_05_LINE}
    * <li> {@link ITechFigure#FIG_TYPE_06_LOSANGE}
    * <li> {@link ITechFigure#FIG_TYPE_07_ELLIPSE}
    * <li> {@link ITechFigure#FIG_TYPE_08_GERMANCROSS}
    * <li> {@link ITechFigure#FIG_TYPE_09_PIXELS}
    * <li> {@link ITechFigure#FIG_TYPE_10_STRING}
    * <li> {@link ITechFigure#FIG_TYPE_11_GRID}
    * <li> {@link ITechFigure#FIG_TYPE_12_ARROW}
    */
   public static final int FIG__OFFSET_01_TYPE1        = A_OBJECT_BASIC_SIZE;

   /**
    * Flag shared by all figures. Existence Flag for 
    * <li>  {@link IBOFigure#FIG_FLAG_1_ANCHOR}
    * <li>  {@link IBOFigure#FIG_FLAG_2_GRADIENT}
    * <li>  {@link IBOFigure#FIG_FLAG_3_COLOR_ARRAY}
    * <li>  {@link IBOFigure#FIG_FLAG_4_MASK}
    * <li>  {@link IBOFigure#FIG_FLAG_5_FILTER}
    * <li>  {@link IBOFigure#FIG_FLAG_6_ANIMATED}
    * <li>  {@link IBOFigure#FIG_FLAG_7_SUB_FIGURE}
    * <li>  {@link IBOFigure#FIG_FLAGZ_8_DIRECTION}
    */
   public static final int FIG__OFFSET_02_FLAG         = A_OBJECT_BASIC_SIZE + 1;

   /**
    * Perf flag for internal use
    */
   public static final int FIG__OFFSET_03_FLAGP        = A_OBJECT_BASIC_SIZE + 2;

   /**
    * 
    */
   public static final int FIG__OFFSET_04_FLAGX        = A_OBJECT_BASIC_SIZE + 3;

   /**
    * 8 bits to define axis or directions.
    * 
    * <p>
    * <b>4 bits</b> for dir type def
    * <li> {@link C#DIR_0_TOP}
    * <li> {@link C#DIR_1_BOTTOM}
    * <li> {@link C#DIR_2_LEFT}
    * <li> {@link C#DIR_3_RIGHT}
    * <li> {@link C#DIR_4_TopLeft}
    * <li> {@link C#DIR_5_TopRight}
    * <li> {@link C#DIR_6_BotLeft}
    * <li> {@link C#DIR_7_BotRight}
    * </p>
    * 
    * 
    * <p>
    * 
    * <b>4 bits</b> for 
    * Different type of figure direction <br>
    * <li>Undefined. Nothing can be said [0] full value.
    * <li>Neutral [1] Figure can be flipped without modifying it
    * <li>TBLR -> T[0] B[1] L[2] R[3] - [2]
    * <li>bi TBLR -> TL[0] TR[1] BL[2] BR[3] - [3]
    * <li>Vector: Horizontal/Vertical [4]
    * </p>
    * 
    * 
    * Those last bits tells the cache engine about the nature of the figure.
    * {@link IBOTypesBOC#TYPE_038_GRADIENT} often modifies Figure's perceptual direction.
    * <br>
    * If Gradient exists, engine must ask the gradient definition sheet for gradient direction
    * For the Rectangle figure, the only neutral gradient is {@link GRADIENT_TYPE_RECT_0SQUARE}.
    * <p>
    * 
    * <b>Parametrized Drawing</b><br>
    * 
    * Call the dynamic direction in the {@link FigureOperator#paintFigureDir(GraphicsX, int, int, int, int, ByteObject, int)} call.
    * 
    * Tag figure as being a transformation of a cached figure.
    * </p>
    * 
    * TL is drawn and flag ask to cache it.
    * TR is drawn with flag cache seek. get cache and draw with transform
    * Same with BL and BR. Provided of course dimension allow for the transform to happen.
    */
   public static final int FIG__OFFSET_05_DIR1         = A_OBJECT_BASIC_SIZE + 4;

   /**
    * offset of figure's main color or pointer to color serie.
    * 
    * <p>
    * MM: {@link IBOMergeMask#MERGE_MASK_OFFSET_5VALUES1} with {@link ITechMergeMaskFigure#MM_VALUES5_FLAG_2_COLOR}
    * </p>
    * 
    */
   public static final int FIG__OFFSET_06_COLOR4       = A_OBJECT_BASIC_SIZE + 5;

   /**
    * <li>{@link IBOFigure#FIG_FLAGZ_1_DEFINED_BLENDER}
    */
   public static final int FIG__OFFSET_07_FLAGZ1       = A_OBJECT_BASIC_SIZE + 9;

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
   public static final int FIG_FLAG_1_ANCHOR           = 1 << 0;

   public static final int FIG_FLAG_2_GRADIENT         = 1 << 1;

   /**
    * Set this flag to true if the figure should be cached for performant
    * repetitive drawing. If not set, figure is not cached
    */
   public static final int FIG_FLAG_3_COLOR_ARRAY      = 1 << 2;

   /**
    * When true, a {@link IBOMask} is defined.
    */
   public static final int FIG_FLAG_4_MASK             = 1 << 3;

   /**
    * When set, the figure has a color filter {@link IBOFilter} in the ByteObject array
    */
   public static final int FIG_FLAG_5_FILTER           = 1 << 4;

   /**
    * Flag reserved to be used by another module that defines animations.
    * <p>
    * 
    * </p>
    */
   public static final int FIG_FLAG_6_ANIMATED         = 1 << 5;

   /**
    * Flag telling sub figures parameters are defined after the optional anchor definition. <br>
    * 
    * <p>
    * Sub figures are defined and will be drawn along specified anchors.     * 
    * </p>
    */
   public static final int FIG_FLAG_7_SUB_FIGURE       = 1 << 6;

   public static final int FIG_FLAG_8_                 = 1 << 7;

   /**
    * If set, the figure creates blank int[] array and build itself on it with no primitives.
    * <br>
    * If not set, the figure is assumed to use primitive functions of the GraphicsX context
    * <br>
    * <b>Why use it</b>? Because {@link GraphicsX} may optimize by taking the int[] array directly from the destination.
    * <br>Thus saving the creation of a buffer.
    */
   public static final int FIG_FLAGP_1_RGB             = 1 << 0;

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
   public static final int FIG_FLAGP_2_EXTRA_BOUNDARY  = 1 << 1;

   /**
    * Set this flag when the figure will cover the whole rectangle area with opaque pixels.
    * This flag allows to draw the figure once and then copy area to another position without jeopardizing
    * the GraphicsX.
    * Typically a triangle or ellipse will never have this flag set.
    * Performance flag set when figure's shape covers the whole rectangular area
    */
   public static final int FIG_FLAGP_3_OPAQUE          = 1 << 2;

   /**
    * Flags this figure definition as being a subfigure of another
    */
   public static final int FIG_FLAGP_4_IS_SUBFIGURE    = 1 << 3;

   /**
    * When this flag is set, figure does not have any alpha channel data. <br>
    * Concerning colors, figure is opaque.is ignored from figure color.
    * <br>
    * From gradient?
    * Opaqueness depends on color and shape.
    */
   public static final int FIG_FLAGP_5_IGNORE_ALPHA    = 1 << 4;

   /**
    * Some figure are unable to fit an area. It computes the closest fitting area
    * by reducing, correctly aligns it and draws itself.
    * Those figure may have a ByteObject anchor or else they are centered
    * They must be able to compute their dimension based given w/h.
    */
   public static final int FIG_FLAGP_6_NO_FIT          = 1 << 5;

   /**
    * The figure is able to set the area for translucent pixels.
    * 
    * A Square shaped border is not opaque but can be made to fill 
    * its center area with transparent pixels.
    */
   public static final int FIG_FLAGP_7_TRANS_FIG       = 1 << 6;

   /**
    * Figure will be postponed when drawn within the Drawable framework 
    */
   public static final int FIG_FLAGP_8_POSTPONE        = 1 << 7;

   /**
    * Function that modifies the area given the paint method of the figure.
    */
   public static final int FIG_FLAGX_1_AREA_FUNCTION   = 1 << 0;

   /**
    * When set, paint method of figure clips on the figure area during the figure's drawing process.
    * <br>
    * Extra boundary pixels will thus not be drawn.
    */
   public static final int FIG_FLAGX_2_CLIP            = 1 << 1;

   /**
    */
   public static final int FIG_FLAGX_3_                = 1 << 2;

   /**
    * When colors from figure and gradient all drawn opaque.
    */
   public static final int FIG_FLAGX_4_OPAQUE_COLORS   = 1 << 3;

   /**
    * {@link IBOScaler} is defined
    * 
    * When the figure is loaded with a {@link IBOTypesDrawX#TYPE_DRWX_05_SCALE} object.
    * 
    * 
    */
   public static final int FIG_FLAGX_5_SCALER          = 1 << 4;

   public static final int FIG_FLAGX_6_                = 1 << 5;

   /**
    * Flag forces host anti alias ON for this figure. 
    * 
    * <p>
    * 
    * Why setting a specific implementation setting here? 
    * <li> Debug purpose
    * <li> Special effects when mixing different figures some with alias, some without alias.
    * </p>
    * 
    * <p>
    * 
    * For the Host
    * <li>{@link GraphicsX#getGraphics()}
    * <li> {@link IGraphics#featureEnable(int, boolean)} with feature {@link ITechFeaturesDraw#SUP_ID_04_ALIAS}
    * </p>
    */
   public static final int FIG_FLAGX_7_ALIAS_ON        = 1 << 6;

   /**
    * Flag forces host anti alias OFF for this figure. 
    */
   public static final int FIG_FLAGX_8_ALIAS_OFF       = 1 << 7;

   /**
    * {@link IBOBlend} is defined
    */
   public static final int FIG_FLAGZ_1_DEFINED_BLENDER = 1 << 0;

   public static final int FIG_FLAGZ_2_                = 1 << 1;

   public static final int FIG_FLAGZ_3_                = 1 << 2;

   public static final int FIG_FLAGZ_4_                = 1 << 3;

   public static final int FIG_FLAGZ_5_                = 1 << 4;

   public static final int FIG_FLAGZ_6_                = 1 << 5;

   /**
    * Flag if this figure definition supports axis drawing using field {@link IBOFigure#FIG__OFFSET_05_DIR1}.
    * 
    * Axis is defined by
    * <li> {@link C#AXIS_0_VERTICAL}
    * <li> {@link C#AXIS_1_HORIZONTAL}
    * <li> {@link C#AXIS_2_ASCENDING}
    * <li> {@link C#AXIS_3_DESCENDING}
    * 
    * <p>
    * Examples of figures with axis
    * <li> {@link ITechFigure#FIG_TYPE_01_RECTANGLE} with {@link ITechGradient#GRADIENT_TYPE_RECT_01_HORIZ}
    * <li> {@link ITechFigure#FIG_TYPE_01_RECTANGLE} with {@link ITechGradient#GRADIENT_TYPE_RECT_02_VERT}
    * </p>
    * 
    */
   public static final int FIG_FLAGZ_7_AXIS            = 1 << 6;

   /**
    * Flag if this figure definition supports Directional drawing using field {@link IBOFigure#FIG__OFFSET_05_DIR1}.
    * 
    * <p>
    * Examples of figures with directions
    * <li> {@link ITechFigure#FIG_TYPE_12_ARROW}
    * <li> {@link ITechFigure#FIG_TYPE_03_TRIANGLE}
    * </p>
    * 
    * 
    */
   public static final int FIG_FLAGZ_8_DIRECTION       = 1 << 7;

}
