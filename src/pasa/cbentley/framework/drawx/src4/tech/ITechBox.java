package pasa.cbentley.framework.drawx.src4.tech;

import pasa.cbentley.byteobjects.src4.tech.ITechByteObject;
import pasa.cbentley.framework.coredraw.src4.interfaces.IGraphics;

/**
 * A Box defines how to size and align a rectangle relative to another rectangle
 * <br>
 * <li> int coded width sizer : {@link ITechBox#BOX_OFFSET_04_WIDTH4} 
 * <li> int coded height sizer : {@link ITechBox#BOX_OFFSET_05_HEIGHT4} 
 * <li> int coded x pozer : {@link ITechBox#BOX_OFFSET_02_HORIZ_ALIGN4} 
 * <li> int coded y pozer : {@link ITechBox#BOX_OFFSET_03_VERTICAL_ALIGN4} 
 * 
 * Encapsulates several {@link ISizer}
 * <br>
 * @author Charles Bentley
 *
 */
public interface ITechBox extends ITechByteObject {

   /**
    * Default anchors
    */
   public static final int ANCHOR                        = IGraphics.TOP | IGraphics.LEFT;

   public static final int BOX_UNDEFINED                 = -1;

   /**
    */
   public static final int BOX_BASIC_SIZE                = A_OBJECT_BASIC_SIZE + 17;

   /**
    * Is width defined
    */
   public static final int BOX_FLAG_5_DEF_WIDTH          = 1 << 4;

   /**
    * Is height defined
    */
   public static final int BOX_FLAG_6_DEF_HEIGHT         = 1 << 5;

   /**
    * When set, horiz value is null/undefined for merge purposes.
    * <br>
    */
   public static final int BOX_FLAG_7_DEF_HORIZ_ALIGN    = 1 << 6;

   /**
    * Existence flag for Vertical Alignement definition
    */
   public static final int BOX_FLAG_8_DEF_VERT_ALIGN     = 1 << 7;

   /**
    * Existence flag
    */
   public static final int BOX_OFFSET_01_FLAG            = A_OBJECT_BASIC_SIZE;

   /**
    * Horizontal alignment byte. <br>
    * Test definition with {@link ITechBox#BOX_FLAG_7_DEF_HORIZ_ALIGN}
    * 
    * <li>
    * 
    */
   public static final int BOX_OFFSET_02_HORIZ_ALIGN4    = A_OBJECT_BASIC_SIZE + 1;

   /**
    * Vertical alignment byte. <br>
    * Test definition with {@link ITechBox#BOX_FLAG_8_DEF_VERT_ALIGN}
    * <br>
    * Simple Anchor or Complex
    */
   public static final int BOX_OFFSET_03_VERTICAL_ALIGN4 = A_OBJECT_BASIC_SIZE + 5;

   /**
    * Encoded {@link ISizer}.
    * <br>
    * Shortcut for fill is {@link ISizer#Z_SIZE_FILL}
    * <br>
    * Box with its width sizer encoded on 4 bytes
    */
   public static final int BOX_OFFSET_04_WIDTH4          = A_OBJECT_BASIC_SIZE + 9;

   /**
    * Height 16 bits value
    */
   public static final int BOX_OFFSET_05_HEIGHT4         = A_OBJECT_BASIC_SIZE + 13;

}
