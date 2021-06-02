package com.example.devposapp2;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

public class VollyRequest{
    private RequestQueue mQueue;
    public VollyRequest(Context context) {
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap
// Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
// Instantiate the RequestQueue with the cache and network.
        mQueue = new RequestQueue(cache, network);
        mQueue.start();
    }

    public void addArrayRequest(JsonArrayRequest request){
        mQueue.add( request);
    }
    public void addObjectRequest(JsonObjectRequest request){
        mQueue.add( request);
    }
}
