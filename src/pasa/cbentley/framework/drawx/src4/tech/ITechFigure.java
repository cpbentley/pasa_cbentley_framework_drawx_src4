/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.tech;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.tech.ITechByteObject;
import pasa.cbentley.core.src4.interfaces.C;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechFont;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.string.ITechStrFx;

public interface ITechFigure extends ITechByteObject {
   /**
    * 1 byte for fig type
    * 1 byte for existence flag
    * 1 byte for perf flag
    * 4 bytes for root color
    * 1 byte for flagx
    */
   public static final int FIG__BASIC_SIZE                    = A_OBJECT_BASIC_SIZE + 11;

   /**
    * Type of figure (circle, losange, line, etc)
    */
   public static final int FIG__OFFSET_01_TYPE1               = A_OBJECT_BASIC_SIZE;

   /**
    * Flag shared by all figures. Existence Flag for 
    */
   public static final int FIG__OFFSET_02_FLAG                = A_OBJECT_BASIC_SIZE + 1;

   /**
    * Perf flag for internal use
    */
   public static final int FIG__OFFSET_03_FLAGP               = A_OBJECT_BASIC_SIZE + 2;

   /**
    * 
    */
   public static final int FIG__OFFSET_04_FLAGX               = A_OBJECT_BASIC_SIZE + 3;

   /**
    * <b>4 bits</b> for dir type def
    * <li> {@link C#DIR_0TOP}
    * <li> {@link C#DIR_1BOTTOM}
    * <li> {@link C#DIR_2LEFT}
    * <li> {@link C#DIR_3RIGHT}
    * <li> {@link C#DIR_4TopLeft}
    * <li> {@link C#DIR_5TopRight}
    * <li> {@link C#DIR_6BotLeft}
    * <li> {@link C#DIR_7BotRight}
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
   public static final int FIG__OFFSET_05_DIR1                = A_OBJECT_BASIC_SIZE + 4;

   /**
    * offset of figure's main color or pointer to color serie
    */
   public static final int FIG__OFFSET_06_COLOR4              = A_OBJECT_BASIC_SIZE + 5;

   /**
    * Blend
    */
   public static final int FIG__OFFSET_07_BLENDER1            = A_OBJECT_BASIC_SIZE + 9;

   public static final int FIG__OFFSET_08_BLENDER_ALPHA1      = A_OBJECT_BASIC_SIZE + 10;

   public static final int FIG_4TRIG_BASIC_SIZE               = 0;

   public static final int FIG_ARLEQUIN_BASIC_SIZE            = FIG__BASIC_SIZE + 9;

   public static final int FIG_ARLEQUIN_OFFSET_1FLAG          = FIG__BASIC_SIZE;

   public static final int FIG_ARLEQUIN_OFFSET_2COLOR4        = FIG__BASIC_SIZE + 1;

   public static final int FIG_ARLEQUIN_OFFSET_3SIZE4         = FIG__BASIC_SIZE + 5;

   /**
    * 1 byte for border flag
    * 1 byte for size, 
    * 2 bytes for arcw and arch
    * 4 bytes for secondary color
    * 1 byte gradient position
    */
   public static final int FIG_BORDER_BASIC_SIZE              = FIG__BASIC_SIZE + 3;

   /**
    * around the define boundary. default is inside
    */
   public static final int FIG_BORDER_FLAG_1OUTER             = 1;

   /**
    * Are coins defined
    */
   public static final int FIG_BORDER_FLAG_4COIN              = 8;

   public static final int FIG_BORDER_FLAG_5FIGURE            = 16;

   /**
    * Flag set when the 8 first ByteObject are the 8 figures
    */
   public static final int FIG_BORDER_FLAG_8FIGURES           = 128;

   public static final int FIG_BORDER_OFFSET_1FLAG            = FIG__BASIC_SIZE;

   /** 
    * The pixel shift applied at the 4 corners.<br>
    * Size of border depends on a ByteObject TBLR.
    * Shift reduce that value
    */
   public static final int FIG_BORDER_OFFSET_2CORNER_SHIFT1   = FIG__BASIC_SIZE + 1;

