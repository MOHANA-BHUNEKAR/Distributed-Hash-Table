package edu.buffalo.cse.cse486586.simpledht;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract.DataUsageFeedback;

public class SimpleDhtProvider extends ContentProvider {
	public static final Uri CONTENT_URI = Uri
			.parse("content://edu.buffalo.cse.cse486586.simpledht.provider/"
					+ DBCreation.TABLE_NAME);
	private DBCreation dn = null;

	String data;
	boolean isDataNotReceived = true;
	String port_avd;
	String node_id, pos;
	String suc_pointer;
	String pre_pointer;
	String portStr;
	int count = 0;
	int i = 0;
	ArrayList<String> nodeId = new ArrayList<String>();
	ArrayList<String> avd = new ArrayList<String>();
	static final String TAG = SimpleDhtProvider.class.getSimpleName();

	public static final int SERVER_PORT = 10000;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	private Uri buildUri(String scheme, String authority) {
		// TODO Auto-generated method stub

		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.authority(authority);
		uriBuilder.scheme(scheme);
		return uriBuilder.build();
		// return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub

		Log.v(TAG, "Suc_pointer: " + suc_pointer + " pre_pointer "
				+ pre_pointer + " and its port number: " + portStr);
		if (portStr.equals(suc_pointer) && portStr.equals(pre_pointer)) {

			Log.v(TAG, "Inserted");

			SQLiteDatabase db = dn.getWritableDatabase();
			long rowIdReturned = db.insertWithOnConflict(DBCreation.TABLE_NAME,
					null, values, SQLiteDatabase.CONFLICT_REPLACE);
			if (rowIdReturned > 0) {
				Log.v("insert", values.toString());
				return uri;
			}
		} else {

			String key = (String) values.get(DBCreation.COLUMN_KEY);
			String value = (String) values.get(DBCreation.COLUMN_VAL);
			String regkeva = key + "^^" + value;

			Thread clientThread = new Thread(new ClientTask(regkeva,"11108"));
			clientThread.start();
		}
		return null;
	}

	/*
	 * public Uri insert(Uri uri, ContentValues values) { // TODO Auto-generated
	 * method stub
	 * 
	 * SQLiteDatabase db = dn.getWritableDatabase(); String key = (String)
	 * values.get(DBCreation.COLUMN_KEY); String value = (String)
	 * values.get(DBCreation.COLUMN_VAL); String keyhash = null; String portHash
	 * = null; String sucportHash = null; String preportHash = null; String
	 * successor = String.valueOf((Integer.parseInt(suc_pointer) * 2));
	 * 
	 * try { keyhash = genHash(key); } catch (NoSuchAlgorithmException e) {
	 * e.printStackTrace(); }
	 * 
	 * try { portHash = genHash(portStr); } catch (NoSuchAlgorithmException e) {
	 * e.printStackTrace(); } try { sucportHash = genHash(suc_pointer); } catch
	 * (NoSuchAlgorithmException e) { e.printStackTrace(); } try { preportHash =
	 * genHash(pre_pointer); } catch (NoSuchAlgorithmException e) {
	 * e.printStackTrace(); } String regkeyhash = key + "@#" + keyhash + "@#" +
	 * value;
	 * 
	 * if (!(sucportHash.compareTo(portHash) < 0) ||
	 * !(preportHash.compareTo(portHash) > 0)) {
	 * 
	 * if (keyhash.compareTo(portHash) > 0 && keyhash.compareTo(sucportHash) <=
	 * 0) { // Insert into content provider ContentResolver cr =
	 * getContext().getContentResolver(); Uri mUri = buildUri("content",
	 * "edu.buffalo.cse.cse486586.simpledht.provider"); ContentValues cv1 = new
	 * ContentValues();
	 * 
	 * cv1 = new ContentValues(); cv1.put("key", key); cv1.put("value", value);
	 * cr.insert(mUri, cv1);
	 * 
	 * }
	 * 
	 * else { Log.v(TAG, "The successor for portstr: " + portStr + "  is " +
	 * successor); new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
	 * regkeyhash, successor);
	 * 
	 * }
	 * 
	 * }
	 * 
	 * else {
	 * 
	 * if ((sucportHash.compareTo(portHash) < 0)) { if
	 * (portHash.equals(keyhash)) { // Insert into content provider
	 * ContentResolver cr = getContext().getContentResolver(); Uri mUri =
	 * buildUri("content", "edu.buffalo.cse.cse486586.simpledht.provider");
	 * ContentValues cv1 = new ContentValues();
	 * 
	 * cv1 = new ContentValues(); cv1.put("key", key); cv1.put("value", value);
	 * cr.insert(mUri, cv1);
	 * 
	 * } else { if (keyhash.compareTo(portHash) > 0 ||
	 * keyhash.compareTo(sucportHash) < 0) { // Insert into 1st node-id (it's
	 * successor).To do this // you need to call sucessor new
	 * ClientTask().executeOnExecutor( AsyncTask.SERIAL_EXECUTOR, regkeyhash,
	 * successor);
	 * 
	 * }
	 * 
	 * else { // call its successor node - 1stnode new
	 * ClientTask().executeOnExecutor( AsyncTask.SERIAL_EXECUTOR, regkeyhash,
	 * successor);
	 * 
	 * } }
	 * 
	 * }
	 * 
	 * else { if (preportHash.compareTo(portHash) > 0) { if
	 * (keyhash.compareTo(preportHash) > 0 || keyhash.compareTo(portHash) <= 0)
	 * { // Insert into content provider of first avd ContentResolver cr =
	 * getContext().getContentResolver(); Uri mUri = buildUri("content",
	 * "edu.buffalo.cse.cse486586.simpledht.provider"); ContentValues cv1 = new
	 * ContentValues();
	 * 
	 * cv1 = new ContentValues(); cv1.put("key", key); cv1.put("value", value);
	 * cr.insert(mUri, cv1);
	 * 
	 * }
	 * 
	 * else { // call successor 2nd avd new ClientTask().executeOnExecutor(
	 * AsyncTask.SERIAL_EXECUTOR, regkeyhash, successor);
	 * 
	 * } }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * return null; }
	 */

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		dn = new DBCreation(getContext());
		TelephonyManager tel = (TelephonyManager) getContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		portStr = tel.getLine1Number().substring(
				tel.getLine1Number().length() - 4);
		port_avd = String.valueOf((Integer.parseInt(portStr) * 2));
		Log.v(TAG, "portStr: " + portStr);

