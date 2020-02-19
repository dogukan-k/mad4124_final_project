package com.dku.dogukankarayilanoglu_mad4124_fp.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dku.dogukankarayilanoglu_mad4124_fp.Database.CategoryDatabase;
import com.dku.dogukankarayilanoglu_mad4124_fp.Modal.Category;
import com.dku.dogukankarayilanoglu_mad4124_fp.Modal.Item;
import com.dku.dogukankarayilanoglu_mad4124_fp.R;

import java.util.ArrayList;

public class ShowNotesController extends AppCompatActivity {

    ListView listView;
    TextView txtFolderName;
    EditText searchBar;

    int incomingFolderId;
    int incomingCategoryIndex;
    Category category;
    ArrayList<Item> arrayList;
    ArrayAdapter arrayAdapter;

    boolean isSearchFinished = true;


    final CategoryDatabase categoryDatabase = new CategoryDatabase(this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notes_controller);



        incomingFolderId = getIntent().getExtras().getInt("incomingCategoryId");
        incomingCategoryIndex = getIntent().getExtras().getInt("incomingCategoryIndex");


        txtFolderName = findViewById(R.id.txtFolderName);
        listView = findViewById(R.id.listViewS);
        searchBar = findViewById(R.id.searchText);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ShowNotesController.this.arrayAdapter.getFilter().filter(charSequence);
                Log.d("tag", "onTextChanged: "+arrayList.size());
                isSearchFinished=false;
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.toString().equals("")){
                    isSearchFinished=true;
                }

            }


        });








        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent intent = new Intent(ShowNotesController.this,NoteDetailAndUpdateController.class);

                Log.d("tag", "onItemClick: "+isSearchFinished);
                if(isSearchFinished==true){
                    intent.putExtra("incomingItemId", arrayList.get(position).getId());

                }

                else {

                    Item item = (Item) adapterView.getItemAtPosition(position);
                    intent.putExtra("incomingItemId", item.getId());
                }

                intent.putExtra("incomingCategoryId", incomingFolderId);
                intent.putExtra("incomingCategoryIndex", incomingCategoryIndex );
                startActivity(intent);

            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();

        category = categoryDatabase.getCategory(incomingFolderId);
        arrayList = category.getArrayList();


        if(arrayList != null){
            arrayAdapter = new ArrayAdapter(ShowNotesController.this,android.R.layout.simple_list_item_1,arrayList);
            listView.setAdapter(arrayAdapter);
        }


        txtFolderName.setText("Category:"+category.getCategoryName());

    }
}
