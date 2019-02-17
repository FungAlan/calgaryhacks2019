package com.calgaryhacks.calvin.hackathon2019;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder>  implements Serializable {

    private ArrayList<Report> report_list;
    private ArrayList<Double> distance_list;
    private Context context;
    private DatabaseReference base_database_reference = FirebaseDatabase.getInstance().getReference();
    private double my_lat;
    private double my_lon;
    private static DecimalFormat df3 = new DecimalFormat(".###");

    public ReportAdapter(){}

    public ReportAdapter(Context con){
        this.context = con;
        this.report_list = new ArrayList<Report>();
        this.distance_list = new ArrayList<Double>();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.report_row, viewGroup, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int i) {
        final Report current = report_list.get(i);

        //set the row's icon
        switch(current.getType()){
            case "icy_roads": holder.type_icon.setImageResource(R.drawable.ice); holder.info1.setText("Icy Roads");break;
            case "road_construction": holder.type_icon.setImageResource(R.drawable.ic_build_black_24dp); holder.info1.setText("Road Construction");break;
            case "car_accident": holder.type_icon.setImageResource(R.drawable.car); holder.info1.setText("Car Accident");break;
            case "dead_animal": holder.type_icon.setImageResource(R.drawable.deer); holder.info1.setText("Dead Animal");break;
            default: break;
        }

        //set the row's text fields
        holder.info1.setText(holder.info1.getText()+", "+df3.format(distance_list.get(i)/1000)+"km");
        //holder.info2.setText(current.getSecondary()+", "+Double.toString(current.getDistance()/1000)+"km");

        //make the row clickable -> if user click, take them to Google Maps and display a pin on the location of the report
        holder.clickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                double lat = Double.parseDouble(current.getLat());
//                double lon = Double.parseDouble(current.getLon());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<"+current.getLat()+">,<"+current.getLon()+">?q=<"+current.getLat()+">,<"+current.getLon()+">("+current.getType()+")"));
                context.startActivity(intent);
            }
        });
    }

    /**
     * This method calculates distance between the phone's current coordinates (fed in through updateAdapter) and a single report.
     *
     * @param report the Report for which we're calculating the distance
     * @return
     */
    private double calculateDistance(Report report){
        Location reportLocation = new Location("");
        Double lon = Double.parseDouble(report.getLon());
        Double lat = Double.parseDouble(report.getLat());
        reportLocation.setLongitude(lon);
        reportLocation.setLatitude(lat);
        Location myLocation = new Location("");
        myLocation.setLongitude(my_lon);
        myLocation.setLatitude(my_lat);

        double distance = myLocation.distanceTo(reportLocation);

        return distance;
    }

    /**
     * This method takes in the device's most recent coordinates and updates the entire list based on the distance.
     * It goes through all the reports in the Firebase and adds them in such an order that the closest items are displayed at the top (loc 0, 1, 2,...).
     *
     * @param lon most recent longitude value of this device's coordinates
     * @param lat most recent latitute value of this device's coordinates
     */
    public void updateAdapter(double lon, double lat){
        my_lat = lat;
        my_lon = lon;
        report_list = new ArrayList<Report>();
        distance_list = new ArrayList<Double>();
        base_database_reference.child("reports").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Report current = ds.getValue(Report.class);
                        //Log.d("loctest", "mine: "+my_lon+" "+my_lat);
                        //Log.d("loctest", "theirs: "+current.getLon()+" "+current.getLat());
                        double dist = calculateDistance(current);
                        //Log.d("loctest", "distance: "+ Double.toString(dist));

                        //to see the Report it has to be within this radius
                        if(dist < 20) {
                            //add the element such that we keep an ascending order (distance)
                            if(report_list.size() < 1){
                                report_list.add(current);
                                distance_list.add(dist);
                                notifyDataSetChanged();
                            }else{
                                for(int i = 0; i<report_list.size(); i++){
                                    if(i == report_list.size()-1){
                                        report_list.add(current);
                                        distance_list.add(dist);
                                        notifyDataSetChanged();
                                        break;
                                    }
                                    if(distance_list.get(i) >= dist){
                                        report_list.add(i, current);
                                        distance_list.add(i, dist);
                                        notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return report_list.size();
    }

    public class ReportViewHolder extends RecyclerView.ViewHolder{

        private ImageView type_icon;
        private TextView info1;
        private TextView info2;
        private LinearLayout clickable;

        public ReportViewHolder(View view){
            super(view);
            type_icon = view.findViewById(R.id.type_icon);
            info1 = view.findViewById(R.id.row_info1);
            info2 = view.findViewById(R.id.row_info2);
            clickable = view.findViewById(R.id.report_clickable);
        }
    }
}
