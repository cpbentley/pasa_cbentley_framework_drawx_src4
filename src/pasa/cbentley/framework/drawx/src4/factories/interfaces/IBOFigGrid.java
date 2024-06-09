package pasa.cbentley.framework.drawx.src4.factories.interfaces;

import pasa.cbentley.layouter.src4.tech.IBOSizer;
import pasa.cbentley.layouter.src4.tech.ITechCodedSizer;

public interface IBOFigGrid extends IBOFigure {

   public static final int FIG_GRID_BASIC_SIZE          = IBOFigure.FIG__BASIC_SIZE + 25;

   public static final int FIG_GRID_FLAG_CACHE_SEP      = 0;

   public static final int FIG_GRID_OFFSET_01_FLAG      = FIG__BASIC_SIZE + 0;

   public static final int FIG_GRID_OFFSET_02_HCOLOR4   = FIG__BASIC_SIZE + 1;

   public static final int FIG_GRID_OFFSET_03_VCOLOR4   = FIG__BASIC_SIZE + 5;

   /**
    * Coded size or {@link IBOSizer} in {@link ITechCodedSizer}
    */
   public static final int FIG_GRID_OFFSET_04_HSIZE4    = FIG__BASIC_SIZE + 9;

   public static final int FIG_GRID_OFFSET_05_VSIZE4    = FIG__BASIC_SIZE + 13;

   /**
    * Also hosts the Cache ID of size values
    */
   public static final int FIG_GRID_OFFSET_06_HSEPSIZE4 = FIG__BASIC_SIZE + 17;

   public static final int FIG_GRID_OFFSET_07_VSEPSIZE4 = FIG__BASIC_SIZE + 21;

}
