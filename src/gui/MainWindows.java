package gui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class MainWindows {

	protected Shell shlMars;
	private Text txtFileToCrypt;
	private Text txtDecryptFilePath;
	private Text txtDecryptedFilePath;
	private Text txtDecryptPrivateKey;
	private Text txtDecryptUserName;
	private Text txtDecryptPassword;
	private Text txtPublicKey;
	private Text txtPrivateKey;
	private Text txtUserName;
	private Text txtPassword;
	private Text txtRepeatPassword;
	private List list;
	
	private java.util.List<engine.User> users;
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
	    long heapsize=Runtime.getRuntime().totalMemory();
	    System.out.println("heapsize is::"+heapsize);
	    System.out.println("max memory: " + java.lang.Runtime.getRuntime().maxMemory()); 
		try {
			MainWindows window = new MainWindows();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlMars.open();
		shlMars.layout();
		while (!shlMars.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlMars = new Shell(SWT.CLOSE | SWT.TITLE | SWT.BORDER | 
				SWT.APPLICATION_MODAL | SWT.MIN);
		shlMars.setSize(450, 450);
		shlMars.setText("Mars");
		

		users = new java.util.ArrayList<engine.User>();
//		users.add(new engine.User("Jan", "D:\\Dokumenty\\studia\\sem6\\bsk\\projekt\\rsa\\jan.pub", 
//				"D:\\Dokumenty\\studia\\sem6\\bsk\\projekt\\rsa\\jan.prv" ));
		
		
		Menu menu = new Menu(shlMars, SWT.BAR);
		shlMars.setMenuBar(menu);
		
		MenuItem mntmNewSubmenu = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu.setText("O programie");
		
		Menu menu_1 = new Menu(mntmNewSubmenu);
		mntmNewSubmenu.setMenu(menu_1);
		
		MenuItem mntmNewItem = new MenuItem(menu_1, SWT.NONE);
		mntmNewItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageDialog dialog = new MessageDialog(shlMars, "O programie", null,
						"Szyfrator MARS - 2014 \n"
						+ "Platforma Java IBMv9 \n"
						+ "Pawe³ Ciep³y, 131474", MessageDialog.INFORMATION, new String[] { "OK" }, 0);
				dialog.open();				
			}
		});
		mntmNewItem.setText("O programie");
		
		MenuItem mntmNewItem_1 = new MenuItem(menu_1, SWT.NONE);
		mntmNewItem_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.exit(0);
			}
		});
		mntmNewItem_1.setText("Zamknij");
		
		TabFolder tabFolder = new TabFolder(shlMars, SWT.NONE);
		tabFolder.setBounds(10, 10, 414, 371);
		
		TabItem tbtmSzyfrowanie = new TabItem(tabFolder, SWT.NONE);
		tbtmSzyfrowanie.setText("Szyfrowanie");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		tbtmSzyfrowanie.setControl(composite);
		composite.setLayout(null);
		
		CLabel lblPlikWejciowy = new CLabel(composite, SWT.NONE);
		lblPlikWejciowy.setBounds(5, 5, 85, 21);
		lblPlikWejciowy.setText("Plik wej\u015Bciowy:");
		
		txtFileToCrypt = new Text(composite, SWT.BORDER);
		txtFileToCrypt.setBounds(5, 33, 337, 21);
		txtFileToCrypt.setText("D:\\Dokumenty\\studia\\sem6\\bsk\\projekt\\dane\\plain.txt");
		txtFileToCrypt.setToolTipText("");
		
		Button btnChooseFileToEncrypt = new Button(composite, SWT.NONE);
		btnChooseFileToEncrypt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(shlMars,  SWT.OPEN  );
				dlg.setText("Wybierz plik do zaszyfrowania");
				//dlg.setFilterPath("D:\\Dokumenty");
				String path = dlg.open();
				if (path == null) return;
				txtFileToCrypt.setText(path);
				
			}
		});
		btnChooseFileToEncrypt.setBounds(347, 31, 54, 25);
		btnChooseFileToEncrypt.setText("Wybierz");
		
		Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(5, 67, 229, 2);
		
		CLabel lblUstawieniaSzyfrowania = new CLabel(composite, SWT.NONE);
		lblUstawieniaSzyfrowania.setBounds(5, 81, 131, 21);
		lblUstawieniaSzyfrowania.setText("Ustawienia szyfrowania:");
		
		CLabel lblUytkownicy = new CLabel(composite, SWT.NONE);
		lblUytkownicy.setBounds(5, 109, 99, 21);
		lblUytkownicy.setText("U\u017Cytkownicy:");
		
		list = new List(composite, SWT.BORDER);
		for(engine.User u : users) {
			list.add(u.getName());
		}
		list.setBounds(10, 143, 131, 190);
		
		
		Button btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(shlMars,  SWT.OPEN  );
				dlg.setText("Wybierz plik klucza publicznego");
				String[] extensions = new String[1];
				extensions[0] = "*.pub";
				dlg.setFilterExtensions(extensions);
				//dlg.setFilterPath("D:\\Dokumenty");
				String path = dlg.open();				
				if (path == null) return;
				
				Path p = Paths.get(path);

				String [] fileparts = p.getFileName().toString().split("\\.");
				String userName = fileparts[0];
				
				if(!checkIfUserExists(userName))
				{
					users.add(new engine.User(userName, path, ""));
					list.add(userName);
				}
				//txtFileToCrypt.setText(path);
			}

			private Boolean checkIfUserExists(String userName) {
				Boolean userExists = false;
				for(String s : list.getItems())
				{
					if(s.equals(userName))
						userExists = true;
				}
				return userExists;
			}
		});
		btnNewButton.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		btnNewButton.setBounds(147, 202, 32, 25);
		btnNewButton.setText("+");
		
		Button btnNewButton_1 = new Button(composite, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(list.getSelectionIndex() >=0) {
					int i = list.getSelectionIndex();
					list.remove(i);
					users.remove(i);
				}
			}
		});
		btnNewButton_1.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		btnNewButton_1.setBounds(147, 254, 32, 25);
		btnNewButton_1.setText("-");
		
		CLabel lblDugoKlucza = new CLabel(composite, SWT.NONE);
		lblDugoKlucza.setBounds(200, 109, 99, 21);
		lblDugoKlucza.setText("D\u0142ugo\u015B\u0107 klucza:");
		
		CLabel lblTryb = new CLabel(composite, SWT.NONE);
		lblTryb.setBounds(200, 147, 61, 21);
		lblTryb.setText("Tryb:");
		
		CLabel lblDugoBloku = new CLabel(composite, SWT.NONE);
		lblDugoBloku.setBounds(200, 189, 120, 21);
		lblDugoBloku.setText("D\u0142ugo\u015B\u0107 podbloku:");
		
		final CCombo cmbKeyLength = new CCombo(composite, SWT.BORDER);
		cmbKeyLength.setText("128");
		cmbKeyLength.setListVisible(true);
		cmbKeyLength.setItems(new String[] {"128", "192", "256"});
		cmbKeyLength.setBounds(326, 109, 70, 21);
		final CCombo cmbSubblockLength = new CCombo(composite, SWT.BORDER);
		final CCombo cmbMode = new CCombo(composite, SWT.BORDER);
		cmbMode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(cmbMode.getText().equals("CFB") || cmbMode.getText().equals("OFB")) {
					cmbSubblockLength.setEnabled(true);
				} else {
					cmbSubblockLength.setEnabled(false);
				}
				
				//cmbSubb
			}
		});
		cmbMode.setListVisible(true);
		cmbMode.setText("ECB");
		cmbMode.setItems(new String[] {"ECB", "CBC", "CFB", "OFB"});
		cmbMode.setBounds(326, 147, 70, 21);
		
		//final CCombo cmbSubblockLength = new CCombo(composite, SWT.BORDER);
		cmbSubblockLength.setEnabled(false);
		cmbSubblockLength.setListVisible(true);
		cmbSubblockLength.setText("0");
		cmbSubblockLength.setItems(new String[] {"0", "8", "16", "32", "64"});
		cmbSubblockLength.setBounds(326, 189, 70, 21);
		
		Button btnEncrypt = new Button(composite, SWT.NONE);
		btnEncrypt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//String mode = modeComboBox.getSelectedItem().toString();
				

				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String mode = cmbMode.getText();
						int keySize = Integer.parseInt(cmbKeyLength.getText());
						int subBlockLength = Integer.parseInt(cmbSubblockLength.getText());
						String filePath = txtFileToCrypt.getText();
						
						FileDialog dlg = new FileDialog(shlMars,  SWT.SAVE  );
						dlg.setText("Zapisz plik wynikowy");
						//dlg.setFilterPath("D:\\Dokumenty");
						String path = dlg.open();
						if (path == null) return;
						
						engine.Engine cipherEngine = new engine.Engine(mode, keySize, subBlockLength, filePath);
						try {
							long startTime = System.currentTimeMillis();									
							cipherEngine.encrypt(users);
							long endTime = System.currentTimeMillis();
							cipherEngine.saveEncryptedToFile(path, users);								
							
							MessageDialog dialog = new MessageDialog(shlMars, "OK", null,
									"Zaszyfrowane w czasie: " + (double)(endTime-startTime)/1000 + " sekund", MessageDialog.OK, new String[] { "OK" }, 0);
							dialog.open();
							
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							System.out.println(e1.toString());
						}	
					}
				});
				
				
			}
		});
		btnEncrypt.setBounds(249, 251, 120, 35);
		btnEncrypt.setText("Szyfruj");
		
		TabItem tbtmOdszyfrowanie = new TabItem(tabFolder, SWT.NONE);
		tbtmOdszyfrowanie.setText("Odszyfrowanie");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmOdszyfrowanie.setControl(composite_1);
		composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		
		CLabel lblPlikWejciowy_1 = new CLabel(composite_1, SWT.NONE);
		lblPlikWejciowy_1.setBounds(0, 10, 87, 21);
		lblPlikWejciowy_1.setText("Plik wej\u015Bciowy:");
		
		txtDecryptFilePath = new Text(composite_1, SWT.BORDER);
		txtDecryptFilePath.setText("D:\\Dokumenty\\studia\\sem6\\bsk\\projekt\\dane\\encrypted.xml");
		txtDecryptFilePath.setBounds(93, 10, 268, 21);
		
		Button btnWybierz = new Button(composite_1, SWT.NONE);
		btnWybierz.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(shlMars,  SWT.OPEN  );
				dlg.setText("Podaj plik do odszyfrowania");
				//dlg.setFilterPath("D:\\Dokumenty");
				String path = dlg.open();
				if (path == null) return;
				txtDecryptFilePath.setText(path);				
			}
		});
		btnWybierz.setBounds(367, 8, 29, 25);
		btnWybierz.setText("...");
		
		CLabel lblPlikWynikowy = new CLabel(composite_1, SWT.NONE);
		lblPlikWynikowy.setBounds(0, 55, 87, 21);
		lblPlikWynikowy.setText("Plik wynikowy:");
		
		txtDecryptedFilePath = new Text(composite_1, SWT.BORDER);
		txtDecryptedFilePath.setText("D:\\Dokumenty\\studia\\sem6\\bsk\\projekt\\dane\\decrypted.txt");
		txtDecryptedFilePath.setBounds(93, 55, 268, 21);
		
		Button button = new Button(composite_1, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(shlMars,  SWT.OPEN  );
				dlg.setText("Open");
				//dlg.setFilterPath("D:\\Dokumenty");
				String path = dlg.open();
				if (path == null) return;
				txtDecryptedFilePath.setText(path);
			}
		});
		button.setBounds(367, 53, 29, 25);
		button.setText("...");
		
		CLabel lblKluczPrywatny = new CLabel(composite_1, SWT.NONE);
		lblKluczPrywatny.setBounds(0, 109, 106, 21);
		lblKluczPrywatny.setText("Klucz prywatny:");
		
		txtDecryptPrivateKey = new Text(composite_1, SWT.BORDER);
		txtDecryptPrivateKey.setBounds(108, 109, 253, 21);
		
		Button button_1 = new Button(composite_1, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(shlMars,  SWT.OPEN  );
				dlg.setText("Wybierz plik klucza prywatnego");
				String[] extensions = new String[1];
				extensions[0] = "*.prvenc";
				dlg.setFilterExtensions(extensions);
				//dlg.setFilterPath("D:\\Dokumenty");
				String path = dlg.open();
				if (path == null) return;
				txtDecryptPrivateKey.setText(path);
			}
		});
		button_1.setBounds(367, 107, 29, 25);
		button_1.setText("...");
		
		CLabel lblNazwaUytkownika = new CLabel(composite_1, SWT.NONE);
		lblNazwaUytkownika.setBounds(0, 164, 123, 21);
		lblNazwaUytkownika.setText("Nazwa u\u017Cytkownika:");
		
		txtDecryptUserName = new Text(composite_1, SWT.BORDER);
		txtDecryptUserName.setBounds(161, 164, 142, 21);
		
		CLabel lblHasoKluczaPrywatnego = new CLabel(composite_1, SWT.NONE);
		lblHasoKluczaPrywatnego.setBounds(0, 216, 142, 21);
		lblHasoKluczaPrywatnego.setText("Has\u0142o klucza prywatnego:");
		
		txtDecryptPassword = new Text(composite_1, SWT.BORDER | SWT.PASSWORD);
		txtDecryptPassword.setBounds(161, 216, 142, 21);
		
		Button btnDeszyfruj = new Button(composite_1, SWT.NONE);
		btnDeszyfruj.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String filePath =  txtDecryptFilePath.getText();
						
						engine.Engine cipherEngine = new engine.Engine(filePath, txtDecryptUserName.getText());				
						engine.User u = null;
						java.util.List<engine.User> fakeUsers = null;
						Boolean fakeCipher = false;
						try {
							u = engine.Engine.decryptPrivateKey(txtDecryptPassword.getText(), txtDecryptPrivateKey.getText(), txtDecryptUserName.getText());
							long startTime = System.currentTimeMillis();														
							cipherEngine.decrypt(u);
							long endTime = System.currentTimeMillis();
							
							MessageDialog dialog = new MessageDialog(shlMars, "OK", null,
									"Odszyfrowane w czasie: " + (double)(endTime-startTime)/1000 + " sekund", MessageDialog.OK, new String[] { "OK" }, 0);
							dialog.open();
						} catch (Exception e1) {
							fakeCipher = true;
							long startTime = System.currentTimeMillis();
							try {
								engine.Engine.encryptPrivateKey(txtDecryptPassword.getText(), filePath);
							} catch (InvalidKeyException
									| NoSuchAlgorithmException
									| NoSuchPaddingException
									| IllegalBlockSizeException
									| BadPaddingException
									| UnsupportedEncodingException e) {
							}
							long endTime = System.currentTimeMillis();
							MessageDialog dialog = new MessageDialog(shlMars, "OK", null,
									"Odszyfrowane w czasie: " + (double)(endTime-startTime)/1000 + " sekund", MessageDialog.OK, new String[] { "OK" }, 0);
							dialog.open();
						} finally {
							//String decryptedFilePath = txtDecryptedFilePath.getText();
							if(!fakeCipher) {
								cipherEngine.saveDecryptedToFile(txtDecryptedFilePath.getText());
							} else {
								byte[] complement = new byte[700];
								SecureRandom random = new SecureRandom();
								random.nextBytes(complement);
								try {
									Files.write( Paths.get(txtDecryptedFilePath.getText()), 
									        complement, 
									        StandardOpenOption.CREATE);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									System.out.println(e.toString());
								}
							}
							if(u!=null) {
								try {
									Files.delete(Paths.get(u.getPrivateKeyFilePath()));
								} catch (IOException ex) {
									// TODO Auto-generated catch block
									//ex.printStackTrace();
									System.out.println(ex.toString());
								}	
							}
							
						}
					}
				});
				
			}
		});
		btnDeszyfruj.setBounds(290, 271, 106, 51);
		btnDeszyfruj.setText("Deszyfruj");
		
		TabItem tbtmZarzdzanieKluczami = new TabItem(tabFolder, SWT.NONE);
		tbtmZarzdzanieKluczami.setText("Zarz\u0105dzanie kluczami");
		
		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		composite_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		tbtmZarzdzanieKluczami.setControl(composite_2);
		
		CLabel lblKluczPubliczny = new CLabel(composite_2, SWT.NONE);
		lblKluczPubliczny.setBounds(10, 10, 91, 21);
		lblKluczPubliczny.setText("Klucz publiczny:");
		
		txtPublicKey = new Text(composite_2, SWT.BORDER);
		txtPublicKey.setText("D:\\Dokumenty\\studia\\sem6\\bsk\\projekt\\rsa");
		txtPublicKey.setBounds(131, 10, 200, 21);
		
		Button btnNewButton_2 = new Button(composite_2, SWT.NONE);
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dlg = new DirectoryDialog(shlMars,  SWT.OPEN  );
				dlg.setText("Wybierz lokalizacjê dla klucza publicznego");
				//dlg.setFilterPath("D:\\Dokumenty");
				String path = dlg.open();
				if (path == null) return;
				txtPublicKey.setText(path);
			}
		});
		btnNewButton_2.setBounds(350, 6, 35, 25);
		btnNewButton_2.setText("...");
		
		CLabel lblKluczPrywatny_1 = new CLabel(composite_2, SWT.NONE);
		lblKluczPrywatny_1.setBounds(10, 51, 91, 21);
		lblKluczPrywatny_1.setText("Klucz prywatny:");
		
		txtPrivateKey = new Text(composite_2, SWT.BORDER);
		txtPrivateKey.setText("D:\\Dokumenty\\studia\\sem6\\bsk\\projekt\\rsa");
		txtPrivateKey.setBounds(131, 51, 200, 21);
		
		Button button_2 = new Button(composite_2, SWT.NONE);
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dlg = new DirectoryDialog(shlMars,  SWT.OPEN  );
				dlg.setText("Wybierz lokalizacjê dla klucza prywatnego");
				//dlg.setFilterPath("D:\\Dokumenty");
				String path = dlg.open();
				if (path == null) return;
				txtPrivateKey.setText(path);
			}
		});
		button_2.setBounds(350, 51, 35, 25);
		button_2.setText("...");
		
		CLabel lblNazwaUytkownika_1 = new CLabel(composite_2, SWT.NONE);
		lblNazwaUytkownika_1.setBounds(10, 112, 113, 21);
		lblNazwaUytkownika_1.setText("Nazwa u\u017Cytkownika:");
		
		txtUserName = new Text(composite_2, SWT.BORDER);
		txtUserName.setBounds(159, 112, 147, 21);
		
		CLabel lblHaso = new CLabel(composite_2, SWT.NONE);
		lblHaso.setBounds(10, 166, 61, 21);
		lblHaso.setText("Has\u0142o:");
		
		txtPassword = new Text(composite_2, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setBounds(159, 166, 147, 21);
		
		CLabel lblPowtrzHaso = new CLabel(composite_2, SWT.NONE);
		lblPowtrzHaso.setBounds(10, 218, 113, 21);
		lblPowtrzHaso.setText("Powt\u00F3rz has\u0142o:");
		
		txtRepeatPassword = new Text(composite_2, SWT.BORDER | SWT.PASSWORD);
		txtRepeatPassword.setBounds(159, 218, 147, 21);
		
		Button btnNewButton_3 = new Button(composite_2, SWT.NONE);
		btnNewButton_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				try {
					if(txtPassword.getText().equals(txtRepeatPassword.getText())) {
						engine.RSA rsaGen = new engine.RSA();
						try {
							rsaGen.generateKeyPair(txtPublicKey.getText() + "\\"+ txtUserName.getText() + ".pub",
									txtPrivateKey.getText() + "\\" + txtUserName.getText() + ".prv", 
									txtUserName.getText(), txtPassword.getText());
						} catch (NoSuchAlgorithmException | InvalidKeySpecException
								| IOException e1) {
							// TODO Auto-generated catch block
							System.out.println(e1.toString());
						}
					} else {
						MessageDialog dialog = new MessageDialog(shlMars, "B³¹d", null,
							    "Has³a siê nie zgadzaj¹", MessageDialog.ERROR, new String[] { "OK" }, 0);
							int result = dialog.open();
							System.out.println(result); 
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					System.out.println(e1.toString());
				}
				
			}
		});
		btnNewButton_3.setBounds(226, 290, 159, 43);
		btnNewButton_3.setText("Generuj par\u0119 kluczy");

	}
}
