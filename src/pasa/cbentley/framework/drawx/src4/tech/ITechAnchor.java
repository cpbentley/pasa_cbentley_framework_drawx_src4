/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.tech;

import pasa.cbentley.byteobjects.src4.core.interfaces.IByteObject;
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

}
