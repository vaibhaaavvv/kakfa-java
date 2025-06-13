import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

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
      
      ByteBuffer responseBody = ByteBuffer.allocate(1024);
      
      responseBody.putShort((short) 0);
      
      responseBody.putInt(3);
      
      responseBody.putShort((short) 18);
      responseBody.putShort((short) 0);
      responseBody.putShort((short) 4);
      
      responseBody.putShort((short) 0);
      responseBody.putShort((short) 0);
      responseBody.putShort((short) 9);
      
      responseBody.putShort((short) 1);
      responseBody.putShort((short) 0);
      responseBody.putShort((short) 12);
      
      responseBody.putInt(0);
      

      byte[] bodyBytes = new byte[responseBody.position()];
      responseBody.rewind();
      responseBody.get(bodyBytes);
      
      int responseLength = 4 + bodyBytes.length; 
      
      // Send response header
      os.write(intToBytes(responseLength));  
      os.write(correlId);                    
      
      // Send response body
      os.write(bodyBytes);
      
      os.flush();
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

  private static byte[] intToBytes(int value) {
    return new byte[] {
      (byte) (value >> 24),
      (byte) (value >> 16),
      (byte) (value >> 8),
      (byte) value
    };
  }
}
}