   /**
    * <li>  {@link ITechFigure#STROKE_0_SOLID}
    * <li>  {@link ITechFigure#STROKE_1_SIMPLE_DOTS}
    */
   public static final int FIG_BORDER_OFFSET_3STROKE_STYLE1   = FIG__BASIC_SIZE + 2;

   public static final int FIG_CROSS_BASIC_SIZE               = FIG__BASIC_SIZE + 9;

   public static final int FIG_CROSS_FLAG_8CROSS              = 128;

   public static final int FIG_CROSS_OFFSET_1FLAG             = FIG__BASIC_SIZE;

   /**
    * Size (Height) of Horizontal bar
    */
   public static final int FIG_CROSS_OFFSET_2HTHICK1          = FIG__BASIC_SIZE + 1;

   /**
    * Size (Width) of Vertical bar
    */
   public static final int FIG_CROSS_OFFSET_3VTHICK1          = FIG__BASIC_SIZE + 2;

   public static final int FIG_CROSS_OFFSET_4XOFFSET1         = FIG__BASIC_SIZE + 3;

   public static final int FIG_CROSS_OFFSET_5YOFFSET1         = FIG__BASIC_SIZE + 4;

   public static final int FIG_CROSS_OFFSET_6SPACINGH1        = FIG__BASIC_SIZE + 5;

   public static final int FIG_CROSS_OFFSET_7SPACINGV1        = FIG__BASIC_SIZE + 6;

   public static final int FIG_CROSS_OFFSET_8ANGLE2           = FIG__BASIC_SIZE + 7;

   public static final int FIG_ELLIPSE_BASIC_SIZE             = FIG__BASIC_SIZE + 10;

   /**
    * The border is drawn using the mask method instead of drawing ellipses of decreasing size
    */
   public static final int FIG_ELLIPSE_FLAG_1_BORDER_MASK1    = 1;

   public static final int FIG_ELLIPSE_FLAG_2_RNDCOLOR        = 1 << 1;

   /**
    * draw method instead of fill for Gradients.
    */
   public static final int FIG_ELLIPSE_FLAG_3_FIL_DE_FER      = 1 << 2;

   /**
    * Fill the rectangle with the primary color
    */
   public static final int FIG_ELLIPSE_FLAG_4_RECTANGLE_FILL  = 1 << 3;

   public static final int FIG_ELLIPSE_FLAG_5_SLIP_FUNCTION   = 1 << 4;

   public static final int FIG_ELLIPSE_OFFSET_01_FLAG1        = FIG__BASIC_SIZE;

   public static final int FIG_ELLIPSE_OFFSET_02_TYPE1        = FIG__BASIC_SIZE + 1;

   /**
    * Size of the fill.
    * <br>
    * Transparent size inside
    */
   public static final int FIG_ELLIPSE_OFFSET_03_SIZE_FILL1   = FIG__BASIC_SIZE + 2;

   /**
    * Increment value when drawing an Ellipse with Rayons or drawing with subfigures as pencil
    */
   public static final int FIG_ELLIPSE_OFFSET_04_INCR1        = FIG__BASIC_SIZE + 3;

   /**
    * 
    */
   public static final int FIG_ELLIPSE_OFFSET_05_ANGLE_START2 = FIG__BASIC_SIZE + 4;

   /**
    * end of angle in the fillArc method
    */
   public static final int FIG_ELLIPSE_OFFSET_06_ANGLE_END2   = FIG__BASIC_SIZE + 6;

   /**
    * Angle slip 
    */
   public static final int FIG_ELLIPSE_OFFSET_07_ANGLE_SLIP2  = FIG__BASIC_SIZE + 8;

   /**
    * 
    */
   public static final int FIG_FALLING_PIXEL_BASIC_SIZE       = FIG__BASIC_SIZE + 3;

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
   public static final int FIG_FLAG_1_ANCHOR                   = 1 << 0;

   public static final int FIG_FLAG_2_GRADIENT                 = 1 << 1;

