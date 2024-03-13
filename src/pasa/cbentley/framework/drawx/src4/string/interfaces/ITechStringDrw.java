/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.string.interfaces;

import pasa.cbentley.core.src4.text.TextModel;

public interface ITechStringDrw {
   /**
    * No breaking at all. Special characters like '\n'
    */
   public static final int BREAK_0_NONE             = 0;

   /**
    * Break after a giving width is consumed.
    */
   public static final int BREAK_1_WIDTH            = 1;

   /**
    * Only breaks at newlines characters \n
    */
   public static final int BREAK_2_NATURAL          = 2;

   /**
    * Overrides ViewPane to expand
    * Removes all new lines characters
    */
   public static final int BREAK_3_ONE_LINE         = 3;

   /**
    * Trim String when not enough room to display it on a single line.
    * <br>
    */
   public static final int BREAK_4_TRIM_SINGLE_LINE = 4;

   /**
    * Trim to fit the break height. Accepts more than 1 line. but at least one.
    * <br>
    * That is if the space is not enough for a line, line is drawn and clipped to available
    * space.
    * <br>
    * If there is only 1 line, no breaking occurs.
    * <br>
    * <br>
    * 
    */
   public static final int BREAK_5_TRIM_FIT_HEIGHT  = 5;

   /**
    * Only one word on each line. White space are ignored.
    */
   public static final int BREAK_6_WORD_LINE        = 6;

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
    * <li> individual char level Mask Figure
    */
   int FX_SCOPE_1_CHAR                 = 1;

   /**
    * FX is applied to string 'word'.
    * What happens when the word is cut with -? Right now words are not cut.
    * 
    * <p>
    * Competing Word and Line Fx ?
    * Examples. Word Mask vs Line Mask Smaller Scope wins in practice.
    * But if Word Mask fx applies to the first word of a line, the line mask is applied to the whole line including the first word
    * Line Mask might make the space glow in a certain way! so Line Mask does not lose completely
    * </p>
    */
   int FX_SCOPE_2_WORD                 = 2;

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
    * TODO how do model alternative line background styling ?
    * index based function .. style applicator ?
    */
   int FX_SCOPE_2_LINE                 = 2;

   /**
    * Each text block separated with an empty line
    */
   int FX_SCOPE_3_PARA                 = 3;

   /**
    * Treat the whole text as referential
    */
   int FX_SCOPE_0_TEXT                 = 0;

   /**
    * Break text into sentences. The start of a sentence is always a abcABC character?
    */
   int FX_SCOPE_5_FRAZ                 = 5;

   /**
    * Only applies to word separators as defined in {@link TextModel}.
    * usually whitespace and ,
    */
   int FX_SCOPE_6_SEPARATORS           = 6;


}
