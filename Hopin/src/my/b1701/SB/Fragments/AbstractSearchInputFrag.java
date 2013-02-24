package my.b1701.SB.Fragments;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import my.b1701.SB.R;
import my.b1701.SB.ActivityHandlers.MapListActivityHandler;
import my.b1701.SB.Adapter.HistoryAdapter;
import my.b1701.SB.HelperClasses.ProgressHandler;
import my.b1701.SB.HelperClasses.ToastTracker;
import my.b1701.SB.HttpClient.AddThisUserScrDstCarPoolRequest;
import my.b1701.SB.HttpClient.AddThisUserSrcDstRequest;
import my.b1701.SB.HttpClient.SBHttpClient;
import my.b1701.SB.HttpClient.SBHttpRequest;
import my.b1701.SB.LocationHelpers.SBGeoPoint;
import my.b1701.SB.Users.ThisUserNew;
import my.b1701.SB.Util.StringUtils;
import my.b1701.SB.provider.HistoryContentProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.analytics.tracking.android.EasyTracker;

public abstract class AbstractSearchInputFrag extends Fragment{
	
	private static final String TAG = "my.b1701.SB.Activities.AbstractSearchInputFragment";
	private static final int MAX_HISTORY_COUNT = 10;
	private static Uri mHistoryUri = Uri.parse("content://" + HistoryContentProvider.AUTHORITY + "/history");
	private static final int MAX_TRIES = 5;
	private static final String GOOGLE_PLACES_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json";
	private static final String API_KEY = "AIzaSyAbahSqDp47FsP_U60bwXdknL_cAUgalrw";

	    private static String[] columns = new String[]{ "sourceLocation",
	            "destinationLocation",
	            "timeOfTravel",
	            "dailyInstantType",
	            "planInstantType",
	            "takeOffer",
	            "dateOfTravel",
	            "reqDate",	            
	            "date"
	    };
	 
	PlacesAutoCompleteAdapter placesAutoCompleteAdapter = null;
	
		
	String time = "";
	String date = "";
	Button cancelFindUsers = null;
	Button takeRideButton = null;
	Button offerRideButton = null;
	AutoCompleteTextView source = null;
	AutoCompleteTextView destination = null;	
	boolean takeRide = false;
	boolean destinationSet = false;
	boolean sourceSet = false;
	//0 daily pool,1 instant share
	//0 take ,1 offer
	
		
	public abstract SBGeoPoint getSourceGeopoint();
	public abstract SBGeoPoint getDestinationGeopoint();
	public abstract String getSource();
	public abstract String getDestination();
	public abstract String getDate();
	public abstract String getTime();
	public abstract int getDailyInstaType(); //required to decide which api to call, daily Vs (today,tomorrow,in 15min, enter date) 
	public abstract int getPlanInstaTabType(); // means sent from plan tab,insta tab and is used to store history
	