   /**
    * Set this flag to true if the figure should be cached for performant
    * repetitive drawing. If not set, figure is not cached
    */
   public static final int FIG_FLAG_3_COLOR_ARRAY              = 1 << 2;

   /**
    */
   public static final int FIG_FLAG_4_MASK                     = 1 << 3;

   /**
    * When set, the figure has a color filter in the ByteObject array
    */
   public static final int FIG_FLAG_5_FILTER                   = 1 << 4;

   /**
    * Flag set when the figure defines one or several Animation {@link ByteObject} of type {@link IBOTypesDrw#TYPE_ANIMATION}.
    * 
    */
   public static final int FIG_FLAG_6_ANIMATED                 = 1 << 5;

   /**
    * Flag telling sub figures parameters are defined after the optional anchor definition. <br>
    * <br>
    * Sub figures are defined and will be drawn along specified anchors. <br>
    */
   public static final int FIG_FLAG_7_SUB_FIGURE               = 1 << 6;

   /**
    * Flag if this figure definition supports Directional drawing.
    * <li> {@link ITechFigure#FIG_TYPE_12_ARROW}
    * <li> {@link ITechFigure#FIG_TYPE_3_TRIANGLE}
    * <br>
    * <br>
    * Direction offset is always the first offset after root + flag.
    * <br>
    * <br>
    *  TODO to be decided
    * {@link ITechFigure#FIG_TRIANGLE_OFFSET_2ANGLE2}
    * 
    */
   public static final int FIG_FLAG_8_TBLR_DIR                 = 1 << 7;

   /**
    * If set, the figure creates blank int[] array and build itself on it with no primitives.
    * <br>
    * If not set, the figure is assumed to use primitive functions of the GraphicsX context
    * <br>
    * <b>Why use it</b>? Because {@link GraphicsX} may optimize by taking the int[] array directly from the destination.
    * <br>Thus saving the creation of a buffer.
    */
   public static final int FIG_FLAGP_1RGB                     = 1;

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
   public static final int FIG_FLAGP_2EXTRA_BOUNDARY          = 2;

   /**
    * Set this flag when the figure will cover the whole rectangle area with opaque pixels.
    * This flag allows to draw the figure once and then copy area to another position without jeopardizing
    * the GraphicsX.
    * Typically a triangle or ellipse will never have this flag set
    */
   public static final int FIG_FLAGP_3OPAQUE                  = 4;

   /**
    * Flags this figure definition as being a subfigure of another
    */
   public static final int FIG_FLAGP_4SUBFIGURE               = 8;

   /**
    * When this flag is set, figure does not have any alpha channel data. <br>
    * Concerning colors, figure is opaque.is ignored from figure color.
    * <br>
    * From gradient?
    * Opaqueness depends on color and shape.
    */
   public static final int FIG_FLAGP_5IGNORE_ALPHA            = 16;

   /**
    * Some figure are unable to fit an area. It computes the closest fitting area
    * by reducing, correctly aligns it and draws itself.
    * Those figure may have a ByteObject anchor or else they are centered
    * They must be able to compute their dimension based given w/h
    */
   public static final int FIG_FLAGP_6NO_FIT                  = 32;

   /**
    * The figure is able to set the area for translucent pixels.<br>
    * A Square shaped border is not opaque but can be made to fill 
    * its center area with transparent pixels.
    */
   public static final int FIG_FLAGP_7TRANS_FIG               = 64;

   /**
    * Figure will be postponed when drawn within the Drawable framework 
    */
   public static final int FIG_FLAGP_8POSTPONE                = 128;

   /**
    * Function that modifies the area given the paint method of the figure.
    */
   public static final int FIG_FLAGX_1AREA_FUNCTION           = 1;

   /**
    * When set, paint method of figure clips on the figure area during the figure's drawing process.
    * <br>
    * Extra boundary pixels will thus not be drawn.
    */
   public static final int FIG_FLAGX_2CLIP                    = 2;

