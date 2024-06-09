package pasa.cbentley.framework.drawx.src4.string.interfaces;

import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;

/**
 * Defines an area in the String on which to apply an effect.
 * 
 * Type = {@link IBOTypesDrawX#TYPE_DRWX_13_FX_APPLICATOR}
 * 
 * 
 * @author Charles Bentley
 *
 */
public interface IBOStrAuxFxApplicator extends IBOStrAux {

   /**
    */
   public static final int FXA_BASIC_SIZE               = STR_AUX_SIZE + 10;

   public static final int FXA_FLAG_1_                  = 1;

   /**
    * 
    */
   public static final int FXA_FLAG_2_                  = 1 << 1;

   /**
    * 
    */
   public static final int FXA_FLAG_3_                  = 1 << 2;

   /**
    * 
    */
   public static final int FXA_FLAG_4_                  = 1 << 3;

   public static final int FXA_FLAG_5_                  = 1 << 4;

   public static final int FXA_FLAG_6_                  = 1 << 5;

   public static final int FXA_FLAG_7_                  = 1 << 6;

   public static final int FXA_FLAG_8_                  = 1 << 7;

   /**
    * <li> {@link IBOStrAuxFxApplicator#FXA_FLAG_1_}
    * <li> {@link IBOStrAuxFxApplicator#FXA_FLAG_2_}
    * <li> {@link IBOStrAuxFxApplicator#FXA_FLAG_3_}
    * <li> {@link IBOStrAuxFxApplicator#FXA_FLAG_4_}
    * <li> {@link IBOStrAuxFxApplicator#FXA_FLAG_5_}
    * <li> {@link IBOStrAuxFxApplicator#FXA_FLAG_6_}
    * <li> {@link IBOStrAuxFxApplicator#FXA_FLAG_7_}
    * <li> {@link IBOStrAuxFxApplicator#FXA_FLAG_8_}
    * 
    */
   public static final int FXA_OFFSET_01_FLAG           = A_OBJECT_BASIC_SIZE + 0;

   /**
    * Holds the index value of the character/word/line for which this Fx applies.
    * <li> 0 means the first character
    * <li> 1 second character
    * <br>
    * etc.
    * <p>
    * From where counting, read scope {@link IBOFxStr#FX_OFFSET_11_INDEX_SCOPE1}
    * <br>
    * <br>
    * <li>{@link ITechStringDrw#FX_SCOPE_1_CHAR}, it means the first char of ? each word? each line?
    * <li>{@link ITechStringDrw#FX_SCOPE_2_WORD} index 0 means the first word of text or line?
    * <li>{@link ITechStringDrw#FX_SCOPE_2_LINE} each word of text/line?
    * <li>For scope Paragraph, 0 is the first element each word of text/line?
    * <li>For scope Text, 0 is the very first word of the whole text
    * <br>
    * </p>
    * 
    */
   public static final int FXA_OFFSET_02_INDEX2         = A_OBJECT_BASIC_SIZE + 15;

   /**
    * Describes how to repeat index in the given scope of this effect.
    * <li> {@link ITechStringer#FX_PATTERN_0_NONE} is not repetition
    * <li> {@link ITechStringer#FX_PATTERN_1_ALL_INSTANCES} is repeat for all instance
    * <br>
    * In which was .
    * <br>
    * <br>
    * How to define a random fx for each word/char?
    * <br>
    * <br>
    * First, root fx is defined with flag {@link IBOFxStr#FX_FLAG_2_ROOT}. Than an array of fxs are subbed.
    * <br>
    * <br>
    * Those are used for the randomization. The scope of those subs is ignored. They inherit scope of root.
    * <br>
    * <br>
    */
   public static final int FXA_OFFSET_03_INDEX_PATTERN1 = A_OBJECT_BASIC_SIZE + 13;

}
