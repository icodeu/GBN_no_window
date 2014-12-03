import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class GBNServer {
	public static void main(String[] args) throws Exception {
		DatagramSocket serverSocket = new DatagramSocket(8888);
		int hasReceive = 0;
		
		while (true){
			byte[] data = new byte[100];
			DatagramPacket receivePacket = new DatagramPacket(data, data.length);
			serverSocket.receive(receivePacket);
			int seq = new String(receivePacket.getData()).charAt(0) - '0';
			if (Math.random()<=0.7){
				System.out.println("服务端发送 ack= " + hasReceive);
				//接收成功，发ack;
				byte[] ackData = new String("ack"+hasReceive).getBytes(); 
				InetAddress clientAddress = receivePacket.getAddress();
				int clientPort = receivePacket.getPort();
				DatagramPacket sendPacket = new DatagramPacket(ackData, ackData.length, clientAddress, clientPort);
				serverSocket.send(sendPacket);
				if (seq == hasReceive + 1)
					hasReceive++;
			}
		}
	}
}