   /**
    * Genetic flag set when figure's shape covers the whole area
    */
   public static final int FIG_FLAGX_3OPAQUE_SHAPE            = 4;

   //   /**
   //    * 1 byte flag
   //    * 1 byte maxsec
   //    * 4 bytes secondary color
   //    */
   //   public static final int FIG_GRADIENT_BASIC_SIZE                       = FIG__BASIC_SIZE + 6;
   //
   //   public static final int FIG_GRADIENT_FLAG_1VERTICAL                   = 1;
   //
   //   /**
   //    * Implements a split at maxSec instead of a gradient
   //    */
   //   public static final int FIG_GRADIENT_FLAG_2SPLIT                      = 2;
   //
   //   /**
   //    * 1 byte
   //    */
   //   public static final int FIG_GRADIENT_OFFSET_1FLAG                     = FIG__BASIC_SIZE;
   //
   //   /**
   //    * 1 byte
   //    */
   //   public static final int FIG_GRADIENT_OFFSET_2MAXSEC1                  = FIG__BASIC_SIZE + 1;
   //
   //   /**
   //    * 4 bytes
   //    */
   //   public static final int FIG_GRADIENT_OFFSET_3SCOLOR4                  = FIG__BASIC_SIZE + 2;

   /**
    * When colors from figure and gradient all drawn opaque.
    */
   public static final int FIG_FLAGX_4OPAQUE_COLORS           = 8;

   /**
    * When the figure is loaded with a {@link IBOTypesDrw#TYPE_055_SCALE} object.
    * <br>
    * <br>
    * 
    */
   public static final int FIG_FLAGX_5_SCALER                 = 1 << 4;

   /**
    * When set, enables hardware anti aliasing in {@link ITechHostDrawer#HOST_FLAGX_1_ANTI_ALIAS}
    * <br>
    * Why setting a specific implementation setting here? TODO
    */
   public static final int FIG_FLAGX_6_HARDWARE_OP            = 1 << 5;

   public static final int FIG_GRID_BASIC_SIZE                = FIG__BASIC_SIZE;

   public static final int FIG_GRID_FLAG_CACHE_SEP            = 0;

   public static final int FIG_GRID_OFFSET_FLAG               = 0;

   public static final int FIG_GRID_OFFSET_HCOLOR             = 0;

   /**
    * Also hosts the Cache ID of size values
    */
   public static final int FIG_GRID_OFFSET_HSEPSIZE           = 0;

   public static final int FIG_GRID_OFFSET_HSIZE              = 0;

   public static final int FIG_GRID_OFFSET_VCOLOR             = 0;

   public static final int FIG_GRID_OFFSET_VSEPSIZE           = 0;

   public static final int FIG_GRID_OFFSET_VSIZE              = 0;

   public static final int FIG_LINE_BASIC_SIZE                = FIG__BASIC_SIZE + 2;

   public static final int FIG_LINE_COLORED_SIZE              = 0;

   /**
    * Draw Extremity
    */
   public static final int FIG_LINE_FLAG_EX                   = 64;

   /**
    * Apply Stick after drawing extremity
    */
   public static final int FIG_LINE_FLAG_EX_STICK             = 128;

   public static final int FIG_LINE_FLAG_HORIZ                = 0;

   public static final int FIG_LINE_FLAGX_STICK_BOT           = 0;

   public static final int FIG_LINE_FLAGX_STICK_LEFT          = 0;

   public static final int FIG_LINE_FLAGX_STICK_RIGHT         = 0;

   public static final int FIG_LINE_FLAGX_STICK_TOP           = 0;

   public static final int FIG_LINE_OFFSET_1FLAG              = FIG__BASIC_SIZE;

   public static final int FIG_LINE_OFFSET_2SIZE1             = FIG__BASIC_SIZE + 1;

   public static final int FIG_LINE_OFFSET_EX_COLOR           = 0;

   public static final int FIG_LINE_OFFSET_EX_SIZE            = 0;

   public static final int FIG_LOSANGE_BASIC_SIZE             = FIG__BASIC_SIZE + 6;

