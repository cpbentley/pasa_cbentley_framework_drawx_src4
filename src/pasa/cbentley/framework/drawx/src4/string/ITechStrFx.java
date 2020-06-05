/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.string;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.functions.Function;
import pasa.cbentley.byteobjects.src4.tech.ITechByteObject;

/**
 * {@link ByteObject} template for defining String text effects. Creator is {@link FxStringFactory}.
 * <br>
 * <br>
 * Each definition is set to a scope
 * <li> {@link ITechStrFx#FX_SCOPE_0_CHAR}
 * <li> {@link ITechStrFx#FX_SCOPE_2_LINE}
 * <li> {@link ITechStrFx#FX_SCOPE_2_COMPOSITE}
 * <br>
 * <br>
 * 
 * Implement same structure as Figures with sub types.
 * <br>
 * <br>
 * Example of hierarchies
 * <br>
 * <li> Root is for characters
 * <li> Sub defines style for characters of first word each line
 * <br>
 * <br>
 * <b>Merging</b>
 * <li>You can't merge two different scope
 * 
 * @author Charles-Philip Bentley
 *
 */
public interface ITechStrFx extends ITechByteObject {

   /**
    * 1 byte for flag
    * 1 byte for type scope
    */
   public static final int FX_BASIC_SIZE                   = A_OBJECT_BASIC_SIZE + 14;

   public static final int FX_COMPOSITE_BASIC_SIZE         = A_OBJECT_BASIC_SIZE + 6;

