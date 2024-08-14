/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.tech;

import pasa.cbentley.framework.drawx.src4.string.interfaces.IBOFigString;

public interface ITechMergeFigureString extends ITechMergeFigure {

   /**
    * {@link IBOFigString#FIG_STRING_OFFSET_03_FACE1}
    */
   public static final int MERGE_FLAG_STR_FACE    = MERGE_MASK_FLAG6_1;

   /**
    * {@link IBOFigString#FIG_STRING_OFFSET_05_SIZE1}
    */
   public static final int MERGE_FLAG_STR_SIZE    = MERGE_MASK_FLAG6_3;

   /**
    * {@link IBOFigString#FIG_STRING_OFFSET_04_STYLE1}
    */
   public static final int MERGE_FLAG_STR_STYLE   = MERGE_MASK_FLAG6_2;

   /**
    * Flag tells it is the first flag at the index
    */
   public static final int MERGE_OFFSET_STR_FACE  = MERGE_MASK_OFFSET_06_VALUES1;

   public static final int MERGE_OFFSET_STR_SIZE  = MERGE_MASK_OFFSET_06_VALUES1;

   public static final int MERGE_OFFSET_STR_STYLE = MERGE_MASK_OFFSET_06_VALUES1;

}
