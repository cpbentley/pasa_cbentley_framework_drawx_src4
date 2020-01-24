package pasa.cbentley.framework.drawx.src4.ctx;

import pasa.cbentley.byteobjects.src4.tech.ITechCtxSettings;

public interface ITechCtxSettingsDrw extends ITechCtxSettings {
   
   
   public static final int MODSET_DRW_BASIC_SIZE              = MODSET_BASIC_SIZE + 5;

   /**
    * 
    */
   public static final int MODSET_DRW_FLAG_1_USER_IMAGE_CACHE = 1 << 0;

   public static final int MODSET_DRW_OFFSET_01_FLAG1         = MODSET_BASIC_SIZE;

   /**
    * Visual theme ID used by the view. convenience spot. Model is not supposed to know about theming. Why not anyways?
    * <br>
    * <br>
    * 
    */
   public static final int MODSET_DRW_OFFSET_02_              = MODSET_BASIC_SIZE + 1;

}
