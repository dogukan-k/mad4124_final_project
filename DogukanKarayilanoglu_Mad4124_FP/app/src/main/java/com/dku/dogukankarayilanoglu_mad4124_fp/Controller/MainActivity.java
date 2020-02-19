package com.dku.dogukankarayilanoglu_mad4124_fp.Controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.dku.dogukankarayilanoglu_mad4124_fp.Database.CategoryDatabase;
import com.dku.dogukankarayilanoglu_mad4124_fp.Modal.Category;
import com.dku.dogukankarayilanoglu_mad4124_fp.Modal.Item;
import com.dku.dogukankarayilanoglu_mad4124_fp.R;
import com.google.gson.Gson;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    final CategoryDatabase categoryDatabase = new CategoryDatabase(this);
    ArrayList<Category> categoryList = new ArrayList();
    ArrayAdapter categoryAdapter = null;



    Button addCategoryButton;
    Button addItemButton;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addCategoryButton = findViewById(R.id.btn_add_category);
        addItemButton = findViewById(R.id.btn_add_item);
        listView = findViewById(R.id.list_view);


        //Fill arraylists...
        categoryList = categoryDatabase.loadCategories();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent intent = new Intent(MainActivity.this,ShowNotesController.class);
                intent.putExtra("incomingCategoryId",categoryList.get(position).getId());
                intent.putExtra("incomingCategoryIndex",position);
                startActivity(intent);

            }
        });


        //Add New Category Button...
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryList = categoryDatabase.loadCategories();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Add Category", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        boolean checkDuplicate = false;

                        if(!input.getText().toString().equals("")){


                            //Check duplication of new category name...
                            for(int x = 0 ; x<categoryList.size() ; x++){
                                if(categoryList.get(x).getCategoryName().equals(input.getText().toString())){
                                    checkDuplicate=true;
                                }
                            }



                            if(checkDuplicate == false){
                                //Add new category...
                                Log.d("tag", "onClick: "+checkDuplicate);
                                Category category = new Category();
                                category.setCategoryName(input.getText().toString());


                                //To initialize category
                                ArrayList<Item> arrayList = new ArrayList<>();
                                Gson gson = new Gson();
                                String input = gson.toJson(arrayList);
                                category.setArrayOfItems(input);



                                categoryDatabase.addCategory(category);

                                //Reload list view
                                categoryList = categoryDatabase.loadCategories();
                                categoryAdapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,categoryList);
                                listView.setAdapter(categoryAdapter);


                            }

                            else{
                                //Duplicate message...
                                Toast.makeText(MainActivity.this,"Category name exists , try new one!",Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();

            }
        });


        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                categoryList = categoryDatabase.loadCategories();

                if(categoryList.size() == 0){
                    Toast.makeText(MainActivity.this,"You should add category first",Toast.LENGTH_SHORT).show();
                }

                else {

                    Intent intent = new Intent(MainActivity.this,AddNoteController.class);
                    startActivity(intent);

                }

            }
        });





//        ArrayList<Item> items = new ArrayList<>();
//
//        Item item = new Item();
//        item.setTitle("first");
//
//        Item item2 = new Item();
//        item2.setTitle("second");
//
//
//        items.add(item);
//        items.add(item2);
//
//
//        Category category = new Category();
//        Gson gson = new Gson();
//        String input = gson.toJson(items);
//        category.setArrayOfItems(input);
//
//        ArrayList<Item> test = category.getArrayList();

    }

    @Override
    protected void onResume() {
        super.onResume();

        //Fill list view...
        categoryAdapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,categoryList);
        listView.setAdapter(categoryAdapter);
    }




}
