package pasa.cbentley.framework.drawx.src4.factories;

import pasa.cbentley.byteobjects.src4.core.ByteObject;
import pasa.cbentley.byteobjects.src4.ctx.IBOTypesDrw;
import pasa.cbentley.core.src4.logging.Dctx;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.tech.ITechArtifact;

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
      ByteObject p = getBOFactory().createByteObject(IBOTypesDrw.TYPE_052_ARTIFACT, ITechArtifact.ARTIFACT_BASIC_SIZE);
      p.setValue(ITechArtifact.ARTIFACT_OFFSET_2W1, w, 1);
      p.setValue(ITechArtifact.ARTIFACT_OFFSET_3H1, h, 1);
      p.setValue(ITechArtifact.ARTIFACT_OFFSET_4SPACING_CAP1, spac, 1);
      p.setValue(ITechArtifact.ARTIFACT_OFFSET_5TYPE1, type, 1);
      p.setFlag(ITechArtifact.ARTIFACT_OFFSET_1FLAG, ITechArtifact.ARTIFACT_FLAG_1RANDOM_W, rw);
      p.setFlag(ITechArtifact.ARTIFACT_OFFSET_1FLAG, ITechArtifact.ARTIFACT_FLAG_2RANDOM_H, rh);
      p.setFlag(ITechArtifact.ARTIFACT_OFFSET_1FLAG, ITechArtifact.ARTIFACT_FLAG_3RANDOM_SPACING, rs);
      return p;
   }

   public void toStringArtifact(ByteObject bo, Dctx sb) {
      sb.append("#Artifact");
      sb.append(" w=" + bo.get1(ITechArtifact.ARTIFACT_OFFSET_2W1));
      sb.append(" h=" + bo.get1(ITechArtifact.ARTIFACT_OFFSET_3H1));
      sb.append(" spacing=" + bo.get1(ITechArtifact.ARTIFACT_OFFSET_4SPACING_CAP1));
      sb.append(" RndW=" + bo.hasFlag(ITechArtifact.ARTIFACT_OFFSET_1FLAG, ITechArtifact.ARTIFACT_FLAG_1RANDOM_W));
      sb.append(" RndH=" + bo.hasFlag(ITechArtifact.ARTIFACT_OFFSET_1FLAG, ITechArtifact.ARTIFACT_FLAG_2RANDOM_H));
      sb.append(" RndS=" + bo.hasFlag(ITechArtifact.ARTIFACT_OFFSET_1FLAG, ITechArtifact.ARTIFACT_FLAG_3RANDOM_SPACING));
   }
}
