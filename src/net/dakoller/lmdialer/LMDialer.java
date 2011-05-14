package net.dakoller.lmdialer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LMDialer extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
              
    	ListView meetingListView = (ListView)findViewById(R.id.meetingListView);
    	
        new CollectLiveMeetingsTask().execute(meetingListView);
        
    }
    
    private final static String BASE_CALENDAR_URI_PRE_2_2 = "content://calendar";
    private final static String BASE_CALENDAR_URI_2_2 = "content://com.android.calendar";
    /*
     * Determines if we need to use a pre 2.2 calendar Uri, or a 2.2 calendar Uri, and returns the base Uri
     */
    private String getCalendarUriBase() {
        Uri calendars = Uri.parse(BASE_CALENDAR_URI_PRE_2_2 + "/calendars");
        try {
            Cursor managedCursor = managedQuery(calendars, null, null, null, null);
            if (managedCursor != null) {
                return BASE_CALENDAR_URI_PRE_2_2;
            }
            else {
                calendars = Uri.parse(BASE_CALENDAR_URI_2_2 + "/calendars");
                managedCursor = managedQuery(calendars, null, null, null, null);

                if (managedCursor != null) {
                    return BASE_CALENDAR_URI_2_2;
                }
            }
        } catch (Exception e) { /* eat any exceptions */ }

        return null; // No working calendar URI found
    }
    
    public static boolean isToday(Date date) {
    	Calendar today = Calendar.getInstance();
    	today.setTime(new Date());
    	Calendar otherday = Calendar.getInstance();
    	otherday.setTime(date);
    	return otherday.get(Calendar.YEAR) == today.get(Calendar.YEAR)
    	&& otherday.get(Calendar.MONTH) == today.get(Calendar.MONTH)
    	&& otherday.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);
    	}
    
    private class CollectLiveMeetingsTask extends AsyncTask<ListView, Void, Void> {
        
        /** The system calls this to perform work in the UI thread and delivers
          * the result from doInBackground() */
        protected void onPostExecute(Void result) {
        	super.onPostExecute(result);
        }
        
        private String getCalendarUriBase() {
            Uri calendars = Uri.parse(BASE_CALENDAR_URI_PRE_2_2 + "/calendars");
            try {
                Cursor managedCursor = managedQuery(calendars, null, null, null, null);
                if (managedCursor != null) {
                    return BASE_CALENDAR_URI_PRE_2_2;
                }
                else {
                    calendars = Uri.parse(BASE_CALENDAR_URI_2_2 + "/calendars");
                    managedCursor = managedQuery(calendars, null, null, null, null);

                    if (managedCursor != null) {
                        return BASE_CALENDAR_URI_2_2;
                    }
                }
            } catch (Exception e) { /* eat any exceptions */ }

            return null; // No working calendar URI found
        }

		protected void doInBackground(ListView... params) {
			 
            final ArrayList<String> liveMeetings= new ArrayList<String>();
            Uri eventsUri = Uri.parse(this.getCalendarUriBase() +   "/events");
            String[] dateQuery = new String[] { "2011-05-04"};
            Cursor cursor = getContentResolver().query(eventsUri, null, null, null, "DTSTART DESC");
          
//            for (int i= 0; i< (cursor.getColumnCount()-1); i++) {
//            	liveMeetings.add(cursor.getColumnName(i));
//            }
            //startManagingCursor(cursor);          
            int i=0;
            String[] result= new String[cursor.getCount()];
            if (cursor.moveToFirst()) {
            	do {
            		String eventTitle = cursor.getString(cursor.getColumnIndex("title"));
            	    //Date eventStart = new Date(cursor.getLong(cursor.getColumnIndex("dtstart")));
            	    Date eventStart = new Date(cursor.getLong(cursor.getColumnIndex("dtstart")));
            	    String eventLocation = cursor.getString(cursor.getColumnIndex("eventLocation"));
            	    String eventDescription = cursor.getString(cursor.getColumnIndex("description"));
            	    
//            	    boolean livemeeting = eventLocation.matches("Meeting");
//            	    if (this.isToday(eventStart) ) {
//            	    	liveMeetings.add(eventTitle + ": " +  (new String()).valueOf(livemeeting));
            	    	liveMeetings.add(eventTitle + ": " +  eventLocation + ", " + eventStart.toGMTString());
            	    
            	    	i++;
//            	    }    
            	
            	} while (cursor.moveToNext() && (i < 35) );
            } else {
            	liveMeetings.add("no entries");
            }
            	
            //stopManagingCursor(cursor);*/
            final ArrayAdapter<String> aa;
            aa= new ArrayAdapter<String>(LMDialer, LMDialer.android.R.layout.simple_list_item_1,liveMeetings);        
        	ListView meetingListView = params[0];
        	meetingListView.setAdapter(aa);
            
		}

        
        
    }
    
    
}