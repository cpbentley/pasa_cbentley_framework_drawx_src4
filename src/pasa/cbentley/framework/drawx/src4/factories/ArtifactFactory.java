package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesDrw;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOArtifact;

public class ArtifactFactory extends AbstractDrwFactory {

   public ArtifactFactory(DrwCtx drc) {
      super(drc);
      // TODO Auto-generated constructor stub
   }

   /**
    * 
    * @param type
    * @param w
    * @param h
    * @param spac
    * @param rw
    * @param rh
    * @param rs
    * @return
    */
   public ByteObject getArtifact(int type, int w, int h, int spac, boolean rw, boolean rh, boolean rs) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_052_ARTIFACT, IBOArtifact.ARTIFACT_BASIC_SIZE);
      p.setValue(IBOArtifact.ARTIFACT_OFFSET_02_W1, w, 1);
      p.setValue(IBOArtifact.ARTIFACT_OFFSET_03_H1, h, 1);
      p.setValue(IBOArtifact.ARTIFACT_OFFSET_04_SPACING_CAP1, spac, 1);
      p.setValue(IBOArtifact.ARTIFACT_OFFSET_05_TYPE1, type, 1);
      p.setFlag(IBOArtifact.ARTIFACT_OFFSET_01_FLAG, IBOArtifact.ARTIFACT_FLAG_1_RANDOM_W, rw);
      p.setFlag(IBOArtifact.ARTIFACT_OFFSET_01_FLAG, IBOArtifact.ARTIFACT_FLAG_2_RANDOM_H, rh);
      p.setFlag(IBOArtifact.ARTIFACT_OFFSET_01_FLAG, IBOArtifact.ARTIFACT_FLAG_3_RANDOM_SPACING, rs);
      return p;
   }

   public void toStringArtifact(ByteObject bo, Dctx sb) {
      sb.append("#Artifact");
      sb.append(" w=" + bo.get1(IBOArtifact.ARTIFACT_OFFSET_02_W1));
      sb.append(" h=" + bo.get1(IBOArtifact.ARTIFACT_OFFSET_03_H1));
      sb.append(" spacing=" + bo.get1(IBOArtifact.ARTIFACT_OFFSET_04_SPACING_CAP1));
      sb.append(" RndW=" + bo.hasFlag(IBOArtifact.ARTIFACT_OFFSET_01_FLAG, IBOArtifact.ARTIFACT_FLAG_1_RANDOM_W));
      sb.append(" RndH=" + bo.hasFlag(IBOArtifact.ARTIFACT_OFFSET_01_FLAG, IBOArtifact.ARTIFACT_FLAG_2_RANDOM_H));
      sb.append(" RndS=" + bo.hasFlag(IBOArtifact.ARTIFACT_OFFSET_01_FLAG, IBOArtifact.ARTIFACT_FLAG_3_RANDOM_SPACING));
   }
}
