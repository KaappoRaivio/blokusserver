import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        ServerSocket server = null;
        try {
            server = new ServerSocket(1212);
            System.out.println("Listening!");
            Socket s = server.accept();
            System.out.println("Got it!!");
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            String objectReceived = in.readObject().toString();
            System.out.println(objectReceived);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
