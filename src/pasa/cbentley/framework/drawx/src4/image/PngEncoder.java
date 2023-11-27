/*
 * (c) 2018-2020 Charles-Philip Bentley
 * This code is licensed under MIT license (see LICENSE.txt for details)
 */

package pasa.cbentley.framework.drawx.src4.image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import pasa.cbentley.core.src4.logging.IStringable;
import pasa.cbentley.core.src4.memory.IMemory;
import pasa.cbentley.framework.coredraw.src4.interfaces.IImage;
import pasa.cbentley.framework.drawx.src4.ctx.DrwCtx;
import pasa.cbentley.framework.drawx.src4.engine.RgbImage;

/**
 * Move it to its own code context for imaging ?
 * @author Charles Bentley
 *
 */
public class PngEncoder {

   public static final String CHUNK_DATA_LOAD_AS_TEXT = "tEXt";

   public static final String CHUNK_ITXT              = "iTXt";

   private DrwCtx         drc;

   public PngEncoder(DrwCtx drc) {
      this.drc = drc;
   }

   public IImage toImage(int width, int height, byte[] alpha, byte[] red, byte[] green, byte[] blue) {
      try {
         byte[] png = toPNG(width, height, alpha, red, green, blue);
         return drc.getImageFactory().createImage(png, 0, png.length);
      } catch (IOException e) {
         e.printStackTrace();
         return null;
      }
   }

   /**
    * Extra bytes
    * @param img
    * @param data
    * @param extra
    * @return
    */
   public byte[] encodePNG(RgbImage img, String data, byte[] extra) {
      return encodePNG(img.getRgbData(), img.getOffset(), img.getScanLength(), img.getM(), img.getN(), img.getWidth(), img.getHeight(), data, extra);
   }

   /**
    * There is no exception because the whole image array has been created.
    * @param dd
    * @param img
    * @return null if PNG could not be decoded.
    */
   public byte[] encodePNG(RgbImage img) {
      return encodePNG(img, null, null);
   }

   /**
    * First object is {@link IImage}
    * Following objects are extra and string is
    * @param is
    * @return
    */
   public Object[] decode(byte[] data) {
      IImage img = drc.getImageFactory().createImage(data, 0, data.length);
      Object[] rs = new Object[3];
      rs[0] = img;
      try {
         int offset = 0;
         ByteArrayInputStream bis = new ByteArrayInputStream(data);
         DataInputStream dis = new DataInputStream(bis);
         while (offset < data.length) {
            int numBytes = dis.readInt();
            char[] ar = new char[4];
            ar[0] = (char) dis.read();
            ar[1] = (char) dis.read();
            ar[2] = (char) dis.read();
            ar[3] = (char) dis.read();
            if (new String(ar).equals(CHUNK_ITXT)) {
               byte[] ntext = new byte[numBytes];
               String str = new String(ntext, "UTF-8");
               rs[1] = str;
            }
            if (new String(ar).equals(CHUNK_DATA_LOAD_AS_TEXT)) {
               byte[] ntext = new byte[numBytes];
               dis.read(ntext, 0, ntext.length);
               rs[2] = ntext;
            }
            offset += numBytes;
         }
      } catch (Exception e) {

      }
      return rs;
   }