   public static final int FIG_LOSANGE_FLAG_1HORIZ            = 1;

   public static final int FIG_LOSANGE_FLAG_2NEG_OVERSTEP     = 2;

   /**
    * Draws the inverse
    * 
    */
   public static final int FIG_LOSANGE_FLAG_3CONTOUR          = 4;

   /**
    * Instead of opposing bases, code oppose the point.
    */
   public static final int FIG_LOSANGE_FLAG_4NOED_PAPILLION   = 8;

   public static final int FIG_LOSANGE_OFFSET_1FLAG           = FIG__BASIC_SIZE;

   /**
    * Defines overstep for the two triangels
    */
   public static final int FIG_LOSANGE_OFFSET_2OVERSTEP2      = FIG__BASIC_SIZE + 1;

   /**
    * size of fill.
    * 0 = no fill.
    */
   public static final int FIG_LOSANGE_OFFSET_3FILL2          = FIG__BASIC_SIZE + 3;

   /**
    * Type of base triangle.
    * <br>
    * <li>0 none trig def
    * <li>1 one trig def both both triangles
    * <li>2 two ByteObject figure triangles
    */
   public static final int FIG_LOSANGE_OFFSET_4TYPE1          = FIG__BASIC_SIZE + 5;

   /**
    * 1 byte for flag
    * 2 bytes for len
    * 4 bytes for seed
    * 3 bytes for colors header
    */
   public static final int FIG_PIXEL_BASIC_SIZE               = FIG__BASIC_SIZE + 10;

   public static final int FIG_PIXEL_FLAG_1_RANDOM_SIZE       = 1;

   public static final int FIG_PIXEL_FLAG_2_RANDOM_COLOR      = 2;

   /**
    * Instead of reading {@link GraphicsX#getBufferRegion(int, int, int, int)}
    */
   public static final int FIG_PIXEL_FLAG_3_NEW_IMAGE         = 4;

   public static final int FIG_PIXEL_OFFSET_01_FLAG           = FIG__BASIC_SIZE;

   /**
    * Seed for computing pixels
    */
   public static final int FIG_PIXEL_OFFSET_03_SEED4          = FIG__BASIC_SIZE + 3;

   public static final int FIG_PIXEL_OFFSET_04_COLOR_EXTRA4   = FIG__BASIC_SIZE + 7;

   public static final int FIG_PIXEL_OFFSET_04_COLORSX        = FIG__BASIC_SIZE + 7;

   /**
    * 
    */
   public static final int FIG_PIXEL_OFFSET_05_BLENDERX1      = FIG__BASIC_SIZE + 7;

   public static final int FIG_PIXEL_OFFSET_07_LENGTH_H2      = FIG__BASIC_SIZE + 8;

   public static final int FIG_PIXEL_OFFSET_08_LENGTH_V2      = FIG__BASIC_SIZE + 10;

   public static final int FIG_PIXEL_OFFSET_09_GRAD_SIZE1     = FIG__BASIC_SIZE + 12;

   public static final int FIG_PIXEL_OFFSET_3VLENGTH2         = 0;

   public static final int FIG_RAYS_BASIC_SIZE                = 0;

   /**
    * uses all default Figure fields
    * 1 byte for flag
    * 1 byte for arcw
    * 1 byte for arch
    * 1 byte for fillSize
    */
   public static final int FIG_RECTANGLE_BASIC_SIZE           = FIG__BASIC_SIZE + 4;

   public static final int FIG_RECTANGLE_FLAG_1ROUND          = 1;

   /**
    * Round inside
    */
   public static final int FIG_RECTANGLE_FLAG_2ROUND_INSIDE   = 2;

   public static final int FIG_RECTANGLE_FLAG_7ARCW1          = 64;

   public static final int FIG_RECTANGLE_FLAG_8ARCH1          = 128;

   public static final int FIG_RECTANGLE_OFFSET_1FLAG         = FIG__BASIC_SIZE;

   public static final int FIG_RECTANGLE_OFFSET_2ARCW1        = FIG__BASIC_SIZE + 1;

