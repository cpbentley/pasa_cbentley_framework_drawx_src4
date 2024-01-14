/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.ctx;

import pasa.cbentley.byteobjects.src4.core.interfaces.IBOCtxSettings;

public interface ITechCtxSettingsDrwx extends IBOCtxSettings {

   public static final int CTX_DRW_BASIC_SIZE              = CTX_BASIC_SIZE + 5;

   /**
    * 
    */
   public static final int CTX_DRW_FLAG_1_USER_IMAGE_CACHE = 1 << 0;

   public static final int CTX_DRW_OFFSET_01_FLAG1         = CTX_BASIC_SIZE;

   /**
    * Visual theme ID used by the view. convenience spot. Model is not supposed to know about theming. Why not anyways?
    * <br>
    * <br>
    * 
    */
   public static final int CTX_DRW_OFFSET_02_              = CTX_BASIC_SIZE + 1;

}
