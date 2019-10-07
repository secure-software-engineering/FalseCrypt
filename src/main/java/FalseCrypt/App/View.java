package FalseCrypt.App;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import FalseCrypt.Crypto.CryptoWrapper;
import FalseCrypt.Crypto.PasswordDeriveData;
import FalseCrypt.Crypto.WeakPasswordDerivation;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class View extends Composite {
	
	private final Combo inpPassword;
	private final Combo inpEncrypt;
	private final Combo inpDecrypt;
	private final Label labInformation;
	
	// used WindowBuilder + SWT Builder
	private View(final Composite parent) {
		super(parent, SWT.DOUBLE_BUFFERED);
		parent.setLayout(new FormLayout());
		setLayout(new FormLayout());
		
		Label labWarning1 = new Label(this, SWT.NONE);
		FormData fd_labWarning1 = new FormData();
		fd_labWarning1.bottom = new FormAttachment(0, 35);
		fd_labWarning1.right = new FormAttachment(0, 497);
		fd_labWarning1.top = new FormAttachment(0, 10);
		fd_labWarning1.left = new FormAttachment(0, 10);
		labWarning1.setLayoutData(fd_labWarning1);
		labWarning1.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		labWarning1.setText("FalseCrypt is probably the most insecure file encryption program");
		
		Label labWarning2 = new Label(this, SWT.NONE);
		FormData fd_labWarning2 = new FormData();
		fd_labWarning2.bottom = new FormAttachment(0, 56);
		fd_labWarning2.right = new FormAttachment(0, 497);
		fd_labWarning2.top = new FormAttachment(0, 31);
		fd_labWarning2.left = new FormAttachment(0, 10);
		labWarning2.setLayoutData(fd_labWarning2);
		labWarning2.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		labWarning2.setText("you will ever see. It's only purpose is to demonstrate the effectiveness");
		
		Label labWarning3 = new Label(this, SWT.NONE);
		FormData fd_labWarning3 = new FormData();
		fd_labWarning3.bottom = new FormAttachment(0, 77);
		fd_labWarning3.right = new FormAttachment(0, 497);
		fd_labWarning3.top = new FormAttachment(0, 52);
		fd_labWarning3.left = new FormAttachment(0, 10);
		labWarning3.setLayoutData(fd_labWarning3);
		labWarning3.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		labWarning3.setText("of a static code analysis tool like Sharper Crypto-API Analysis.");
		
		Label labWarning4 = new Label(this, SWT.NONE);
		FormData fd_labWarning4 = new FormData();
		fd_labWarning4.bottom = new FormAttachment(0, 99);
		fd_labWarning4.right = new FormAttachment(0, 440);
		fd_labWarning4.top = new FormAttachment(0, 73);
		fd_labWarning4.left = new FormAttachment(0, 10);
		labWarning4.setLayoutData(fd_labWarning4);
		labWarning4.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		labWarning4.setText("You have been warned!");
		
		Label labPassword = new Label(this, SWT.NONE);
		FormData fd_labPassword = new FormData();
		fd_labPassword.bottom = new FormAttachment(0, 131);
		fd_labPassword.right = new FormAttachment(0, 85);
		fd_labPassword.top = new FormAttachment(0, 108);
		fd_labPassword.left = new FormAttachment(0, 10);
		labPassword.setLayoutData(fd_labPassword);
		labPassword.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		labPassword.setText("Password:");
		
		inpPassword = new Combo(this, SWT.NONE);
		FormData fd_inpPassword = new FormData();
		fd_inpPassword.right = new FormAttachment(labWarning1, 0, SWT.RIGHT);
		fd_inpPassword.top = new FormAttachment(0, 105);
		fd_inpPassword.left = new FormAttachment(0, 91);
		inpPassword.setLayoutData(fd_inpPassword);
		inpPassword.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		
		final Button butEncrypt = new Button(this, SWT.NONE);
		butEncrypt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				butEncrypt.setEnabled(false);
				encrypt(inpEncrypt.getText(), inpPassword.getText());
				butEncrypt.setEnabled(true);
			}
		});
		FormData fd_butEncrypt = new FormData();
		fd_butEncrypt.right = new FormAttachment(0, 85);
		fd_butEncrypt.top = new FormAttachment(0, 137);
		fd_butEncrypt.left = new FormAttachment(0, 10);
		butEncrypt.setLayoutData(fd_butEncrypt);
		butEncrypt.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		butEncrypt.setText("Encrypt");
		
		inpEncrypt = new Combo(this, SWT.NONE);
		FormData fd_inpEncrypt = new FormData();
		fd_inpEncrypt.right = new FormAttachment(labWarning1, 0, SWT.RIGHT);
		fd_inpEncrypt.top = new FormAttachment(0, 138);
		fd_inpEncrypt.left = new FormAttachment(0, 91);
		inpEncrypt.setLayoutData(fd_inpEncrypt);
		inpEncrypt.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		inpEncrypt.setToolTipText("Accepts Folders and Files");
		
		final Button butDecrypt = new Button(this, SWT.NONE);
		butDecrypt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				butDecrypt.setEnabled(false);
				decrypt(inpDecrypt.getText(), inpPassword.getText());
				butDecrypt.setEnabled(true);
			}
		});
		FormData fd_butDecrypt = new FormData();
		fd_butDecrypt.right = new FormAttachment(0, 85);
		fd_butDecrypt.top = new FormAttachment(0, 170);
		fd_butDecrypt.left = new FormAttachment(0, 10);
		butDecrypt.setLayoutData(fd_butDecrypt);
		butDecrypt.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		butDecrypt.setText("Decrypt");

		inpDecrypt = new Combo(this, SWT.NONE);
		FormData fd_inpDecrypt = new FormData();
		fd_inpDecrypt.right = new FormAttachment(labWarning1, 0, SWT.RIGHT);
		fd_inpDecrypt.top = new FormAttachment(0, 171);
		fd_inpDecrypt.left = new FormAttachment(0, 91);
		inpDecrypt.setLayoutData(fd_inpDecrypt);
		inpDecrypt.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		inpDecrypt.setToolTipText("Accepts Folders and Files");
		
		labInformation = new Label(this, SWT.NONE);
		labInformation.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		FormData fd_labInformation = new FormData();
		fd_labInformation.left = new FormAttachment(labWarning1, 0, SWT.LEFT);
		fd_labInformation.top = new FormAttachment(butDecrypt, 6);
		fd_labInformation.right = new FormAttachment(labWarning1, 0, SWT.RIGHT);
		labInformation.setLayoutData(fd_labInformation);
		labInformation.setText("");

	}
	
	private void encrypt(final String path, final String password) {
		encrypt(path, password, null);
	}
	
	private void encrypt(final String path, final String password, PasswordDeriveData keyData) {
		final File file = new File(path);
		
		if (!file.exists()) {
			labInformation.setText("file / folder doesn't exist");
			return;
		} else {
			labInformation.setText("");
		}
		
		if (password.isEmpty()) {
			labInformation.setText("password cant be empty");
			return;
		}
		
		// if no key is given
		if (keyData == null) {
			// BUG 1: Key derivation should not be performed outside a foreach block that is using its return value.
	        // Otherwise all operations in "encrypt directory" have the same encryption key
			// TODO changed a little bit
			try {
				keyData = WeakPasswordDerivation.DerivePassword(password);
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				labInformation.setText(e.getMessage());
				e.printStackTrace();
			}
		}
		
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				encrypt(child.getAbsolutePath(), password, keyData);
			}
		} else /* file isn't directory */ {
			try {
				CryptoWrapper.EncryptFileWithPassword(file, password);
			} catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
				labInformation.setText(e.getClass().getSimpleName() + ": " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	private void decrypt(final String path, final String password) {
		final File file = new File(path);
		
		if (!file.exists()) {
			labInformation.setText("file / folder doesn't exist");
			return;
		} else {
			labInformation.setText("");
		}
		
		if (password.isEmpty()) {
			labInformation.setText("password cant be empty");
			return;
		}
		
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				decrypt(child.getAbsolutePath(), password);
			}
		} else /* file isn't directory */ {
			try {
				CryptoWrapper.DecryptFileWithPassword(file, password);
			} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
					| InvalidAlgorithmParameterException | IOException e) {
				labInformation.setText(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display, SWT.SHELL_TRIM & (~SWT.RESIZE));
		shell.setSize(511, 265);
		new View(shell);
		shell.open();

		// run the event loop as long as the window is open
		while (!shell.isDisposed()) {
		    // read the next OS event queue and transfer it to a SWT event
		    if (!display.readAndDispatch())
		     {
		    // if there are currently no other OS event to process
		    // sleep until the next OS event is available
		        display.sleep();
		     }
		}

		// disposes all associated windows and their components
		display.dispose();
	}
}
