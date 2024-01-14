package pasa.cbentley.framework.drawx.src4.tech;

import pasa.cbentley.byteobjects.src4.core.interfaces.IByteObject;

public interface IBOScaler extends IByteObject, ITechScaler {

   public static final int SCALE_BASE_OFFSET         = ITechPass.PASS_BASIC_SIZE;

   /**
    * 1 byte flags
    * 1 byte scale type
    * 1 byte scale id
    */
   public static final int SCALE_BASIC_SIZE          = ITechPass.PASS_BASIC_SIZE + 3;

   /**
    * Try to use
    */
   public static final int SCALE_FLAG_01_FRAMEWORK   = 1;

   /**
    * 
    */
   public static final int SCALE_OFFSET_01_FLAG      = SCALE_BASE_OFFSET;

   /**
    * Describes how to compute newWidth and newHeight of an image to be fitted inside an area.
    * 
    * <li> {@link ITechScaler#SCALER_TYPE_0_FIT_NONE}
    * <li> {@link ITechScaler#SCALER_TYPE_1_FIT_BOTH}
    * <li> {@link ITechScaler#SCALER_TYPE_2_FIT_W}
    * <li> {@link ITechScaler#SCALER_TYPE_3_FIT_H}
    * <li> {@link ITechScaler#SCALER_TYPE_4_FIT_FIRST}
    * <li> {@link ITechScaler#SCALER_TYPE_5_FIT_LAST}
    * 
    */
   public static final int SCALE_OFFSET_02_FIT_TYPE1 = SCALE_BASE_OFFSET + 1;

   /**
    * <li> {@link ITechScaler#SCALER_ID_0_LINEAR}
    * <li> {@link ITechScaler#SCALER_ID_1_BI_LINEAR}
    * <li> {@link ITechScaler#SCALER_ID_2_BI_CUBIC}
    */
   public static final int SCALE_OFFSET_03_ID1       = SCALE_BASE_OFFSET + 2;

}
