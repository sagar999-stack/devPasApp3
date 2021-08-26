package com.example.devposapp2.ui.reservations;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.example.devposapp2.Connection;
import com.example.devposapp2.MessageDialog;
import com.example.devposapp2.R;


import java.util.List;

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
    Connection connection = Connection.getInstance();

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

    public  class  ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener ,PopupMenu.OnMenuItemClickListener, MessageDialog.ExampleDialogListener{
        TextView firstName,reservationDate,contact,numberOfGuest,email,createdDate,reservationTime;
        CardView view_container;
        ImageView dotMenu,colorBar;
        private TextView textViewMsg;
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
            textViewMsg = itemView.findViewById(R.id.edit_msg);

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
                    String reservationId = reservations.get(position).getReservationId();

//                    SharedPreferences sharedPreferences = context.getSharedPreferences("reservationStatusPref", context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("status", "Accepted");
//                    editor.putString("reservationId", reservationId);
//                    editor.apply();

                    openDialog("Accepted",reservationId);

                    return true;
                case R.id.action_popup_delete:
                    String reservationId2 = reservations.get(position).getReservationId();

//                    SharedPreferences sharedPreferences2 = context.getSharedPreferences("reservationStatusPref", context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor2 = sharedPreferences2.edit();
//                    editor2.putString("status", "Declined");
//                    editor2.putString("reservationId", reservationId2);
//                    editor2.apply();
                    openDialog("Declined",reservationId2);

//                    reservationManager2.acceptReservation(reservations.get(position2).getReservationId(),"Declined");
                    return true;
                default:
                    return false;
            }
        }
        @Override
        public void applyTexts(String msg) {
            textViewMsg.setText(msg);

        }

        public void openDialog(String status, String reservationId) {
            MessageDialog exampleDialog = MessageDialog.getInstance();
            exampleDialog.set_id(reservationId);
            exampleDialog.setStatus(status);
            exampleDialog.setOrderOrReservation("reservation");

            exampleDialog.show(((FragmentActivity)context).getSupportFragmentManager(), "example dialog");
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
