/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.string.interfaces;

import pasa.cbentley.byteobjects.src4.objects.function.Function;
import pasa.cbentley.byteobjects.src4.objects.pointer.IBOMerge;
import pasa.cbentley.core.src4.utils.interfaces.IColors;
import pasa.cbentley.core.src4.utils.interfaces.IColorsStatic;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechFont;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechGraphics;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;
import pasa.cbentley.framework.drawx.src4.string.StringFx;
import pasa.cbentley.framework.drawx.src4.string.StringStyleApplicatorIndex;
import pasa.cbentley.framework.drawx.src4.tech.ITechFigure;

/**
 * {@link IBOTypesDrawX#TYPE_DRWX_07_STRING_AUX_4_FX}
 * 
 * Defines
 * 
 * <li> Masks


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
 * @author Charles-Philip Bentley
 *
 */
public interface IBOStrAuxFx extends IBOStrAux {

   /**
    * When effect is very specific and {@link IBOStrAuxFx#FX_OFFSET_12_INDEX2} gives the
    * ID of the effect.
    */
   public static final int FX_FLAG_1_SPECIFIC_SWITCH       = 1;

   /**
    * Simple so code can double check it is with a template for a root fx definition.
    * 
    */
   public static final int FX_FLAG_2_ROOT                  = 1 << 1;

   /**
    * Perf flag to go straight to the vertical method
    * and a FontHeight yShift 
    * Usually a small negative yShift is used.
    * <br>
    * <br>
    * 
    */
   public static final int FX_FLAG_3_VERTICAL              = 1 << 2;

   /**
    * Has the Text effect a {@link IDrwTypes#FTYPE_2_TBLR}  defining extra pixel space used by text effect
    * on the fringe of the to be displayed text.
    * <br>
    * <br>
    * 
    */
   public static final int FX_FLAG_4_EXTRA_SPACE_TBLR      = 1 << 3;

   public static final int FX_FLAG_5_BASIC_BREAK           = 1 << 4;

   public static final int FX_FLAG_6_BLOCK                 = 1 << 5;

   public static final int FX_FLAG_7_LINE                  = 1 << 6;

   public static final int FX_FLAG_8_CHAR                  = 1 << 7;

   /**
    * When set font face definition is transparent and value should be ignored.
    */
   public static final int FX_FLAGX_1_UNDEFINED_FONT_FACE  = 1 << 0;

   /**
    * When set font style definition is transparent and value should be ignored.
    */
   public static final int FX_FLAGX_2_UNDEFINED_FONT_STYLE = 1 << 1;

   /**
    * When set font size definition is transparent and value should be ignored.
    */
   public static final int FX_FLAGX_3_UNDEFINED_FONT_SIZE  = 1 << 2;

   /**
    * When set font color definition is transparent and value should be ignored.
    */
   public static final int FX_FLAGX_4_UNDEFINED_COLOR      = 1 << 3;

   public static final int FX_FLAGX_5_UNDEFINED_SCOPE      = 1 << 4;

   /**
    * 
    */
   public static final int FX_FLAGX_6_UNDEFINED            = 1 << 5;

   /**
    * 
    */
   public static final int FX_FLAGX_7_UNDEFINED            = 1 << 6;

   /**
    * Flag telling this object is semi transparent for the purpose of merges.
    * <p>
    * This avoid the use of {@link IBOMerge}s which are cumbersome. StringFx uses
    * a lot of transparent font/color combos.
    * </p>
    * 
    * <li> {@link IBOStrAuxFx#FX_FLAGX_1_UNDEFINED_FONT_FACE} 
    * <li> {@link IBOStrAuxFx#FX_FLAGX_2_UNDEFINED_FONT_STYLE} 
    * <li> {@link IBOStrAuxFx#FX_FLAGX_3_UNDEFINED_FONT_SIZE} 
    * <li> {@link IBOStrAuxFx#FX_FLAGX_4_UNDEFINED_COLOR} 
    * <li> {@link IBOStrAuxFx#FX_FLAGX_5_UNDEFINED_SCOPE} 
    * 
    */
   public static final int FX_FLAGX_8_INCOMPLETE           = 1 << 7;

   /**
    * After the figure is the TRANS filter to apply
    * <br>
    * <br>
    * This filter might change the lead in and lead out
    * of the string (because of the halo).
    * <br>
    * So it might impact the stringWidth method if
    * the filter applies to pixels outside the character pixels
    */
   public static final int FX_FLAGY_1_COLOR_FILTER         = 1;

   /**
    * Sets when a Figure object is paramed. it will be used a simple background figure
    * for the scope of the text effect
    */
   public static final int FX_FLAGY_2_FIGURE_BG            = 1 << 1;

   /**
    * Defines a {@link IDrwTypes#TYPE_DRWX_06_MASK} in its parameters.
    * <br>
    * <br>
    * 
    */
   public static final int FX_FLAGY_3_MASK                 = 1 << 2;

