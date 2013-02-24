package my.b1701.SB.Adapter;

import java.util.List;

import my.b1701.SB.R;
import my.b1701.SB.Activities.SearchInputActivityNew;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HistoryAdapter extends BaseAdapter{

    private List<HistoryItem> historyItemList;
    private LayoutInflater inflater;
    private Activity underlyingActiviy = null;

    public HistoryAdapter(Activity activity, List<HistoryItem> historyItemList){
        this.historyItemList = historyItemList;
        this.inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.underlyingActiviy = activity;
    }

    @Override
    public int getCount() {
        return historyItemList.size();
    }

    @Override
    public HistoryItem getItem(int i) {
        return historyItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

   
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final HistoryItem historyItem = historyItemList.get(position);
        View view=convertView;
        if(convertView==null) {
            view = inflater.inflate(R.layout.history_list_row, null);
        }
                      
        TextView source = (TextView)view.findViewById(R.id.history_source);
        TextView destination = (TextView)view.findViewById(R.id.history_destination);        
        TextView details = (TextView)view.findViewById(R.id.history_details);     
        ImageView edit_button = (ImageView)view.findViewById(R.id.history_editbutton);
        TextView reqDateView = (TextView)view.findViewById(R.id.history_req_date);
        final String sourceStr = historyItem.getSourceLocation();
        final String dstStr = historyItem.getDestinationLocation();
        String time = historyItem.getTimeOfRequest();//HH:mm 24hr
        int type = historyItem.getPlanInstantType();//plan 0,insta 1
        String dateOfTravel = historyItem.getDateOfTravel(); //yyyy-MM-dd 
        String reqdate = historyItem.getReqDate();
        source.setText(sourceStr);
        destination.setText(dstStr);
        details.setText(dateOfTravel + ","+time);
        reqDateView.setText(reqdate);
        edit_button.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View chatIconView) {								
				Intent i = new Intent(underlyingActiviy,SearchInputActivityNew.class);	
				i.putExtra("source", sourceStr);
				i.putExtra("destination", dstStr);
				i.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
				underlyingActiviy.startActivity(i);
			}
		});
        
	    return view;
    }

    public static class HistoryItem {
        String sourceLocation;
        String destinationLocation;
        String timeOfRequest;
        Integer dailyInstantType;
        Integer planInstantType;
        Integer takeOffer;
        String dateOfTravel;
        String reqDate;       

        public HistoryItem(String sourceLocation, String destinationLocation, String timeOfRequest, 
        		           int dailyInstantType,int planInstantType, int takeOffer, String dateOfTravel,String reqdate
        		           ) {
            this.sourceLocation = sourceLocation;
            this.destinationLocation = destinationLocation;
            this.timeOfRequest = timeOfRequest;
            this.planInstantType = planInstantType;
            this.dailyInstantType = dailyInstantType;
            this.takeOffer = takeOffer;
            this.dateOfTravel = dateOfTravel;
            this.reqDate = reqdate;
            
        }

        public String getSourceLocation() {
            return sourceLocation;
        }

        public String getDestinationLocation() {
            return destinationLocation;
        }

        /**
         * 12 hr format
         * @return
         */
        public String getTimeOfRequest() {
            return timeOfRequest;
        }

        public int getPlanInstantType() {
            return planInstantType;
        }

        public Integer getDailyInstantType() {
			return dailyInstantType;
		}

		public void setDailyInstantType(Integer dailyInstantType) {
			this.dailyInstantType = dailyInstantType;
		}

		public int getTakeOffer() {
            return takeOffer;
        }

        public String getDateOfTravel() {
            return dateOfTravel;
        }
        
        /**
         * it of format d MMM,ie.  14 Feb
         * @return
         */
        public String getReqDate() {
            return reqDate;
        }
		
    }
}

