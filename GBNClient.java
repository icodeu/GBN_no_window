import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.Timer;


public class GBNClient {
	public static void main(String[] args) throws Exception {
		InetAddress serverAddress = InetAddress.getByName("localhost");
		DatagramSocket clientSocket = new DatagramSocket(9999);
		byte[] sendData;
		int end_ack;
		Timer[] timers = new Timer[10];
		System.out.println("丢包概率为0.3,在服务器端设定");
		System.out.println("重传定时器为3秒,在客户端设定,逾期则GoBack重新发送");
		System.out.println("客户端即将发送10个数据包");
		System.out.println("客户端发送数据 0--9");
		for (int i=0;i<10;i++){
			sendData = (i + "seq").getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 8888);
			clientSocket.send(sendPacket);
			timers[i] = new Timer(3000, new DelayActionListener(clientSocket, i, timers));
			timers[i].start();
		}
		
		while (true){
			byte[] recvData = new byte[100];
			DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
			clientSocket.receive(recvPacket);
			int ack_seq = new String(recvPacket.getData()).charAt(3) -'0';
			System.out.println("客户端接收 ack=" + ack_seq);
			timers[ack_seq].stop();
			if (ack_seq == 9){
				System.out.println("全部数据已被发送成功！");
				return;
			}
		}
	}
}

class DelayActionListener implements ActionListener{

	DatagramSocket clientSocket;
	int end_ack;
	Timer[] timers;
	public DelayActionListener(DatagramSocket clientSocket, int end_ack, Timer[] timers){
		this.clientSocket = clientSocket;
		this.end_ack = end_ack;
		this.timers = timers;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("客户端重传数据 " + end_ack +"--9");
		for (int i=end_ack;i<10;i++){
			byte[] sendData;
			InetAddress serverAddress = null;
			try {
				serverAddress = InetAddress.getByName("localhost");
				sendData = (i + "seq").getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 8888);
				clientSocket.send(sendPacket);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			timers[i].stop();
			timers[i].start();
		}
	}
}


