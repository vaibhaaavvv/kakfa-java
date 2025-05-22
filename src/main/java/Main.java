import java.io.BufferedInputStream;
import java.io.IOException;
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

      // Get Byte input stream aka request
      BufferedInputStream in = new BufferedInputStream(clientSocket.getInputStream());
      // Read first four bytes i.e. message size
      byte[] messageSizeBytes = in.readNBytes(4);
       // Read next two bytes i.e. api key
      byte[] apiKey = in.readNBytes(2);
      // fetch correlation id
      int correlationId = ByteBuffer.wrap(in.readNBytes(4)).getInt();
      // write a message size to response
      clientSocket.getOutputStream().write(messageSizeBytes);
      // write correlation id to response
      var res = ByteBuffer.allocate(4).putInt(correlationId).array();
      int apiVersion = ByteBuffer.wrap(in.readNBytes(2)).getInt();
      clientSocket.getOutputStream().write(res);    
      if (apiVersion > 5 || apiVersion < 0){
        // write error code to response
        clientSocket.getOutputStream().write(ByteBuffer.allocate(2).putInt(35).array());
      }
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
}