   public static final int FIG_RECTANGLE_OFFSET_3ARCH1        = FIG__BASIC_SIZE + 2;

   /**
    * When différent from 0, draws "Border" rectangle.
    */
   public static final int FIG_RECTANGLE_OFFSET_4SIZEF1       = FIG__BASIC_SIZE + 3;

   /**
    * 1 byte flag
    * 2 bytes w
    * 2 bytes h
    */
   public static final int FIG_REPEATER_BASIC_SIZE            = FIG__BASIC_SIZE + 5;

   public static final int FIG_REPEATER_FLAG_1FORCECOPYAREA   = 1;

   /**
    * Else transparent
    */
   public static final int FIG_REPEATER_FLAG_2USE_BGCOLOR     = 2;

   public static final int FIG_REPEATER_OFFSET_1FLAG          = FIG__BASIC_SIZE;

   /**
    * Width for the Unit
    */
   public static final int FIG_REPEATER_OFFSET_2W2            = FIG__BASIC_SIZE + 1;

   public static final int FIG_REPEATER_OFFSET_3H2            = FIG__BASIC_SIZE + 3;

   /**
    * 1 byte flag
    * 2 bytes repeat
    * 2 bytes  separation
    * 1 byte line thickness
    * 
    */
   public static final int FIG_SL_BASIC_SIZE                  = FIG__BASIC_SIZE + 6;

   public static final int FIG_SL_FLAG_1SIMPLE                = 1;

   public static final int FIG_SL_FLAG_2ANGLE                 = 2;

   public static final int FIG_SL_FLAG_3HORIZ                 = 4;

   /**
    * Tells we have explicit seperation values
    */
   public static final int FIG_SL_FLAG_4EXPLICIT_SEP          = 8;

   public static final int FIG_SL_FLAG_5EXPLICIT_COLORS       = 16;

   public static final int FIG_SL_FLAG_6FILL                  = 32;

   public static final int FIG_SL_FLAG_7IGNORE_FIRST          = 64;

   public static final int FIG_SL_FLAG_8IGNORE_LAST           = 128;

   public static final int FIG_SL_OFFSET_1FLAG                = FIG__BASIC_SIZE;

   public static final int FIG_SL_OFFSET_2LINE_SIZE1          = FIG__BASIC_SIZE + 1;

   /**
    * Maximum number of line repeat. Ignored if ask to do a fill.
    */
   public static final int FIG_SL_OFFSET_3REPEAT2             = FIG__BASIC_SIZE + 2;

   /**
    * Pixels separating 2 lines
    */
   public static final int FIG_SL_OFFSET_4SEPARATION2         = FIG__BASIC_SIZE + 4;

   /**
    * Minimum for drawing a string
    * 1 existence flag
    * 3 bytes for font
    * 2 bytes for char
    */
   public static final int FIG_STRING_BASIC_SIZE              = FIG__BASIC_SIZE + 8;

   /**
    * Scaling switch. <br>
    * <li>scaler type : 
    * <li>scale id (linear,bilinear
    */
   public static final int FIG_STRING_FLAG_1_SCALING           = 1 << 0;

   public static final int FIG_STRING_FLAG_4_RAW               = 1 << 3;

   /**
    * Set when an extra effect like 
    * <li>mask
    * <li>shadow
    * <li>vertical/diagonal text
    */
   public static final int FIG_STRING_FLAG_5_EFFECT            = 1 << 4;

   /**
    * Set if a RawType string is present.
    * Must check for {@link ITechFigure#FIG_STRING_FLAG_7_CHAR} or  {@link ITechFigure#FIG_STRING_FLAG_4_RAW}
    * <br>
    * <br>
    * 
    */
   public static final int FIG_STRING_FLAG_6_EXPLICIT          = 1 << 5;

   /**
    * Figure is just one char defined at {@link ITechFigure#FIG_STRING_OFFSET_05_CHAR2}.
    * <br>
    * <br>
    * Text effect are applied
    */
   public static final int FIG_STRING_FLAG_7_CHAR              = 1 << 6;

