/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.tech;

import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigRectangle;

public interface ITechMergeFigureRect extends ITechMergeFigure {

   /**
    * {@link IBOFigRectangle#FIG_RECTANGLE_OFFSET_3_SIZE_ARCH1}
    */
   public static final int MERGE_FLAG_SIZE_ARCH   = MERGE_MASK_FLAG6_2;

   /**
    * {@link IBOFigRectangle#FIG_RECTANGLE_OFFSET_2_SIZE_ARCW1}
    */
   public static final int MERGE_FLAG_SIZE_ARCW   = MERGE_MASK_FLAG6_1;

   /**
    * {@link IBOFigRectangle#FIG_RECTANGLE_OFFSET_4_SIZE_FILL1}
    */
   public static final int MERGE_FLAG_SIZE_FILL   = MERGE_MASK_FLAG6_3;

   public static final int MERGE_FLAG_SIZE_GRAD   = MERGE_MASK_FLAG6_4;

   /**
    * {@link IBOFigRectangle#FIG_RECTANGLE_OFFSET_3_SIZE_ARCH1}
    */
   public static final int MERGE_OFFSET_SIZE_ARCH = MERGE_MASK_OFFSET_06_VALUES1;

   public static final int MERGE_OFFSET_SIZE_ARCW = MERGE_MASK_OFFSET_06_VALUES1;

   /**
    * {@link IBOFigRectangle#FIG_RECTANGLE_OFFSET_4_SIZE_FILL1}
    */
   public static final int MERGE_OFFSET_SIZE_FILL = MERGE_MASK_OFFSET_06_VALUES1;

   public static final int MERGE_OFFSET_SIZE_GRAD = MERGE_MASK_OFFSET_06_VALUES1;

}
