package com.example.mitswallpaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    wallpaperAdapter wallpaperAdapter;
    RecyclerView recyclerView;
    List<wallpaperModel> wallpaperModelList;
    int pageNumber =1;

    //scrolling
    Boolean isScrolling = false;
    int currentItem,totalItem,scrollOutItems;
    String url = "https://api.pexels.com/v1/curated/?page="+pageNumber+"&per_page=80";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        wallpaperModelList = new ArrayList<>();
        wallpaperAdapter = new wallpaperAdapter(this,wallpaperModelList);
        recyclerView.setAdapter(wallpaperAdapter);
        final GridLayoutManager gridLayoutManager= new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //when start then it become true
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                currentItem = gridLayoutManager.getChildCount();
                totalItem = gridLayoutManager.getItemCount();
                scrollOutItems =gridLayoutManager.findFirstVisibleItemPosition();
                //we have scrolled 1 page know next page
                if (isScrolling &&(currentItem + scrollOutItems == totalItem))
                {
                    isScrolling = false;
                    fetchWallpaper();
                }
            }
        });

        fetchWallpaper();
    }

    public void fetchWallpaper()
    {
        StringRequest request = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("photos");
                            int length = jsonArray.length();
                            for(int i=0;i<length;i++)
                            {
                                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                                int id = jsonObject1.getInt("id");
                                JSONObject jsonObjectImg2 =jsonObject1.getJSONObject("src");
                                String OriginalUrl = jsonObjectImg2.getString("original");
                                String MediumUrl = jsonObjectImg2.getString("medium");
                                wallpaperModel wallpaperModel = new wallpaperModel(id,OriginalUrl,MediumUrl);
                                wallpaperModelList.add(wallpaperModel);
                            }
                            wallpaperAdapter.notifyDataSetChanged();
                            pageNumber++;


                        }catch(JSONException e) {
                        }

                    }
                },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization","enter key");
                return super.getHeaders();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater() .inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.nav_search)
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            final EditText editText = new EditText(this);
            editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            alert.setMessage("Enter Category ex nature");
            alert.setTitle("Search Wallpaper");
            alert.setView(editText);
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //bec modify url for search
                    String query = editText.getText().toString().toLowerCase();
                    //bec fetch another url
                    url = "https://api.pexels.com/v1/search?query=nature&per_page=1";
                    //clear
                    wallpaperModelList.clear();
                    fetchWallpaper();
                }
            });
            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
