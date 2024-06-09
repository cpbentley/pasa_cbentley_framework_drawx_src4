package pasa.cbentley.framework.drawx.src4.string.interfaces;

import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;

/**
 * {@link IBOTypesDrawX#TYPE_DRWX_07_STRING_AUX_1_FORMAT}
 * 
 * @author Charles Bentley
 *
 */
public interface IBOStrAuxFormat extends IBOStrAux {

   /**
    */
   public static final int STR_FORMAT_BASIC_SIZE             = STR_AUX_SIZE + 5;

   /**
    * 
    */
   public static final int STR_FORMAT_FLAG_1_                = 1 << 0;

   /**
    */
   public static final int STR_FORMAT_FLAG_2_                = 1 << 1;

   /**
    * When true, a "..." artifact is drawn where a line a trimmed because of the lack of space.
    */
   public static final int STR_FORMAT_FLAG_3_TRIM_ARTIFACT   = 1 << 2;

   /**
    * Each Lines are drawn vertically next to each other
    * 
    * E L a
    * a l r
    * c n e
    * h e 
    */
   public static final int STR_FORMAT_FLAG_4_VERTICAL        = 1 << 3;

   /**
    * 
    */
   public static final int STR_FORMAT_FLAG_5_                = 1 << 4;

   /**
    * 
    */
   public static final int STR_FORMAT_FLAG_6_                = 1 << 5;

   public static final int STR_FORMAT_FLAG_7_                = 1 << 6;

   public static final int STR_FORMAT_FLAG_8_                = 1 << 7;

   public static final int STR_FORMAT_OFFSET_01_FLAG         = STR_AUX_SIZE;

   /**
    * <li> {@link ITechStringer#WORDWRAP_0_NONE}
    * <li> {@link ITechStringer#WORDWRAP_1_ANYWHERE}
    * <li> {@link ITechStringer#WORDWRAP_2_NICE_WORD}
    * <li> {@link ITechStringer#WORDWRAP_3_NICE_HYPHENATION}
    */
   public static final int STR_FORMAT_OFFSET_02_WRAP_WIDTH1  = STR_AUX_SIZE + 1;

   /**
    * Decides what to do on a constrained height dimension
    * <li> {@link ITechStringer#LINEWRAP_0_NONE}
    * <li> {@link ITechStringer#LINEWRAP_1_ANYWHERE}
    */
   public static final int STR_FORMAT_OFFSET_03_WRAP_HEIGHT1 = STR_AUX_SIZE + 2;

   /**
    * Decide the policy of dealing with spaces
    * <li> {@link ITechStringer#SPACETRIM_0_NONE} 
    * <li> {@link ITechStringer#SPACETRIM_1_NORMAL} 
    * <li> {@link ITechStringer#SPACETRIM_2_JUSTIFIED} 
    */
   public static final int STR_FORMAT_OFFSET_04_SPACE_TRIM1  = STR_AUX_SIZE + 3;

   /**
    * Ignores 0 and <0 and assumes 1 as default
    * 
    * When 2 or more, draw that many lines.
    * 
    * A trim artifacts might be drawn {@link IBOStrAuxFormat#STR_FORMAT_FLAG_3_TRIM_ARTIFACT}
    */
   public static final int STR_FORMAT_OFFSET_05_MAXLINES1    = STR_AUX_SIZE + 4;

}
