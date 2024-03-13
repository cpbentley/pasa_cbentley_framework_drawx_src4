/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.tech;

import pasa.cbentley.core.src4.interfaces.ITech;
import pasa.cbentley.framework.coredraw.src4.interfaces.ITechGraphics;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOBox;
import pasa.cbentley.layouter.src4.tech.IBOPozer;
import pasa.cbentley.layouter.src4.tech.IBOSizer;

/**
 * A {@link ITechAnchor} defines how to position some content relative to a Box.
 * 
 * <p>
 * It is a simple ancestor of the pozer.
 * See
 * <li> {@link IBOPozer}
 * <li> {@link IBOSizer}
 * <li> {@link IBOBox}
 * </p>
 * 
 * @author Charles Bentley
 *
 */
public interface ITechAnchor extends ITech {

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
