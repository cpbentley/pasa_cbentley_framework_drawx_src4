/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */
package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesDrw;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.ToStringStaticDrawx;
import pasa.cbentley.framework.drawx.src4.tech.IBOScaler;
import pasa.cbentley.framework.drawx.src4.tech.ITechMosaic;
import pasa.cbentley.framework.drawx.src4.tech.ITechPass;
import pasa.cbentley.framework.drawx.src4.tech.ITechScaler;
import pasa.cbentley.framework.drawx.src4.tech.ITechSkewer;

public class ScalerFactory extends AbstractDrwFactory {

   public ScalerFactory(DrwCtx drc) {
      super(drc);
   }

   /**
    * Create a scaler with its rgb filter to be used after the scaling.
    * @param type
    * @param postRGBFilter
    * @return
    */
   public ByteObject getScaler(int type, ByteObject postRGBFilter) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_055_SCALE, IBOScaler.SCALE_BASIC_SIZE);
      p.setValue(IBOScaler.SCALE_OFFSET_02_FIT_TYPE1, type, 1);
      if (postRGBFilter != null) {
         p.setFlag(ITechPass.PASS_OFFSET_01_FLAG1, ITechPass.PASS_FLAG_2_POST_FILTER, true);
         p.addSub(postRGBFilter);
      }
      return p;
   }
   
   public ByteObject getSkewer(int interpolationID, int edgeType) {
      ByteObject skewer = getBOFactory().createByteObject(IBOTypesDrw.TYPE_054_SKEWER, ITechSkewer.SKEWER_BASIC_SIZE);
      skewer.set1(ITechSkewer.SKEWER_OFFSET_02_EDGE_TYPE1, edgeType);
      skewer.set1(ITechSkewer.SKEWER_OFFSET_03_INTERPOLATION_TYPE1, interpolationID);
      return skewer;
   }

   /**
    * 
    * @param type {@link IBOScaler#SCALE_OFFSET_02_FIT_TYPE1}
    * @param id {@link IBOScaler#SCALE_OFFSET_03_ID1}
    * @return
    */
   public ByteObject getScaler(int type, int id) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_055_SCALE, IBOScaler.SCALE_BASIC_SIZE);
      p.setValue(IBOScaler.SCALE_OFFSET_02_FIT_TYPE1, type, 1);
      p.setValue(IBOScaler.SCALE_OFFSET_03_ID1, id, 1);
      return p;
   }
   
   public ByteObject getMosaic(int type, boolean trans) {
      ByteObject mosaic = getBOFactory().createByteObject(IBOTypesDrw.TYPE_053_MOSAIC, ITechMosaic.PMOSAIC_BASIC_SIZE);
      mosaic.set1(ITechMosaic.PMOSAIC_OFFSET_02_TYPE1, type);
      mosaic.setFlag(ITechMosaic.PMOSAIC_OFFSET_01_FLAG1, ITechMosaic.PMOSAIC_FLAG_1_TRANSFORMATION, trans);
      return mosaic;
   }


   /**
    * Creates a scaler template.
    * <br>
    * <br>
    * 
    * @param id {@link IBOScaler#SCALE_OFFSET_03_ID1} {@link ITechScaler#SCALER_ID_0_LINEAR}
    * @param type {@link IBOScaler#SCALE_OFFSET_02_FIT_TYPE1} => {@link ITechScaler#SCALER_TYPE_1_FIT_BOTH} {@link ITechScaler#SCALER_TYPE_4_FIT_FIRST}
    * @param preRGBFilter
    * @param postRGBFilter
    * @return
    */
   public ByteObject getScaler(int id, int type, ByteObject preRGBFilter, ByteObject postRGBFilter) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_055_SCALE, IBOScaler.SCALE_BASIC_SIZE);
      p.setValue(IBOScaler.SCALE_OFFSET_02_FIT_TYPE1, type, 1);
      p.setValue(IBOScaler.SCALE_OFFSET_03_ID1, id, 1);
      if (preRGBFilter != null) {
         p.setFlag(ITechPass.PASS_OFFSET_01_FLAG1, ITechPass.PASS_FLAG_1_PRE_FILTER, true);
         p.addSub(preRGBFilter);
      }
      if (postRGBFilter != null) {
         p.setFlag(ITechPass.PASS_OFFSET_01_FLAG1, ITechPass.PASS_FLAG_2_POST_FILTER, true);
         p.addSub(postRGBFilter);
      }
      return p;
   }
   
   public void toStringScaler(ByteObject bo, Dctx sb) {
      sb.append("#Scaler ");
      if (bo.hasFlag(ITechPass.PASS_OFFSET_01_FLAG1, ITechPass.PASS_FLAG_1_PRE_FILTER)) {
         sb.append("PreFilter");
      }
      if (bo.hasFlag(ITechPass.PASS_OFFSET_01_FLAG1, ITechPass.PASS_FLAG_2_POST_FILTER)) {
         sb.append("PostFilter");
      }
      int type = bo.get1(IBOScaler.SCALE_OFFSET_02_FIT_TYPE1);
      sb.append(" Type:" + ToStringStaticDrawx.toStringScaleType(type));
      int id = bo.get1(IBOScaler.SCALE_OFFSET_03_ID1);
      sb.append(" ID:" + ToStringStaticDrawx.toStringScaleID(id));
   }

}
