
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

public class Cliente {
	
	public static final String PATH_TO_JASPERT_FILE = "C:\\Users\\dcues\\JaspersoftWorkspace\\Reportes\\EntradaConcierto.jrxml";
	
	private JFrame root;
	private JLabel lblEntradas1;
	private JLabel lblEntradas2;
	private JLabel lblEntradas3;
	private JTextField txtEntradas1;
	private JTextField txtEntradas2;
	private JTextField txtEntradas3;
	private JProgressBar progressBar;
	private JButton btnReservar;
	private boolean estaReservando = false;
	private final int MAX_SECONDS_TO_WAIT = 10;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Cliente window = new Cliente();
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
	public Cliente() {
		initialize();
		updateTickets();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		root = new JFrame();
		root.setBounds(100, 100, 450, 300);
		root.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		root.getContentPane().setLayout(null);

		lblEntradas1 = new JLabel("Entradas Normales:");
		lblEntradas1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblEntradas1.setBounds(25, 24, 151, 13);
		root.getContentPane().add(lblEntradas1);

		lblEntradas2 = new JLabel("Entradas Medio:");
		lblEntradas2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblEntradas2.setBounds(25, 47, 151, 13);
		root.getContentPane().add(lblEntradas2);

		lblEntradas3 = new JLabel("Entradas VIP:");
		lblEntradas3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblEntradas3.setBounds(25, 70, 151, 13);
		root.getContentPane().add(lblEntradas3);

		JButton btnComprar = new JButton("Comprar");
		btnComprar.setBounds(158, 186, 85, 21);
		root.getContentPane().add(btnComprar);

		txtEntradas1 = new JTextField();
		txtEntradas1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtEntradas1.setText("0");
		txtEntradas1.setBounds(204, 23, 39, 19);
		root.getContentPane().add(txtEntradas1);
		txtEntradas1.setColumns(10);

		txtEntradas2 = new JTextField();
		txtEntradas2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtEntradas2.setText("0");
		txtEntradas2.setBounds(204, 46, 39, 19);
		root.getContentPane().add(txtEntradas2);
		txtEntradas2.setColumns(10);

		txtEntradas3 = new JTextField();
		txtEntradas3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtEntradas3.setText("0");
		txtEntradas3.setBounds(204, 69, 39, 19);
		root.getContentPane().add(txtEntradas3);
		txtEntradas3.setColumns(10);

		JButton btnActualizarInterfaz = new JButton("Actualizar");
		btnActualizarInterfaz.setBounds(35, 186, 100, 21);
		root.getContentPane().add(btnActualizarInterfaz);

		progressBar = new JProgressBar();
		progressBar.setBounds(158, 129, 146, 11);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setVisible(false);
		root.getContentPane().add(progressBar);

		btnReservar = new JButton("Reservar");
		btnReservar.setBounds(281, 186, 100, 21);
		root.getContentPane().add(btnReservar);

		btnComprar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Integer.parseInt(txtEntradas1.getText())>0 || Integer.parseInt(txtEntradas2.getText())>0 || Integer.parseInt(txtEntradas3.getText())>0) {
					comprarEntradas();
				}else {
					JOptionPane.showMessageDialog(root, "Debes comprar alguna entrada para continuar");
				}
				
			}
		});
		btnActualizarInterfaz.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateTickets();
			}
		});
		btnReservar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Integer.parseInt(txtEntradas1.getText())>0 || Integer.parseInt(txtEntradas2.getText())>0 || Integer.parseInt(txtEntradas3.getText())>0) {
					reservarEntradas();
				}else {
					JOptionPane.showMessageDialog(root, "Debes reservar alguna entrada para continuar");
				}
				
			}
		});
	}

	private void updateTickets() {
		try (Socket socket = new Socket("localhost", 1234)) {
			OutputStream outputStream = socket.getOutputStream();
			outputStream.write(("consultar\n").getBytes());
			outputStream.flush();
			BufferedReader entradaServidor = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String[] preciosString = entradaServidor.readLine().split(",");
			int[] precios = {
					Integer.parseInt(preciosString[0]),
					Integer.parseInt(preciosString[1]),
					Integer.parseInt(preciosString[2])
			};

			new Thread(() -> {
				SwingUtilities.invokeLater(() -> {
					lblEntradas1.setText("Entradas Normales: " + precios[0]);
					lblEntradas2.setText("Entradas Medias: " + precios[1]);
					lblEntradas3.setText("Entradas VIP: " + precios[2]);
				});
			}).start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int[] getUpdatedTickets() {
		int[] precios = new int[3];
		try (Socket socket = new Socket("localhost", 1234)) {
			OutputStream outputStream = socket.getOutputStream();
			outputStream.write(("consultar\n").getBytes());
			outputStream.flush();
			BufferedReader entradaServidor = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String[] preciosString = entradaServidor.readLine().split(",");
			precios[0] = Integer.parseInt(preciosString[0]);
			precios[1] = Integer.parseInt(preciosString[1]);
			precios[2] = Integer.parseInt(preciosString[2]);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return precios;
	}

	private boolean validarTickets() {
		updateTickets();
		boolean cantidadEntradasValidas = true;
		int[] newTickets = getUpdatedTickets();
		try {
			if (Integer.parseInt(txtEntradas1.getText()) > newTickets[0]) {
				JOptionPane.showMessageDialog(root, "No hay suficientes entradas normales");
				cantidadEntradasValidas = false;
			}
			if(Integer.parseInt(txtEntradas1.getText()) > 3 || Integer.parseInt(txtEntradas1.getText()) <0) {
				JOptionPane.showMessageDialog(root, "Cantidad de entradas normales no valida");
				cantidadEntradasValidas = false;
			}
			if (Integer.parseInt(txtEntradas2.getText()) > newTickets[1]) {
				JOptionPane.showMessageDialog(root, "No hay suficientes entradas medias");
				cantidadEntradasValidas = false;
			}
			if(Integer.parseInt(txtEntradas2.getText()) > 3 || Integer.parseInt(txtEntradas2.getText()) <0) {
				JOptionPane.showMessageDialog(root, "Cantidad de entradas medias no valida");
				cantidadEntradasValidas = false;
			}
			if (Integer.parseInt(txtEntradas3.getText()) > newTickets[2]) {
				JOptionPane.showMessageDialog(root, "No hay suficientes entradas vip");
				cantidadEntradasValidas = false;
			}
			if(Integer.parseInt(txtEntradas3.getText()) > 3 || Integer.parseInt(txtEntradas3.getText()) <0) {
				JOptionPane.showMessageDialog(root, "Cantidad de entradas vip no valida");
				cantidadEntradasValidas = false;
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(root, "No has introducido entradas");
		}
		return cantidadEntradasValidas;
	}

	public void reservarEntradas() {
		if(!estaReservando) {
			if(validarTickets()) {
				comprarEntradas();
				btnReservar.setText("Confirmar");
				progressBar.setVisible(true);
				progressBar.setValue(0);
				new Thread(()->{
					progressBar.setMaximum(MAX_SECONDS_TO_WAIT);
					for(int i=0;i<=MAX_SECONDS_TO_WAIT;i++) {
						//System.out.println(i);
						progressBar.setValue(i);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (!estaReservando) {
							progressBar.setValue(0);
							break;
						}
						if(i == MAX_SECONDS_TO_WAIT) {
							// verifico si las entradas que se desean comprar son una cantidad válida
							if (validarTickets()) {
								try (Socket socket = new Socket("localhost", 1234)) {
									OutputStream os = socket.getOutputStream();
									os.write(("devolver\n").getBytes());
									os.flush();
									// envio la cantidad de entradas que se quieren comprar por orden de tipo
									os.write((txtEntradas1.getText() + "\n").getBytes());
									os.flush();
									os.write((txtEntradas2.getText() + "\n").getBytes());
									os.flush();
									os.write((txtEntradas3.getText() + "\n").getBytes());
									os.flush();
									updateTickets();
									estaReservando = !estaReservando;
									progressBar.setVisible(false);
									progressBar.setValue(0);
									btnReservar.setText("Reservar");

								} catch (UnknownHostException e1) {
									e1.printStackTrace();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
							progressBar.setValue(0);
							progressBar.setVisible(false);
						}
					}
				}).start();
				updateTickets();
				estaReservando = !estaReservando;	
			}
		}else {
			System.out.println("has confirmado la compra");
			btnReservar.setText("Reservar");
			//progressBar = new JProgressBar();
			progressBar.setVisible(false);
			progressBar.setValue(0);
			estaReservando = !estaReservando;
		}
	}

	public void comprarEntradas() {
		// verifico si las entradas que se desean comprar son una cantidad válida
		if (validarTickets()) {
			try (Socket socket = new Socket("localhost", 1234)) {
				OutputStream os = socket.getOutputStream();
				os.write(("comprar\n").getBytes());
				os.flush();
				// envio la cantidad de entradas que se quieren comprar por orden de tipo
				os.write((txtEntradas1.getText() + "\n").getBytes());
				os.flush();
				os.write((txtEntradas2.getText() + "\n").getBytes());
				os.flush();
				os.write((txtEntradas3.getText() + "\n").getBytes());
				os.flush();
				updateTickets();
				
				//si no ha habido ningún error genero el pdf
				Map<String, Object> parametros = new HashMap<String, Object>();
				parametros.put("entradasNormales", txtEntradas1.getText());
				parametros.put("entradasMedias", txtEntradas2.getText());
				parametros.put("entradasVip", txtEntradas3.getText());
				
				try {
					JasperReport jasperReport = JasperCompileManager.compileReport("src/jasperfiles/EntradaConcierto.jrxml");
					JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, new JREmptyDataSource());
					JasperViewer.viewReport(jasperPrint);
				} catch (JRException e1) {
					JOptionPane.showMessageDialog(root, "Se ha producido un error generando el pdf");
					e1.printStackTrace();
				}

			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
