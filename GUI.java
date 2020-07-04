import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Label;
import java.awt.SystemColor;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.IllegalBlockSizeException;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.sun.corba.se.spi.orbutil.fsm.Input;

import sun.print.resources.serviceui;

public class GUI {
	public static String URL_DIR = System.getProperty("user.dir");
	private static String TEMP = "/temp/";
	
	public JFrame frame;
	//Choose file panel: file path and browse button
	JPanel panel;
	JLabel label;
	JTextField filePath;
	JButton btnBrowse;
	ImageIcon icnBrowse;
	boolean isSent;
	private File filename;
	
	
	//Encrypt & Decrypt panel
	JPanel panelEncrypt;
	JLabel lblAlgorithm, lblOption, lblKeyLength;
	static JComboBox cbAlgorithm, cbOption, cbKeyLength;
	String algorithm[] = {"AES", "DES", "RSA", "TripleDES"};
	String option[] = {"Encrypt", "Decrypt"};
	static String DESKeyLength[] = {"56"};
	static String RSAKeyLength[] = {"512", "1024", "2048"};
	static String AESKeyLength[] = {"128", "192", "256"};
	
	
	//Key Panel
	JPanel panelKey, panelSelectKey;
	JLabel lblEnter, lblOr;
	JTextField txtEnter;
	JTextArea txtKeyHex;
	JButton btnGen, btnAutoGen, btnDoCrypt;
	ImageIcon icnDoCrypt;
	static JComboBox cbSelectKey;
	static ArrayList<String> keyList;
	private static String keyStr, keyByte, keyPath, hMACLine, pubKeyStr; //keyPath only for RSA
	private int keyLength;
	
