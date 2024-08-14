/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.tech;

import pasa.cbentley.byteobjects.src4.objects.pointer.IBOMerge;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOFigure;

/**
 * Defines merge flags for {@link IBOFigure}
 * 
 * @author Charles Bentley
 *
 */
public interface ITechMergeFigure extends IBOFigure, IBOMerge {

   /**
    * Merge for {@link IBOFigure#FIG__OFFSET_06_COLOR4}
    */
   public static final int MERGE_FLAG_FIG_COLOR   = MERGE_MASK_FLAG5_2;

   /**
    * Merge for {@link IBOFigure#FIG__OFFSET_05_DIR1}
    */
   public static final int MERGE_FLAG_FIG_DIR     = MERGE_MASK_FLAG5_1;

   public static final int MERGE_FLAG_FIG_TYPE    = MERGE_MASK_FLAG5_1;

   public static final int MERGE_OFFSET_FIG_FLAG  = MERGE_MASK_OFFSET_01_FLAG1;

   public static final int MERGE_OFFSET_FIG_COLOR = MERGE_MASK_OFFSET_05_VALUES1;

   public static final int MERGE_OFFSET_FIG_DIR   = MERGE_MASK_OFFSET_05_VALUES1;

   public static final int MERGE_OFFSET_FIG_TYPE  = MERGE_MASK_OFFSET_05_VALUES1;

}
