package pasa.cbentley.framework.drawx.src4.factories.interfaces;

import pasa.cbentley.byteobjects.src4.core.interfaces.IByteObject;

/**
 * A type that controls a figure and a box
 * 
 * @author Charles Bentley
 *
 */
public interface IBOStructFigSub extends IByteObject {

   public static final int FIG_LAYOUT_BASIC_SIZE            = A_OBJECT_BASIC_SIZE + 8;

   /**
    * When true draws
    */
   public static final int FIG_LAYOUT_FLAG_1_               = 1 << 0;

   /**
    * A Figure is drawn for primary area
    */
   public static final int FIG_LAYOUT_FLAG_2_               = 1 << 1;

   /**
    * A Figure is drawn for secondary area
    */
   public static final int FIG_LAYOUT_FLAG_3_               = 1 << 2;

   public static final int FIG_LAYOUT_FLAG_4_               = 1 << 3;

   public static final int FIG_LAYOUT_FLAG_5_               = 1 << 4;

   public static final int FIG_LAYOUT_OFFSET_1_FLAG         = A_OBJECT_BASIC_SIZE;

   /**
    */
   public static final int FIG_LAYOUT_OFFSET_2_TYPE1        = A_OBJECT_BASIC_SIZE + 1;

   /**
    * Number of figures layouted.
    * 
    */
   public static final int FIG_LAYOUT_OFFSET_3_NUM_FIGURES2 = A_OBJECT_BASIC_SIZE + 2;

   /**
    * 
    */
   public static final int FIG_LAYOUT_OFFSET_5_INDEX_START2 = A_OBJECT_BASIC_SIZE + 4;

}
