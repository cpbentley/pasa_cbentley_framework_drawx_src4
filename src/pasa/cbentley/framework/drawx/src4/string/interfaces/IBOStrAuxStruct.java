package pasa.cbentley.framework.drawx.src4.string.interfaces;

import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;

/**
 * {@link IBOTypesDrawX#TYPE_DRWX_07_STRING_AUX_1_STRUCT}
 * 
 * @author Charles Bentley
 *
 */
public interface IBOStrAuxStruct extends IBOStrAux {

   /**
    */
   public static final int FIG_STRING_BASIC_SIZE             = STR_AUX_SIZE + 15;

   /**
    * 
    */
   public static final int FIG_STRING_FLAG_1_                = 1 << 0;

   /**
    */
   public static final int FIG_STRING_FLAG_2_                = 1 << 1;

   /**
    * When true, a "..." artifact is drawn where a line a trimmed because of the lack of space.
    */
   public static final int FIG_STRING_FLAG_3_TRIM_ARTIFACT   = 1 << 2;

   /**
    * Each Lines are drawn vertically next to each other
    * 
    * E L a
    * a l r
    * c n e
    * h e 
    */
   public static final int FIG_STRING_FLAG_4_VERTICAL        = 1 << 3;

   /**
    * 
    */
   public static final int FIG_STRING_FLAG_5_                = 1 << 4;

   /**
    * 
    */
   public static final int FIG_STRING_FLAG_6_                = 1 << 5;

   public static final int FIG_STRING_FLAG_7_                = 1 << 6;

   public static final int FIG_STRING_FLAG_8_                = 1 << 7;

   public static final int FIG_STRING_OFFSET_01_FLAG         = STR_AUX_SIZE;

   /**
    * <li> {@link ITechStringer#WORDWRAP_0_NONE}
    * <li> {@link ITechStringer#WORDWRAP_1_ANYWHERE}
    * <li> {@link ITechStringer#WORDWRAP_2_NICE_WORD}
    * <li> {@link ITechStringer#WORDWRAP_3_NICE_HYPHENATION}
    */
   public static final int FIG_STRING_OFFSET_02_WRAP_WIDTH1  = STR_AUX_SIZE + 7;

   /**
    * Decides what to do on a constrained height dimension
    * <li> {@link ITechStringer#LINEWRAP_0_NONE}
    * <li> {@link ITechStringer#LINEWRAP_1_ANYWHERE}
    */
   public static final int FIG_STRING_OFFSET_03_WRAP_HEIGHT1 = STR_AUX_SIZE + 8;

   /**
    * Decide the policy of dealing with spaces
    * <li> {@link ITechStringer#SPACETRIM_0_NONE} 
    * <li> {@link ITechStringer#SPACETRIM_1_NORMAL} 
    * <li> {@link ITechStringer#SPACETRIM_2_JUSTIFIED} 
    */
   public static final int FIG_STRING_OFFSET_04_SPACE_TRIM1  = STR_AUX_SIZE + 9;

   /**
    * Ignores 0 and <0 and assumes 1 as default
    * 
    * When 2 or more, draw that many lines.
    * 
    * A trim artifacts might be drawn {@link IBOStrAuxStruct#FIG_STRING_FLAG_3_TRIM_ARTIFACT}
    */
   public static final int FIG_STRING_OFFSET_05_MAXLINES1    = STR_AUX_SIZE + 10;

}
