import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args){
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.err.println("Logs from your program will appear here!");

    
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    int port = 9092;
    try {
      serverSocket = new ServerSocket(port);
      // Since the tester restarts your program quite often, setting SO_REUSEADDR
      // ensures that we don't run into 'Address already in use' errors
      serverSocket.setReuseAddress(true);
      // Wait for connection from client.
      clientSocket = serverSocket.accept();

      InputStream is = clientSocket.getInputStream();
      byte[] length = is.readNBytes(4);
      byte[] api_key = is.readNBytes(2);
      byte[] api_ver = is.readNBytes(2);
      byte[] correlId = is.readNBytes(4);
      OutputStream os = clientSocket.getOutputStream();
      ByteArrayOutputStream responseBodyBuffer = new ByteArrayOutputStream();
      DataOutputStream responseBodyStream = new DataOutputStream(responseBodyBuffer);
      responseBodyStream.write(correlId);
      responseBodyStream.writeShort(0);
      responseBodyStream.writeInt(1);

      responseBodyStream.writeShort(18);  // ApiKey
      responseBodyStream.writeShort(0);   // MinVersion
      responseBodyStream.writeShort(4);

      responseBodyStream.writeInt(0);
      writeUnsignedVarInt(0, responseBodyStream);
      responseBodyStream.flush();
      byte[] responseBody = responseBodyBuffer.toByteArray();

      int responseLength = responseBody.length;

      DataOutputStream dos = new DataOutputStream(os);

      dos.writeInt(responseLength);

      dos.write(responseBody);
      
      os.write(length);
      os.write(correlId);
      os.write(new byte[] {0, 35});
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    } finally {
      try {
        if (clientSocket != null) {
          clientSocket.close();
        }
      } catch (IOException e) {
        System.out.println("IOException: " + e.getMessage());
      }
    }
  }

  public static void writeUnsignedVarInt(int value, DataOutputStream dos) throws IOException {
    while ((value & 0xFFFFFF80) != 0L) {
      dos.writeByte((value & 0x7F) | 0x80);
      value >>>= 7;
    }
    dos.writeByte(value & 0x7F);
  }
}