   public static final int FIG_STRING_OFFSET_01_FLAG          = FIG__BASIC_SIZE;

   /**
    * MM: flag 1 of offset 6.
    * <li>{@link ITechFont#FACE_MONOSPACE} 
    * <li>{@link ITechFont#FACE_PROPORTIONAL}
    * <li>{@link ITechFont#FACE_SYSTEM}
    * <br>
    * Bigger values means a custom font
    */
   public static final int FIG_STRING_OFFSET_02_FACE1         = FIG__BASIC_SIZE + 1;

   /**
    * MM: flag 2 of offset 6.
    * <li>{@link ITechFont#STYLE_BOLD} 
    * <li>{@link ITechFont#STYLE_ITALIC}
    * <li>{@link ITechFont#STYLE_PLAIN}
    */
   public static final int FIG_STRING_OFFSET_03_STYLE1          = FIG__BASIC_SIZE + 2;

   /**
    * MM: flag 3 of offset 6.
    * <br>
    * <br>
    * <li>{@link ITechFont#SIZE_0_DEFAULT} 16
    * <li>{@link ITechFont#SIZE_1_TINY} 16
    * <li>{@link ITechFont#SIZE_2_SMALL} 16
    * <li>{@link ITechFont#SIZE_3_MEDIUM} 0
    * <li>{@link ITechFont#SIZE_4_LARGE} 8
    * <li>{@link ITechFont#SIZE_5_HUGE} 8
    * <br>
    * <br>
    * When a value is different than those, it is used a such if the platform
    * has a integer font size granularity. Otherwise it is automatically moved to one
    * of those 3 choices.
    * <li> Values of 16 and above are large.
    * <li>Values of 0, 9 to 15 are medium
    * <li>Values of 1 to to 8 are small
    * <br>
    * <br>
    * 
    */
   public static final int FIG_STRING_OFFSET_04_SIZE1           = FIG__BASIC_SIZE + 3;

   /**
    * Character Unicode. <br>
    * Often used with a scaler to draw big numbers. Centered and scaled on the drawing area
    */
   public static final int FIG_STRING_OFFSET_05_CHAR2           = FIG__BASIC_SIZE + 4;

   /**
    * 4 bits for type:
    * <li>0 none
    * 
    */
   public static final int FIG_STRING_OFFSET_06_SCALE1          = FIG__BASIC_SIZE + 6;

   /**
    * Number of dynamic text effects. i.e. differiante from static text effects.
    * <br>
    * <br>
    * Convenience that counts subs which have the flag {@link ITechStrFx#FX_FLAGX_2_DYNAMIC}
    * <br>
    * <br>
    * 
    */
   public static final int FIG_STRING_OFFSET_07_NUM_DYNAMIC1    = FIG__BASIC_SIZE + 7;

   /**
    * 1 byte flag
    */
   public static final int FIG_TRIANGLE_BASIC_SIZE            = FIG__BASIC_SIZE + 7;

   /**
    * Set when complex angle i.e. not Up,Down,Left,Right
    * or <br>
    * Predefined anchor
    */
   public static final int FIG_TRIANGLE_FLAG_2ANGLE           = 2;

   /**
    * Anchor Points
    */
   public static final int FIG_TRIANGLE_FLAG_3ANCHOR_POINTS   = 4;

   public static final int FIG_TRIANGLE_OFFSET_1FLAG1         = FIG__BASIC_SIZE;

   /**
    * 2 bytes degree when {@link ITechFigure#FIG_TRIANGLE_FLAG_2ANGLE}
    * or type
    * <li> {@link C#TYPE_00TOP}
    * <li> {@link C#TYPE_01BOTTOM}
    * <li> {@link C#TYPE_02LEFT}
    * <li> {@link C#TYPE_03RIGHT}
    * <li> {@link C#TYPE_04TopLeft}
    * <li> {@link C#TYPE_05TopRight}
    * <li> {@link C#TYPE_06BotLeft}
    * <li> {@link C#TYPE_07BotRight}
    * <li> {@link C#TYPE_08MID_TopLeft}
    * <li> {@link C#TYPE_09MID_TopRight}
    * <li> {@link C#TYPE_10MID_BotLeft}
    * <li> {@link C#TYPE_11MID_BotRight}
    * <li> ..
    * 
    */
   public static final int FIG_TRIANGLE_OFFSET_2ANGLE2        = FIG__BASIC_SIZE + 1;

