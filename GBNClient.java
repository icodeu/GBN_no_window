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
		System.out.println("��������Ϊ0.3,�ڷ��������趨");
		System.out.println("�ش���ʱ��Ϊ3��,�ڿͻ����趨,������GoBack���·���");
		System.out.println("�ͻ��˼�������10�����ݰ�");
		System.out.println("�ͻ��˷������� 0--9");
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
			System.out.println("�ͻ��˽��� ack=" + ack_seq);
			timers[ack_seq].stop();
			if (ack_seq == 9){
				System.out.println("ȫ�������ѱ����ͳɹ���");
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
		System.out.println("�ͻ����ش����� " + end_ack +"--9");
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