		try {

			ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
			Log.v(TAG,
					"Server Socket created: and call new ServerTask().execute........");

			Thread t = new Thread(new ServerTask(serverSocket));
			t.start();

		} catch (IOException e) {
			/*
			 * Log is a good way to debug your code. LogCat prints out all the
			 * messages that Log class writes.
			 * 
			 * Please read
			 * http://developer.android.com/tools/debugging/debugging
			 * -projects.html and
			 * http://developer.android.com/tools/debugging/debugging-log.html
			 * for more information on debugging.
			 */
			Log.e(TAG, "Can't create a ServerSocket");
			return false;
		}
		Log.v(TAG, "Reached line 97");
		if (portStr.equals("5554")) {

			Log.v(TAG, "In portStr: expected 5554 and we got " + portStr);
			try {
				node_id = genHash(portStr);
				nodeId.add(node_id);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			Log.v(TAG, "Node-Id and its avd is: " + node_id + "  " + portStr);
			avd.add(portStr);
			pre_pointer = portStr;
			suc_pointer = portStr;
			count++;
			Log.v("count", "This is " + portStr);
			Log.v(TAG, "COUNT HAS TO BE ONE: " + count);
		}

		else {
			Log.v(TAG, "Not 5554:" + portStr);
			try {
				String msg = portStr;
				Log.v(TAG, "Assigned PortStr: " + msg);
				Thread clientThread = new Thread(new ClientTask(msg,"11108"));
				clientThread.start();
			} catch (Exception e) {
				e.printStackTrace();
				Log.v(TAG, "Exception thrown");
				suc_pointer = pre_pointer = portStr;
			}
		}
		return true;
	}

