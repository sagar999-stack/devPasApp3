package com.example.devposapp2.ui.reservations;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.devposapp2.Connection;
import com.example.devposapp2.LoginStatus;
import com.example.devposapp2.PinCodeActivity;
import com.example.devposapp2.Print;
import com.example.devposapp2.R;
import com.example.devposapp2.RefreshAdapter;
import com.example.devposapp2.ReservationManager;
import com.example.devposapp2.ResetPassword;
import com.example.devposapp2.Socketmanager;
import com.example.devposapp2.SpaceManager;
import com.example.devposapp2.VollyRequest;
import com.example.devposapp2.ui.orders.OrderDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ViewHolder> {
    public Context context ;
    String printerIp;
    int port = 9100;
    String resId;
    private final int SHOW_MENU = 1;
    private final int HIDE_MENU = 2;
    private RequestQueue mQueue;
    String deliveryOrCollection;
    List<ReservationsViewModel> reservations;
    Connection connection = new Connection();
    private Socketmanager mSockManager;
    EditText x;
    View view2;
    RecyclerView recyclerView;
ReservationsAdapter reservationsAdapter;


    public ReservationsAdapter(Context mContext, List<ReservationsViewModel> reservations) {
        this.context = mContext;
        this.reservations = reservations;
    }

@NonNull
@Override
public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

    View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.reservations_item,parent,false);
    recyclerView = view.findViewById(R.id.recycleView2);
    view2=view;
    ViewHolder viewHolder=new ViewHolder(view);
    mSockManager=new Socketmanager(parent.getContext());

    return viewHolder;
}

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(reservations.get(position).getBookingStatus().matches("Accepted")){
            holder.colorBar.setImageResource(R.drawable.green_line);
        }
        if(reservations.get(position).getBookingStatus().matches("Declined")){
            holder.colorBar.setImageResource(R.drawable.red_line);
        }
        if(reservations.get(position).getBookingStatus().matches("Pending")){
            holder.colorBar.setImageResource(R.drawable.yellow_line);
        }
        holder.firstName.setText(reservations.get(position).getFirstName());
        holder.reservationDate.setText("Date: "+reservations.get(position).getReservationDate());
        holder.reservationTime.setText("Time: "+reservations.get(position).getReservationTime());
        holder.contact.setText(reservations.get(position).getPhoneNumber());
        holder.numberOfGuest.setText(reservations.get(position).getNoOfGuest());
        holder.email.setText(reservations.get(position).getEmail());
        holder.createdDate.setText("Created Date: "+reservations.get(position).getCreatedDate());

        holder.dotMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.showPopupMenu(v);
            }
        });

    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public  class  ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener ,PopupMenu.OnMenuItemClickListener{
        TextView firstName,reservationDate,contact,numberOfGuest,email,createdDate,reservationTime;
        CardView view_container;
        ImageView dotMenu,colorBar;
        ClipData.Item accept;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view_container = itemView.findViewById(R.id.container2);
            firstName = itemView.findViewById(R.id.customerName);
            reservationDate = itemView.findViewById(R.id.reservation_date);
            contact = itemView.findViewById(R.id.contact);
            numberOfGuest = itemView.findViewById(R.id.num_of_guest);
            email = itemView.findViewById(R.id.email);
            createdDate = itemView.findViewById(R.id.create_date);
            reservationDate = itemView.findViewById(R.id.reservation_date);
            reservationTime = itemView.findViewById(R.id.reservation_time);
            colorBar = itemView.findViewById(R.id.signal);
            itemView.setOnClickListener(this);
            dotMenu = itemView.findViewById(R.id.dotMenu);


        }

        @Override
        public void onClick(View v) {
            int postion = getAdapterPosition();
            Intent intent = new Intent(context, ReservationDetails.class);
            intent.putExtra("status" , reservations.get(postion).getBookingStatus());
            intent.putExtra("bookingDate" , reservations.get(postion).getReservationDate());
            intent.putExtra("customerName" , reservations.get(postion).getFirstName());
            intent.putExtra("email" , reservations.get(postion).getEmail());
            intent.putExtra("phone" , reservations.get(postion).getPhoneNumber());
            intent.putExtra("numOfGuest" , reservations.get(postion).getNoOfGuest());
            intent.putExtra("createdDate" , reservations.get(postion).getCreatedDate());
            context.startActivity(intent);
        }
        private void showPopupMenu(View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.popup_menu);
            int position = getAdapterPosition();
            if(reservations.get(position).getBookingStatus().matches("Accepted"))
            {
                popupMenu.getMenu().findItem(R.id.action_popup_edit).setVisible(false);

            }
           else if(reservations.get(position).getBookingStatus().matches("Declined"))
            {
                popupMenu.getMenu().findItem(R.id.action_popup_delete).setVisible(false);

            }
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();


        }
        /**
         * This method will be invoked when a menu item is clicked if the item
         * itself did not already handle the event.
         *
         * @param item the menu item that was clicked
         * @return {@code true} if the event was handled, {@code false}
         * otherwise
         */

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int position = getAdapterPosition();

            switch (item.getItemId()) {

                case R.id.action_popup_edit:

                    ReservationManager reservationManager = new ReservationManager(context);
                    reservationManager.acceptReservation(reservations.get(position).getReservationId(),"Accepted");

                    notifyDataSetChanged();

                    return true;
                case R.id.action_popup_delete:
                    int position2 = getAdapterPosition();
                    ReservationManager reservationManager2 = new ReservationManager(context);
                    reservationManager2.acceptReservation(reservations.get(position2).getReservationId(),"Declined");
                    return true;
                default:
                    return false;
            }
        }

        /**
         * This method will be invoked when a menu item is clicked if the item
         * itself did not already handle the event.
         *
         * @param item the menu item that was clicked
         * @return {@code true} if the event was handled, {@code false}
         * otherwise
         */

    }
    public void swapItems() {
     reservations.clear();
     reservations.addAll(reservations);
    }

}