   /**
    * Read
    * @param chunk
    * @param index
    * @param data
    * @return
    */
   public byte[] decodeChunk(String chunk, int index, byte[] data) {
      try {
         int offset = 0;
         int count = 0;
         ByteArrayInputStream bis = new ByteArrayInputStream(data);
         DataInputStream dis = new DataInputStream(bis);
         //read the sig
         byte[] signature = new byte[] { (byte) 137, (byte) 80, (byte) 78, (byte) 71, (byte) 13, (byte) 10, (byte) 26, (byte) 10 };
         byte[] track = new byte[signature.length];
         dis.read(track);

         while (offset < data.length) {
            int numBytes = dis.readInt();
            char[] ar = new char[4];
            ar[0] = (char) dis.read();
            ar[1] = (char) dis.read();
            ar[2] = (char) dis.read();
            ar[3] = (char) dis.read();
            String chu = new String(ar);
            //#debug
            drc.toDLog().pWork("Reading Chunk " + chu, (IStringable) null, PngEncoder.class, "decodeChunk");
            if (chu.equals(chunk)) {
               if (count == index) {
                  byte[] ntext = new byte[numBytes];
                  dis.read(ntext, 0, ntext.length);
                  return ntext;
               } else {
                  count++;
               }
            }
            dis.skipBytes(numBytes);
            int crc = dis.readInt();
            offset += (numBytes + 12);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }

   public byte[] encodePNG(int[] rgb, int offset, int scan, int m, int n, int w, int h, String data, byte[] extra) {
      int len = w * h;
      IMemory mem = drc.getMem();
      byte[] alpha = mem.createByteArray(len);
      byte[] red = mem.createByteArray(len);
      byte[] green = mem.createByteArray(len);
      byte[] blue = mem.createByteArray(len);

      int count = 0;
      for (int i = 0; i < h; i++) {
         int index = offset + m + (scan * (n + i));
         for (int j = 0; j < w; j++) {
            int p = rgb[index];
            alpha[count] = (byte) ((p >> 24) & 0xff);
            red[count] = (byte) ((p >> 16) & 0xff);
            green[count] = (byte) ((p >> 8) & 0xff);
            blue[count] = (byte) ((p >> 0) & 0xff);
            index++;
            count++;
         }
      }
      return toPNG(w, h, alpha, red, green, blue, data, extra);
   }

   public byte[] encodePNG(int[] rgb, int w, int h) throws IOException {
      return encodePNG(rgb, 0, w, 0, 0, w, h, null, null);
   }

   /**
    * Method is not memory safe
    * @param image
    * @return
    * @throws IOException
    */
   public byte[] encodePNG(IImage image) throws IOException {
      int w = image.getWidth();
      int h = image.getHeight();
      int[] rgb = new int[h * w];
      image.getRGB(rgb, 0, w, 0, 0, w, h);
      return encodePNG(rgb, w, h);
   }

   public byte[] encodePNG(IImage image, String txt, byte[] data) throws IOException {
      int w = image.getWidth();
      int h = image.getHeight();
      int[] rgb = new int[h * w];
      image.getRGB(rgb, 0, w, 0, 0, w, h);
      return encodePNG(rgb, w, h);
   }

   public static byte[] toPNG(int width, int height, byte[] alpha, byte[] red, byte[] green, byte[] blue) throws IOException {
      byte[] signature = new byte[] { (byte) 137, (byte) 80, (byte) 78, (byte) 71, (byte) 13, (byte) 10, (byte) 26, (byte) 10 };
      byte[] header = createHeaderChunk(width, height);
      byte[] data = createDataChunk(width, height, alpha, red, green, blue);
      byte[] trailer = createTrailerChunk();

      ByteArrayOutputStream png = new ByteArrayOutputStream(signature.length + header.length + data.length + trailer.length);
      png.write(signature);
      png.write(header);
      png.write(data);
      png.write(trailer);
      return png.toByteArray();
   }

   public static byte[] toPNG(int width, int height, byte[] alpha, byte[] red, byte[] green, byte[] blue, String txt, byte[] extra) {
      ByteArrayOutputStream png = null;
      try {
         byte[] signature = new byte[] { (byte) 137, (byte) 80, (byte) 78, (byte) 71, (byte) 13, (byte) 10, (byte) 26, (byte) 10 };
         byte[] header = createHeaderChunk(width, height);
         byte[] data = createDataChunk(width, height, alpha, red, green, blue);
         byte[] trailer = createTrailerChunk();

         png = new ByteArrayOutputStream(signature.length + header.length + data.length + trailer.length);
         png.write(signature);
         png.write(header);
         if (txt != null) {
            byte[] text = createTextUTFChunk(txt.getBytes("UTF-8"));
            png.write(text);
         }
         if (extra != null) {
            byte[] extrab = createTextChunk(extra);
            png.write(extrab);
         }
         png.write(data);
         png.write(trailer);
      } catch (IOException e) {
         //will never happen with a bytearra
      }
      return png.toByteArray();
   }

   public static byte[] createHeaderChunk(int width, int height) throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream(13);
      DataOutputStream chunk = new DataOutputStream(baos);
      chunk.writeInt(width);
      chunk.writeInt(height);
      chunk.writeByte(8); // Bitdepth
      chunk.writeByte(6); // Colortype ARGB
      chunk.writeByte(0); // Compression
      chunk.writeByte(0); // Filter
      chunk.writeByte(0); // Interlace    
      return toChunk("IHDR", baos.toByteArray());
   }

   public static byte[] createDataChunk(int width, int height, byte[] alpha, byte[] red, byte[] green, byte[] blue) throws IOException {
      int source = 0;
      int dest = 0;
      //there is a filter byte for each horizontal line
      byte[] raw = new byte[4 * (width * height) + height];
      for (int y = 0; y < height; y++) {
         raw[dest++] = 0; // No filter
         for (int x = 0; x < width; x++) {
            raw[dest++] = red[source];
            raw[dest++] = green[source];
            raw[dest++] = blue[source];
            raw[dest++] = alpha[source++];
         }
      }
      return toChunk("IDAT", toZLIB(raw));
   }

   public static byte[] createTextChunk(byte[] raw) throws IOException {
      return toChunk(CHUNK_DATA_LOAD_AS_TEXT, raw);
   }

   public static byte[] createTextUTFChunk(byte[] raw) throws IOException {
      return toChunk(CHUNK_ITXT, raw);
   }

   public static byte[] createTrailerChunk() throws IOException {
      return toChunk("IEND", new byte[] {});
   }

   public static byte[] toChunk(String id, byte[] raw) throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream(raw.length + 12);
      DataOutputStream chunk = new DataOutputStream(baos);

      //does not inclue the 12 header bytes
      chunk.writeInt(raw.length);

      byte[] bid = new byte[4];
      for (int i = 0; i < 4; i++) {
         bid[i] = (byte) id.charAt(i);
      }

      chunk.write(bid);

      chunk.write(raw);

      int crc = 0xFFFFFFFF;
      crc = updateCRC(crc, bid);
      crc = updateCRC(crc, raw);
      chunk.writeInt(~crc);

      return baos.toByteArray();
   }

