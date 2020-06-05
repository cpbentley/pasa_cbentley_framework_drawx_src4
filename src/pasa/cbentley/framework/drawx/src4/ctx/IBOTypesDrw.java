/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.ctx;

import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.framework.coredraw.src4.interfaces.IMFont;
import pasa.cbentley.framework.drawx.src4.engine.BlendOp;
import pasa.cbentley.framework.drawx.src4.engine.GraphicsX;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;

/**
 * 51-80
 * @author Charles-Philip Bentley
 *
 */
public interface IBOTypesDrw extends IBOTypesBOC {

   public static final int A_SID_DRWTYPE_A         = 50;

   /**
    * When Length is not sufficient anymore, you have to create a new module
    * or sub module
    */
   public static final int A_SID_DRWTYPE_Z         = 99;

   /**
    * 1 or 2 bytes defines a figure
    * Some Figures are TBLR directional.
    */
   public static final int TYPE_050_FIGURE         = 50;

   /**
    * Defines a Rectangle Box and its position  a figure in a 2D rectangular area. <br>
    * <br>
    * Used to define a box in which to draw a {@link IBOTypesDrw#TYPE_050_FIGURE}.
    * Gives a size<br>
    * Used to define a position and an alignment and a size<br>
    * 1 byte = type<br>
    * 2 byte = flag<br>
    * Most compact version is<br>
    * 2 bits for horizontal alignment<br>
    * 2 bits for vertical alignment<br>
    * 4 bits for size (max size is 16)<br>
    * <br>
    * Most complete is<br>
    * x offset relative to origin of coordinate<br>
    * y offset relative to origin of coordinate<br>
    * <br><br>
    * Several Types of Anchor encoding exists.<br>
    */
   public static final int TYPE_051_BOX            = 51;

   public static final int TYPE_052_ARTIFACT       = 52;

   public static final int TYPE_053_MOSAIC         = 53;

   public static final int TYPE_054_SKEWER         = 54;

   /**
    * Parameters for scaling an image, including a post scaling RGB filter to smooth the scaling result
    */
   public static final int TYPE_055_SCALE          = 55;

   /**
    * Filters a RGB array (RgbImage or Image) using a given function.
    * An important category is Translucent filters that work on the alpha channel
    * Several filters may be chained. Each filter will be applied in turn
    * Function of filtering may be implicit or defined in a Function object
    * Some filters will use a maskcolor
    * 
    * Type which stores data of a translucent filtering function
    * 1st byte = Flag => maskColor
    * 2-3 bytes. length of filter
    * Can apply to all non mask colors. One value.
    * Random value
    * To a set of colors
    * Change only the Border Pixels.
    * 4 byte: function type Linear, Value Defined
    * 4 bytes: function values
    */
   public static final int TYPE_056_COLOR_FILTER   = 56;

   public static final int TYPE_057_COLOR_FUNCTION = 57;

   /**
    * Defines an interaction between 2 layers of RGB data. 
    * <br>
    * <br>
    * One layer is the bottom layer (destination of merge) and the other is the top aka mask layer (source of merge). 
    * <br>
    * Each layer may have a color filter. 
    * <br>
    * Finally a <font color=#CC1111> Merge </font> operation is done with a {@link BlendOp} where <br>
    * <br>
    * pixBot = f(pixBot,pixTop) <br>
    * <br>
    * The final pixel to be drawn is a function of itself and the corresponding pixel of the top layer. <br>
    * <br>
    * <b>Classic</b> : top layer pixels decide the alpha value of bottom layer pixels. Only the bottom
    * layer is drawn to the {@link GraphicsX}.
    * <br>
    * When applied to Figures, {@link ITechFigure#FIG_FLAG_4_MASK} is set. The figure is drawn in black over a white background. 
    * The mask figure is drawn on the bottom layer.
    * <br>
    * <font color=#CC1111>Merge</font> : a top white pixel at x,y makes the bottom pixel x,y fully transparent. 
    * <br>
    * The mask may have a bg figure and a shape figure
    * A color filter may be applied on the mask layer.
    * <br>
    * <b>Halo Effect</b>: 
    * <br>
    * A Touch color filter is applied on the top Black and White layer. Pixels touching white are given an alpha value.
    * <br>
    * <font color=#CC1111>Merge</font> : a top opaque pixel at x,y makes the bottom pixel x,y fully transparent. 
    */
   public static final int TYPE_058_MASK           = 58;

   /**
    * Defines a color function from between a primary color and secondary color.
    * <br>
    * <br>
    * <li>Need root color from {@link IBOTypesDrw#TYPE_050_FIGURE} definition.
    * <li>Supports {@link IBOTypesDrw#TYPE_MERGE_MASK}
    * <li>
    * <br>
    * <li>3 colors scheme
    * <li>stepping from 1 to area size
    * <li>position
    * <li>Filling over either side of gradient change position. When both side are filled, act like a split.
    * <li>Channel gradient switch : gradient may only works on specific ARGB channels
    * 
    * <br>
    * <br>
    * How to link the gradent position to the String figure font baseline?
    * {@link IMFont#getBaselinePosition()}.
    * String figure overrides when needed
    */
   public static final int TYPE_059_GRADIENT       = 59;

   /**
    * Type which stores X bytes of Top/Bottom,Left, Right data
    * 
    * X is defined by byte #2 
    * <br>
    * 
    * In Swing, java.awt.Insets
    */
   public static final int TYPE_060_TBLR           = 60;

   public static final int TYPE_061_COLOR_RANDOM   = 61;

   public static final int TYPE_062_BLENDER        = 62;

   public static final int TYPE_063_PIX_STAR       = 63;

   public static final int TYPE_069_ANCHOR         = 69;

   /**
    * Text effect works on the String figure. 
    * <br>
    * <br>
    * 
    * <b>4 scopes of definitions</b>. Each layer<br>
    * <ol>
    * <li>Character Level Effect = a Text Effect is rooted at the character level. <br>
    * All effects defined at this level are applied for all characters in the string<br>
    * But some effects may be targeted at specific indexes.
    * <li>Line Level Effect = draw Method draw the String and apply line level effects and filters<br>
    * <li>Paragraph Level Effect<br>
    * <li>Page Level Effect is used for several lines of text where other
    * effect may be defined.
    * </ol>
    * <br>
    * <br>
    * The text direction is decided at the char level. Default is horizontal<br>
    * Extra spacing allows to draw characters spaced differently<br>
    * Defines Horizontal/Function<br>
    * For Function we have diagonal, <br>
    * 1- x and y pixels offset from the last position <br>
    * 2- x = f(c,n) where x is x coordinate, n is the char index in the String<br>
    * 3- y = f(n,x) the y depends on  the num 
    * <br>
    * This allows the characters to be placed on a Sinusoid.<br>
    * <br>
    * <li>char level decoration
    * <li>Font spec + mods (extra spacing)
    * <li>Color or Mask Filter (using as mask layer) for antialiasing
    * <li>Trans filter for aliasing
    * <li>string level decoration
    * <li>scaling
    * <br>
    * <br>
    * 
    * Complex Txt effect plugs into the cache framework offered by Drawable.
    * <br>
    * <br>
    *  
    * Necessary for mutable text box
    * Text effect are plugged in styles. So one may define a Bold text effect,
    * <br>
    * <br>
    *  
    */
   public static final int TYPE_070_TEXT_EFFECTS   = 70;

   public static final int TYPE_071_STYLE          = 71;

}