	//Crypt Content
	TextArea txtContent;
	JScrollPane jp;
	JTextField txtFileMac, txtCalculatedMac;
	JLabel lblFileMac, lblCalculatedMac, lblPercent;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setTitle("Basic Cryptography Application");
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public GUI() {
		initialize();
	}
	
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(true);
		frame.setBounds(100, 100, 820, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		  
		//File Panel
		panel = new JPanel();
		panel.setBackground(/*SystemColor.menu*/Color.ORANGE);
		panel.setBounds(20, 20, 760, 60);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		label = new JLabel("Choose file to crypt ");
		label.setBounds(10, 20, 135, 22);
		panel.add(label);
		
		filePath = new JTextField("");
		filePath.setBackground(SystemColor.menu);
		filePath.setBounds(150, 20, 510, 25);
		panel.add(filePath);
		filePath.setEditable(false);
		filePath.setColumns(10);
		
		btnBrowse = new JButton("");
		btnBrowse.setForeground(new Color(204, 255, 0));
		btnBrowse.setBounds(675, 10, 40, 40);
		icnBrowse = new ImageIcon(getClass().getResource("/images/browse.png"));
		btnBrowse.setIcon(new ImageIcon(icnBrowse.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH)));
		btnBrowse.setBorder(BorderFactory.createEmptyBorder());
		btnBrowse.setContentAreaFilled(false);
		panel.add(btnBrowse);
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System
						.getProperty("user.home")));
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int result = fileChooser.showOpenDialog(frame);
				if (result == JFileChooser.APPROVE_OPTION) {
					isSent = true;
					String path_send = (fileChooser.getSelectedFile()
							.getAbsolutePath()) ;
					System.out.println(path_send);
					filename = fileChooser.getSelectedFile();
					filePath.setText(path_send);
				}
			}
		});
		
		btnBrowse.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseEntered(java.awt.event.MouseEvent evt) {
		    	btnBrowse.setIcon(new ImageIcon(icnBrowse.getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH)));
		    }

		    public void mouseExited(java.awt.event.MouseEvent evt) {
		    	btnBrowse.setIcon(new ImageIcon(icnBrowse.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH)));
		    }
		});
		
		//Encrypt Panel
		panelEncrypt = new JPanel();
		panelEncrypt.setBackground(/*SystemColor.menu*/Color.ORANGE);
		panelEncrypt.setBounds(20, 90, 760, 60);
		frame.getContentPane().add(panelEncrypt);
		panelEncrypt.setLayout(null);
		
		lblAlgorithm = new JLabel("Algorithm");
		lblAlgorithm.setBounds(10, 20, 60, 22);
		panelEncrypt.add(lblAlgorithm);
		
		cbAlgorithm = new JComboBox(algorithm);
		cbAlgorithm.setBackground(Color.WHITE);
		cbAlgorithm.setBounds(70, 20, 100, 25);
		panelEncrypt.add(cbAlgorithm);
		cbAlgorithm.addActionListener(new ActionListener() {	
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				switch(cbAlgorithm.getSelectedItem().toString()) {
				case "DES":
					txtEnter.enable();
					txtKeyHex.setVisible(true);
					panelSelectKey.setVisible(false);
					cbKeyLength.setModel(new DefaultComboBoxModel(DESKeyLength));
					break;
				case "AES":
					txtEnter.enable();
					txtKeyHex.setVisible(true);
					panelSelectKey.setVisible(false);
					cbKeyLength.setModel(new DefaultComboBoxModel(AESKeyLength));
					break;
				case "RSA":
					txtEnter.disable();
					txtKeyHex.setVisible(false);
					panelSelectKey.setVisible(true);
					cbKeyLength.setModel(new DefaultComboBoxModel(RSAKeyLength));
					break;
				}
			}
		}); 
		
		lblOption = new JLabel("Encrypt/Decrypt");
		lblOption.setBounds(200, 20, 100, 22);
		panelEncrypt.add(lblOption);
		
		cbOption = new JComboBox(option);
		cbOption.setBackground(Color.WHITE);
		cbOption.setBounds(300, 20, 100, 25);
		panelEncrypt.add(cbOption);
		cbOption.addActionListener(new ActionListener() {	
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (cbAlgorithm.getSelectedItem().toString() == "RSA") {
					if (cbOption.getSelectedItem().toString() == "Encrypt") {
						keyList = new ArrayList<String>();
						File folder = new File(URL_DIR + "/PublicKey/");
						for (final File fileEntry : folder.listFiles()) {
					        if (fileEntry.isDirectory()) {
					            continue;
					        } else {
					            try {
									//keyList.add(Base64.getEncoder().encodeToString(RSA.loadPublicKey(URL_DIR + "/PublicKey/" + fileEntry.getName()).getEncoded()));
					            	keyList.add("/PublicKey/" + fileEntry.getName());
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
					        }
					    }
						cbSelectKey.setModel(new DefaultComboBoxModel(keyList.toArray()));
					}
					else {
						keyList = new ArrayList<String>();
						File folder = new File(URL_DIR + "/PrivateKey/");
						for (final File fileEntry : folder.listFiles()) {
					        if (fileEntry.isDirectory()) {
					            continue;
					        } else {
					            try {
									//keyList.add(Base64.getEncoder().encodeToString(RSA.loadPrivateKey(URL_DIR + "/PrivateKey/" + fileEntry.getName()).getEncoded()));
					            	keyList.add("/PrivateKey/" + fileEntry.getName());
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
					        }
					    }
						cbSelectKey.setModel(new DefaultComboBoxModel(keyList.toArray()));
					}
				}
			}
		}); 
		
		lblKeyLength = new JLabel("Key Length");
		lblKeyLength.setBounds(420, 20, 80, 22);
		panelEncrypt.add(lblKeyLength);
		
		cbKeyLength = new JComboBox(AESKeyLength);
		cbKeyLength.setBackground(Color.WHITE);
		cbKeyLength.setBounds(500, 20, 200, 25);
		panelEncrypt.add(cbKeyLength);
		
		//Key Panel
		panelKey = new JPanel();
		panelKey.setBackground(/*SystemColor.menu*/Color.ORANGE);
		panelKey.setBounds(20, 160, 760, 160);
		frame.getContentPane().add(panelKey);
		panelKey.setLayout(null);
		
		lblEnter = new JLabel("Enter your key");
		lblEnter.setBounds(10, 20, 135, 22);
		panelKey.add(lblEnter);
		
		txtEnter = new JTextField("");
		txtEnter.setBackground(Color.WHITE);
		txtEnter.setBounds(150, 20, 170, 25);
		panelKey.add(txtEnter);
		txtEnter.setEditable(true);
		txtEnter.setColumns(10); 
		
		btnGen = new JButton("Generate Key");
		btnGen.setBounds(350, 10, 120, 40);
		panelKey.add(btnGen);
		btnGen.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					if (cbAlgorithm.getSelectedItem().toString() == "RSA") {
						JOptionPane.showMessageDialog(frame, "Only Auto Generating available in the case of RSA!");
						return;
					}
					keyLength = Integer.parseInt(cbKeyLength.getSelectedItem().toString());
					keyStr = txtEnter.getText();
					keyByte = toHex(keyStr);
					txtKeyHex.setText(addWhiteSpace(keyByte.substring(keyByte.length() - keyLength/4), 5));
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		lblOr = new JLabel("or");
		lblOr.setBounds(490, 20, 20, 22);
		panelKey.add(lblOr);
		
		btnAutoGen = new JButton("Auto Generate");
		btnAutoGen.setBounds(530, 10, 130, 40);
		panelKey.add(btnAutoGen);
		btnAutoGen.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (cbAlgorithm.getSelectedItem().toString()) {
					case "AES":
						try {
							keyLength = Integer.parseInt(cbKeyLength.getSelectedItem().toString());
							keyStr = AES.keyGen(keyLength);
							keyByte = toHex(keyStr);
							txtKeyHex.setText(addWhiteSpace(keyByte.substring(keyByte.length() - keyLength/4), 5));
						} catch (NoSuchAlgorithmException | NumberFormatException | UnsupportedEncodingException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						break;
					case "DES":
						try {
							keyLength = Integer.parseInt(cbKeyLength.getSelectedItem().toString());
							keyStr = DES.keyGen(keyLength);
							keyByte = toHex(keyStr);
							txtKeyHex.setText(addWhiteSpace(keyByte.substring(keyByte.length() - keyLength/4), 5));
						} catch (NoSuchAlgorithmException | NumberFormatException | UnsupportedEncodingException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						break;
					case "RSA":
						try {
							RSA.keyGen(Integer.parseInt(cbKeyLength.getSelectedItem().toString()));
						} catch (NumberFormatException | IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						break;
				}
				txtEnter.setText(keyStr);
			}
		});
		
		txtKeyHex = new JTextArea("");
		txtKeyHex.setBackground(Color.WHITE);
		txtKeyHex.setBounds(150, 60, 510, 80);
		panelKey.add(txtKeyHex);
		txtKeyHex.setEditable(true);
		txtKeyHex.setColumns(10);
		txtKeyHex.setFont(new Font("Consolas", Font.PLAIN, 16));
		txtKeyHex.setLineWrap(true);
		txtKeyHex.setWrapStyleWord(true);
		
		panelSelectKey = new JPanel();
		panelSelectKey.setBounds(150, 60, 510, 80);
		panelSelectKey.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Select RSA Key"));
		panelKey.add(panelSelectKey);
		panelSelectKey.setLayout(null);
		cbSelectKey = new JComboBox();
		cbSelectKey.setBounds(20, 20, 480, 40);
		cbSelectKey.setBackground(Color.WHITE);
		panelSelectKey.add(cbSelectKey);
		
		btnDoCrypt = new JButton();
		btnDoCrypt.setBounds(675, 70, 50, 50);
		icnDoCrypt = new ImageIcon(getClass().getResource("/images/encrypt.png"));
		btnDoCrypt.setIcon(new ImageIcon(icnDoCrypt.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH)));
		btnDoCrypt.setBorder(BorderFactory.createEmptyBorder());
		btnDoCrypt.setContentAreaFilled(false);
		panelKey.add(btnDoCrypt);
		//Hieu ung chuot
		btnDoCrypt.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseEntered(java.awt.event.MouseEvent evt) {
		    	btnDoCrypt.setIcon(new ImageIcon(icnDoCrypt.getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH)));
		    }

		    public void mouseExited(java.awt.event.MouseEvent evt) {
		    	btnDoCrypt.setIcon(new ImageIcon(icnDoCrypt.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH)));
		    }
		});
		//Ma hoa - giai ma
		btnDoCrypt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				txtContent.setText("");
				if (filename == null) {
					JOptionPane.showMessageDialog(frame, "You haven't selected any files yet!");
					return;
				}
				else if (cbOption.getSelectedItem().toString() == "Encrypt") {
					switch (cbAlgorithm.getSelectedItem().toString()) {
					case "AES":
						try {
							AES.encrypt(keyStr, filename, new File("document.encrypted"));
							BufferedReader br = new BufferedReader(new FileReader("document.encrypted"));
							String line;
							while((line = br.readLine()) != null) {
						    	txtContent.append(line + "\n");
							}
							br.close();
						} catch (CryptoException | IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							JOptionPane.showMessageDialog(frame, "Wrong key size. Your key must contain exactly " + String.valueOf(keyLength/4) +
									" characters including WS!");
							return;
						}
						break;
					case "DES":
						try {
							DES.encrypt(keyStr, filename, new File("document.encrypted"));
							BufferedReader br = new BufferedReader(new FileReader("document.encrypted"));
							String line;
							while((line = br.readLine()) != null) {
						    	txtContent.append(line + "\n");
							}
							br.close();
						} catch (InvalidKeySpecException | CryptoException | IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							JOptionPane.showMessageDialog(frame, "Wrong key size. Your key must contain exactly 16 characters including WS!");
							return;
						}
						break;
					default:
						try {
							keyPath = cbSelectKey.getSelectedItem().toString();
							keyStr = Base64.getEncoder().encodeToString(RSA.loadPublicKey(URL_DIR + keyPath).getEncoded());
							RSA.encrypt(keyStr, filename, new File("document.encrypted"));
							BufferedReader br = new BufferedReader(new FileReader("document.encrypted"));
							String line;
							while((line = br.readLine()) != null) {
								txtContent.append(line + "\n");
							}
							br.close();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						break;
					}
				}
				else {
					switch (cbAlgorithm.getSelectedItem().toString()) {
					case "AES":
						try {
							AES.decrypt(keyStr, filename, new File("document.decrypted"));
							BufferedReader br = new BufferedReader(new FileReader("document.decrypted"));
							String line;
							while((line = br.readLine()) != null) {
								txtContent.append(line + "\n");
						    	hMACLine = line;
							}
							
							br.close();
							txtFileMac.setText(hMACLine.substring(hMACLine.length() - 64));
							txtCalculatedMac.setText(HMAC.hmacDigestDecrypt(new File("document.decrypted"), keyStr));
							lblPercent.setVisible(true);
							lblPercent.setText(Integer.toString(ByteDifference(txtFileMac.getText().toCharArray(), txtCalculatedMac.getText().toCharArray())) + "%");
							rewritingHashedFile(new File("document.decrypted"));
						} catch (CryptoException | IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							
							JOptionPane.showMessageDialog(frame, "Wrong key size. Your key must contain exactly " + String.valueOf(keyLength/4) +
									" characters including WS!");
							return;
						}
						break;
					case "DES":
						try {
							DES.decrypt(keyStr, filename, new File("document.decrypted"));
							BufferedReader br = new BufferedReader(new FileReader("document.decrypted"));
							String line;
							while((line = br.readLine()) != null) {
								txtContent.append(line + "\n");
						    	hMACLine = line;
							}
							
							br.close();
							txtFileMac.setText(hMACLine.substring(hMACLine.length() - 64));
							txtCalculatedMac.setText(HMAC.hmacDigestDecrypt(new File("document.decrypted"), keyStr));
							lblPercent.setVisible(true);
							lblPercent.setText(Integer.toString(ByteDifference(txtFileMac.getText().toCharArray(), txtCalculatedMac.getText().toCharArray())) + "%");
							rewritingHashedFile(new File("document.decrypted"));
						} catch (InvalidKeySpecException | CryptoException | IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							JOptionPane.showMessageDialog(frame, "Wrong key size. Your key must contain exactly 16 characters including WS!");
							return;
						}
						break;
					default:
						try {
							keyPath = cbSelectKey.getSelectedItem().toString();
							keyStr = Base64.getEncoder().encodeToString(RSA.loadPrivateKey(URL_DIR + keyPath).getEncoded());
							pubKeyStr = Base64.getEncoder().encodeToString(RSA.loadPublicKey(URL_DIR + keyPath.replace("PrivateKey", "PublicKey")).getEncoded());
							RSA.decrypt(keyStr, filename, new File("document.decrypted"));
							BufferedReader br = new BufferedReader(new FileReader("document.decrypted"));
							String line;
							while((line = br.readLine()) != null) {
						    	txtContent.append(line);
						    	txtContent.append("\n");
						    	hMACLine = line;
							}
							br.close();
							txtFileMac.setText(hMACLine.substring(hMACLine.length() - 64));
							txtCalculatedMac.setText(HMAC.hmacDigestDecrypt(new File("document.decrypted"), pubKeyStr));
							lblPercent.setVisible(true);
							lblPercent.setText(Integer.toString(ByteDifference(txtFileMac.getText().toCharArray(), txtCalculatedMac.getText().toCharArray())) + "%");
							rewritingHashedFile(new File("document.decrypted"));
						} catch (InvalidKeySpecException | CryptoException | IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						break;
					}
				}
			}
		});
		
		
		//Crypto Content
		txtContent = new TextArea();
		txtContent.setBounds(20, 330, 760, 250);
		txtContent.setBackground(/*SystemColor.menu*/Color.WHITE);
		frame.getContentPane().add(txtContent);
		txtContent.setFont(new Font("Consolas", Font.PLAIN, 18));
		//txtContent.setLineWrap(true);
		//txtContent.setWrapStyleWord(true);
		
		lblFileMac = new JLabel("MAC from file");
		lblFileMac.setBounds(30, 590, 120, 22);
		frame.getContentPane().add(lblFileMac);
		
		txtFileMac = new JTextField("");
		txtFileMac.setBackground(Color.WHITE);
		txtFileMac.setBounds(150, 590, 500, 25);
		frame.getContentPane().add(txtFileMac);
		txtFileMac.setEditable(false);
		
		lblCalculatedMac = new JLabel("Calculated MAC");
		lblCalculatedMac.setBounds(30, 620, 120, 22);
		frame.getContentPane().add(lblCalculatedMac);
		
		txtCalculatedMac = new JTextField("");
		txtCalculatedMac.setBackground(Color.WHITE);
		txtCalculatedMac.setBounds(150, 620, 500, 25);
		frame.getContentPane().add(txtCalculatedMac);
		txtCalculatedMac.setEditable(false);
		
		lblPercent = new JLabel("");
		lblPercent.setBounds(660, 590, 100, 30);
		frame.getContentPane().add(lblPercent);
		lblPercent.setVisible(false);
		lblPercent.setFont(new Font("Consolas", Font.PLAIN, 22));
	}
	
	//Chuyen string sang hexadecimal
	public String toHex(String arg) throws UnsupportedEncodingException {
	    return String.format("%064x", new BigInteger(1, arg.getBytes("UTF8")));
	}
	
	//Tao khoang trong tren key
	public String addWhiteSpace(String str, int number) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < str.length(); i+=2) {
		   if (i > 0) {
			  for (int j = 0; j < number; j++)
				  result.append(" ");
		    }
		   result.append(str.charAt(i));
		   result.append(str.charAt(i+1));
		}
		return result.toString();
	}
	
	public int ByteDifference(char[] str1, char[] str2) {
		int s = 0;
		for (int i = 0; i < 64; i++) {
			if (str1[i] == str2[i]) {
				s = s + 1;
			}
		}
		return (int) 100*s/64;
	}
	
	public void rewritingHashedFile(File path) throws IOException {
		//Doc file tru 64 byte cuoi von da danh cho 64 bit SHA256
		FileInputStream fis = new FileInputStream(path);
		byte[] inputBytes = new byte[fis.available() - 64];
		fis.read(inputBytes);
		fis.close();
		
		FileOutputStream fos =  new FileOutputStream(path);
		fos.write(inputBytes);
		fos.close();
	}
}
