package com.example.shareholders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class News extends Activity {
	
	PublicVariable PV;
	Typeface FontMitra;
	InternetConnection IC;
	
	TextView btnPageTitle;	
	ListView lst_data;
	DatabaseHelper dbh;
	SQLiteDatabase db;
	ListAdapter adapter;
	
	String PersonGuid;
	String BackActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        try
        {
        	BackActivity = "Empty";
        	PersonGuid = getIntent().getStringExtra("PGuid").toString();
        }
        catch (Exception e) {
        	PersonGuid = "0000-0000-0000-0000";
        	try{BackActivity = getIntent().getStringExtra("BackActivity").toString();}catch (Exception e1) {e1.printStackTrace();}
		}
        
        overridePendingTransition(R.drawable.activity_open_translate,R.drawable.activity_close_scale);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				 WindowManager.LayoutParams.FLAG_FULLSCREEN);
        PV = new PublicVariable();
        FontMitra = Typeface.createFromAsset(getAssets(), "font/BMitra.ttf");
        IC = new InternetConnection(getApplicationContext());
        
        btnPageTitle = (TextView)findViewById(R.id.newspagetitle);
        
        btnPageTitle.setTypeface(FontMitra);
        
        lst_data=(ListView)findViewById(R.id.listView1);
		dbh=new DatabaseHelper(getApplicationContext());
		try {

			dbh.createDataBase();

   		} catch (IOException ioe) {

   			throw new Error("Unable to create database");

   		}

   		try {

   			dbh.openDataBase();

   		} catch (SQLException sqle) {

   			throw sqle;

   		}
        try
        {
        	FillData();
        }
        catch (Exception e) {
        	Toast.makeText(getApplicationContext(), PersianReshape.reshape("اطلاعاتی موجود نمی باشد ، لطفا جهت دریافت اطلاعات از منوی اصلی گزینه بروزرسانی را انتخاب کنید"), Toast.LENGTH_LONG).show();
		}
        
    }
    
    
    private void FillData()
	{
    	
    	String title,sDate,id,pic,dicription;
		
		ArrayList<HashMap<String, String>> DataLList;
		DataLList = new ArrayList<HashMap<String, String>>();
		
		db = dbh.getReadableDatabase();
		Cursor cursors = db.rawQuery("select id,title,sdate,description,pic from news order by sDate desc", null);

		if(cursors.getCount() > 0)
		{
			
			for (int i = 0; i < cursors.getCount(); i++) {
				cursors.moveToNext();
				HashMap<String, String> map = new HashMap<String, String>();
				
				id = cursors.getString(cursors.getColumnIndex("id"));
				title = cursors.getString(cursors.getColumnIndex("title"));
				sDate = cursors.getString(cursors.getColumnIndex("sDate"));
				dicription = cursors.getString(cursors.getColumnIndex("description"));
				pic = cursors.getString(cursors.getColumnIndex("pic"));
				map.put("id", id);
				map.put("title", title);
				map.put("sDate", sDate);
				map.put("pic", GetNewsPicFromTempTbl(pic));
				map.put("pic2", GetNewsPicFromTempTbl(pic));
				map.put("dicription", dicription);
				map.put("PGuid", PersonGuid);
				DataLList.add(map);
	
			}
			
			adapter = new NewsAdapter(this, DataLList);
			lst_data.setAdapter(adapter);
		}
		else
		{
			Toast.makeText(getApplicationContext(), PersianReshape.reshape("اطلاعاتی موجود نمی باشد ، لطفا جهت دریافت اطلاعات از منوی اصلی گزینه بروزرسانی را انتخاب کنید"), Toast.LENGTH_LONG).show();
		}

	}
    
    public String GetNewsPicFromTempTbl(String NewsId)
	{
    	String Res = "0";
		db = dbh.getReadableDatabase();
		Cursor cursors = db.rawQuery("select * from picgallery where picCode = "+NewsId, null);
		if(cursors.getCount() > 0)
		{
			cursors.moveToNext();
			Res = cursors.getString(cursors.getColumnIndex("pic"));
		}
		return Res;
	}
    
    
    @Override
	  protected void onPause()
	  {
	    super.onPause();
	    //closing transition animations
	    overridePendingTransition(R.drawable.activity_open_scale,R.drawable.activity_close_translate);
	  }

	public void LoadActivity(Class<?> Cls,String VariableName,String VariableValue)
	{
		Intent intent = new Intent(this,Cls);
		intent.putExtra(VariableName, VariableValue);
		startActivity(intent);
		this.finish();
	}
	
	private void ExitApplication()
	{
		//Exit All Activity And Kill Application
		 Builder alertbox = new AlertDialog.Builder(News.this);
	       // set the message to display
	       alertbox.setMessage("آیا می خواهید از برنامه خارج شوید ؟");
	       
	       // set a negative/no button and create a listener
	       alertbox.setPositiveButton("خیر", new DialogInterface.OnClickListener() {
	           // do something when the button is clicked
	           public void onClick(DialogInterface arg0, int arg1) {
	               arg0.dismiss();
	           }
	       });
	       
	       // set a positive/yes button and create a listener
	       alertbox.setNegativeButton("بله", new DialogInterface.OnClickListener() {
	           // do something when the button is clicked
	           public void onClick(DialogInterface arg0, int arg1) {
	        	   //Declare Object From Get Internet Connection Status For Check Internet Status
	        	  System.exit(0);
	   			  arg0.dismiss();
	   			  
	           }
	       });
	      
	       alertbox.show();
	}
	
	@Override
	public boolean onKeyDown( int keyCode, KeyEvent event )  {
	    if ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
	    	if(BackActivity.compareTo("Guest")==0)
	    	{
	    		LoadActivity(AppMenuGuest.class, "PGuid", PersonGuid);
	    	}
	    	else
	    	{
	    		LoadActivity(AppMenu.class, "PGuid", PersonGuid);
	    	}
	        return true;
	    }

	    return super.onKeyDown( keyCode, event );
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.MnuAppMenu:
	        	if(BackActivity.compareTo("Guest")==0)
		    	{
		    		LoadActivity(AppMenuGuest.class, "PGuid", PersonGuid);
		    	}
		    	else
		    	{
		    		LoadActivity(AppMenu.class, "PGuid", PersonGuid);
		    	}
	            return true;
	        case R.id.MnuAboutOwner:
	        	LoadActivity(AboutOwner.class,"PGuid",PersonGuid);
	            return true;
	        case R.id.MnuExitApp:
	        	ExitApplication();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}
