package com.example.devposapp2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialogFragment;

public class MessageDialog  extends AppCompatDialogFragment {
    private EditText editMsg;
    String status ,_id, orderOrReservation;
    Context context;
    private ExampleDialogListener listener;
    private static MessageDialog INSTANCE = null;
    private static Object mutex = new Object();
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getOrderOrReservation() {
        return orderOrReservation;
    }

    public void setOrderOrReservation(String orderOrReservation) {
        this.orderOrReservation = orderOrReservation;
    }

    @Nullable
    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
    private MessageDialog() {

    }
    public static MessageDialog getInstance() {
        synchronized (mutex) {
            if (INSTANCE == null) {
                INSTANCE = new MessageDialog();
            }
        }
        return(INSTANCE);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);
        editMsg = view.findViewById(R.id.edit_msg);
        builder.setView(view)
                .setTitle("Type a message .")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        SharedPreferences reservationStatusPref = getContext().getSharedPreferences("reservationStatusPref",getContext().MODE_PRIVATE);
//
//                     String status = reservationStatusPref.getString("status","data not found");
//                     String reservationId = reservationStatusPref.getString("reservationId","data not found");
                    if(orderOrReservation.matches("reservation")){
                        ReservationManager reservationManager2 = ReservationManager.getInstance();
                        reservationManager2.acceptReservation(_id,status,editMsg.getText().toString(),context);
                    }
                       else if(orderOrReservation.matches("order")){
                        OrderManager orderManager = OrderManager.getInstance();
                        orderManager.acceptOrder(_id,status,editMsg.getText().toString(),context);
                        }
                    }
                });



        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }
public void submitReservationInfo(String msg){

}

    public void setInfo(int position, String status) {
    }

    public interface ExampleDialogListener {
        void applyTexts(String msg);
    }
}

