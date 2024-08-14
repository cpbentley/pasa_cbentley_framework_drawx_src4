package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.ctx.IBOTypesDrawX;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.IBOArtifact;
import pasa.cbentley.framework.drawx.src4.factories.interfaces.ITechArtifact;

public class ArtifactFactory extends AbstractDrwFactory implements IBOArtifact {

   public ArtifactFactory(DrwCtx drc) {
      super(drc);
   }

   public ByteObject getArtifact(int type, int w, int h, int spac, boolean rw, boolean rh, boolean rs) {
      return getArtifact(type, w, h, spac, ITechArtifact.ART_FIG_MAX, rw, rh, rs, true, 0);
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
   public ByteObject getArtifact(int type, int w, int h, int spac, int fig, boolean rw, boolean rh, boolean rs, boolean rf, long seed) {
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrawX.TYPE_DRWX_02_FIG_ARTIFACT, ARTIFACT_BASIC_SIZE);
      p.set1(ARTIFACT_OFFSET_02_W1, w);
      p.set1(ARTIFACT_OFFSET_03_H1, h);
      p.set1(ARTIFACT_OFFSET_04_SPACING_CAP1, spac);
      p.set1(ARTIFACT_OFFSET_05_TYPE1, type);
      p.set1(ARTIFACT_OFFSET_06_FIG1, fig);
      p.set4(ARTIFACT_OFFSET_07_SEED4, (int) seed);
      p.setFlag(ARTIFACT_OFFSET_01_FLAG, ARTIFACT_FLAG_1_RANDOM_W, rw);
      p.setFlag(ARTIFACT_OFFSET_01_FLAG, ARTIFACT_FLAG_2_RANDOM_H, rh);
      p.setFlag(ARTIFACT_OFFSET_01_FLAG, ARTIFACT_FLAG_3_RANDOM_SPACING, rs);
      p.setFlag(ARTIFACT_OFFSET_01_FLAG, ARTIFACT_FLAG_4_RANDOM_FIG, rf);
      return p;
   }

   public ByteObject mergeArtifact(ByteObject root, ByteObject merge) {
      if (root == null) {
         return merge;
      }
      if (merge == null) {
         return root;
      }
      if (merge.getType() != IBOTypesDrawX.TYPE_DRWX_02_FIG_ARTIFACT) {
         throw new IllegalArgumentException();
      }
      return merge;
   }

   //#mdebug
   public void toStringArtifact(ByteObject bo, Dctx sb) {
      sb.append("#Artifact");
      sb.append(" w=" + bo.get1(ARTIFACT_OFFSET_02_W1));
      sb.append(" h=" + bo.get1(ARTIFACT_OFFSET_03_H1));
      sb.append(" spacing=" + bo.get1(ARTIFACT_OFFSET_04_SPACING_CAP1));
      sb.append(" RndW=" + bo.hasFlag(ARTIFACT_OFFSET_01_FLAG, ARTIFACT_FLAG_1_RANDOM_W));
      sb.append(" RndH=" + bo.hasFlag(ARTIFACT_OFFSET_01_FLAG, ARTIFACT_FLAG_2_RANDOM_H));
      sb.append(" RndS=" + bo.hasFlag(ARTIFACT_OFFSET_01_FLAG, ARTIFACT_FLAG_3_RANDOM_SPACING));
   }
   //#enddebug
}