   /**
    * When effect is very specific and {@link ITechStrFx#FX_OFFSET_04_INDEX2} gives the
    * ID of the effect.
    */
   public static final int FX_FLAG_1_SPECIFIC_SWITCH       = 1;

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
    * Has the Text effect a {@link IDrwTypes#TYPE_060_TBLR}  defining extra pixel space used by text effect
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
    * Simple so code can double check it is with a template for a root fx definition.
    * 
    */
   public static final int FX_FLAGX_1_ROOT                 = 1;

   /**
    * Flag saying this definition is to be activated upon some user actions.
    * <br>
    * <br>
    * 99% of the time, a dynamic fx is character scoped.
    */
   public static final int FX_FLAGX_2_DYNAMIC              = 1 << 1;

   /**
    * When set font definition overrides previous ones.
    */
   public static final int FX_FLAGX_4_DEFINED_FONT         = 1 << 3;

   /**
    * When set color definition overrides previous ones.
    * <br>
    * <br>
    * Used by merging procedure.
    */
   public static final int FX_FLAGX_5_DEFINED_COLOR        = 1 << 4;

   public static final int FX_FLAGX_6_DEFINED_INDEX        = 1 << 5;

   public static final int FX_FLAGX_7_INCOMPLETE           = 1 << 6;

   /**
    * The effect is actually to be choosen by a function using the sub text effect
    * of this instance.
    * <li> Random function
    * <li> Serie function.
    * <li> ...
    */
   public static final int FX_FLAGX_8_FUNCTION             = 1 << 7;

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
   public static final int FX_FLAGZ_1_COLOR_FILTER         = 1;

   /**
    * Sets when a Figure object is paramed. it will be used a simple background figure
    */
   public static final int FX_FLAGZ_2_FIGURE               = 1 << 1;

   /**
    * Defines a {@link IDrwTypes#TYPE_058_MASK} in its parameters.
    * <br>
    * <br>
    * 
    */
   public static final int FX_FLAGZ_3_MASK                 = 1 << 2;

   /**
    * Filter post mask
    */
   public static final int FX_FLAGZ_4_MIDDLE_FILTER        = 1 << 3;

   /**
    * Decoration post 1st filter
    * <br>
    * <br>
    * 
    */
   public static final int FX_FLAGZ_5_DECORATION           = 1 << 4;

   /**
    * Filter applied after the decoration figure
    */
   public static final int FX_FLAGZ_6_LAST_FILTER          = 1 << 5;

   /**
    * The rectangular area controlled by the scoped element is treated with a Style
    * element.
    * <br>
    * <br>
    * The style {@link IViewTypes#TYPE_071_STYLE} is stored as a sub parameter of this object.
    * <br>
    * <br>
    * When this happens, a {@link FigDrawable} is used to wrap around a basic String figure.
    * <br>
    * The figure will be a char, a word or a line.
    * <br>
    * 
    */
   public static final int FX_FLAGZ_7_STYLE                = 1 << 6;

   /**
    * 1 byte for normal switches
    * 
    */
   public static final int FX_OFFSET_01_FLAG               = A_OBJECT_BASIC_SIZE;

   public static final int FX_OFFSET_02_FLAGX              = A_OBJECT_BASIC_SIZE;

   /**
    * The scope of the text effect. 
    * <br>
    * <br>
    * This allows code to know which template to use for reading this {@link ByteObject} without error.
    * <li> {@link ITechStrFx#FX_SCOPE_0_CHAR}
    * <li> {@link ITechStrFx#FX_SCOPE_2_LINE}
    * <li> {@link ITechStrFx#FX_SCOPE_1_WORD}
    * <li> {@link ITechStrFx#FX_SCOPE_3_PARAGRAPH}
    * <li> {@link ITechStrFx#FX_SCOPE_3_PARAGRAPH}
    * <br>
    * <br>
    * 
    */
   public static final int FX_OFFSET_03_SCOPE1             = A_OBJECT_BASIC_SIZE + 1;

   /**
    * Holds the index value of the character/word/line for which this Fx applies.
    * <br>
    * <br>
    * <li> 0 means the first character
    * <li> 1 second character
    * <br>
    * etc.
    * From where counting, read scope {@link ITechStrFx#FX_OFFSET_11_INDEX_SCOPE1}
    * <br>
    * <br>
    * <li>{@link ITechStrFx#FX_SCOPE_0_CHAR}, it means the first char of ? each word? each line?
    * <li>{@link ITechStrFx#FX_SCOPE_1_WORD} index 0 means the first word of text or line?
    * <li>{@link ITechStrFx#FX_SCOPE_2_LINE} each word of text/line?
    * <li>For scope Paragraph, 0 is the first element each word of text/line?
    * <li>For scope Text, 0 is the very first word of the whole text
    * <br>
    * <br>
    * 
    */
   public static final int FX_OFFSET_04_INDEX2             = A_OBJECT_BASIC_SIZE + 2;

   /**
    * Describes how to repeat index in the given scope of this effect.
    * <br>
    * <br>
    * <li> 0 is not repetition
    * <li> 1 is repeat for all instance
    * <br>
    * In which was .
    * <br>
    * <br>
    * How to define a random fx for each word/char?
    * <br>
    * <br>
    * First, root fx is defined with flag {@link ITechStrFx#FX_FLAGX_1_ROOT}. Than an array of fxs are subbed.
    * <br>
    * <br>
    * Those are used for the randomization. The scope of those subs is ignored. They inherit scope of root.
    * <br>
    * <br>
   
    */
   public static final int FX_OFFSET_05_INDEX_PATTERN1     = A_OBJECT_BASIC_SIZE + 4;

   /**
    * Provides {@link ITechStrFx#FX_OFFSET_04_INDEX2} scope
    * <li> First character of every word char scope index 0, scope {@link ITechStrFx#FX_SCOPE_1_WORD}.
    * <li> First line of every paragraph.
    * <li> Random index of every word bigger than 2 letters. Index {@link Function} and Acceptor for Word.
    * <br>
    * <br>
    * Scope in this field must be bigger than scope of Fx definition.
    * 
    * 
    */
   public static final int FX_OFFSET_11_INDEX_SCOPE1       = A_OBJECT_BASIC_SIZE + 13;

   /**
    * MM: flag 1 of offset 6.
    * <br>
    * <br>
    * ID To a set of fonts defined by driver.
    */
   public static final int FX_OFFSET_06_FACE1              = A_OBJECT_BASIC_SIZE + 5;

   /**
    * MM: flag 2 of offset 6.
    */
   public static final int FX_OFFSET_07_STYLE1             = A_OBJECT_BASIC_SIZE + 6;

   /**
    * MM: flag 3 of offset 6.
    */
   public static final int FX_OFFSET_08_SIZE1              = A_OBJECT_BASIC_SIZE + 7;

   /**
    * Fx base color
    */
   public static final int FX_OFFSET_09_COLOR4             = A_OBJECT_BASIC_SIZE + 8;

   public static final int FX_OFFSET_10_FLAGZ              = A_OBJECT_BASIC_SIZE + 12;

   /**
    * The anchoring to be used for this effect. Such as {@link Graphics#TOP} | {@link Graphics#LEFT}
    * <br>
    * <br>
    * Value of 0 defaults to TOP LEFT.
    * <br>
    * <br>
    * 
    */
   public static final int FX_OFFSET_11_ANCHOR1            = A_OBJECT_BASIC_SIZE + 13;

   /**
    * The CHAR level is the first one. It defines how characters are drawn relative to each other.
    * <br>
    * <br>
    * 
    * Random styling: each char has a random font from pooling defined
    * <br>
    * <br>
    * Effect to be create for each characters.
    * <br>
    * <br>
    * 
    */
   public static final int FX_SCOPE_0_CHAR                 = 0;

   /**
    * At the LINE level we find the following options : 
    * <br>
    * <li>The Relative function positioning.
    * 	Default is x unchanged and y increment by Font/Line Height
    * 	Function similar to the Relative Function Position of characters 
    * 	but applied to lines
    * <li>RFP has an impact the width and height consumed by the text effect
    * <li>One Word Per Line
    * <li>x-Line Level Mask Figure (apply mask underyling figure to X lines at a time
    * <br>
    * <br>
    * 
    */
   public static final int FX_SCOPE_2_LINE                 = 2;

   /**
    * FX is applied to string 'word'.
    * What happens when the word is cut with -? Right now words are not cut.
    * 
    */
   public static final int FX_SCOPE_1_WORD                 = 1;

   /**
    * Treat the whole text as referential
    */
   public static final int FX_SCOPE_3_PARAGRAPH            = 3;

   public static final int FX_SCOPE_4_TEXT                 = 4;

   /**
    * 
    */
   public static final int FXCHAR_BASIC_SIZE               = FX_BASIC_SIZE + 4;

   /**
    * Flags specific to the {@link ITechStrFx#FX_SCOPE_0_CHAR}.
    * 
    */
   public static final int FXCHAR_OFFSET_01_FLAG           = FX_BASIC_SIZE;

   /**
    * Type of decoration around the character.
    */
   public static final int FXCHAR_OFFSET_02_TYPE_DECO1     = FX_BASIC_SIZE + 1;

   public static final int FXLINE_BASIC_SIZE               = FX_BASIC_SIZE + 4;

   /**
    * Is there a figure to draw between chars
    * <br>
    * <br>
    * When this flag is set the last figure in sub parameters is that figure.
    * <br>
    * <br>
    * POssibly the first one if there are 2, is the bg figure.
    */
   public static final int FXLINE_FLAG_1_INTERCHAR_FIG     = 1;

   /**
    * Simple color gradient over the characters in a line
    * <br>
    * <br>
    * When this flag is set, a {@link IDrwTypes#TYPE_059_GRADIENT} is in the sub parameters.
    * 
    */
   public static final int FXLINE_FLAG_2_GRADIENT          = 1 << 1;

   /**
    * Is there a XF.
    * <br>
    * <br>
    * A Function is defined to 
    */
   public static final int FXLINE_FLAG_5_DEFINED_XF        = 1 << 4;

   /**
    * Is a Y shift defined?
    */
   public static final int FXLINE_FLAG_6_DEFINED_YF        = 1 << 5;

   /**
    * A function decides how to position characters on a line.
    * <br>
    * <br>
    * By default, input is character width and returns this value.
    * <br>
    * <br>
    * It is a relative function. Relative? Well, it is or can be absolute, but 
    * in this case, the character index must be given to the function.
    * <br>
    * <br>
    * 
    */
   public static final int FXLINE_FLAG_7_FUNCTION_XF       = 1 << 6;

   /**
    * A second function define the y.
    * <br>
    * <br>
    * By default input is current y position and returns y.
    */
   public static final int FXLINE_FLAG_8_FUNCTION_Y        = 1 << 7;

   /**
    * 
    */
   public static final int FXLINE_OFFSET_01_FLAG           = 0;

   /**
    * Additional x offset between chars in a line of text
    * <br>
    * signed
    * <br>
    * 0 -> vertical
    */
   public static final int FXLINE_OFFSET_02_CHAR_X_OFFSET1 = A_OBJECT_BASIC_SIZE + 2;

   /**
    * Additional y offset between chars in a line
    * 0 -> horizontal
    */
   public static final int FXLINE_OFFSET_03_CHAR_Y_OFFSET1 = A_OBJECT_BASIC_SIZE + 3;

}
