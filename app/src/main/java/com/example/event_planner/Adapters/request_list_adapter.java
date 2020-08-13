package com.example.event_planner.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.event_planner.Model.approvalRequest;
import com.example.event_planner.R;
import com.example.event_planner.requestDetails;
import com.google.gson.Gson;

import java.util.ArrayList;

public class request_list_adapter extends RecyclerView.Adapter<request_list_adapter.request_list_viewholder> {
    Context context;
    ArrayList<approvalRequest> approvalRequests;
    ArrayList<String> requestId;

    public request_list_adapter(Context context, ArrayList<approvalRequest> approvalRequests,ArrayList<String> requestId) {
        this.context = context;
        this.approvalRequests = approvalRequests;
        this.requestId=requestId;
    }

    @NonNull
    @Override
    public request_list_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.request_list_layout,parent,false);
        return new request_list_viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull request_list_viewholder holder, final int position) {
       holder.hallName.setText(approvalRequests.get(position).getServiceProviderName());
       holder.requestDate.setText(approvalRequests.get(position).getRequestDate());
       holder.requestCard.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent i=new Intent(context, requestDetails.class);
               i.putExtra("requestData",new Gson().toJson(approvalRequests.get(position)));
               i.putExtra("requestId",requestId.get(position));
               context.startActivity(i);
           }
       });
    }

    @Override
    public int getItemCount() {
        return approvalRequests.size();
    }

    class request_list_viewholder extends RecyclerView.ViewHolder{
       CardView requestCard;
       TextView hallName,requestDate;
        public request_list_viewholder(@NonNull View itemView) {
            super(itemView);
            requestCard=itemView.findViewById(R.id.card);
            hallName=itemView.findViewById(R.id.hall_name);
            requestDate=itemView.findViewById(R.id.requestDate);
        }
    }
}
