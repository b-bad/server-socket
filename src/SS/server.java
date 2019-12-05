package SS;
import java.net.*;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.nio.charset.*;
import java.util.FormatFlagsConversionMismatchException;
import java.time.*;

//import org.omg.CORBA.portable.OutputStream;
public class server{
	private static int ServerPort = 5000;//任意一个空端口
	private static Socket socket = null;
	private static ServerSocket serverSocket = null;
	private String pythonFilePath = "F:\\数据备份\\大四上（电脑）\\大创1\\task_xuedi（pytorch测试用）\\demo_RFB_for_Android_seg.py";
	private String imagePath = "F:\\数据备份\\大四上（电脑）\\大创1\\task_xuedi（pytorch测试用）\\test_inter\\test.jpg";
	public boolean flag = false;
	public String getImagePath() {
		return this.imagePath;
	}
	public String getPythonFilePath() {
		return this.pythonFilePath;
	}
	public Communication() throws IOException, InterruptedException{
		getMessage();
		try {
			String[] argStrings = new String[] {"python", this.getPythonFilePath()};
			Process process = Runtime.getRuntime().exec(argStrings);
			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while((line = bufferedReader.readLine()) != null) {
				System.out.println(line);
			}
			flag = true;
			System.out.println("分析结束");
			bufferedReader.close();
			process.waitFor();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		sendResult();
	}
	
	public void getMessage() throws IOException, InterruptedException{
		System.out.println("接收服务器启动中...");
		serverSocket = new ServerSocket(ServerPort);
		System.out.println("接收服务器已启动");
		socket = serverSocket.accept();
		DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
		getInputImage(dataInputStream);
		dataInputStream.close();
		socket.close();
		serverSocket.close();
	}
	
	public void getInputImage(DataInputStream input) throws IOException, InterruptedException{
		long len = input.readLong();
		System.out.println("length of file:" + len +"bytes");
		byte[] bytes = new byte[(int)len];
		input.readFully(bytes);
		File file = new File(this.getImagePath());
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		fileOutputStream.write(bytes);
		System.out.println("图片接收成功");
		fileOutputStream.close();
	}
	
	
	private void sendResult() throws IOException, InterruptedException{
		System.out.println("发送服务器启动中...");
		serverSocket = new ServerSocket(ServerPort);
		System.out.println("发送服务器已启动");
		Socket socket = serverSocket.accept();		
		DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
		getResult(dataOutputStream);
		dataOutputStream.close();
		socket.close();
		serverSocket.close();
	}
	
	public void getResult(DataOutputStream output) throws IOException {
		File resultFile = new File("F:\\数据备份\\大四上（电脑）\\大创1\\task_xuedi（pytorch测试用）\\android_result.txt");
		FileInputStream fileInputStream = new FileInputStream(resultFile);
		int size = fileInputStream.available();
		byte[] bytes = new byte[size];
		fileInputStream.read(bytes);
		fileInputStream.close();
		String message = new String(bytes, "utf-8");
		System.out.println(message);
		output.writeInt(message.getBytes().length);
		output.write(message.getBytes());
		System.out.println("结果已发送");
	}
	
	public static void main(String arg[]) throws IOException, InterruptedException {
		new server();
	}	
}
