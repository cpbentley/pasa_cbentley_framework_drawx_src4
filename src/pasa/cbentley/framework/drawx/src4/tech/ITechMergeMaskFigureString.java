/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.tech;

import pasa.cbentley.byteobjects.src4.tech.ITechMergeMask;

public interface ITechMergeMaskFigureString extends ITechMergeMaskFigure {
   
   public static final int FIG_STR_MM_INDEX1        = ITechMergeMask.MERGE_MASK_BASIC_SIZE + 0;

   public static final int FIG_STR_MM_INDEX2        = ITechMergeMask.MERGE_MASK_BASIC_SIZE + 1;

   public static final int FIG_STR_MM_01_FLAG_INDEX = FIG_STR_MM_INDEX1;

   public static final int FIG_STR_MM_02_FACE_INDEX = FIG_STR_MM_INDEX2;

   /**
    * Flag tells it is the first flag at the index
    */
   public static final int FIG_STR_MM_02_FACE_FLAG  = ITechMergeMask.MERGE_MASK_FLAG5_1;

   public static final int FIG_STR_MM_03_STYLE_FLAG = ITechMergeMask.MERGE_MASK_FLAG5_1;

   public static final int FIG_STR_MM_04_SIZE_FLAG  = ITechMergeMask.MERGE_MASK_FLAG5_1;

   public static final int FIG_STR_MM_05_CHAR_FLAG  = ITechMergeMask.MERGE_MASK_FLAG5_1;

   public static final int FIG_STR_MM_06_SCALE_FLAG = ITechMergeMask.MERGE_MASK_FLAG5_1;

   public static final int FIG_STR_MM_07_SCALE_FLAG = ITechMergeMask.MERGE_MASK_FLAG5_1;
}
