

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import model.Entrada;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

public class Servidor {
	
	private ReentrantLock lock = new ReentrantLock();
	private final int MAX_NUMBER_OF_TICKETS = 10;
	private int entradas1 = 10;
	private int entradas2 = 10;
	private int entradas3 = 10;
	
	private JLabel lblEntradas1;
	private JLabel lblEntradas2;
	private JLabel lblEntradas3;

	private JFrame root;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Servidor window = new Servidor();
					window.root.setVisible(true);
					
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
		root = new JFrame();
		root.setBounds(100, 100, 450, 300);
		root.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		root.getContentPane().setLayout(null);
		
		lblEntradas1 = new JLabel("Entradas Normales: " + entradas1);
		lblEntradas1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblEntradas1.setBounds(33, 26, 183, 13);
		root.getContentPane().add(lblEntradas1);
		
		lblEntradas2 = new JLabel("Entradas Medias: " + entradas2);
		lblEntradas2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblEntradas2.setBounds(33, 62, 183, 13);
		root.getContentPane().add(lblEntradas2);
		
		lblEntradas3 = new JLabel("Entradas VIP: " + entradas3);
		lblEntradas3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblEntradas3.setBounds(33, 102, 162, 13);
		root.getContentPane().add(lblEntradas3);
		
		JButton btnIngresos = new JButton("Ver Ingresos");
		btnIngresos.setBounds(157, 190, 129, 21);
		root.getContentPane().add(btnIngresos);
		
		btnIngresos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!hayEntradasCompradas()) {
					
				}else {
					ArrayList<Entrada> cantidadEntradas = generarEntradasCompradas();
					Map<String, Object> parametros = new HashMap<String, Object>();
					parametros.forEach((key, value)->{
						System.out.println(key + " " + value);
					});
					parametros.put("TotalRecaudado", String.valueOf(getAllProfit()));
					DataSource datasource = new DataSource(cantidadEntradas);
					
					try {
						JasperReport jasperReport = JasperCompileManager.compileReport("src/jasperfiles/BuenosDineros.jrxml");
						JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, datasource);
						
						JasperViewer.viewReport(jasperPrint);
					} catch (JRException e1) {
						e1.printStackTrace();
					}
				}

			}
		});
		
	}
	protected double getAllProfit() {
		double totalRecaudado = 0;
		if(entradas1!=10) {
			totalRecaudado += (MAX_NUMBER_OF_TICKETS - entradas1)*10;
		}
		
		if(entradas2!=10) {
			totalRecaudado += (MAX_NUMBER_OF_TICKETS - entradas2)*20;
		}
		
		if(entradas3!=10) {
			totalRecaudado += (MAX_NUMBER_OF_TICKETS - entradas3)*30;
		}
		return totalRecaudado;
	}

	private boolean hayEntradasCompradas() {
		boolean hayEntradasCompradas = false;
		
		if(entradas1!= MAX_NUMBER_OF_TICKETS || entradas2!= MAX_NUMBER_OF_TICKETS || entradas3!= MAX_NUMBER_OF_TICKETS) {
			hayEntradasCompradas = true;
		}
		
		return hayEntradasCompradas;
	}

	private ArrayList<Entrada> generarEntradasCompradas() {
		ArrayList<Entrada> cantidadEntradas = new ArrayList<>();
		if(entradas1!=10) {
			cantidadEntradas.add(new Entrada("Entrada Barata", String.valueOf(MAX_NUMBER_OF_TICKETS - entradas1)));
		}
		
		if(entradas2!=10) {
			cantidadEntradas.add(new Entrada("Entrada Media", String.valueOf(MAX_NUMBER_OF_TICKETS - entradas2)));
		}
		
		if(entradas3!=10) {
			cantidadEntradas.add(new Entrada("Entrada Vip", String.valueOf(MAX_NUMBER_OF_TICKETS - entradas3)));
		}
		
		return cantidadEntradas;
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
