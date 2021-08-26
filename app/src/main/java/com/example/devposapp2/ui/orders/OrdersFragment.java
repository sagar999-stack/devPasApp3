package com.example.devposapp2.ui.orders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.devposapp2.Connection;
import com.example.devposapp2.LoginActivity;
import com.example.devposapp2.MySingleton;
import com.example.devposapp2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.os.Build;
import android.widget.Toast;

public class OrdersFragment extends Fragment {
    public OrdersFragment() {
        // Required empty public constructor
    }

    OrdersAdapter myadapter;

    List<OrdersViewModel> orders = new ArrayList<>();
    RecyclerView recyclerView;

    int port=9100;
    String printerIp;
    String resId;
    TextToSpeech t1;

    private ProgressBar spinner;
    private TextView noData;
    SwipeRefreshLayout swipeRefreshLayout;
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
        View root = inflater.inflate(R.layout.fragment_orders, container, false);
        spinner = (ProgressBar)root.findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
        noData= root.findViewById(R.id.noData);

        recyclerView = root.findViewById(R.id.recycleView);
        swipeRefreshLayout = root.findViewById(R.id.swipeRefresh);

        SharedPreferences connectionFields = getContext().getSharedPreferences("connectionFields",getContext().MODE_PRIVATE);
        printerIp= connectionFields.getString("ipAddress","data not found");
        String  portStr= connectionFields.getString("port","data not found");
        if(portStr=="data not found"){

        }else{
            port = Integer.parseInt(portStr);
        }
        noData.setVisibility(View.GONE);
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
                noData.setVisibility(View.GONE);
                if(connection.checkInternetConnection(getContext())) {
                    allSettingsAndLoadDataAndPassToAdapter();
                    myadapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    spinner.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "No Internet. Please check your internet connection.", Toast.LENGTH_LONG).show();
                }

            }
        });


        return root;
    }
    public void sendToLogin(View view) {
        Intent intent = new Intent(getContext(), LoginActivity.class);

        startActivity(intent);
    }

@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
public void allSettingsAndLoadDataAndPassToAdapter(){

    orders = new ArrayList<>();

    jsonParseListOfPrintedOrders();
    myadapter = new OrdersAdapter(getContext(),orders) ;
    recyclerView.setAdapter(myadapter);
}
    public void data(
            String firstName,
            String phoneNumber,
            String customerAddress,
            String orderDate,
            String orderTime,
            String deliveryTime,
            JSONArray orderedItems,
            String subTotal,
            String discount,
            String serviceCharge,
            String deliveryCharge,
            String grandTotal,
            String paymentMethod,
            String orderPolicy,
            String resName,
            String offerText,
            String orderId,
            String orderStatus,
            String discountText,
            String order_id,
            String printingStatus)  {

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        OrdersViewModel ordersViewModel = new OrdersViewModel();
        ordersViewModel.setOrderId(orderId);
        ordersViewModel.setOrderStatus(orderStatus);
        ordersViewModel.setResName(resName);
        ordersViewModel.setFirstName(firstName);
        ordersViewModel.setCustomerPhoneNum(phoneNumber);
        ordersViewModel.setCustomerAddress(customerAddress);
        ordersViewModel.setOrderDate(orderDate);
        ordersViewModel.setSubTotal(subTotal);
        ordersViewModel.setOrderTime(orderTime);
        ordersViewModel.setDeliveryTime(deliveryTime);
        ordersViewModel.setDiscount(discount);
        ordersViewModel.setServiceCharge(serviceCharge);
        ordersViewModel.setDeliveryCharge(deliveryCharge);
        ordersViewModel.setGrandTotal(grandTotal);
        ordersViewModel.setPaymentMethod(paymentMethod);
        ordersViewModel.setOrderPolicy(orderPolicy);
        ordersViewModel.setOrderedItems(orderedItems);
        ordersViewModel.setOfferText(offerText);
        ordersViewModel.setResId(resId);
        ordersViewModel.setDiscountText(discountText);
        ordersViewModel.setOrder_id(order_id);
        ordersViewModel.setPrintingStatus(printingStatus);

        orders.add(ordersViewModel);


    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
    public void jsonParseListOfPrintedOrders() {
        RequestQueue queue = MySingleton.getInstance(getContext()).getRequestQueue();
        String url = "https://devoretapi.co.uk/api/v1/admin/orders/"+resId+"/today";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,null,
                new com.android.volley.Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        spinner.setVisibility(View.GONE);
                         int arrayLength=0;
                        int count = 0;
                        if(response.length()<1){
                            noData.setVisibility(View.VISIBLE);
                        }
                        else{
                            noData.setVisibility(View.GONE);
                        }
                        if(arrayLength<=response.length()){
                            while (count<response.length()){
                                if(count>=arrayLength){
                                    arrayLength++;
                                    try {


                                        JSONObject obj = response.getJSONObject(count);
                                        JSONObject customerInfo = obj.getJSONObject("customer_info");
                                        String address1 = null;
                                        String address2 = null;
                                        String printingStatus = obj.getString("printing_status");

                                        String orderId = obj.getString("_id");
                                        String order_Id = obj.getString("order_id");
                                        String orderStatus = obj.getString("order_status");
                                        String firstName = customerInfo.getString("first_name");
                                        String phoneNumber = customerInfo.getString("mobile_no");
                                         address1 = customerInfo.getString("address1");

                                        String postCode = customerInfo.getString("postcode");
                                        String city = customerInfo.getString("city");
                                        String customerAddress = address1+","+postCode+","+city;
                                        String orderDate = obj.getString("order_date");
                                        String orderTime = obj.getString("order_time");
                                        String deliveryTime = obj.getString("delivery_time");
                                        JSONArray orderedItems = obj.getJSONArray("order_items");
                                        String subTotal = obj.getString("sub_total");
                                        String discount = obj.getString("discount_amount");
                                        String serviceCharge = obj.getString("service_charge");
                                        String deliveryCharge = obj.getString("delivery_charge");
                                        String grandTotal = obj.getString("grand_total");
                                        String order_policy = obj.getString("order_policy");
                                        String paymentMethod = obj.getString("payment_method");
                                        String resName = obj.getString("restaurant_name");
                                        String offerText = obj.getString("offer_text");
                                        String discountText = obj.getString("discount_text");

                                        boolean printed = false;

                                        data(firstName,phoneNumber,customerAddress,orderDate,orderTime,deliveryTime,orderedItems,subTotal,discount,serviceCharge,deliveryCharge,grandTotal,paymentMethod,order_policy,resName,offerText,orderId,orderStatus,discountText,order_Id,printingStatus);

                                    } catch (JSONException e) {
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

        MySingleton.getInstance(getContext()).addToRequestQueue(request);
    }
}