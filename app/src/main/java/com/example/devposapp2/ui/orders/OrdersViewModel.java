package com.example.devposapp2.ui.orders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;

public class OrdersViewModel  {
    public String orderId;
    public String orderStatus;


    public String resName;
  public String firstName;


    public String customerAddress;
  public String orderDate;
  public String orderTime;
  public String customerPhoneNum;
  public String deliveryTime;
  public JSONArray orderedItems;
  public String subTotal;
  public String discount;
  public String serviceCharge;
  public String deliveryCharge;
  public String grandTotal;
  public String paymentMethod;
  public String orderPolicy;
  public boolean printed;
  public  boolean menu;
  public String offerText;
  public String order_id;
    public String printingStatus;

    public String getPrintingStatus() {
        return printingStatus;
    }

    public void setPrintingStatus(String printingStatus) {
        this.printingStatus = printingStatus;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getDiscountText() {
        return discountText;
    }

    public void setDiscountText(String discountText) {
        this.discountText = discountText;
    }

    public String discountText;
  public  String resId;

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getOfferText() {
        return offerText;
    }

    public void setOfferText(String offerText) {
        this.offerText = offerText;
    }

    public OrdersViewModel() {

    }
    public boolean isPrinted() {
        return printed;
    }

    public void setPrinted(boolean printed) {
        this.printed = printed;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public void setCustomerPhoneNum(String customerPhoneNum) {
        this.customerPhoneNum = customerPhoneNum;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public void setOrderedItems(JSONArray orderedItems) {
        this.orderedItems = orderedItems;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public void setServiceCharge(String serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    public void setOrderPolicy(String orderPolicy) {
        this.orderPolicy = orderPolicy;
    }

    public String getResName() {
        return resName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public String getCustomerPhoneNum() {
        return customerPhoneNum;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public JSONArray getOrderedItems() {
        return orderedItems;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public String getDiscount() {
        return discount;
    }

    public String getServiceCharge() {
        return serviceCharge;
    }

    public String getDeliveryCharge() {
        return deliveryCharge;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
    public String getOrderPolicy() {
        return orderPolicy;
    }

    public boolean isShowMenu() {
        return menu;
    }

    public void setShowMenu(boolean b) {
        this.menu =b;
    }
}