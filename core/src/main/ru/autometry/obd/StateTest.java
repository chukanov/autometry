package ru.autometry.obd;

import ru.autometry.obd.state.OBDState;
import ru.autometry.obd.state.dump.Base64Serializer;
import ru.autometry.obd.state.dump.ByteArraySerializer;
import ru.autometry.obd.state.dump.StateSerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

/**
 * Created by jeck on 08/09/16.
 */
public class StateTest {
  public static void main(String[] args) throws Exception {
    String stateString =  "AAAAAAAESx2eQDkp1DfjPMpAAoiDEIhviwAAAVcH7oIJAAAAAAASa+AAAAAAADX4oAAAAAAdAPoAAQAAmEQHjwKn";
    String stateString2 = "AAAAAAADwR32QDgnJDe99GVAAIiLtGjVEgAAAVcH5Xr6AAAAAAAMGC4AAAAAACzxngAAAAAdAPoAAQAAmEQHj3fX";

    System.out.println("base64: "+stateString.getBytes().length);
    StateSerializer<String> serializer = new Base64Serializer();
    OBDState state = serializer.load(stateString);
    OBDState state1 = serializer.load(stateString2);


    byte[] statebytes = new ByteArraySerializer().serialize(state);
    byte[] statebytes1 = new ByteArraySerializer().serialize(state1);

    System.out.println("original: "+ statebytes.length);

    byte[] input = new byte[statebytes.length*2];
    System.arraycopy(statebytes, 0, input, 0, statebytes.length);
    System.arraycopy(statebytes1, 0, input, statebytes.length, statebytes.length);

    // Compressor with highest level of compression
    Deflater compressor = new Deflater();
    compressor.setLevel(Deflater.BEST_COMPRESSION);

    // Give the compressor the data to compress
    compressor.setInput(input);
    compressor.finish();

    // Create an expandable byte array to hold the compressed data.
    // It is not necessary that the compressed data will be smaller than
    // the uncompressed data.
    ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);

    // Compress the data
    byte[] buf = new byte[1024];
    while (!compressor.finished()) {
      int count = compressor.deflate(buf);
      bos.write(buf, 0, count);
    }
    try {
      bos.close();
    } catch (IOException e) {
    }

    // Get the compressed data
    byte[] compressedData = bos.toByteArray();
    System.out.println("compressed:"+compressedData.length);
    String encoded = org.apache.commons.codec.binary.Base64.encodeBase64String(compressedData);
    System.out.print(""+encoded.getBytes().length+" --- "+encoded);
  }
}
