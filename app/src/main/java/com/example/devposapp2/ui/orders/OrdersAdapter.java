package com.example.devposapp2.ui.orders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.speech.tts.TextToSpeech;
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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.example.devposapp2.AudioManager;
import com.example.devposapp2.AutoPrint;
import com.example.devposapp2.Connection;
import com.example.devposapp2.MessageDialog;
import com.example.devposapp2.Print;
import com.example.devposapp2.R;
import com.example.devposapp2.Socketmanager;
import com.example.devposapp2.SpaceManager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {
    private Context context ;
    String printerIp;
    int port = 9100;
    String resId;
    private final int SHOW_MENU = 1;
    private final int HIDE_MENU = 2;
    private RequestQueue mQueue;
    String deliveryOrCollection;
    List<OrdersViewModel> orders;
    RequestQueue queue;
    Connection connection = new Connection();
    private Socketmanager mSockManager;
    EditText x;
    SpaceManager spaceManager = new SpaceManager();

    ArrayList<DishDetailsModel> orderedItemsList = new ArrayList<>();
    public OrdersAdapter(Context mContext, List<OrdersViewModel> orders) {
        this.context = mContext;
        this.orders = orders;
    }
    @Override
    public int getItemViewType(int position) {
        if(orders.get(position).isShowMenu()){
            return SHOW_MENU;
        }else{
            return HIDE_MENU;
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        mSockManager=new Socketmanager(parent.getContext());
        SharedPreferences connectionFields = parent.getContext().getSharedPreferences("connectionFields",parent.getContext().MODE_PRIVATE);


        printerIp= connectionFields.getString("ipAddress","data not found");
        String  portStr= connectionFields.getString("port","data not found");
//        port = Integer.parseInt(portStr);
        return viewHolder;


    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.grandTotal.setText("£"+orders.get(position).getGrandTotal());
        holder.firstName.setText(orders.get(position).getFirstName());
        holder.date.setText(orders.get(position).getOrderDate());
        int len = orders.get(position).getOrderPolicy().length();
        if(orders.get(position).getOrderStatus().matches("Accepted")){
            holder.leftIcon.setBackgroundResource(R.drawable.icon_background_green);
            holder.signalBar.setBackgroundResource(R.drawable.green_line);
        }
        if(orders.get(position).getOrderStatus().matches("Declined")){
            holder.leftIcon.setBackgroundResource(R.drawable.icon_background_red);
            holder.signalBar.setBackgroundResource(R.drawable.red_line);
        }
        if(orders.get(position).getOrderStatus().matches("Pending")){
            holder.leftIcon.setBackgroundResource(R.drawable.icon_background_yellow);
            holder.signalBar.setBackgroundResource(R.drawable.yellow_line);
        }
        if(len == 8)
        {

            holder.leftIcon.setImageResource(R.drawable.bike);


        }
       else if(len == 10)
        {

            holder.leftIcon.setImageResource(R.drawable.bag_icon_outlined);

        }
        boolean paymentMethodCash = orders.get(position).getPaymentMethod().matches("CASH");
       if(paymentMethodCash){

           holder.paymentMethodIcon.setImageResource(R.drawable.cash_icon_gray);
       }
        boolean paymentMethodCard = orders.get(position).getPaymentMethod().matches("CARD");
        if(paymentMethodCard){

            holder.paymentMethodIcon.setImageResource(R.drawable.card);
        }
        if(orders.get(position).getPrintingStatus().matches("1")){
            holder.printButton.setBackgroundResource(R.drawable.green_print_icon);

        }
        if(orders.get(position).getPrintingStatus().matches("0")){
            holder.printButton.setBackgroundResource(R.drawable.red_print_icon);

        }

        holder.printButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(View v) {
                if(connection.checkInternetConnection(context)) {
                    if (connection.conTest(printerIp, port)) {
                        print(position);

                    } else {
                        Toast.makeText(v.getContext(), "printer not connected.", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(v.getContext(), "No Internet. Please check you internet connection.", Toast.LENGTH_LONG).show();
                }
            }
        });


        holder.dotMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.showPopupMenu(v);
            }
        });

    }


    @Override
    public int getItemCount() {
        return orders.size();
    }

    public  class  ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener, PopupMenu.OnMenuItemClickListener, MessageDialog.ExampleDialogListener{
        TextView firstName,date,grandTotal;
        CardView viewContainer;
        ImageView printButton;
        ImageView leftIcon, dotMenu, signalBar;
        ImageView paymentMethodIcon;

        private TextView textViewMsg;
        private Button button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            firstName = itemView.findViewById(R.id.first_name);
            date = itemView.findViewById(R.id.date);
            grandTotal = itemView.findViewById(R.id.grand_total);
            viewContainer = itemView.findViewById(R.id.container);
            printButton = itemView.findViewById(R.id.print_button_dynamic);
            leftIcon = itemView.findViewById(R.id.iv_image);
            paymentMethodIcon = itemView.findViewById(R.id.paymentIcon);
            dotMenu = itemView.findViewById(R.id.dotMenu);
            signalBar = (ImageView) itemView.findViewById(R.id.signalBar);
            textViewMsg = itemView.findViewById(R.id.edit_msg);

            itemView.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */

        @Override
        public void onClick(View v) {

            int postion = getAdapterPosition();
            String positionStr = String.valueOf(postion);
            Intent intent = new Intent(context , OrderDetails.class);
            intent.putExtra("firstName" , orders.get(postion).getFirstName());
            intent.putExtra("address" , orders.get(postion).getCustomerAddress());
            intent.putExtra("orderDate" , orders.get(postion).getOrderDate());
            intent.putExtra("orderTime" , orders.get(postion).getOrderTime());
            intent.putExtra("mobileNumber" , orders.get(postion).getCustomerPhoneNum());
            intent.putExtra("deliveryTime" , orders.get(postion).getDeliveryTime());

            intent.putExtra("subTotal" , orders.get(postion).getSubTotal());
            intent.putExtra("discount" , orders.get(postion).getDiscount());
            intent.putExtra("serviceCharge" , orders.get(postion).getServiceCharge());
            intent.putExtra("deliveryCharge" , orders.get(postion).getDeliveryCharge());
            intent.putExtra("grandTotall" , orders.get(postion).getGrandTotal());
            intent.putExtra("paymentMethod" , orders.get(postion).getPaymentMethod());
            intent.putExtra("resName" , orders.get(postion).getResName());
            intent.putExtra("offerText" , orders.get(postion).getOfferText());
            intent.putExtra("discountText" , orders.get(postion).getDiscountText());
            intent.putExtra("_id" , orders.get(postion).getResId());
            intent.putExtra("order_id" , orders.get(postion).getOrder_id());

            intent.putExtra("jsonArray", orders.get(postion).getOrderedItems().toString());

            JSONArray orderedItems = orders.get(postion).getOrderedItems();
            int numberOfDish = orderedItems.length();
            String numberOfDishStr =String.valueOf(numberOfDish);
            intent.putExtra("numberOfDish" , numberOfDishStr);
            orderedItemsList.clear();
            for (int i = 0; i < orderedItems.length(); i++) {

                try {
                    DishDetailsModel dishDetailsModel = new DishDetailsModel();
                    JSONObject order_item = orderedItems.getJSONObject(i);
                    String dishName = order_item.getString("dish_name");
                    double totalPriceDishInt = order_item.getDouble("total_price");
                    String totalPriceDish = String.valueOf(totalPriceDishInt);
                    String quantity = order_item.getString("quantity");
                    String quantityRdishName = quantity+" X "+dishName;
                    dishDetailsModel.setDishName(quantityRdishName);
                    dishDetailsModel.setPrice(totalPriceDish);
                    dishDetailsModel.setQuantity(quantity);
                    orderedItemsList.add(dishDetailsModel);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            intent.putExtra("arrayList",orderedItemsList);


            context.startActivity(intent);

        }



        private void showPopupMenu(View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.popup_menu);
            int position = getAdapterPosition();
            if(orders.get(position).getOrderStatus().matches("Accepted"))
            {
                popupMenu.getMenu().findItem(R.id.action_popup_edit).setVisible(false);

            }
            else if(orders.get(position).getOrderStatus().matches("Declined"))
            {
                popupMenu.getMenu().findItem(R.id.action_popup_delete).setVisible(false);

            }
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int position = getAdapterPosition();

            switch (item.getItemId()) {

                case R.id.action_popup_edit:
                    String orderId = orders.get(position).getOrderId();
                    openDialog("Accepted",orderId);
//                    MessageDialog exampleDialog = new MessageDialog();
//                    exampleDialog.setInfo(position,"Accepted");
//                   String msg = exampleDialog.getMsg();
//
//                    OrderManager orderManager = new OrderManager(context);
//                    orderManager.acceptOrder(orders.get(position).getOrderId(),"Accepted",msg);



                    return true;
                case R.id.action_popup_delete:
                    String orderId2 = orders.get(position).getOrderId();
                    openDialog("Declined",orderId2);
//                    openDialog("Declined");
//                    int position2 = getAdapterPosition();
//                    MessageDialog exampleDialog2 = new MessageDialog();
//                    String msg2 = exampleDialog2.getMsg();
//                    OrderManager orderManager2 = new OrderManager(context);
//                    orderManager2.acceptOrder(orders.get(position2).getOrderId(),"Declined",msg2);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void applyTexts(String msg) {
            textViewMsg.setText(msg);

        }

        public void openDialog(String status, String orderId) {
            MessageDialog exampleDialog = new MessageDialog(status, orderId,"order");
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




@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
public void print(int position){
    String gbp = "£";

//    String _id = obj.getString("_id");
    String firstName = orders.get(position).getFirstName();
    String phoneNumber = orders.get(position).getCustomerPhoneNum();
    String customerAddress = orders.get(position).getCustomerAddress();
    String orderDate = orders.get(position).getOrderDate();
    String orderTime = orders.get(position).getOrderTime();
    String deliveryTime = orders.get(position).getDeliveryTime();
    JSONArray orderedItems = orders.get(position).getOrderedItems();
    String subTotal = orders.get(position).getSubTotal();
    String discount = orders.get(position).getDiscount();
    String serviceCharge = orders.get(position).getServiceCharge();
    String deliveryCharge = orders.get(position).getDeliveryCharge();
    String grandTotal = orders.get(position).getGrandTotal();
    String order_policy = orders.get(position).getOrderPolicy();
    String paymentMethod = orders.get(position).getPaymentMethod();
    String resName = orders.get(position).getResName();
    String offerText = orders.get(position).getOfferText();
    String discountText = orders.get(position).getDiscountText();
    String resId = orders.get(position).getResId();
    String order_id = orders.get(position).getOrder_id();
    String orderId = orders.get(position).getOrderId();

    Print print = new Print( context,resName, orderDate, orderTime, deliveryTime, orderedItems, subTotal, discount, grandTotal,offerText, printerIp,port,resId,discountText, order_id);

    if (print.PrintOut()) {
        AutoPrint autoPrint = AutoPrint.getInstance(context);

        autoPrint.updateStatus(orderId);



    } else {


    }

}




}