   /**
    * Filter post mask
    */
   public static final int FX_FLAGY_4_MIDDLE_FILTER        = 1 << 3;

   /**
    * Decoration post 1st filter
    * <br>
    * <br>
    * 
    */
   public static final int FX_FLAGY_5_DECORATION           = 1 << 4;

   /**
    * Filter applied after the decoration figure
    */
   public static final int FX_FLAGY_6_LAST_FILTER          = 1 << 5;

   /**
    * The rectangular area controlled by the scoped element is treated with a Style element.
    * <br>
    * <br>
    * The style {@link IViewTypes#TYPE_DRWX_08_STYLE} is stored as a sub parameter of this object.
    * <br>
    * <br>
    * The figure will be a char, a word or a line.
    * <br>
    * Words will be styled if scoped to word, in the interval of this {@link StringFx}
    */
   public static final int FX_FLAGY_7_STYLE                = 1 << 6;

   /**
    * A pointer defines an index to which to apply the effects.
    * <br>
    * When not defined, the effect is applied to all elements
    * 
    * Allows to define a specific scope based 
    * Provides {@link IBOStrAuxFx#FX_OFFSET_12_INDEX2} scope
    * <li> First character of every word char scope index 0, scope {@link ITechStringer#FX_SCOPE_2_WORD}.
    * <li> First line of every paragraph.
    * <li> Random index of every word bigger than 2 letters. Index {@link Function} and Acceptor for Word.
    * <br>
    * <br>
    * Scope in this field must be bigger than scope of Fx definition.
    * 
    * A function that defines which interval is targeted by this fx.
    * 
    * It could be a recurring interval,
    */
   public static final int FX_FLAGY_8_APPLICATOR           = 1 << 7;

   /**
    * Definition here and implemented by {@link StringStyleApplicatorIndex}
    * <p>
    * <b>Examples</b><br>
    * <li>This Fx applies for the 1st word of each line/sentence. 
    * <li>This Fx applies for the 1st char of each line/word. 
    * <li>This Fx applies for the 1st char text (selection layer or root layer). 
    * </p>
    * 
    * It will not change. Selection layer style usually are dynamic unless you want the first char of selection text
    * to have a specific 
    * @see IBOStrAuxFx#FX_FLAGZ_2_DYNAMIC
    */
   public static final int FX_FLAGZ_1_                     = 1 << 0;

   /**
    * Flag saying this definition is to be activated upon some user actions.
    * 
    * <p>
    * 99% of the time, a dynamic fx is character scoped.
    * </p>
    * 
    * When this flag is false, the fx is deemed static, it applies to everything
    * 
    * <p>
    * <b>Example</b> <br>
    * AbbbC is an interval. We want a style for the first letter and another Fx for the last letter.
    * 
    * <li>A has a static fx for color, static index first 0
    * <li>C has a static fx as well. index last 0
    * </p>
    */
   public static final int FX_FLAGZ_2_DYNAMIC              = 1 << 1;

   /**
    * Index is computed starting from the last element
    * 
    * @see IBOStrAuxFx#FX_FLAG_1_SPECIFIC_SWITCH
    */
   public static final int FX_FLAGZ_3_                     = 1 << 2;

   public static final int FX_FLAGZ_4_                     = 1 << 3;

   public static final int FX_FLAGZ_5_                     = 1 << 4;

   public static final int FX_FLAGZ_6_                     = 1 << 5;

   public static final int FX_FLAGZ_7_                     = 1 << 6;

   /**
    * The effect is actually to be choosen by a function using the sub text effect of this instance.
    * <li> Random function
    * <li> Serie function.
    * <li> ...
    * 
    * TODO Color function that gives a color based on a the String of the interval
    * Like intellij gives colored method names. Word scope color function
    * 
    * Char scoped color function
    * 
    * We could also have _ separated colors 
    * <li>FX
    * <li>FLAGX
    * <li>8
    * <li>FUNCTION
    * 
    * Each applied
    */
   public static final int FX_FLAGZ_8_FUNCTION             = 1 << 7;

   /**
    * <li> {@link IBOStrAuxFx#FX_FLAG_1_SPECIFIC_SWITCH}
    * <li> {@link IBOStrAuxFx#FX_FLAG_3_VERTICAL}
    * <li> {@link IBOStrAuxFx#FX_FLAG_4_EXTRA_SPACE_TBLR}
    * <li> {@link IBOStrAuxFx#FX_FLAG_5_BASIC_BREAK}
    * <li> {@link IBOStrAuxFx#FX_FLAG_6_BLOCK}
    * <li> {@link IBOStrAuxFx#FX_FLAG_7_LINE}
    * <li> {@link IBOStrAuxFx#FX_FLAG_8_CHAR}
    * 
    */
   public static final int FX_OFFSET_01_FLAG               = STR_AUX_SIZE + 0;