	class ServerTask implements Runnable {
		ServerSocket serverSocket;
		public ServerTask(ServerSocket serverSocket){
			this.serverSocket = serverSocket;
		}
		@Override
		public void run() {

			try {

				Log.v(TAG, "in doInBackground(ServerSocet....sockets");
				Socket clientSocket;
				InputStreamReader inputStreamReader;
				BufferedReader bufferedReader;
				String message = "";
				/*
				 * TODO: Fill in your server code that receives messages and
				 * passes them to onProgressUpdate().
				 */

				if (serverSocket != null)
					while (true) {
						try {

							// Log.v(TAG,
							// "Entered while loop of server class of port number: "
							// + port_avd);
							clientSocket = serverSocket.accept();
							inputStreamReader = new InputStreamReader(
									clientSocket.getInputStream());
							bufferedReader = new BufferedReader(
									inputStreamReader); // getting
														// the
														// client
														// message
							Log.v(TAG,
									"reached till buffered reader: --- blocking call ");
							message = bufferedReader.readLine();
							Log.v(TAG, "Received message: " + message
									+ " and its count is: " + count);

							Log.v(TAG, "We are currently in server of adv: "
									+ port_avd);

							if (message.contains("---")) {
								data = message;
								isDataNotReceived = false;
								return;
							}

							else if (message.equals("~~")) {

								String portn = message.substring(2,
										message.length());

								String str = "select * from "
										+ DBCreation.TABLE_NAME;
								SQLiteDatabase db = dn.getReadableDatabase();
								Cursor qc = db.rawQuery(str, null);

								String tempkeyval = null;
								for (int p1 = 0; p1 < qc.getCount() - 1; p1++) {
									qc.moveToNext();
									tempkeyval += (qc.getString(0) + "," + qc
											.getString(1));
									tempkeyval += "---";

								}
								tempkeyval += (qc.getString(0) + "," + qc
										.getString(1));

								Thread clientThread = new Thread(new ClientTask(tempkeyval,portn));
								clientThread.start();
								return;

							}

							else if (message.contains("^%")) {
								data = message;
								isDataNotReceived = false;
								return;
							}

							else if (message.contains("**")) {
								String array112[] = message.split("\\*\\*");
								String portnu = array112[0];
								String keywant = array112[1];

								String str = "select * from "
										+ DBCreation.TABLE_NAME + " where "
										+ DBCreation.COLUMN_KEY + "='"
										+ keywant + "'";
								SQLiteDatabase db = dn.getReadableDatabase();
								Cursor qc = db.rawQuery(str, null);
								qc.moveToFirst();
								String keyfinal = qc.getString(0);
								String valuefinal = qc.getString(1);
								String keval = keyfinal + "^%" + valuefinal;
								Thread clientThread = new Thread(new ClientTask(keval,portnu));
								clientThread.start();
								return;

							}

							else if (message.contains("__")) {
								String splitt[] = message.split("__");
								String key99 = splitt[0];
								String value99 = splitt[1];
								ContentResolver cr = getContext()
										.getContentResolver();
								Uri mUri = buildUri("content",
										"edu.buffalo.cse.cse486586.simpledht.provider");
								ContentValues cv1 = new ContentValues();

								cv1 = new ContentValues();
								cv1.put("key", key99);
								cv1.put("value", value99);
								cr.insert(mUri, cv1);
								return;
							}

							else if (message.contains("^^")) {
								// In avd0 which identifies which avd the key
								// value
								// should be inserted

								String split[] = message.split("\\^\\^");
								String key9 = split[0];
								String value9 = split[1];
								String keyhash1 = null;
								try {
									keyhash1 = genHash(key9);
								} catch (NoSuchAlgorithmException e) {
									e.printStackTrace();
								}
								int sizequ = nodeId.size();
								String idenavd = null;
								String portnum1 = null;
								String remesage = key9 + "__" + value9;

								if (keyhash1.compareTo(nodeId.get(0)) <= 0
										|| keyhash1.compareTo(nodeId
												.get(sizequ - 1)) > 0) {
									idenavd = avd.get(0);
									Log.v(TAG,
											"For insert avd.get(0): "
													+ avd.get(0));
									portnum1 = String.valueOf((Integer
											.parseInt(idenavd) * 2));

									Thread clientThread = new Thread(new ClientTask(remesage,portnum1));
									clientThread.start();
									return;
								}

								else {

									for (int y = 1; y < nodeId.size(); y++) {

										if (keyhash1.compareTo(nodeId
												.get(y - 1)) > 0
												&& keyhash1.compareTo(nodeId
														.get(y)) <= 0) {
											idenavd = avd.get(y);

											portnum1 = String.valueOf((Integer
													.parseInt(idenavd) * 2));
											Log.v(TAG,
													"For insert avd.get(y): "
															+ avd.get(y));

											Thread clientThread = new Thread(new ClientTask(remesage,portnum1));
											clientThread.start();
											return;
										}
									}
									return;
								}

							}

							else if (message.contains("##")) {

								String s12[] = message.split("##");// key##portnumber
								String key1 = s12[0];// extraction of key from
														// regex
								int sizequ = nodeId.size();
								String idenavd = null;
								String remesage = null;
								String portnum1 = null;
								String keyhash1 = null;
								try {
									keyhash1 = genHash(key1);
								} catch (NoSuchAlgorithmException e) {
									e.printStackTrace();
								}

								if (keyhash1.compareTo(nodeId.get(0)) <= 0
										|| keyhash1.compareTo(nodeId
												.get(sizequ - 1)) > 0) {
									// then value must be there avd.get(0)'s
									idenavd = avd.get(0); // remesage="**"+s12[0];
									portnum1 = String.valueOf((Integer
											.parseInt(idenavd) * 2));
									remesage = s12[1] + "**" + s12[0];// portnumberofvalreq**key
									Thread clientThread = new Thread(new ClientTask(remesage,portnum1));
									clientThread.start();
									return;
								}

								else {

									for (int y = 0; y < nodeId.size(); y++) {
										if ((y + 1) != (nodeId.size() - 1)) {
											if (keyhash1.compareTo(nodeId
													.get(y)) > 0
													&& keyhash1
															.compareTo(nodeId
																	.get(y + 1)) <= 0) {
												idenavd = avd.get(y);
												portnum1 = String
														.valueOf((Integer
																.parseInt(idenavd) * 2));

												Thread clientThread = new Thread(new ClientTask(remesage,portnum1));
												clientThread.start();

											}
										}
									}
									return;
								}

							}

							/*
							 * else if (message.contains("@#")) { String[]
							 * keyandhash = message.split("@#"); String keyCP =
							 * keyandhash[0]; String hash_key = keyandhash[1];
							 * String valueCP = keyandhash[2]; String portHash1
							 * = null; String sucportHash1 = null; String
							 * preportHash1 = null; String successor1 = null;
							 * try { portHash1 = genHash(portStr); } catch
							 * (NoSuchAlgorithmException e) {
							 * e.printStackTrace(); }
							 * 
							 * try { sucportHash1 = genHash(suc_pointer); }
							 * catch (NoSuchAlgorithmException e) {
							 * e.printStackTrace(); }
							 * 
							 * try { preportHash1 = genHash(pre_pointer); }
							 * catch (NoSuchAlgorithmException e) {
							 * e.printStackTrace(); }
							 * 
							 * if (!(sucportHash1.compareTo(portHash1) < 0) ||
							 * !(preportHash1.compareTo(portHash1) > 0)) {
							 * 
							 * if (hash_key.compareTo(portHash1) > 0 &&
							 * hash_key.compareTo(sucportHash1) <= 0) { //
							 * Insert into content provider ContentResolver cr =
							 * getContext() .getContentResolver(); Uri mUri =
							 * buildUri("content",
							 * "edu.buffalo.cse.cse486586.simpledht.provider");
							 * ContentValues cv1 = new ContentValues();
							 * 
							 * cv1 = new ContentValues(); cv1.put("key", keyCP);
							 * cv1.put("value", valueCP); cr.insert(mUri, cv1);
							 * }
							 * 
							 * else { successor1 = String.valueOf((Integer
							 * .parseInt(suc_pointer) * 2)); Log.v(TAG,
							 * "The successor for portstr: " + portStr + "  is "
							 * + successor1); // String regkeyhash=message; new
							 * ClientTask().executeOnExecutor(
							 * AsyncTask.SERIAL_EXECUTOR, message, successor1);
							 * 
							 * }
							 * 
							 * }
							 * 
							 * else {
							 * 
							 * if ((sucportHash1.compareTo(portHash1) < 0)) { //
							 * last node if (portHash1.equals(hash_key)) { //
							 * Insert into content provider ContentResolver cr =
							 * getContext() .getContentResolver(); Uri mUri =
							 * buildUri("content",
							 * "edu.buffalo.cse.cse486586.simpledht.provider");
							 * ContentValues cv1 = new ContentValues();
							 * 
							 * cv1 = new ContentValues(); cv1.put("key", keyCP);
							 * cv1.put("value", valueCP); cr.insert(mUri, cv1);
							 * } else { if (hash_key.compareTo(portHash1) > 0 ||
							 * hash_key .compareTo(sucportHash1) < 0) { //
							 * Insert into 1st node-id (it's // successor).To do
							 * this you need to // call sucessor new
							 * ClientTask().executeOnExecutor(
							 * AsyncTask.SERIAL_EXECUTOR, message, successor1);
							 * 
							 * }
							 * 
							 * else { // call its successor node - 1stnode new
							 * ClientTask().executeOnExecutor(
							 * AsyncTask.SERIAL_EXECUTOR, message, successor1);
							 * 
							 * } }
							 * 
							 * }
							 * 
							 * else { if (preportHash1.compareTo(portHash1) > 0)
							 * { if (hash_key.compareTo(preportHash1) > 0 ||
							 * hash_key .compareTo(portHash1) <= 0) { // Insert
							 * into content provider of // first avd
							 * ContentResolver cr = getContext()
							 * .getContentResolver(); Uri mUri =
							 * buildUri("content",
							 * "edu.buffalo.cse.cse486586.simpledht.provider");
							 * ContentValues cv1 = new ContentValues();
							 * 
							 * cv1 = new ContentValues(); cv1.put("key", keyCP);
							 * cv1.put("value", valueCP); cr.insert(mUri, cv1);
							 * }
							 * 
							 * else { // call successor 2nd avd new
							 * ClientTask().executeOnExecutor(
							 * AsyncTask.SERIAL_EXECUTOR, message, successor1);
							 * 
							 * } }
							 * 
							 * }
							 * 
							 * } }
							 */
							else if (message.contains(":")
									&& !message.contains("---")
									&& !message.contains("##")
									&& !message.contains("^^")
									&& !message.contains("**")
									&& !message.contains("^%")
									&& !message.contains("~~")
									&& !message.contains(",")
									&& !message.contains("__")) {
								Log.v(TAG, "Message with $$: " + message
										+ " PORTSTR: " + port_avd);
								String pre_suc[] = message.split(":");
								Log.v(TAG, "pre_suc: " + pre_suc[0]);
								if (!pre_suc[0].equals("N/A")) {
									pre_pointer = pre_suc[0];
									Log.v(TAG, "Predessor pointer: "
											+ pre_pointer + " for avd  "
											+ port_avd);
								}
								if (pre_suc[0].equals("N/A"))
									Log.v(TAG, "Predessor pointer: "
											+ pre_pointer + " for avd  "
											+ port_avd);

								if (!pre_suc[1].equals("N/A")) {
									suc_pointer = pre_suc[1];
									Log.v(TAG, "Successor Pointer: "
											+ suc_pointer + " for avd  "
											+ port_avd);
								}
								if (pre_suc[1].equals("N/A"))
									Log.v(TAG, "Successor Pointer: "
											+ suc_pointer + " for avd  "
											+ port_avd);
								return;
							}
							// The message contains portStr
							else if (!message.contains(":") && count == 1
									&& !message.contains("---")
									&& !message.contains("##")
									&& !message.contains("^^")
									&& !message.contains("**")
									&& !message.contains("^%")
									&& !message.contains("~~")
									&& !message.contains(",")
									&& !message.contains("__")) {
								try {
									node_id = genHash(message);
								} catch (NoSuchAlgorithmException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Log.v(TAG, "port number: " + message
										+ "  and it's node_id is: " + node_id);
								if (nodeId.get(0).compareTo(node_id) > 0) {
									Log.v(TAG,
											"nodeId of avd0 greater than other node");
									nodeId.add(0, node_id);
									avd.add(0, message);
									pre_pointer = avd.get(0);
									suc_pointer = avd.get(0);
									Log.v(TAG,
											"Successor and predessor of avdo is: "
													+ avd.get(0));
									String values = "5554:5554";// pred$$succ
									count++;
									Log.v(TAG, "COUNT VALUE IN LINE 189: "
											+ count);
									String s11 = String.valueOf((Integer
											.parseInt(avd.get(0)) * 2));

									Thread clientThread = new Thread(new ClientTask(values,s11));
									clientThread.start();
									return;
								}

								else {
									if (!message.contains("__")
											&& !message.contains("---")
											&& !message.contains("##")
											&& !message.contains("^^")
											&& !message.contains("**")
											&& !message.contains("^%")
											&& !message.contains("~~")
											&& !message.contains(",")) {
										Log.v(TAG,
												"Other avd is greater than avd0");
										nodeId.add(node_id);
										avd.add(message);
										pre_pointer = avd.get(1);
										suc_pointer = avd.get(1);
										String values = "5554:5554";// pred$$succ
										count++;
										Log.v(TAG, "COUNT VALUE IN 206: "
												+ count);
										String s22 = String.valueOf((Integer
												.parseInt(avd.get(1)) * 2));

										Thread clientThread = new Thread(new ClientTask(values,s22));
										clientThread.start();
										return;
									}

								}
							} else {
								if (!message.contains(":") && count > 1
										&& !message.contains("---")
										&& !message.contains("##")
										&& !message.contains("^^")
										&& !message.contains("**")
										&& !message.contains("^%")
										&& !message.contains("~~")
										&& !message.contains(",")) {
									Log.v(TAG, "count: in final else block "
											+ count + " and message is: "
											+ message);
									try {
										node_id = genHash(message);
									} catch (NoSuchAlgorithmException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									boolean flag = false;
									int j = 0;
									Log.v(TAG,
											"Before entering for loop size of node array is: "
													+ nodeId.size());
									for (j = 0; j < nodeId.size(); j++) {
										Log.v(TAG,
												"Inside for loop: with j value: "
														+ j);
										if (node_id.compareTo(nodeId.get(j)) < 0) {
											Log.v(TAG,
													"Inside if condition our avd node  is less that elements in arraylist");
											/*
											 * try { node_id=genHash(message); }
											 * catch (NoSuchAlgorithmException
											 * e) { // TODO Auto-generated catch
											 * block e.printStackTrace(); }
											 */
											nodeId.add(j, node_id);
											avd.add(j, message);
											flag = true;
											break;
										}
									}
									if (flag == false) {
										nodeId.add(node_id);
										avd.add(message);
										Log.v(TAG,
												" In line 255 with j value: "
														+ j
														+ " and size of array: "
														+ nodeId.size());
									}
									if (j != 0 && j != (nodeId.size() - 1)) {
										Log.v(TAG,
												"Inside line 259: with j value: "
														+ j);
										String s1 = String.valueOf((Integer
												.parseInt(avd.get(j)) * 2));
										String s2 = String.valueOf((Integer
												.parseInt(avd.get(j - 1)) * 2));
										String s3 = String.valueOf((Integer
												.parseInt(avd.get(j + 1)) * 2));

										Thread clientThread1 = new Thread(new ClientTask(avd.get(j - 1) + ":"
												+ avd.get(j + 1), s1));
										clientThread1.start();
										Thread clientThread2 = new Thread(new ClientTask("N/A" + ":" + avd.get(j), s2));
										clientThread2.start();
										Thread clientThread3 = new Thread(new ClientTask(avd.get(j) + ":" + "N/A", s3));
										clientThread3.start();

									} else {
										if (j == 0) {
											Log.v(TAG,
													"Inside line 275: with j value: "
															+ j);

											String s4 = String.valueOf((Integer
													.parseInt(avd.get(j)) * 2));
											String s5 = String.valueOf((Integer
													.parseInt(avd.get(nodeId
															.size() - 1)) * 2));
											String s6 = String
													.valueOf((Integer.parseInt(avd
															.get(j + 1)) * 2));
											Thread clientThread1 = new Thread(new ClientTask(avd.get(nodeId.size() - 1)
													+ ":"
													+ avd.get(j + 1),
											s4));
											clientThread1.start();
											Thread clientThread2 = new Thread(new ClientTask("N/A" + ":" + avd.get(0),
													s5));
											clientThread2.start();
											Thread clientThread3 = new Thread(new ClientTask(avd.get(j - 1) + ":"
													+ "N/A", s6));
											clientThread3.start();
									
											
										

										}

										if (j == nodeId.size() - 1) {
											Log.v(TAG,
													"Inside line 288: with j value: and portstr:"
															+ j + "  "
															+ port_avd);

											String s7 = String.valueOf((Integer
													.parseInt(avd.get(j)) * 2));

											String s8 = String
													.valueOf((Integer.parseInt(avd
															.get(j - 1)) * 2));
											Log.v(TAG, "s8: " + s8);
											String s9 = String.valueOf((Integer
													.parseInt(avd.get(0)) * 2));

											Thread clientThread1 = new Thread(new ClientTask(avd.get(j - 1) + ":"
													+ avd.get(0), s7));
											clientThread1.start();
											// if(!avd.get(j-1).equals("5554"))
											// {
											Thread clientThread2 = new Thread(new ClientTask("N/A" + ":" + avd.get(j),
													s8));
											clientThread2.start();
											// }
											/*
											 * else {
											 * Log.v(TAG,"For port number: "
											 * +port_avd );
											 * pre_pointer=avd.get(j);
											 * Log.v(TAG,"Predessor pointer: "+
											 * pre_pointer);
											 * Log.v(TAG,"Succesor pointer: "+
											 * suc_pointer); }
											 */
											Thread clientThread3 = new Thread(new ClientTask(avd.get(j) + ":" + "N/A",
													s9));
											clientThread3.start();

										}

									}

								}

							}

							inputStreamReader.close();
							clientSocket.close();

						} catch (IOException ex) {
							System.out.println("Error in reading message");
						}
					}

				// return null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}

	}

	class ClientTask implements Runnable{
		private PrintWriter printwriter;
		String message;
		String portNo;
		public ClientTask(String message, String portNo){
			this.message = message;
			this.portNo = portNo;
		}
		@Override
		public void run(){
			try {

				// String msgToSend;
				// Socket socket;

				// Log.v(TAG,"COUNT: "+count);
				Log.v(TAG, "msgs to send: " + message + " msgs[1]: " + portNo);

				// String remotePort=msgs[1];
				// Log.v(TAG,
				// "now about to create the socket remote port is: "+remotePort);
				Socket socket = new Socket(InetAddress.getByAddress(new byte[] {
						10, 0, 2, 2 }), Integer.parseInt(portNo));
				String msgToSend = message;
				Log.v(TAG, "Message sending to client: " + msgToSend);

				printwriter = new PrintWriter(socket.getOutputStream(), true);
				printwriter.write(msgToSend);
				printwriter.flush();
				printwriter.close();

				/*
				 * TODO: Fill in your client code that sends out a message.
				 */
				socket.close();

			} catch (UnknownHostException e) {
				Log.e(TAG, "ClientTask UnknownHostException: " + e);
				suc_pointer = pre_pointer = portStr;
				Log.e("demo2", "ClientTask socket IOException :" + suc_pointer);
			} catch (IOException e) {
				System.out.println();
				Log.e(TAG, "ClientTask socket IOException :" + e);
				suc_pointer = pre_pointer = portStr;
				Log.e("demo1", "ClientTask socket IOException :" + suc_pointer);
			} catch (Exception e){
				suc_pointer = pre_pointer = portStr;
				Log.e("demo", "ClientTask socket IOException :" + suc_pointer);
			}

			return;
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		// we need to find out which avd the key is present and retrieve its
		// value
		String keyreceived = selection;
		String keyhash = null;

		try {
			// calculate hash value the key received
			keyhash = genHash(selection);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		Log.v(TAG, "Inside query: Suc_pointer: " + suc_pointer
				+ " pre_pointer " + pre_pointer + " and its port number: "
				+ portStr);

		if (!selection.equals("*") && !selection.equals("@")) {

			if (portStr.equals(suc_pointer) && portStr.equals(pre_pointer)) {

				Log.v(TAG, "Query1");
				String str = "select * from " + DBCreation.TABLE_NAME
						+ " where " + DBCreation.COLUMN_KEY + "='" + selection
						+ "'";
				SQLiteDatabase db = dn.getReadableDatabase();
				Cursor qc = db.rawQuery(str, null);
				return qc;

			} else {
				String regkey = keyreceived + "##" + port_avd;
Thread clientThread = new Thread(new ClientTask(regkey,"11108"));
clientThread.start();
				while (isDataNotReceived) {

				}
				String[] s33 = data.split("^%");
				String key11 = s33[0];
				String val11 = s33[1];
				String matrix[] = new String[] { "key", "value" };
				String matrix11[] = new String[] { key11, val11 };
				MatrixCursor m = new MatrixCursor(matrix);
				m.addRow(matrix11);
				data = null;
				isDataNotReceived = true;
				return m;
			}

		}

		else if (selection.equals("@")) {
			String str = "select * from " + DBCreation.TABLE_NAME;
			SQLiteDatabase db = dn.getReadableDatabase();
			Cursor qc = db.rawQuery(str, null);
			return qc;

		}

		else {
			if (selection.equals("*")) {
				if (portStr.equals(suc_pointer) && portStr.equals(pre_pointer)) {
					String str = "select * from " + DBCreation.TABLE_NAME;
					SQLiteDatabase db = dn.getReadableDatabase();
					Cursor qc = db.rawQuery(str, null);
					return qc;
				} else {
					String[] portnum = { "11108", "11112", "11116", "11120",
							"11124" };
					String matrix[] = new String[] { "key", "value" };
					MatrixCursor m = new MatrixCursor(matrix);
					;
					for (int z = 0; z < portnum.length; z++) {
						data = null;
						isDataNotReceived = true;
						String iden = "~~" + port_avd;
						Thread clientThread = new Thread(new ClientTask(iden, portnum[z]));
						clientThread.start();
						while (isDataNotReceived) {

						}

						String[] star = data.split("---");
						String keyy = null;
						String vall = null;
						for (int l = 0; l < star.length; l++) {
							String[] kv = star[l].split(",");
							keyy = kv[0];
							vall = kv[1];
							String matrix11[] = new String[] { keyy, vall };

							m.addRow(matrix11);

						}

					}

					data = null;
					isDataNotReceived = true;
					return m;
				}
			}

		}
		return null;

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	private String genHash(String input) throws NoSuchAlgorithmException {
		MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
		byte[] sha1Hash = sha1.digest(input.getBytes());
		Formatter formatter = new Formatter();
		for (byte b : sha1Hash) {
			formatter.format("%02x", b);
		}
		return formatter.toString();
	}

}

/*
 * package edu.buffalo.cse.cse486586.simpledht;
 * 
 * import java.io.BufferedReader; import java.io.IOException; import
 * java.io.InputStreamReader; import java.io.PrintWriter; import
 * java.net.InetAddress; import java.net.ServerSocket; import java.net.Socket;
 * import java.net.UnknownHostException; import java.security.MessageDigest;
 * import android.telephony.TelephonyManager; import android.util.Log; import
 * android.database.sqlite.SQLiteDatabase;
 * 
 * import java.security.NoSuchAlgorithmException; import java.util.ArrayList;
 * import java.util.Formatter; import java.util.HashMap;
 * 
 * 
 * 
 * 
 * 
 * 
 * import android.content.ContentProvider; import android.content.ContentValues;
 * import android.content.Context; import android.database.Cursor; import
 * android.net.Uri; import android.os.AsyncTask;
 * 
 * public class SimpleDhtProvider extends ContentProvider { public static final
 * Uri CONTENT_URI =
 * Uri.parse("content://edu.buffalo.cse.cse486586.simpledht.provider/"
 * +DBCreation.TABLE_NAME); private DBCreation dn=new DBCreation(getContext());
 * 
 * String port_avd,portStr; String node_id,pos; Node n[]; String suc_pointer;
 * String pre_pointer;
 * 
 * int count=0; int i=0; ArrayList<String> nodeId= new ArrayList<String>();
 * ArrayList<String> avd=new ArrayList<String>(); static final String TAG =
 * SimpleDhtProvider.class.getSimpleName();
 * 
 * public static final int SERVER_PORT = 10000;
 * 
 * 
 * @Override public int delete(Uri uri, String selection, String[]
 * selectionArgs) { // TODO Auto-generated method stub return 0; }
 * 
 * @Override public String getType(Uri uri) { // TODO Auto-generated method stub
 * return null; }
 * 
 * @Override public Uri insert(Uri uri, ContentValues values) { // TODO
 * Auto-generated method stub
 * 
 * SQLiteDatabase db = dn.getWritableDatabase(); String key = (String)
 * values.get(DBCreation.COLUMN_KEY); String value = (String)
 * values.get(DBCreation.COLUMN_VAL); String keyhash = null; String
 * portHash=null; String sucportHash=null; String preportHash=null; String
 * successor=String.valueOf((Integer.parseInt(suc_pointer) * 2));
 * 
 * 
 * try { keyhash = genHash(key); } catch (NoSuchAlgorithmException e) {
 * e.printStackTrace(); }
 * 
 * try { portHash = genHash(portStr); } catch (NoSuchAlgorithmException e) {
 * e.printStackTrace(); } try { sucportHash = genHash(suc_pointer); } catch
 * (NoSuchAlgorithmException e) { e.printStackTrace(); } try { preportHash =
 * genHash(pre_pointer); } catch (NoSuchAlgorithmException e) {
 * e.printStackTrace(); } String regkeyhash=key+"@#"+keyhash;
 * 
 * if(!(sucportHash.compareTo(portHash)<0)||!(preportHash.compareTo(portHash)>0))
 * {
 * 
 * if(keyhash.compareTo(portHash)>0 && keyhash.compareTo(sucportHash)<=0) {
 * //Insert into content provider
 * 
 * }
 * 
 * 
 * else { Log.v(TAG,"The successor for portstr: "+portStr+"  is "+successor);
 * new
 * ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,regkeyhash,successor
 * );
 * 
 * }
 * 
 * }
 * 
 * else {
 * 
 * if((sucportHash.compareTo(portHash)<0)) { if(portHash.equals(keyhash)) {
 * //Insert into content provider
 * 
 * } else { if(keyhash.compareTo(portHash)>0 ||
 * keyhash.compareTo(sucportHash)<0) { //Insert into 1st node-id (it's
 * successor).To do this you need to call sucessor }
 * 
 * else { //call its successor node - 1stnode new
 * ClientTask().executeOnExecutor(
 * AsyncTask.SERIAL_EXECUTOR,regkeyhash,successor);
 * 
 * } }
 * 
 * }
 * 
 * else { if(preportHash.compareTo(portHash)>0) {
 * if(keyhash.compareTo(preportHash)>0 || keyhash.compareTo(portHash)<=0) {
 * //Insert into content provider of first avd
 * 
 * }
 * 
 * else { //call successor 2nd avd new
 * ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR
 * ,regkeyhash,successor);
 * 
 * } }
 * 
 * }
 * 
 * }
 * 
 * 
 * 
 * return null; }
 * 
 * @Override public boolean onCreate() { // TODO Auto-generated method stub
 * 
 * TelephonyManager tel = (TelephonyManager)
 * getContext().getSystemService(Context.TELEPHONY_SERVICE); portStr =
 * tel.getLine1Number().substring(tel.getLine1Number().length() - 4); port_avd=
 * String.valueOf((Integer.parseInt(portStr) * 2));
 * Log.v(TAG,"portStr: "+portStr);
 * 
 * 
 * try {
 * 
 * ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
 * Log.v(TAG,"Server Socket created: and call new ServerTask().execute........"
 * );
 * 
 * 
 * new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
 * serverSocket);
 * 
 * 
 * } catch (IOException e) { /* Log is a good way to debug your code. LogCat
 * prints out all the messages that Log class writes.
 * 
 * Please read
 * http://developer.android.com/tools/debugging/debugging-projects.html and
 * http://developer.android.com/tools/debugging/debugging-log.html for more
 * information on debugging.
 * 
 * Log.e(TAG, "Can't create a ServerSocket"); return false; }
 * Log.v(TAG,"Reached line 97"); if(portStr.equals("5554")) {
 * 
 * Log.v(TAG,"In portStr: expected 5554 and we got "+portStr); try {
 * node_id=genHash(portStr); nodeId.add(node_id); } catch
 * (NoSuchAlgorithmException e) { e.printStackTrace(); }
 * Log.v(TAG,"Node-Id and its avd is: "+ node_id+"  "+portStr);
 * avd.add(portStr); pre_pointer=portStr; suc_pointer=portStr; count++;
 * Log.v("count", "This is "+ portStr);
 * Log.v(TAG,"COUNT HAS TO BE ONE: "+count); }
 * 
 * 
 * 
 * else { String msg=portStr; Log.v(TAG,"PortStr: "+msg); new
 * ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg,"11108"); }
 * return true; }
 * 
 * class ServerTask extends AsyncTask<ServerSocket, String, Void> {
 * 
 * @Override protected Void doInBackground(ServerSocket... sockets) {
 * 
 * Log.v(TAG,"in doInBackground(ServerSocet....sockets"); ServerSocket
 * serverSocket = sockets[0]; Socket clientSocket; InputStreamReader
 * inputStreamReader; BufferedReader bufferedReader; String message ="";
 * 
 * if(serverSocket != null) while (true) { try {
 * 
 * Log.v(TAG,"Entered while loop of server class of port number: "+port_avd);
 * clientSocket = serverSocket.accept(); inputStreamReader = new
 * InputStreamReader(clientSocket.getInputStream()); bufferedReader = new
 * BufferedReader(inputStreamReader); //getting the client message
 * Log.v(TAG,"reached till buffered reader: --- blocking call "); message =
 * bufferedReader.readLine();
 * Log.v(TAG,"Received message: "+message+" and its count is: "+count);
 * 
 * Log.v(TAG,"We are currently in server of adv: "+port_avd);
 * if(message.contains("@#")) { String[] keyandhash= message.split("@#"); String
 * keyCP=keyandhash[0]; String hash_key=keyandhash[1]; String portHash1=null;
 * String sucportHash1=null; String preportHash1=null; String successor1=null;
 * try { portHash1 = genHash(portStr); } catch (NoSuchAlgorithmException e) {
 * e.printStackTrace(); }
 * 
 * try { sucportHash1 = genHash(suc_pointer); } catch (NoSuchAlgorithmException
 * e) { e.printStackTrace(); }
 * 
 * try { preportHash1 = genHash(pre_pointer); } catch (NoSuchAlgorithmException
 * e) { e.printStackTrace(); }
 * 
 * if(!(sucportHash1.compareTo(portHash1)<0)||!(preportHash1.compareTo(portHash1)
 * >0)) {
 * 
 * if(hash_key.compareTo(portHash1)>0 && hash_key.compareTo(sucportHash1)<=0) {
 * //Insert into content provider
 * 
 * }
 * 
 * 
 * else { successor1=String.valueOf((Integer.parseInt(suc_pointer) * 2));
 * Log.v(TAG,"The successor for portstr: "+portStr+"  is "+successor1); //
 * String regkeyhash=message; new
 * ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,message,successor1);
 * 
 * }
 * 
 * }
 * 
 * else {
 * 
 * if((sucportHash1.compareTo(portHash1)<0)) { if(portHash1.equals(hash_key)) {
 * //Insert into content provider
 * 
 * } else { if(hash_key.compareTo(portHash1)>0 ||
 * hash_key.compareTo(sucportHash1)<0) { //Insert into 1st node-id (it's
 * successor).To do this you need to call sucessor }
 * 
 * else { //call its successor node - 1stnode new
 * ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,message,successor1);
 * 
 * } }
 * 
 * }
 * 
 * else { if(preportHash1.compareTo(portHash1)>0) {
 * if(hash_key.compareTo(preportHash1)>0 || hash_key.compareTo(portHash1)<=0) {
 * //Insert into content provider of first avd
 * 
 * }
 * 
 * else { //call successor 2nd avd new
 * ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,message,successor1);
 * 
 * } }
 * 
 * }
 * 
 * }
 * 
 * 
 * 
 * 
 * }
 * 
 * 
 * 
 * else if(message.contains(":")) {
 * Log.v(TAG,"Message with $$: "+message+" PORTSTR: "+port_avd); String
 * pre_suc[]= message.split(":"); Log.v(TAG,"pre_suc: "+pre_suc[0]); if(
 * !pre_suc[0].equals("N/A")) { pre_pointer=pre_suc[0];
 * Log.v(TAG,"Predessor pointer: "+pre_pointer+" for avd  "+port_avd); } if(
 * pre_suc[0].equals("N/A"))
 * Log.v(TAG,"Predessor pointer: "+pre_pointer+" for avd  "+port_avd);
 * 
 * if(!pre_suc[1].equals("N/A")) { suc_pointer=pre_suc[1];
 * Log.v(TAG,"Successor Pointer: "+suc_pointer+" for avd  "+port_avd); }
 * if(pre_suc[1].equals("N/A"))
 * Log.v(TAG,"Successor Pointer: "+suc_pointer+" for avd  "+port_avd);
 * 
 * } // The message contains portStr else if(!message.contains(":")&& count==1)
 * { try { node_id=genHash(message); } catch (NoSuchAlgorithmException e) { //
 * TODO Auto-generated catch block e.printStackTrace(); }
 * Log.v(TAG,"port number: "+message+"  and it's node_id is: "+node_id);
 * if(nodeId.get(0).compareTo(node_id)>0) {
 * Log.v(TAG,"nodeId of avd0 greater than other node"); nodeId.add(0,node_id);
 * avd.add(0,message); pre_pointer=avd.get(0); suc_pointer=avd.get(0);
 * Log.v(TAG,"Successor and predessor of avdo is: "+avd.get(0)); String
 * values="5554:5554";//pred$$succ count++;
 * Log.v(TAG,"COUNT VALUE IN LINE 189: "+count); String s11=
 * String.valueOf((Integer.parseInt(avd.get(0)) * 2));
 * 
 * new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,values ,s11);
 * 
 * }
 * 
 * 
 * else {
 * 
 * Log.v(TAG,"Other avd is greater than avd0") ; nodeId.add(node_id);
 * avd.add(message); pre_pointer=avd.get(1); suc_pointer=avd.get(1); String
 * values="5554:5554";//pred$$succ count++;
 * Log.v(TAG,"COUNT VALUE IN 206: "+count); String s22=
 * String.valueOf((Integer.parseInt(avd.get(1)) * 2));
 * 
 * new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,values ,s22);
 * 
 * 
 * }
 * 
 * }
 * 
 * else { if(!message.contains(":")&& count>1) {
 * Log.v(TAG,"count: in final else block "+count+" and message is: "+message);
 * try { node_id=genHash(message); } catch (NoSuchAlgorithmException e) { //
 * TODO Auto-generated catch block e.printStackTrace(); } boolean flag=false;
 * int j=0;
 * Log.v(TAG,"Before entering for loop size of node array is: "+nodeId.size());
 * for( j=0;j<nodeId.size();j++) {
 * Log.v(TAG,"Inside for loop: with j value: "+j);
 * if(node_id.compareTo(nodeId.get(j))<0) {
 * Log.v(TAG,"Inside if condition our avd node  is less that elements in arraylist"
 * );
 * 
 * nodeId.add(j, node_id); avd.add(j, message); flag=true; break; } }
 * if(flag==false) { nodeId.add(node_id); avd.add(message);
 * Log.v(TAG," In line 255 with j value: "
 * +j+" and size of array: "+nodeId.size()); } if(j!=0 && j!=(nodeId.size()-1))
 * { Log.v(TAG,"Inside line 259: with j value: "+j); String
 * s1=String.valueOf((Integer.parseInt(avd.get(j)) * 2)); String
 * s2=String.valueOf((Integer.parseInt(avd.get(j-1)) * 2)); String
 * s3=String.valueOf((Integer.parseInt(avd.get(j+1)) * 2));
 * 
 * 
 * new
 * ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,avd.get(j-1)+":"+
 * avd.get(j+1) ,s1); new
 * ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,"N/A"+":"+avd.get(j)
 * ,s2); new
 * ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,avd.get(j)+":"+"N/A"
 * ,s3);
 * 
 * } else { if(j==0) { Log.v(TAG,"Inside line 275: with j value: "+j);
 * 
 * String s4=String.valueOf((Integer.parseInt(avd.get(j)) * 2)); String
 * s5=String.valueOf((Integer.parseInt(avd.get(nodeId.size()-1)) * 2)); String
 * s6=String.valueOf((Integer.parseInt(avd.get(j+1)) * 2));
 * 
 * new
 * ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,avd.get(nodeId.size
 * ()-1)+":"+avd.get(j+1) ,s4); new
 * ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,"N/A"+":"+avd.get(0)
 * ,s5); new
 * ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,avd.get(j-1
 * )+":"+"N/A" ,s6);
 * 
 * }
 * 
 * if(j==nodeId.size()-1) {
 * Log.v(TAG,"Inside line 288: with j value: and portstr:"+j+ "  "+port_avd);
 * 
 * String s7=String.valueOf((Integer.parseInt(avd.get(j)) * 2));
 * 
 * String s8=String.valueOf((Integer.parseInt(avd.get(j-1)) * 2));
 * Log.v(TAG,"s8: "+s8); String s9=String.valueOf((Integer.parseInt(avd.get(0))
 * * 2));
 * 
 * new
 * ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,avd.get(j-1)+":"+
 * avd.get(0) ,s7);
 * 
 * // if(!avd.get(j-1).equals("5554")) // { new
 * ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,"N/A"+":"+avd.get(j)
 * ,s8); // }
 * 
 * new
 * ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,avd.get(j)+":"+"N/A"
 * ,s9);
 * 
 * 
 * }
 * 
 * }
 * 
 * }
 * 
 * 
 * 
 * 
 * 
 * }
 * 
 * 
 * 
 * 
 * 
 * 
 * inputStreamReader.close(); clientSocket.close();
 * 
 * } catch (IOException ex) { System.out.println("Error in reading message"); }
 * }
 * 
 * return null; }
 * 
 * 
 * }
 * 
 * class ClientTask extends AsyncTask<String, Void, Void> { private PrintWriter
 * printwriter;
 * 
 * @Override protected Void doInBackground(String... msgs) { try {
 * 
 * // String msgToSend; // Socket socket;
 * 
 * //Log.v(TAG,"COUNT: "+count);
 * Log.v(TAG,"msgs to send: "+msgs[0]+" msgs[1]: "+msgs[1]);
 * 
 * 
 * // String remotePort=msgs[1]; // Log.v(TAG,
 * "now about to create the socket remote port is: "+remotePort); Socket socket
 * = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
 * Integer.parseInt(msgs[1])); String msgToSend=msgs[0];
 * Log.v(TAG,"Message sending to client: "+ msgToSend);
 * 
 * printwriter = new PrintWriter(socket.getOutputStream(),true);
 * printwriter.write(msgToSend); printwriter.flush(); printwriter.close();
 * 
 * 
 * socket.close();
 * 
 * 
 * 
 * 
 * } catch (UnknownHostException e) { Log.e(TAG,
 * "ClientTask UnknownHostException: "+e); } catch (IOException e) {
 * System.out.println(); Log.e(TAG, "ClientTask socket IOException :"+e); }
 * 
 * return null; } }
 * 
 * @Override public Cursor query(Uri uri, String[] projection, String selection,
 * String[] selectionArgs, String sortOrder) { // TODO Auto-generated method
 * stub return null; }
 * 
 * @Override public int update(Uri uri, ContentValues values, String selection,
 * String[] selectionArgs) { // TODO Auto-generated method stub return 0; }
 * 
 * private String genHash(String input) throws NoSuchAlgorithmException {
 * MessageDigest sha1 = MessageDigest.getInstance("SHA-1"); byte[] sha1Hash =
 * sha1.digest(input.getBytes()); Formatter formatter = new Formatter(); for
 * (byte b : sha1Hash) { formatter.format("%02x", b); } return
 * formatter.toString(); }
 * 
 * 
 * }
 */