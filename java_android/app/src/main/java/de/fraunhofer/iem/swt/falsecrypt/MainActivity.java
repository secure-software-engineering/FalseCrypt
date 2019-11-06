package de.fraunhofer.iem.swt.falsecrypt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import FalseCrypt.Crypto.CryptoWrapper;
import FalseCrypt.Crypto.PasswordDeriveData;
import FalseCrypt.Crypto.WeakPasswordDerivation;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_STORAGE_PERMISSION_BROWSE = 1;
    private final static int REQUEST_STORAGE_PERMISSION_API29 = 10;

    /** only needed for Android Q, user granted from here read / write access */
    private DocumentFile root = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void showUserInfo(final String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void onClick_Browse(View view) {
        // TODO out for Android Q ==>
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            showUserInfo("Android 10+ isn't supported sorry!");
            return;
        } else {
            setPath("");
            enableWidgets();
            root = DocumentFile.fromFile(Environment.getExternalStorageDirectory());
        }
        // <== out for Android Q

        // if permission aren't already granted, ask
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_STORAGE_PERMISSION_BROWSE);

        // permission granted, open browse dialog for root directory
        } // TODO in for Android Q
        //else {
        //    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        //    intent.addFlags(
        //            Intent.FLAG_GRANT_READ_URI_PERMISSION
        //                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        //                    | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        //                    | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        //    startActivityForResult(intent, REQUEST_STORAGE_PERMISSION_API29 );
        //}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION_BROWSE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showUserInfo("Permission granted, you can use browse.");
                } else {
                    showUserInfo("Permission denied, you can't use browse.");
                }
                return;
            }
        }
    }

    // TODO only needed for Android Q
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case  REQUEST_STORAGE_PERMISSION_API29:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    root = DocumentFile.fromTreeUri(this, uri);

                    // ==> TODO no idea if needed
                    int takeFlags = data.getFlags();
                    takeFlags &= (Intent.FLAG_GRANT_READ_URI_PERMISSION |
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        this.getContentResolver().takePersistableUriPermission(uri, takeFlags);
                    }
                    // <== no idea if needed

                    setPath("");
                    enableWidgets();
                }
        }
    }

    private void setPath(final String txt) {
        final EditText path = findViewById(R.id.path);
        path.setText(txt, TextView.BufferType.EDITABLE);
    }

    private void setPassword(final String password) {
        final EditText pw = findViewById(R.id.password);
        pw.setText(password, TextView.BufferType.EDITABLE);
    }

    private void enableWidgets() {
        final Button encrypt = findViewById(R.id.encrypt);
        final Button decrypt = findViewById(R.id.decrypt);
        final Button testfiles = findViewById(R.id.testfiles);
        final EditText path = findViewById(R.id.path);

        encrypt.setEnabled(true);
        decrypt.setEnabled(true);
        testfiles.setEnabled(true);
        path.setEnabled(true);
    }

    public void onClick_createTestFiles(View view) {
        if (root == null) return;

        // define used vars
        boolean ret = false;
        final String folder_1 = "TestFolder";

        // if folder exists, delete it (recreate test files)
        if (root.findFile(folder_1) != null) {
            root.findFile(folder_1).delete();
            showUserInfo("Deleted testfiles");
        }
        if (root.findFile(folder_1) == null) {
            DocumentFile folder = root.createDirectory(folder_1);
            if (folder != null) {
                DocumentFile file = folder.createFile("text/plain", "testfile");
                try {
                    final String msg = "Hello World";
                    OutputStream out = this.getContentResolver().openOutputStream(file.getUri());
                    out.write(msg.getBytes());
                    out.flush();
                    out.close();
                    ret = true;
                } catch (IOException e) {
                    showUserInfo("Testfile creation failed." + e.getMessage());
                    ret = false;
                }
            } else {
                showUserInfo("Testfolder creation failed.");
                ret = false;
            }
        } else {
            showUserInfo("Couldn't delete testfiles");
            ret = false;
        }

        // if successful created, set default values
        if (ret) {
            setPath(folder_1);
            setPassword("password");
            showUserInfo("Created testfiles!");
        } else {
            setPath("");
            setPassword("");
            showUserInfo("Failed to create testfiles!");
        }
    }

    public void onClick_Encrypt(View view) {
        showUserInfo("Encrypting...");

        encrypt(getPath(), getPassword());
    }

    public void onClick_Decrypt(View view) {
        showUserInfo("Decrypting...");

        decrypt(getPath(), getPassword());
    }

    private String getPassword() {
        EditText pw = findViewById(R.id.password);
        return pw.getText().toString();
    }

    private String getPath() {
        EditText path = findViewById(R.id.path);
        return path.getText().toString();
    }

    private void encrypt(final String path, final String password) {
        encrypt(path, password, null);
    }

    // TODO Android Q, work only with DocumentFile instead of File
    private void encrypt(final String path, final String password, PasswordDeriveData keyData) {
        File file = null;
        if (path.contains(Environment.getExternalStorageDirectory().getAbsolutePath())) {
            file = new File(path);
        } else {
            file = new File(Environment.getExternalStorageDirectory(), path);
        }
        System.out.println(file.getAbsolutePath());

        if (!file.exists()) {
            showUserInfo("file / folder doesn't exist");
            return;
        }

        if (password.isEmpty()) {
            showUserInfo("password cant be empty");
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
                showUserInfo(e.getMessage());
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
            } catch (Throwable e) {
                showUserInfo(e.getClass().getSimpleName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // TODO Android Q, work only with DocumentFile instead of File
    private void decrypt(final String path, final String password) {
        File file = null;
        if (path.contains(Environment.getExternalStorageDirectory().getAbsolutePath())) {
            file = new File(path);
        } else {
            file = new File(Environment.getExternalStorageDirectory(), path);
        }
        System.out.println(file.getAbsolutePath());

        if (!file.exists()) {
            showUserInfo("file / folder doesn't exist");
            return;
        }

        if (password.isEmpty()) {
            showUserInfo("password cant be empty");
            return;
        }

        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                decrypt(child.getAbsolutePath(), password);
            }
        } else /* file isn't directory */ {
            try {
                CryptoWrapper.DecryptFileWithPassword(file, password);
            } catch (Throwable e) {
                showUserInfo(e.getClass().getSimpleName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
