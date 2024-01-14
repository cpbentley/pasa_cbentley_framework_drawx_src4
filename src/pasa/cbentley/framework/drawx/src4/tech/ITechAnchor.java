/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.tech;

import pasa.cbentley.byteobjects.src4.core.interfaces.IByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesDrw;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechGraphics;

/**
 * A {@link ITechAnchor} defines how to position some content relative to a Box.
 * <br>
 * It is some kind of pozer with parent/context the box?
 * 
 * @author Charles Bentley
 *
 */
public interface ITechAnchor extends IByteObject {

   public static final int ANCHOR_TYPE                      = IBOTypesDrw.TYPE_069_ANCHOR;

   public static final int ALIGN_0_UNDEFINED                = 0;

   public static final int ALIGN_1_TOP                      = 1;

   public static final int ALIGN_2_BOTTOM                   = 2;

   public static final int ALIGN_3_LEFT                     = 3;

   public static final int ALIGN_4_RIGHT                    = 4;

   /**
    * 
    */
   public static final int ALIGN_5_FILL                     = 5;

   public static final int ALIGN_6_CENTER                   = 6;

   public static final int ALIGN_BITS_0_CENTER              = 0;

   public static final int ALIGN_BITS_1_LEFT                = 1;

   public static final int ALIGN_BITS_1_TOP                 = 1;

   public static final int ALIGN_BITS_2_BOT                 = 2;

   public static final int ALIGN_BITS_2_RIGHT               = 2;

   /**
    * Default anchors
    */
   public static final int ANCHOR                           = ITechGraphics.TOP | ITechGraphics.LEFT;

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
    * Test definition with {@link ITechAnchor#ANCHOR_FLAG_7_DEF_HORIZ_ALIGN}
    * 
    * <li>
    * 
    */
   public static final int ANCHOR_OFFSET_02_HORIZ_ALIGN1    = A_OBJECT_BASIC_SIZE + 1;

   /**
    * Vertical alignment byte. <br>
    * Test definition with {@link ITechAnchor#ANCHOR_FLAG_8_DEF_VERT_ALIGN}
    * <br>
    * Simple Anchor or Complex
    */
   public static final int ANCHOR_OFFSET_03_VERTICAL_ALIGN1 = A_OBJECT_BASIC_SIZE + 2;

}
