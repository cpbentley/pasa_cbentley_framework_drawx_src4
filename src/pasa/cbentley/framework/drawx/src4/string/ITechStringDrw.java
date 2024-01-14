/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.string;

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


}
