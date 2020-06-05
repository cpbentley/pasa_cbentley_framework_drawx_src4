/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.style;

import pasa.cbentley.core.src4.interfaces.ITech;

public interface ITechStyleCache extends ITech {

   public static final int SC_FLAG_01_WIDTH_ALL      = 1 << 0;

   public static final int SC_FLAG_02_HEIGHT_ALL     = 1 << 1;

   public static final int SC_FLAG_03_WIDTH_LEFT     = 1 << 2;

   public static final int SC_FLAG_04_WIDTH_RITE     = 1 << 3;

   public static final int SC_FLAG_05_HEIGHT_TOP     = 1 << 4;

   public static final int SC_FLAG_06_HEIGHT_BOT     = 1 << 5;

   public static final int SC_FLAG_10_PADDING_TOP    = 1 << 9;

   public static final int SC_FLAG_11_PADDING_BOT    = 1 << 10;

   public static final int SC_FLAG_12_PADDING_LEFT   = 1 << 11;

   public static final int SC_FLAG_13_PADDING_RITE   = 1 << 12;

   public static final int SC_FLAG_14_BORDER_TOP     = 1 << 13;

   public static final int SC_FLAG_15_BORDER_BOT     = 1 << 14;

   public static final int SC_FLAG_16_BORDER_LEFT    = 1 << 15;

   public static final int SC_FLAG_17_BORDER_RITE    = 1 << 16;

   public static final int SC_FLAG_18_MARGIN_TOP     = 1 << 17;

   public static final int SC_FLAG_19_MARGIN_BOT     = 1 << 18;

   public static final int SC_FLAG_20_MARGIN_LEFT    = 1 << 19;

   public static final int SC_FLAG_21_MARGIN_RITE    = 1 << 20;

   public static final int SC_FLAG_22_PADDING_WIDTH  = 1 << 21;

   public static final int SC_FLAG_23_PADDING_HEIGHT = 1 << 22;

   public static final int SC_FLAG_24_BORDER_WIDTH   = 1 << 23;

   public static final int SC_FLAG_25_BORDER_HEIGHT  = 1 << 24;

   public static final int SC_FLAG_26_MARGIN_WIDTH   = 1 << 25;

   public static final int SC_FLAG_27_MARGIN_HEIGHT  = 1 << 26;

   public static final int SC_FLAG_30_AREAS          = 1 << 29;
}
