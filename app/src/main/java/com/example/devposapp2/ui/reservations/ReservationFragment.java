package com.example.devposapp2.ui.reservations;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.devposapp2.Connection;
import com.example.devposapp2.LoginActivity;
import com.example.devposapp2.MySingleton;
import com.example.devposapp2.R;

import com.example.devposapp2.ui.orders.OrdersAdapter;
import com.example.devposapp2.ui.orders.OrdersViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReservationFragment extends Fragment {

    ReservationsAdapter reservationsAdapter;

    List<ReservationsViewModel> reservations = new ArrayList<>();
   public RecyclerView recyclerView;

    int port=9100;
    String printerIp;
    String resId;
    TextToSpeech t1;
    private Button buttonPf=null;
    private ProgressBar spinner;
    private TextView noData;
    private ImageView reservationIcon;
    SwipeRefreshLayout swipeRefreshLayout;
   public View view;
    Connection connection = Connection.getInstance();
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences loginInfo = getContext().getSharedPreferences("loginInfo", getContext().MODE_PRIVATE);
        resId = loginInfo.getString("resId", "data not found");

        if(resId =="data not found"){
            sendToLogin(null);
        }else{

        }
        View root = inflater.inflate(R.layout.fragment_reservations, container, false);
        view =root;
        spinner = (ProgressBar)root.findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
        noData= root.findViewById(R.id.noData);
        reservationIcon= root.findViewById(R.id.reserIcon);

        recyclerView = root.findViewById(R.id.recycleView2);
        swipeRefreshLayout = root.findViewById(R.id.swipeRefresh);

        SharedPreferences connectionFields = getContext().getSharedPreferences("connectionFields",getContext().MODE_PRIVATE);
        printerIp= connectionFields.getString("ipAddress","data not found");
        String  portStr= connectionFields.getString("port","data not found");
        if(portStr=="data not found"){

        }else{
            port = Integer.parseInt(portStr);
        }

        t1=new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        if(connection.checkInternetConnection(getContext())) {
            allSettingsAndLoadDataAndPassToAdapter();
        }
        else{
            allSettingsAndLoadDataAndPassToAdapter();
            spinner.setVisibility(View.GONE);
            Toast.makeText(getContext(), "No Internet. Please check your internet connection.", Toast.LENGTH_LONG).show();

        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
            @Override
            public void onRefresh() {
                spinner.setVisibility(View.VISIBLE);
                if(connection.checkInternetConnection(getContext())) {
                    allSettingsAndLoadDataAndPassToAdapter();
                    reservationsAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);

                }
                else{
                    spinner.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "No Internet. Please check your internet connection.", Toast.LENGTH_LONG).show();
                }

            }
        });

//            Thread thread = new Thread(){
//                @Override
//                public  void  run(){
//                    while (!interrupted()){
//                        try {
//                            jsonParseListOfPrintedOrders();
//
//                            Thread.sleep(5000);
//
//
//
//
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            };
//
//
//            thread.start();
        return root;
    }
    public void sendToLogin(View view) {
        Intent intent = new Intent(getContext(), LoginActivity.class);

        startActivity(intent);
    }
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
    public void allSettingsAndLoadDataAndPassToAdapter(){

        reservations = new ArrayList<>();
        jsonParseListOfPrintedOrders();
        reservationsAdapter = new ReservationsAdapter(getContext(),reservations) ;
        recyclerView.setAdapter(reservationsAdapter);
    }

    public void data(
            String reservationId,
            String bookingStatus,
           String firstName,
           String phoneNumber,
           String customerAddress,
           String email,
           String reservationDate,
            String reservationTime,
           String noOfGuest,
           String createdDate,
           String updatedAt)  {

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ReservationsViewModel reservationsViewModel = new ReservationsViewModel();
        reservationsViewModel.setReservationId(reservationId);
        reservationsViewModel.setBookingStatus(bookingStatus);
        reservationsViewModel.setFirstName(firstName);
        reservationsViewModel.setPhoneNumber(phoneNumber);
        reservationsViewModel.setCustomerAddress(customerAddress);
        reservationsViewModel.setEmail(email);
        reservationsViewModel.setReservationDate(reservationDate);
        reservationsViewModel.setReservationTime(reservationTime);
        reservationsViewModel.setNoOfGuest(noOfGuest);
        reservationsViewModel.setCreatedDate(createdDate);
        reservationsViewModel.setUpdatedAt(updatedAt);


        reservations.add(reservationsViewModel);


    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)

    public void jsonParseListOfPrintedOrders() {

        RequestQueue queue = MySingleton.getInstance(this.getContext()).
                getRequestQueue();
        String url = "https://devoretapi.co.uk/epos/getReservations/"+resId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,null,
                new com.android.volley.Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONArray response) {
                        spinner.setVisibility(View.GONE);
                        int arrayLength=0;
                        int count = 0;
                        if(response.length()<1){
                            noData.setVisibility(View.VISIBLE);
                            reservationIcon.setVisibility(View.VISIBLE);
                        }
                        else{
                            noData.setVisibility(View.GONE);
                            reservationIcon.setVisibility(View.GONE);
                        }
                        if(arrayLength<=response.length()){
                            while (count<response.length()){
                                if(count>=arrayLength){
                                    arrayLength++;
                                    try {
                                        JSONObject obj = response.getJSONObject(count);

                                        String _idReservation = obj.getString("_id");
                                        String bookingStatus = obj.getString("status");
                                        String reservationDateTime = obj.getString("reservation_date");
                                        String firstName = obj.getString("first_name");
                                        String phoneNumber = obj.getString("mobile");
                                        String email = obj.getString("email");
                                        String address1 = obj.getString("address1");
                                        String address2;
                                        if(obj.getString("address2").matches("")){
                                            address2="";
                                        }
                                        else{
                                             address2 = obj.getString("address2");
                                        }
                                        String postCode = obj.getString("postcode");
                                        String createdDate = obj.getString("created_date");
                                        DateFormat outputFormat = new SimpleDateFormat("MM/yyyy", Locale.US);
                                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US);
                                        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
                                        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
                                        Date date = inputFormat.parse(reservationDateTime);
                                        Date createdDateDate = inputFormat.parse(createdDate);
                                        String reservationDate = dateFormatter.format(date);
                                        String reservationTime = timeFormatter.format(date);
                                        String createdDateFormatted = dateFormatter.format(createdDateDate);
                                        String customerAddress = address1+","+address2+","+postCode;

                                        String noOfGuest = obj.getString("no_of_guest");

                                        String updatedAt = obj.getString("updated_at");
                                        String dateFormat = "MMMM dd,yyyy G";
                                        String bookingCreatedDate = obj.getString("booking_created_date");
//                                        String serviceCharge = obj.getString("service_charge");
//                                        String deliveryCharge = obj.getString("delivery_charge");
//                                        String grandTotal = obj.getString("grand_total");
//                                        String order_policy = obj.getString("order_policy");
//                                        String paymentMethod = obj.getString("payment_method");
//                                        String resName = obj.getString("restaurant_name");
//                                        String offerText = obj.getString("offer_text");
//                                        String discountText = obj.getString("discount_text");
                                        boolean printed = false;

                                        data(_idReservation,bookingStatus,firstName,phoneNumber,customerAddress,email,reservationDate,reservationTime,noOfGuest,createdDateFormatted,updatedAt);

                                    } catch (JSONException | ParseException e) {
                                        e.printStackTrace();
                                    }
                                }

                                count++;

                            }
                        }
                        else{

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        MySingleton.getInstance(this.getContext()).addToRequestQueue(request);

    }



    }
