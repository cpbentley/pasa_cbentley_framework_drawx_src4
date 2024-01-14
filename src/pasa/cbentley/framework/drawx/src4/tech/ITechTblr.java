/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.tech;

import pasa.cbentley.byteobjects.src4.core.interfaces.IByteObject;

public interface ITechTblr extends IByteObject {

   public static final int TBLR_UNDEF              = -1;

   /**
    * 1 byte for flag
    * 1 byte for type
    */
   public static final int TBLR_BASIC_SIZE         = A_OBJECT_BASIC_SIZE + 6;

   public static final int TBLR_FLAG_1_USING_ARRAY = 1;

   /**
    * Same when all TBLR have the same value
    */
   public static final int TBLR_FLAG_4_SAME_VALUE  = 8;

   /**
    * 4 bits mask to tell Merge if it is defined. So no need of a merge mask
    */
   public static final int TBLR_FLAG_5_DEF_TOP     = 16;

   public static final int TBLR_FLAG_6_DEF_BOT     = 32;

   public static final int TBLR_FLAG_7_DEF_LEFT    = 64;

   public static final int TBLR_FLAG_8_DEF_RIGHT   = 128;

   public static final int TBLR_OFFSET_01_FLAG     = A_OBJECT_BASIC_SIZE;

   /**
    * Type of encoding for {@link ITechTblr#TBLR_OFFSET_03_DATA4}
    * <li> {@link ITechTblr#TYPE_0_ONE} // 4 bytes coded sizer
    * <li> {@link ITechTblr#TYPE_1_SIZER} //full sizer ByteObject
    */
   public static final int TBLR_OFFSET_02_TYPE1    = A_OBJECT_BASIC_SIZE + 1;

   /**
    * Start of data. for same size value.
    * <br>
    * When using a {@link ITypesBentleyFw#FTYPE_3_SIZER}, the value is a pointer
    * <br>
    * Sometimes the figure using the TBLR will use its own SIZER to interpret TBLR values
    */
   public static final int TBLR_OFFSET_03_DATA4    = A_OBJECT_BASIC_SIZE + 2;

   /**
    * Single value for all.
    */
   public static final int TYPE_0_ONE              = 0;

   /**
    * Sizers are defined with a 1 byte pointer. When flag samevalue is set,
    * one sizer definition, one pointer
    */
   public static final int TYPE_1_SIZER            = 1;

}
