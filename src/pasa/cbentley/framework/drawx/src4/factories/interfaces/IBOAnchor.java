package pasa.cbentley.framework.drawx.src4.factories.interfaces;

import pasa.cbentley.byteobjects.src4.core.interfaces.IByteObject;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechGraphics;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;

public interface IBOAnchor extends IByteObject {

   public static final int ANCHOR_TYPE                      = IBOTypesDrawX.TYPE_DRWX_10_ANCHOR;

   public static final int ANCHOR_G_TOP_LEFT                = ITechGraphics.TOP | ITechGraphics.LEFT;

   public static final int ANCHOR_G_CENTER_CENTER           = ITechGraphics.HCENTER | ITechGraphics.VCENTER;

   /**
    */
   public static final int ANCHOR_BASIC_SIZE                = A_OBJECT_BASIC_SIZE + 3;

   public static final int ANCHOR_FLAG_1_INCOMPLETE         = 1 << 0;

   /**
    * When set, horiz value is null/undefined for merge purposes.
    * <br>
    */
   public static final int ANCHOR_FLAG_7_DEF_HORIZ_ALIGN    = 1 << 6;

   /**
    * Existence flag for Vertical Alignement definition
    */
   public static final int ANCHOR_FLAG_8_DEF_VERT_ALIGN     = 1 << 7;

   /**
    * Existence flag
    */
   public static final int ANCHOR_OFFSET_01_FLAG            = A_OBJECT_BASIC_SIZE;

   /**
    * Horizontal alignment byte. <br>
    * Test definition with {@link IBOAnchor#ANCHOR_FLAG_7_DEF_HORIZ_ALIGN}
    * 
    * <li>
    * 
    */
   public static final int ANCHOR_OFFSET_02_HORIZ_ALIGN1    = A_OBJECT_BASIC_SIZE + 1;

   /**
    * Vertical alignment byte. <br>
    * Test definition with {@link IBOAnchor#ANCHOR_FLAG_8_DEF_VERT_ALIGN}
    * <br>
    * Simple Anchor or Complex
    */
   public static final int ANCHOR_OFFSET_03_VERTICAL_ALIGN1 = A_OBJECT_BASIC_SIZE + 2;

}
