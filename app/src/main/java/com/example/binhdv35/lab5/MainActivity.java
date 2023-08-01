package com.example.binhdv35.lab5;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.binhdv35.lab5.contacts.OnLoad;
import com.example.binhdv35.lab5.contacts.URLJson;
import com.example.binhdv35.lab5.models.Product;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnLoad {

    private ListView lvProduct;
    private Button btnAdd;
    private List<Product> productList;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvProduct = findViewById(R.id.lv_product);
        btnAdd = findViewById(R.id.btn_add_product);

        productList = new ArrayList<>();

        getData();

        adapter = new ProductAdapter(productList);
        adapter.interCall(MainActivity.this::onload);

        lvProduct.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(R.layout.dialog_product);
            EditText edName = bottomSheetDialog.findViewById(R.id.ed_name);
            EditText edPrice = bottomSheetDialog.findViewById(R.id.ed_price);
            EditText edDes = bottomSheetDialog.findViewById(R.id.ed_des);
            Button btnAdd = bottomSheetDialog.findViewById(R.id.dia_btn_add);
            btnAdd.setOnClickListener(v1 -> {
                String name = edName.getText().toString().trim();
                int price = Integer.parseInt(edPrice.getText().toString().trim());
                String des = edDes.getText().toString().trim();

                JSONObject productJson = new JSONObject();
                try {
                    productJson.put("name" , name);
                    productJson.put("price" , price);
                    productJson.put("description" , des);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URLJson.KEY_POST_PRODUCT, productJson,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int success = response.getInt("success");
                                    String message = response.getString("message");
                                    Toast.makeText(MainActivity.this,
                                            ""+message, Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                getData();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
//                            Toast.makeText(getContext(),
//                                    "Error: "+ error.getMessage() , Toast.LENGTH_SHORT).show();
                                Log.d("json_error" , error.getMessage());
                            }
                        }
                );

                RequestQueueController.getInstance().addToRequestQueue(request);
                bottomSheetDialog.dismiss();
            });
            bottomSheetDialog.show();
        });
    }

    private void getData() {
        JsonObjectRequest objectRequest = new JsonObjectRequest(URLJson.KEY_GET_PRODUCT, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int success = response.getInt("success");
                    JSONArray jsonArray = response.getJSONArray("products");
                    for (int i = 0; i < jsonArray.length() ; i++) {
                        JSONObject product = (JSONObject) jsonArray.get(i);
                        String id = product.getString("_id");
                        String name = product.getString("name");
                        int price = product.getInt("price");
                        String description = product.getString("description");

                        productList.add(new Product(id,name,description,price));
                        adapter = new ProductAdapter(productList);
                        lvProduct.setAdapter(adapter);
                    }

                    if (success ==0 ){
                        Toast.makeText(MainActivity.this,
                                "Lay du lieu that bai!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MainActivity.this,
                                "Lay du lieu thanh cong!", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueueController.getInstance().addToRequestQueue(objectRequest);
    }

    @Override
    public void onload() {
        getData();
    }
}