   /**
    * <li> {@link IBOStrAuxFx#FX_FLAG_2_ROOT}
    * <li> {@link IBOStrAuxFx#FX_FLAGZ_2_DYNAMIC}
    * <li> {@link IBOStrAuxFx#FX_FLAGX_1_UNDEFINED_FONT_FACE}
    * <li> {@link IBOStrAuxFx#FX_FLAGX_4_UNDEFINED_COLOR}
    * <li> {@link IBOStrAuxFx#FX_FLAGZ_1_STATIC_INDEX}
    * <li> {@link IBOStrAuxFx#FX_FLAGX_8_INCOMPLETE}
    * <li> {@link IBOStrAuxFx#FX_FLAGZ_8_FUNCTION}
    */
   public static final int FX_OFFSET_02_FLAGX              = STR_AUX_SIZE + 1;

   /**
    * <li> {@link IBOStrAuxFx#FX_FLAGY_1_COLOR_FILTER}
    * <li> {@link IBOStrAuxFx#FX_FLAGY_2_FIGURE_BG}
    * <li> {@link IBOStrAuxFx#FX_FLAGY_3_MASK}
    * <li> {@link IBOStrAuxFx#FX_FLAGY_4_MIDDLE_FILTER}
    * <li> {@link IBOStrAuxFx#FX_FLAGY_5_DECORATION}
    * <li> {@link IBOStrAuxFx#FX_FLAGY_6_LAST_FILTER}
    * <li> {@link IBOStrAuxFx#FX_FLAGY_7_STYLE}
    * <li> {@link IBOStrAuxFx#FX_FLAGY_8_APPLICATOR}
    */
   public static final int FX_OFFSET_03_FLAGY              = STR_AUX_SIZE + 2;

   /**
    * 
    */
   public static final int FX_OFFSET_04_FLAGZ              = STR_AUX_SIZE + 3;

   /**
    * Tells how to apply mask, bg and fg figures.
    * <p>
    * <li> {@link ITechStringer#FX_SCOPE_0_TEXT}
    * <li> {@link ITechStringer#FX_SCOPE_1_CHAR}
    * <li> {@link ITechStringer#FX_SCOPE_2_WORD}
    * </p>
    * 
    * Sub type of fx that defines scope of the text effect, the smallest unit of text on which is applied
    * the effects.
    * Character scoped text fx is applied to each characters.
    * A word scoped text fx is applied to words (alphanumberical contiguous string).
    * 
    * Mask background figure will be drawn over the area of scope.
    */
   public static final int FX_OFFSET_05_SCOPE_FX1          = STR_AUX_SIZE + 5;

   /**
    * ID To a set of fonts defined by driver.
    * 
    * {@link ITechFigure}
    * 
    * <li> {@link ITechFont#FACE_MONOSPACE}
    * <li> {@link ITechFont#FACE_PROPORTIONAL}
    * <li> {@link ITechFont#FACE_SYSTEM}
    * 
    * <p>
    * MergeMask: flag 1 of offset 6.
    * </p>
    */
   public static final int FX_OFFSET_06_FACE1              = STR_AUX_SIZE + 6;

   /**
    * 
    * MM: flag 2 of offset 6.
    * 
    * 
    * <li> {@link ITechFont#STYLE_BOLD}
    * <li> {@link ITechFont#STYLE_ITALIC}
    * <li> {@link ITechFont#STYLE_PLAIN}
    * <li> {@link ITechFont#STYLE_UNDERLINED}
    */
   public static final int FX_OFFSET_07_STYLE1             = STR_AUX_SIZE + 7;

   /**
    * MM: flag 3 of offset 6.
    * 
    * Relation to {@link IBOFigString#FIG_STRING_OFFSET_05_SIZE1}
    * 
    * <li> {@link ITechFont#SIZE_0_DEFAULT}
    * <li> {@link ITechFont#SIZE_1_TINY}
    * <li> {@link ITechFont#SIZE_2_SMALL}
    * <li> {@link ITechFont#SIZE_3_MEDIUM}
    * <li> {@link ITechFont#SIZE_4_LARGE}
    * <li> {@link ITechFont#SIZE_5_HUGE}
    */
   public static final int FX_OFFSET_08_SIZE1              = STR_AUX_SIZE + 8;

   /**
    * Fx base color
    * 
    * <li> {@link IColorsStatic}
    * <li> {@link IColors}
    */
   public static final int FX_OFFSET_09_COLOR4             = STR_AUX_SIZE + 9;

   /**
    * The anchoring to be used for this effect. Such as {@link ITechGraphics#TOP} | {@link ITechGraphics#LEFT}
    * <br>
    * Value of 0 defaults to TOP LEFT.
    * <li> {@link ITechGraphics#TOP}
    * <li> {@link ITechGraphics#LEFT}
    * 
    */
   public static final int FX_OFFSET_10_ANCHOR1            = STR_AUX_SIZE + 13;

   /**
    * 1 byte for flag
    */
   public static final int STR_AUX_FX_BASIC_SIZE           = STR_AUX_SIZE + 14;

}
