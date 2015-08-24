package edu.buffalo.cse.cse486586.simpledht;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SimpleDhtActivity extends Activity {

	private Button LDump;
	private Button GDump;
	//private Button Test;
	Cursor resultCursor=null;
	SimpleDhtProvider s=new SimpleDhtProvider(); 	
	public static final Uri CONTENT_URI = Uri.parse("content://edu.buffalo.cse.cse486586.simpledht.provider");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_dht_main);
       // Test=(Button) findViewById(R.id.button3);

        
        final TextView tv = (TextView) findViewById(R.id.textView1);       
        LDump=(Button) findViewById(R.id.button1);
        GDump=(Button) findViewById(R.id.button2);   
        tv.setMovementMethod(new ScrollingMovementMethod());
        
        findViewById(R.id.button3).setOnClickListener(
                new OnTestClickListener(tv, getContentResolver()));
        
        LDump.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {        	
        		resultCursor = getContentResolver().query(SimpleDhtProvider.CONTENT_URI,null,null,null,null);
        		tv.setText("");
        		int index_value = resultCursor.getColumnIndex(DBCreation.COLUMN_VAL);
        		int index_key = resultCursor.getColumnIndex(DBCreation.COLUMN_KEY);
        		if(resultCursor==null) {
        			tv.append("\n No values in AVD");
        		}
        		else if(resultCursor!=null) {
        			while(resultCursor.moveToNext()) {        				
        				tv.append("\n"+resultCursor.getString(index_key)+" "+resultCursor.getString(index_value));
        			}
        		}
        		resultCursor.close();
        	}
        	
        }); 
        
        GDump.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) { 
        		tv.setText("");
        		try {        		
        			for(int i=0;i<50;i++) {
        				String key= "key"+Integer.toString(i);
        				resultCursor = getContentResolver().query(SimpleDhtProvider.CONTENT_URI, null,key, null, null);
        				resultCursor.moveToFirst();        					
        				int keyIndex = resultCursor.getColumnIndex(DBCreation.COLUMN_KEY);
        				int valueIndex = resultCursor.getColumnIndex(DBCreation.COLUMN_VAL);					    					  	
        				String returnKey = resultCursor.getString(keyIndex);
        				String returnValue = resultCursor.getString(valueIndex);
        				tv.append("\n"+returnKey+" "+returnValue);  	        			      		     			     			        
        				resultCursor.close();
        			}        			
        		}catch(Exception e) {
        			e.printStackTrace();
        		}
        	}
        });
        
       
        
       
    }
    private Uri buildUri(String scheme, String authority) {
        // TODO Auto-generated method stub
       
         Uri.Builder uriBuilder = new Uri.Builder();
         uriBuilder.authority(authority);
         uriBuilder.scheme(scheme);
         return uriBuilder.build();
        //return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_simple_dht_main, menu);
        return true;
    }

}
