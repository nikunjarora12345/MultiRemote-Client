package com.kaminari.multiremote;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;


public class MainActivity extends ActionBarActivity implements ListView.OnItemClickListener {

    public static boolean isConnected = false;
    public static Socket socket;
    public static PrintWriter out;

    private ListView list;
    private String[] items = {"Mouse", "Keyboard", "GamePad", "GamePad for Racing"};
    private ArrayAdapter<String> adapter;

    private static final String TAG = "MultiRemote";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        init();
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
    }

    private void init() {
        list = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, items);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()) {
            case R.id.connect:
                if(!MainActivity.isConnected) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Enter the IP address");

                    // Set up the input
                    final EditText input = new EditText(this);
                    // Specify the type of input expected
                    input.setInputType(InputType.TYPE_CLASS_PHONE);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ConnectToServer connect = new ConnectToServer();
                            connect.execute(input.getText().toString()); // Runs the
                            // Asynchronous task
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isConnected && out != null) {
            try {
                out.println("exit"); // Requests disconnection from the server
                socket.close();
                Toast.makeText(this, "Connection closed!!", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Log.e(TAG, "Failed to close connection!! - " + e.getMessage());
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) list.getItemAtPosition(position);
        Intent intent = new Intent();

        switch (item) {
            case "Mouse":
                intent.setClass(this, MouseActivity.class);
                startActivity(intent);
                break;
            case "Keyboard":
                intent.setClass(this, KeyboardActivity.class);
                startActivity(intent);
                break;
            case "GamePad":
                intent.setClass(this, GamePadActivity.class);
                startActivity(intent);
                break;
            case "GamePad for Racing":
                intent.setClass(this, RacingControllerActivity.class);
                startActivity(intent);
                break;
        }
    }

    public class ConnectToServer extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                InetAddress serverAddress = InetAddress.getByName(params[0]); // Get server IP
                // address
                socket = new Socket(serverAddress, 6789); // Connect to the server
            } catch (IOException e) {
                Log.e(TAG, "Failed to create a connection!! - " + e.getMessage());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            isConnected = result;
            Toast.makeText(MainActivity.this,
                    isConnected ? "Connected to the Server!!" : "Error while connecting!!",
                    Toast.LENGTH_LONG).show();
            if(isConnected) {
                try {
                    out = new PrintWriter(socket.getOutputStream(), true); // Create the stream
                    // through which data
                    // is sent to the server
                    if(out.checkError())
                        Log.e(TAG, "Error in PrintWriter!!");
                } catch (IOException e) {
                    Log.e(TAG, "Failed to create a stream!! - " + e.getMessage());
                }
            }
        }
    }
}
