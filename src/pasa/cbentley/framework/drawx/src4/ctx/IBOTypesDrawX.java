/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.ctx;

import pasa.cbentley.byteobjects.src4.ctx.IBOTypesBOC;
import pasa.cbentley.byteobjects.src4.objects.color.BlendOp;
import pasa.cbentley.framework.coredraw.src4.ctx.IBOTypesCoreDraw;
import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOStrAux;
import pasa.cbentley.framework.drawx.src4.tech.ITechAnchor;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;
import pasa.cbentley.layouter.src4.ctx.IBOTypesLayout;

/**
 * <li> {@link IBOTypesBOC}
 * <li> {@link IBOTypesLayout}
 * <li> {@link IBOTypesCoreDraw}
 * 
 * @author Charles-Philip Bentley
 *
 */
public interface IBOTypesDrawX extends IBOTypesBOC {

   public static final int A_SID_DRWTYPE_A                      = 161;

   /**
    * When Length is not sufficient anymore, you have to create a new module
    * or sub module
    */
   public static final int A_SID_DRWTYPE_Z                      = 180;

   /**
    * 1 or 2 bytes defines a figure
    * Some Figures are TBLR directional.
    */
   public static final int TYPE_DRWX_00_FIGURE                  = A_SID_DRWTYPE_A + 0;

   /**
    * Defines a Rectangle Box and its position  a figure in a 2D rectangular area. <br>
    * <br>
    * Used to define a box in which to draw a {@link IBOTypesDrawX#TYPE_DRWX_00_FIGURE}.
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
   public static final int TYPE_DRWX_01_BOX                     = A_SID_DRWTYPE_A + 1;

   public static final int TYPE_DRWX_02_ARTIFACT                = A_SID_DRWTYPE_A + 2;

   public static final int TYPE_DRWX_03_MOSAIC                  = A_SID_DRWTYPE_A + 3;

   public static final int TYPE_DRWX_04_SKEWER                  = A_SID_DRWTYPE_A + 4;

   /**
    * Parameters for scaling an image, including a post scaling RGB filter to smooth the scaling result
    */
   public static final int TYPE_DRWX_05_SCALE                   = A_SID_DRWTYPE_A + 5;

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
    * layer is drawn to the hardware Graphics.
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
   public static final int TYPE_DRWX_06_MASK                    = A_SID_DRWTYPE_A + 6;

   /**
    * {@link IBOStrAux}
    */
   public static final int TYPE_DRWX_07_STRING_AUX              = A_SID_DRWTYPE_A + 7;

   public static final int TYPE_DRWX_07_STRING_AUX_0_FX         = 0;

   public static final int TYPE_DRWX_07_STRING_AUX_1_FORMAT     = 1;

   public static final int TYPE_DRWX_07_STRING_AUX_2_SPECIALS_C = 2;

   public static final int TYPE_DRWX_07_STRING_AUX_3_APPLICATOR = 3;

   public static final int TYPE_DRWX_07_STRING_AUX_4_FX_STRUCT  = 4;

   public static final int TYPE_DRWX_07_STRING_AUX_XXX          = 5;

   public static final int TYPE_DRWX_08_CONTENT_FX              = A_SID_DRWTYPE_A + 8;

   /**
    * What's this?
    */
   public static final int TYPE_DRWX_09_PIX_STAR                = A_SID_DRWTYPE_A + 9;

   /**
    * {@link ITechAnchor}
    */
   public static final int TYPE_DRWX_10_ANCHOR                  = A_SID_DRWTYPE_A + 10;

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
   public static final int TYPE_DRWX_11_TEXT_EFFECTS            = A_SID_DRWTYPE_A + 11;

   public static final int TYPE_DRWX_12_STYLE                   = A_SID_DRWTYPE_A + 12;

   public static final int TYPE_DRWX_13_FX_APPLICATOR           = A_SID_DRWTYPE_A + 13;

}
