package com.main.mycarinspector.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.main.mycarinspector.APIUrl;
import com.main.mycarinspector.DB.ReadPref;
import com.main.mycarinspector.DB.SavePref;
import com.main.mycarinspector.Model.Mechanic_model;
import com.main.mycarinspector.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    DrawerLayout mDrawerLayout;
    ImageView menu,mechanic_list;
    private View navHeader;
    TextView user_profile,edite_profile,filter_text,short_text;
    BottomSheetBehavior sheetBehavior;
    BottomSheetBehavior sheetBehavior_short;
    LinearLayout layoutBottomSheet,bottom_sheet_short;
    SeekBar seekbar_filter;
    CircleImageView imageView_userpic;
    TextView name_user,user_email,seekBarValue;
    Button apply_filter_button,apply_short_button;
    ReadPref readPref;
    ProgressDialog pd;
    SavePref savePref;
    MarkerOptions markerOptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        readPref = new ReadPref(this);
        savePref = new SavePref();
        init();
        }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /*LatLng sydney = new LatLng(28.5414, 15177.3970);
        mMap.addMarker(new MarkerOptions().position(sydney).title("NSEZ"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        mMap.animateCamera( CameraUpdateFactory.zoomTo( 16.0f ) );
        Mechanic_list();
    }
    public  void init(){
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        menu=findViewById(R.id.menu);
        mechanic_list=findViewById(R.id.mechanic_list);
        filter_text=findViewById(R.id.filter_text);
        short_text=findViewById(R.id.short_text);
        seekbar_filter=findViewById(R.id.seekbar_filter);
        apply_filter_button=findViewById(R.id.apply_filter_button);
        apply_short_button=findViewById(R.id.apply_short_button);
        layoutBottomSheet=findViewById(R.id.bottom_sheet);
        bottom_sheet_short=findViewById(R.id.bottom_sheet_short);
        layoutBottomSheet.setVisibility(View.GONE);
        bottom_sheet_short.setVisibility(View.GONE);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior_short= BottomSheetBehavior.from(bottom_sheet_short);
        mechanic_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        apply_short_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheetBehavior_short.setState(BottomSheetBehavior.STATE_COLLAPSED);
                bottom_sheet_short.setVisibility(View.GONE);
            }
        });
        apply_filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                layoutBottomSheet.setVisibility(View.GONE);
            }
        });
        short_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottom_sheet_short.setVisibility(View.VISIBLE);
                if (sheetBehavior_short.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior_short.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    sheetBehavior_short.setState(BottomSheetBehavior.STATE_COLLAPSED);

                }
            }
        });
        filter_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutBottomSheet.setVisibility(View.VISIBLE);
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                }
            }
        });
        seekbar_filter.setProgress(0);
        seekbar_filter.incrementProgressBy(0);
        seekbar_filter.setMax(10);
        seekBarValue= findViewById(R.id.seekbarvalue);
        seekBarValue.setText(" 0 "+getText(R.string.year_text));
        seekbar_filter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress +1;
                seekBarValue.setText(String.valueOf(progress)+" "+ getText(R.string.year_text));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




    }

    public void Mechanic_list() {
        {
            pd = new ProgressDialog(MapsActivity.this);
            pd.setMessage("Please Wait ..");
            pd.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, APIUrl.BASE_URL+"users/search_mechanics",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.dismiss();
                            String message = response;
                            Log.d("TAG@123", response);
                            System.out.println(response);
                            if (response != null) {
                                try{
                                    JSONObject jsonObject=new JSONObject(response.toString());
                                    JSONArray jsonArray=jsonObject.getJSONArray("mechanics");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject1=jsonArray.getJSONObject(i);

                                        if(!jsonObject1.getString("latitude").equals("")){
                                            int height = 100;
                                            int width = 70;
                                            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.map_icon);
                                            Bitmap b=bitmapdraw.getBitmap();
                                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                                            LatLng sydney = new LatLng(Double.parseDouble(jsonObject1.getString("latitude")), Double.parseDouble(jsonObject1.getString("longitude")));
                                            mMap.addMarker(new MarkerOptions().position(sydney).icon(BitmapDescriptorFactory.fromBitmap(smallMarker))).showInfoWindow();
                                            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                                        }






                                    }



                                }
                                catch (JSONException e){
                                    Log.d("TAG@123", e.toString());
                                }


                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.dismiss();
                            String message;
                            Log.d("TAG@123", error.getMessage().toString());
                            if (error instanceof NetworkError) {
                                message = "Cannot connect to Internet...Please check your connection!";
                            } else if (error instanceof ServerError) {
                                message = "The server could not be found. Please try again after some time!!";
                            } else if (error instanceof AuthFailureError) {
                                message = "Cannot connect to Internet...Please check your connection!";
                            } else if (error instanceof ParseError) {
                                message = "Parsing error! Please try again after some time!!";
                            } else if (error instanceof NoConnectionError) {
                                message = "Cannot connect to Internet...Please check your connection!";
                            } else if (error instanceof TimeoutError) {
                                message = "Connection TimeOut! Please check your internet connection.";
                            }

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("latitude", readPref.getcar_latitude());
                    params.put("longitude", readPref.getcar_longitude());
                    params.put("city", readPref.getcar_city());
                    params.put("post_code", readPref.getcar_post());
                    params.put("car_id", readPref.getpcar_id());
                    System.out.println("params - " + params.toString());
                    Log.d("TAG@123", params.toString());
                    return params;
                }
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<String, String>();
                    String credentials = "apiadmin" + ":" + "mycar@123";
                    String auth = "Basic "
                            + Base64.encodeToString(credentials.getBytes(),
                            Base64.NO_WRAP);
                    params.put("Authorization", auth);
                    params.put("X-API-KEY", "mycarinspectors@123");
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            stringRequest.setShouldCache(false);
            requestQueue.add(stringRequest);
        }
    }


}