   static int[] crcTable = null;

   public static void createCRCTable() {
      crcTable = new int[256];

      for (int i = 0; i < 256; i++) {
         int c = i;
         for (int k = 0; k < 8; k++) {
            c = ((c & 1) > 0) ? 0xedb88320 ^ (c >>> 1) : c >>> 1;
         }
         crcTable[i] = c;
      }
   }

   public static int updateCRC(int crc, byte[] raw) {
      if (crcTable == null) {
         createCRCTable();
      }

      for (int i = 0; i < raw.length; i++) {
         crc = crcTable[(crc ^ raw[i]) & 0xFF] ^ (crc >>> 8);
      }

      return crc;
   }

   /* This method is called to encode the image data as a zlib
      block as required by the PNG specification. This file comes
      with a minimal ZLIB encoder which uses uncompressed deflate
      blocks (fast, short, easy, but no compression). If you want
      compression, call another encoder (such as JZLib?) here. */
   public static byte[] toZLIB(byte[] raw) throws IOException {
      return ZLIB.toZLIB(raw);
   }
}

class ZLIB {
   static final int BLOCK_SIZE = 32000;

   public static byte[] toZLIB(byte[] raw) throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream(raw.length + 6 + (raw.length / BLOCK_SIZE) * 5);
      DataOutputStream zlib = new DataOutputStream(baos);

      byte tmp = (byte) 8;
      zlib.writeByte(tmp); // CM = 8, CMINFO = 0
      zlib.writeByte((31 - ((tmp << 8) % 31)) % 31); // FCHECK (FDICT/FLEVEL=0)

      int pos = 0;
      while (raw.length - pos > BLOCK_SIZE) {
         writeUncompressedDeflateBlock(zlib, false, raw, pos, (char) BLOCK_SIZE);
         pos += BLOCK_SIZE;
      }

      writeUncompressedDeflateBlock(zlib, true, raw, pos, (char) (raw.length - pos));

      // zlib check sum of uncompressed data
      zlib.writeInt(calcADLER32(raw));

      return baos.toByteArray();
   }

   private static void writeUncompressedDeflateBlock(DataOutputStream zlib, boolean last, byte[] raw, int off, char len) throws IOException {
      zlib.writeByte((byte) (last ? 1 : 0)); // Final flag, Compression type 0
      zlib.writeByte((byte) (len & 0xFF)); // Length LSB
      zlib.writeByte((byte) ((len & 0xFF00) >> 8)); // Length MSB
      zlib.writeByte((byte) (~len & 0xFF)); // Length 1st complement LSB
      zlib.writeByte((byte) ((~len & 0xFF00) >> 8)); // Length 1st complement MSB 
      zlib.write(raw, off, len); // Data    
   }

   private static int calcADLER32(byte[] raw) {
      int s1 = 1;
      int s2 = 0;
      for (int i = 0; i < raw.length; i++) {
         int abs = raw[i] >= 0 ? raw[i] : (raw[i] + 256);
         s1 = (s1 + abs) % 65521;
         s2 = (s2 + s1) % 65521;
      }
      return (s2 << 16) + s1;
   }
}
