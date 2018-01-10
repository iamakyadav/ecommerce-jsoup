package com.example.main.demo;

import android.content.Context;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import Model.SearchListModel;

public class MainActivity extends AppCompatActivity {

    private EditText et_search;
    private RecyclerView recyclerView_list;
    private SearchListAdapter searchListAdapter;
    ArrayList<SearchListModel> SearchArrayList;
    private String search_text="mobile";
    private Timer timer;
    private ProgressBar progbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SearchArrayList = new ArrayList<>();
        init_view();
    }

    private void init_view(){
        et_search=(EditText)findViewById(R.id.et_search);
        recyclerView_list=(RecyclerView) findViewById(R.id.recyclerView_list);
        progbar=(ProgressBar)findViewById(R.id.progbar);
        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 2);
        recyclerView_list.setLayoutManager(layoutManager);
        searchListAdapter = new SearchListAdapter();
        recyclerView_list.setAdapter(searchListAdapter);
        if(isNetworkAvailable(MainActivity.this)) {
            new GetData().execute("");
        }else{
            Toast.makeText(getApplicationContext(),"No Internet connection!",Toast.LENGTH_LONG).show();
        }
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    timer.cancel();
                } catch (Exception e) {
                }

                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(et_search.getText().length() > 0)
                                {
                                    search_text=et_search.getText().toString().trim();
                                    if(isNetworkAvailable(MainActivity.this)) {
                                        new GetData().execute("");
                                    }else{
                                        Toast.makeText(getApplicationContext(),"No Internet connection!",Toast.LENGTH_LONG).show();
                                    }
                                }
                                hideKeyboard(MainActivity.this,et_search);

                            }
                        });

                    }
                }, 1000);
            }
        });
    }

    private class GetData extends AsyncTask<String, Void, String> {
        Document doc;
        @Override
        protected void onPreExecute() {
            progbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                doc = Jsoup.connect("https://www.snapdeal.com/search?keyword="+search_text+"&sort=rlvncy").get();
                Elements all_product=doc.getElementsByClass("disInBlock");
                SearchArrayList.clear();
                for(int i=0;i<all_product.size();i++) {
                    SearchListModel model=new SearchListModel();
                    String off_value=all_product.get(i).getElementsByClass("perOff").text();
                    String title=all_product.get(i).getElementsByClass("pdName").text();
                    String NewPrice=all_product.get(i).getElementsByClass("pdNewPrice").text();
                    String OldPrice=all_product.get(i).getElementsByClass("pdOldPrice").text();
                    String temp = all_product.get(i).getElementsByAttribute("style")
                            .toString();
                    // URL of image
                    String imageStrg = temp.substring(temp.indexOf("(") + 1, temp.indexOf(")"));
                    imageStrg=imageStrg.replaceAll("[$']", "");
                    model.set_image("http:"+imageStrg);
                    model.set_title(title);
                    model.set_new_price(NewPrice);
                    model.set_old_price(OldPrice);
                    model.set_off_percentage(off_value);
                    SearchArrayList.add(model);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            progbar.setVisibility(View.GONE);
            searchListAdapter.notifyDataSetChanged();
        }
    }

    public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.MyViewHolder> {


        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView iv_pic;
            public TextView tv_title,tv_old_price,tv_new_price,tv_off;

            public MyViewHolder(View view) {
                super(view);
                iv_pic=(ImageView)view.findViewById(R.id.iv_pic);
                tv_title=(TextView) view.findViewById(R.id.tv_title);
                tv_old_price=(TextView) view.findViewById(R.id.tv_old_price);
                tv_new_price=(TextView) view.findViewById(R.id.tv_new_price);
                tv_off=(TextView) view.findViewById(R.id.tv_off);
            }
        }


        public SearchListAdapter()
        {

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_search_list, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
            SearchListModel model=SearchArrayList.get(position);
            Ion.with(viewHolder.iv_pic)
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.no_image)
                    .load(model.get_image());
            viewHolder.tv_title.setText(model.get_title());
            viewHolder.tv_old_price.setText(model.get_old_price());
            viewHolder.tv_old_price.setPaintFlags(viewHolder.tv_old_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.tv_new_price.setText(model.get_new_price());
            viewHolder.tv_off.setText(model.get_off_percentage());

        }

        @Override
        public int getItemCount() {
            return SearchArrayList.size();
        }

    }

    /**
     * this method is used to hide keyboard
     */
    public static void hideKeyboard(Context context, View view) {
        try {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
