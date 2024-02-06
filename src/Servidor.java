

import java.awt.EventQueue;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class Servidor {
	
	private ReentrantLock lock = new ReentrantLock();
	private int entradas1 = 10;
	private int entradas2 = 10;
	private int entradas3 = 10;
	
	private JLabel lblEntradas1;
	private JLabel lblEntradas2;
	private JLabel lblEntradas3;

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Servidor window = new Servidor();
					window.frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Servidor() {
		initialize();
		new Thread(()->{
			startServer();
		}).start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		lblEntradas1 = new JLabel("Entradas Normales: " + entradas1);
		lblEntradas1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblEntradas1.setBounds(33, 26, 183, 13);
		frame.getContentPane().add(lblEntradas1);
		
		lblEntradas2 = new JLabel("Entradas Medias: " + entradas2);
		lblEntradas2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblEntradas2.setBounds(33, 62, 183, 13);
		frame.getContentPane().add(lblEntradas2);
		
		lblEntradas3 = new JLabel("Entradas VIP: " + entradas3);
		lblEntradas3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblEntradas3.setBounds(33, 102, 162, 13);
		frame.getContentPane().add(lblEntradas3);
	}
	private void startServer() {
		String entradaCliente;
		try(ServerSocket server = new ServerSocket(1234)){
			while(true) {
				Socket socket = server.accept();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				OutputStream entradaUsuario = socket.getOutputStream();
				entradaCliente = bufferedReader.readLine();
				
				if(entradaCliente.equals("consultar")) {
					entradaUsuario.write((entradas1+","+entradas2+","+entradas3+"\n").getBytes());
					entradaUsuario.flush();
					entradaUsuario.close();
				}else if (entradaCliente.equals("comprar")) {
					int [] entradasCompradas = {
							Integer.parseInt(bufferedReader.readLine()),
							Integer.parseInt(bufferedReader.readLine()),
							Integer.parseInt(bufferedReader.readLine())
					};
					actualizarCantidadEntradas(entradasCompradas,"-");
				}else if (entradaCliente.equals("devolver")) {
					int [] entradasCompradas = {
							Integer.parseInt(bufferedReader.readLine()),
							Integer.parseInt(bufferedReader.readLine()),
							Integer.parseInt(bufferedReader.readLine())
					};
					actualizarCantidadEntradas(entradasCompradas,"+");
				}
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void actualizarCantidadEntradas(int[] entradasCompradas, String causa) {
		lock.lock();
		if (causa.equals("-")) {			
			entradas1 -= entradasCompradas[0];
			entradas2 -= entradasCompradas[1];
			entradas3 -= entradasCompradas[2];
		}else if (causa.equals("+")) {
			entradas1 += entradasCompradas[0];
			entradas2 += entradasCompradas[1];
			entradas3 += entradasCompradas[2];
		}
		lock.unlock();
		updateTickets();
	}
	private void updateTickets() {
		new Thread(()->{
			SwingUtilities.invokeLater(() -> {
				lblEntradas1.setText("Entradas Normales: " + entradas1);
				lblEntradas2.setText("Entradas Medias: " + entradas2);
				lblEntradas3.setText("Entradas VIP: " + entradas3);
			});
		}).start();
	}
}