   /**
    * Value that will be read according to the {@link ISizer} definition.
    * 4 bytes size (percent size)
    * <br>
    * H Size
    */
   public static final int FIG_TRIANGLE_OFFSET_3h4            = FIG__BASIC_SIZE + 3;

   /**
    * Most basic shape.
    * <br>
    * Without a gradient, rectangle shape is direction agnostic.
    * <br>
    * Direction depends on gradient. <br>
    * {@link C#DIR_0TOP}
    * <br>
    * value 0
    */
   public static final int FIG_TYPE_01_RECTANGLE              = 1;

   /**
    * 2 definitions of BORDER.
    * <br>
    * Simple outer, coins, size, color, gradient color, maxGrad
    * <br>
    * Main Flag: outer, coins, tblr, arc, figure
    * <br>
    * Border has 1 TBLR {@link ByteObject} and 1 {@link ITechFigure#FIG_TYPE_01_RECTANGLE} {@link ByteObject}.
    * <br>
    */
   public static final int FIG_TYPE_02_BORDER                 = 2;

   public static final int FIG_TYPE_04_CHAR                   = 4;

   public static final int FIG_TYPE_05_LINE                   = 5;

   /**
    * Two triangles upside down <br>
    * Top - Bot
    * Left - Right
    */
   public static final int FIG_TYPE_06_LOSANGE                = 6;

   /**
    * defines a cirle(w==h)/ellipse figure
    * May be filled o
    */
   public static final int FIG_TYPE_07_ELLIPSE                = 7;

   public static final int FIG_TYPE_08_GERMANCROSS            = 36;

   public static final int FIG_TYPE_09_PIXELS                 = 9;

   /**
    * The single char figure with scaling to fit the figure's drawing area. <br>
    * More complex string of characters are possible with {@link IBOTypesDrw#TYPE_RAW}.
    * How does it relate to a number 10 scaled up in big font?
    */
   public static final int FIG_TYPE_10_STRING                 = 60;

   public static final int FIG_TYPE_11_GRID                   = 44;

   public static final int FIG_TYPE_12_ARROW                  = 16;

   public static final int FIG_TYPE_13_REPEATER               = 32;

   public static final int FIG_TYPE_15_RAYS                   = 79;

   public static final int FIG_TYPE_16_SUPERLINES             = 23;

   /**
    * Arlequin draws many rectangle of different colors.
    * Used by Repeater for couverture
    */
   public static final int FIG_TYPE_17_ARLEQUIN               = 26;

   /**
    * A figure that draw blocks of colors
    */
   public static final int FIG_TYPE_18_DIAG_BLOCKS            = 35;

   /**
    * Grid of lines filled by colors. On intersection, an artifact may be drawn.
    * <br>
    * The lines are actually rectangles whose sizes are defined by a sizer.
    * <br>
    * The rectangles can be gradiented.
    * <br>
    * The artifacts are figures repeated. As small artifact shift is defined.
    * <br>
    * The base size of the artifact is the size of the vertical and horizontal line intersection.
    * <br>
    * The lines are drawn using primitives or a repeated pattern of an RGB array. each pixels being
    * blended using the figure blender.
    * <br>
    * When a color gradient is global
    */
   public static final int FIG_TYPE_20_CROSS                  = 34;

   /**
    * Param has all the flags to define number of triangles, distance, type(iso, arrow shaped)
    * filters (gradient, trans).
    */
   public static final int FIG_TYPE_3_TRIANGLE                = 3;

   public static final int STROKE_0_SOLID                     = 0;

   public static final int STROKE_1_SIMPLE_DOTS               = 1;

}
