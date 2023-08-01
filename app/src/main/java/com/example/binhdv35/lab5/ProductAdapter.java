package com.example.binhdv35.lab5;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.binhdv35.lab5.contacts.OnLoad;
import com.example.binhdv35.lab5.contacts.URLJson;
import com.example.binhdv35.lab5.models.Product;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ProductAdapter extends BaseAdapter {

    private List<Product> productList;
     OnLoad onLoad;

    public void interCall(OnLoad onLoad){
        this.onLoad = onLoad;
    }

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class ViewHolder{
        private TextView tvName, tvPrice, tvDes, tvDelete, tvEdit;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView ==null){
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) parent.getContext()
                    .getSystemService(parent.getContext().LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_product, null);

            viewHolder.tvName = convertView.findViewById(R.id.item_tv_name);
            viewHolder.tvDes = convertView.findViewById(R.id.item_tv_des);
            viewHolder.tvPrice = convertView.findViewById(R.id.item_tv_price);
            viewHolder.tvDelete = convertView.findViewById(R.id.item_tv_delete);
            viewHolder.tvEdit = convertView.findViewById(R.id.item_tv_edit);

            convertView.setTag(viewHolder);

        }else viewHolder = (ViewHolder)convertView.getTag();

        Product product = productList.get(position);
        viewHolder.tvName.setText(product.getName());
        viewHolder.tvPrice.setText(product.getPrice()+"");
        viewHolder.tvDes.setText(product.getDes());

        viewHolder.tvEdit.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(v.getContext());
            bottomSheetDialog.setContentView(R.layout.dialog_product);
            EditText edName = bottomSheetDialog.findViewById(R.id.ed_name);
            EditText edPrice = bottomSheetDialog.findViewById(R.id.ed_price);
            EditText edDes = bottomSheetDialog.findViewById(R.id.ed_des);
            Button btnAdd = bottomSheetDialog.findViewById(R.id.dia_btn_add);

            edName.setText(product.getName());
            edPrice.setText(product.getPrice()+"");
            edDes.setText(product.getDes());

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

                String url = URLJson.KEY_PUT_PRODUCT + product.getId();
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, productJson,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    int success = response.getInt("success");
                                    String message = response.getString("message");
                                    Toast.makeText(v.getContext(), ""+message, Toast.LENGTH_SHORT).show();
                                    onLoad.onload();
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
//                            Log.d("json_error" , error.getMessage());
                            }
                        }
                );

                RequestQueueController.getInstance().addToRequestQueue(request);
                bottomSheetDialog.dismiss();
            });
            bottomSheetDialog.show();
        });

        viewHolder.tvDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Xoa");
            builder.setMessage("Bạn có muốn đăng xoa không?");
            builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String url = URLJson.KEY_DELETE_PRODUCT + product.getId();
                    StringRequest request = new StringRequest(Request.Method.DELETE, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(v.getContext(),
                                    "Error: "+error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                    RequestQueueController.getInstance().addToRequestQueue(request);
//                    onLoad.onload();
                }
            });

            builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        });

        return convertView;
    }
}
