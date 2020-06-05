/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.param;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.framework.drawx.src4.tech.ITechGradient;

public class Gradient {

   public static void setGradientFct(ByteObject grad, ByteObject fct) {
      grad.setFlag(ITechGradient.GRADIENT_OFFSET_01_FLAG, ITechGradient.GRADIENT_FLAG_2_EXTERNAL_FUNCTION, true);
      grad.addByteObject(fct);
   }

   public static void addArtifact(ByteObject grad, ByteObject artifac) {
      if (grad == null || artifac == null)
         return;
      grad.addSub(artifac);
      grad.setFlag(ITechGradient.GRADIENT_OFFSET_01_FLAG, ITechGradient.GRADIENT_FLAG_7_ARTIFACTS, true);
   }


}