	@Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        placesAutoCompleteAdapter = new PlacesAutoCompleteAdapter(getActivity().getApplicationContext(), R.layout.address_suggestion_layout);        
        cancelFindUsers.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				EasyTracker.getTracker().sendEvent("ui_action", "button_press", "cancelFindUsers_button", 1L);
				getActivity().finish();				
			}
		});
        
        takeRideButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EasyTracker.getTracker().sendEvent("ui_action", "button_press", "takeRide_button", 1L);
				takeRide = true;
				findUsers();			
			}
		});
        offerRideButton.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(View v) {
                EasyTracker.getTracker().sendEvent("ui_action", "button_press", "offerRide_button", 1L);
                takeRide = false;
                findUsers();
			}
		});
        
        if(source!= null)
        {
	        source.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
	            	 EasyTracker.getTracker().sendEvent("ui_action", "autocomplete_text", "setSource", 1L);
	                 String sourceAddress =(String) adapterView.getItemAtPosition(i);
	                 if(!StringUtils.isBlank(sourceAddress))
	                 {
		                 hideSoftKeyboard();
		                 source.setSelection(0);
		                 source.clearFocus();
		                 sourceSet = true;
	                } 
	                 else 
	                {
	                    source.setText("");
	                    showErrorDialog("Failed to get Source address", "Please try again...");
	                }
	            }
	        
	        });
	        
	        source.setAdapter(placesAutoCompleteAdapter);
        }
        destination.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            	 EasyTracker.getTracker().sendEvent("ui_action", "autocomplete_text", "setSource", 1L);
                 String destinationAddress =(String) adapterView.getItemAtPosition(i);
                 if(!StringUtils.isBlank(destinationAddress))
                 {
	                 hideSoftKeyboard();
	                 destination.setSelection(0);
	                 destination.clearFocus();
	                 destinationSet = true;
                } else 
                {
                	destination.setText("");
                    showErrorDialog("Failed to get destination address", "Please try again...");
                }
            }
        
        });
        
        destination.setAdapter(placesAutoCompleteAdapter);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       return  super.onCreateView(inflater, container, savedInstanceState);
    }

	
	public void findUsers()
	{ 
		if(!destinationSet)
		{
			ToastTracker.showToast("Please set destination");
			return;
		}
		getActivity().finish();
		
		//here in abstract type we are setting all..its responsibility of individual class to 
		// implement these methods and return "" is its not required
		//we use ThisUSer as intermediate storage place of current state of req of this user
		//anything map or list which needs to update picks from ThisUSer
		// we are setting any of source desti address,lat longi
		ThisUserNew.getInstance().setSourceGeoPoint(getSourceGeopoint());  //this could be null
		ThisUserNew.getInstance().setDestinationGeoPoint(getDestinationGeopoint());  //this could be null
		ThisUserNew.getInstance().setSourceFullAddress(getSource());
		ThisUserNew.getInstance().setDestinationFullAddress(getDestination());
		ThisUserNew.getInstance().setTimeOfTravelt(getTime());
		ThisUserNew.getInstance().setDateOfTravel(getDate());
		ThisUserNew.getInstance().set_Daily_Instant_Type(getDailyInstaType());//0 daily pool,1 instant share
		ThisUserNew.getInstance().set_Take_Offer_Type(takeRide?0:1);//0 take ,1 offer
		MapListActivityHandler.getInstance().updateSrcDstTimeInListView();
				
		Log.i(TAG, "user destination set... querying server");
		ProgressHandler.showInfiniteProgressDialoge(MapListActivityHandler.getInstance().getUnderlyingActivity(), "Fetching users", "Please wait..");
		SBHttpRequest addThisUserSrcDstRequest;
		if(getDailyInstaType() == 0)        		
			addThisUserSrcDstRequest = new AddThisUserScrDstCarPoolRequest();        		
		else
			addThisUserSrcDstRequest = new AddThisUserSrcDstRequest();        		
         
        SBHttpClient.getInstance().executeRequest(addThisUserSrcDstRequest);
        saveSearch();
        
        //moveTaskToBack(true);			
	}
	
	 private void showErrorDialog(String title, String message) {
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle(title).setMessage(message);
	        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialogInterface, int i) {
	                return;
	            }
	        });
	        builder.create().show();
	    }
	   
	    
	    @Override
	    public void onStart(){
	        super.onStart();
	        EasyTracker.getInstance().activityStart(getActivity());
	    }

	    @Override
	    public void onStop(){
	        super.onStop();
	        EasyTracker.getInstance().activityStop(getActivity());
	    }
	
	public void hideSoftKeyboard() {
	    InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
	}
	
    private void saveSearch() {

        new Thread("saveSearch") {
            @Override
            public void run() {
                saveHistoryBlocking();
            }
        }.start();
    }

   
	private void saveHistoryBlocking() {        
        ContentResolver cr = getActivity().getContentResolver();       
        ThisUserNew thisUser = ThisUserNew.getInstance();

        // Use content resolver (not cursor) to insert/update this query
        try {
            ContentValues values = new ContentValues();
            values.put(columns[0], thisUser.getSourceFullAddress());
            values.put(columns[1], thisUser.getDestinationFullAddress());
            values.put(columns[2], thisUser.getTimeOfTravel() ); //24 hr
            values.put(columns[3], thisUser.get_Daily_Instant_Type());
            values.put(columns[4], thisUser.get_Plan_Instant_Type());
            values.put(columns[5], thisUser.get_Take_Offer_Type());
            values.put(columns[6], thisUser.getDateOfTravel()); // yyyy-mm--MM-dd
            values.put(columns[7], StringUtils.gettodayDateInFormat("d MMM"));           
            values.put(columns[8], System.currentTimeMillis());
            cr.insert(mHistoryUri, values);
        } catch (RuntimeException e) {
            Log.e(TAG, "saveHistoryQueryerror", e);
        }

        // Shorten the list (if it has become too long)
        truncateHistory(cr, MAX_HISTORY_COUNT);

        //update the inmemory cache
        HistoryAdapter.HistoryItem historyItem = new HistoryAdapter.HistoryItem(thisUser.getSourceFullAddress(),
                thisUser.getDestinationFullAddress(),
                thisUser.getTimeOfTravel(),
                thisUser.get_Daily_Instant_Type(),
                thisUser.get_Plan_Instant_Type(),
                thisUser.get_Take_Offer_Type(),
                thisUser.getDateOfTravel(),
                StringUtils.gettodayDateInFormat("d MMM")
               );

        addHistoryToMemory(historyItem);
        
    }

    private void addHistoryToMemory(HistoryAdapter.HistoryItem historyItem) {
        List<HistoryAdapter.HistoryItem> historyItemList = ThisUserNew.getInstance().getHistoryItemList();
        if (historyItemList != null) {
            historyItemList.add(0, historyItem);
            if (historyItemList.size() > MAX_HISTORY_COUNT) {
                for (int i = historyItemList.size() - 1; i >= MAX_HISTORY_COUNT; i--) {
                    historyItemList.remove(i);
                }
            }
        }
    }

    private void truncateHistory(ContentResolver cr, int maxEntries) {
        if (maxEntries < 0) {
            throw new IllegalArgumentException();
        }

        try {
            // null means "delete all".  otherwise "delete but leave n newest"
            String selection = null;
            if (maxEntries > 0) {
                selection = "_id IN " +
                        "(SELECT _id FROM history" +
                        " ORDER BY date DESC" +
                        " LIMIT -1 OFFSET " + String.valueOf(maxEntries) + ")";
            }
            cr.delete(mHistoryUri, selection, null);
        } catch (RuntimeException e) {
            Log.e(TAG, "truncateHistory", e);
        }
    }
    
    /**************************************************************?
     * below all is code for auto complete textview
     * @param input
     * @return
     */
    
    private ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(GOOGLE_PLACES_URL);
            sb.append("?sensor=false&key=" + API_KEY);
            sb.append("&components=country:in");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }
    
    class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    }
                    else {
                        notifyDataSetInvalidated();
                    }
                }};
            return filter;
        }
    }

}
