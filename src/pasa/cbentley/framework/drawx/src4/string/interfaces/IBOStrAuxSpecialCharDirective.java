package pasa.cbentley.framework.drawx.src4.string.interfaces;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.utils.StringUtils;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;

/**
 * 
 * {@link ByteObject} of type {@link IBOStrAux} with subtype {@link IBOTypesDrawX#TYPE_DRWX_07_STRING_AUX_2_SPECIALS_C}.
 * 
 * <p>
 * How do deal with 
 * <li>newline
 * <li>formfeed
 * <li>tab
 * </p>
 * 
 * @author Charles Bentley
 *
 */
public interface IBOStrAuxSpecialCharDirective extends IBOStrAux {

   /**
    */
   public static final int AUX_CHARS_BASIC_SIZE                 = STR_AUX_SIZE + 5;

   /**
    * 
    */
   public static final int AUX_CHARS_FLAG_1_                    = 1 << 0;

   /**
    */
   public static final int AUX_CHARS_FLAG_2_SHOW_HIDDEN_CHARS   = 1 << 1;

   /**
    */
   public static final int AUX_CHARS_FLAG_3_                    = 1 << 2;

   /**
    */
   public static final int AUX_CHARS_FLAG_4_                    = 1 << 3;

   /**
    * 
    */
   public static final int AUX_CHARS_FLAG_5_                    = 1 << 4;

   /**
    * 
    */
   public static final int AUX_CHARS_FLAG_6_                    = 1 << 5;

   public static final int AUX_CHARS_FLAG_7_                    = 1 << 6;

   public static final int AUX_CHARS_FLAG_8_                    = 1 << 7;

   public static final int AUX_CHARS_OFFSET_01_FLAG             = STR_AUX_SIZE;

   /**
    * How to manage special character \n
    * <li> {@link ITechStringer#SPECIALS_NEWLINE_0_IGNORED}
    * <li> {@link ITechStringer#SPECIALS_NEWLINE_3_WORK}
    */
   public static final int AUX_CHARS_OFFSET_02_NEWLINE1         = STR_AUX_SIZE + 1;

   /**
    * How to manage special character \f -> {@link StringUtils#FORM_FEED_F}
    * <li> {@link ITechStringer#SPECIALS_NEWLINE_0_IGNORED}
    * <li> {@link ITechStringer#SPECIALS_NEWLINE_3_WORK}
    */
   public static final int AUX_CHARS_OFFSET_03_FORMFEED1        = STR_AUX_SIZE + 2;

   /**
    * How to manage special character  \t
    * <li> {@link ITechStringer#SPECIALS_TAB_0_SINGLE_SPACE}
    * <li> {@link ITechStringer#SPECIALS_TAB_2_JAVA_ESCAPED}
    * <li> {@link ITechStringer#SPECIALS_TAB_5_COLUMN}
    */
   public static final int AUX_CHARS_OFFSET_04_MANAGER_TAB1     = STR_AUX_SIZE + 3;

   /**
    * Depends on the Tab manager value.
    * 
    * <p>
    * In Column mode tells the size in spaces for one column. Historically it is often 8 or 4
    * </p>
    */
   public static final int AUX_CHARS_OFFSET_05_MANAGER_TAB_AUX1 = STR_AUX_SIZE + 4;

}
