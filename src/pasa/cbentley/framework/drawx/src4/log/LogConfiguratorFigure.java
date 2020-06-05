/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.log;

import pasa.cbentley.core.src4.logging.IDLogConfig;
import pasa.cbentley.core.src4.logging.ILogConfigurator;
import pasa.cbentley.framework.drawx.src4.factories.FigureOperator;

public class LogConfiguratorFigure implements ILogConfigurator {

   public LogConfiguratorFigure() {
   }

   public void apply(IDLogConfig log) {
      
      
      log.setLevelGlobal(LVL_03_FINEST);
      
      log.setFlagPrint(MASTER_FLAG_03_ONLY_POSITIVES, true);
      log.setFlagPrint(MASTER_FLAG_05_IGNORE_FLAGS, true);
      
      log.setClassPositives(FigureOperator.class, true);

   }



}
