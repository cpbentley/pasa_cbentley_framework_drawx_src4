package pasa.cbentley.framework.drawx.src4.string.interfaces;

import pasa.cbentley.byteobjects.src4.core.interfaces.IByteObject;

/**
 * 
 * @author Charles Bentley
 *
 */
public interface IBOStrAux extends IByteObject {

   /**
    * 1 byte for sub type
    */
   public static final int STR_AUX_SIZE               = A_OBJECT_BASIC_SIZE + 1;

   public static final int STR_AUX_OFFSET_1_EXT_TYPE1 = A_OBJECT_BASIC_SIZE;

}
