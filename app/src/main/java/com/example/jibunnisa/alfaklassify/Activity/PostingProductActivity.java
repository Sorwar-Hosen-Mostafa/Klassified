package com.example.jibunnisa.alfaklassify.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.jibunnisa.alfaklassify.MainActivity;
import com.example.jibunnisa.alfaklassify.R;
import com.example.jibunnisa.alfaklassify.Utils;
import com.example.jibunnisa.alfaklassify.model.PostingProduct;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PostingProductActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private static final int GALLERY_REQUEST = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;
    //private TravelMoment objMoment;
    Button takePhotoBtn, chosePhotoBtn;
    ImageView imageView;
    EditText descriptionET;
    Button submitBtn;

    String eventName;
    String date,time;
    Bitmap rotatedBMP;
    java.util.Calendar calendar;
    SimpleDateFormat timeFormat,dateFormat;
    private Uri imageUri = null;
    private StorageReference mStorage;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;
    String mCurrentPhotoPath;
    String photoName;
    String momentId ;
    String brandName;
    String categoryName;
    String typeName;
    String modelName;
    String locationName;
    String condition;
    String userId;
    String priceText;
    String description;
    AutoCompleteTextView txtProductCategory, txtProductType,txtLocation;
    AutoCompleteTextView txtBrand,txtCondition;
    EditText txtModel,priceET;
    ArrayList<String> productCategoryArray=new ArrayList<String>();
    ArrayList<String> productTypeArray=new ArrayList<String>();
    ArrayList<String> brandArray=new ArrayList<String>();
    ArrayList<String> locationArray=new ArrayList<String>();
    ArrayList<String> conditionArray=new ArrayList<String>();
    ArrayAdapter<String> categoryAdapter ;
    ArrayAdapter<String> typeAdapter ;
    ArrayAdapter<String> brandAdapter ;
    ArrayAdapter<String> locationAdapter ;
    private Toolbar toolbar;
    ArrayAdapter<String> conditionAdapter ;
    private static final String TAG = "PostProduct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting_product);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Post Advertisement");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){

            loadLoginActivity();
        }
        else {
            postProduct();
        }
    }

    private void postProduct() {
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Utils.FIRE_PRODUCTS);
        mProgress = new ProgressDialog(this);

        chosePhotoBtn = (Button) findViewById(R.id.chose_photo_btn);
        imageView = (ImageView) findViewById(R.id.imageView);

        descriptionET = (EditText) findViewById(R.id.descriptionET);
        txtModel = (EditText) findViewById(R.id.txt_model);
        submitBtn = (Button)findViewById(R.id.submitBtn);
        takePhotoBtn = (Button) findViewById(R.id.take_photo_btn);
        txtProductCategory = (AutoCompleteTextView) findViewById(R.id.txt_product_category);
        txtProductType = (AutoCompleteTextView) findViewById(R.id.txt_product_type);
        txtLocation = (AutoCompleteTextView) findViewById(R.id.txt_location);
        txtBrand = (AutoCompleteTextView) findViewById(R.id.txt_product_brand);
        txtCondition = (AutoCompleteTextView) findViewById(R.id.txt_condition);
        priceET = (EditText) findViewById(R.id.priceET);
        submitBtn.setOnClickListener(onClick);
        chosePhotoBtn.setOnClickListener(onClick);
        takePhotoBtn.setOnClickListener(onClick);
        txtProductCategory.setOnClickListener(onClick);
        txtProductType.setOnClickListener(onClick);
        txtLocation.setOnClickListener(onClick);
        txtBrand.setOnClickListener(onClick);
        txtCondition.setOnClickListener(onClick);
        Intent intent = getIntent();
       /* objMoment = (TravelMoment)intent.getSerializableExtra("moment");
        if(objMoment != null){
            momentId = objMoment.getMomentId();
            eventid= objMoment.getEventId();
            eventName = objMoment.getEventName();
            descriptionET.setText(objMoment.getDescription());
            Picasso.with(getApplicationContext()).load(objMoment.getImageUrl()).fit().centerCrop().into(imageView);
        }
        else {
            eventid=intent.getStringExtra("event_id");
            eventName = intent.getStringExtra("event_name");
        }*/

        calendar = java.util.Calendar.getInstance(Locale.getDefault());

        timeFormat = new SimpleDateFormat("HH:mm a");
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        date= dateFormat.format(calendar.getTime());
        time = timeFormat.format(calendar.getTime());
        loadProductCategory();
        loadCondition();
        loadLocation();

    }

    private void loadProductCategory() {
        productCategoryArray =new ArrayList<String>();
        productCategoryArray.add("Electronics");
        productCategoryArray.add("Cars & Vehicles");
        productCategoryArray.add("Clothing,Health & Beauty");

        categoryAdapter =new ArrayAdapter<String>(getApplicationContext(), R.layout.autocompletetext, productCategoryArray);

        txtProductCategory.setAdapter(categoryAdapter);
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.take_photo_btn){
                dispatchTakePictureIntent();
            }
            else if(view.getId() == R.id.chose_photo_btn){
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
            else if(view.getId() ==  R.id.submitBtn){

                categoryName = txtProductCategory.getText().toString().trim();
                typeName = txtProductType.getText().toString().trim();
                locationName = txtLocation.getText().toString().trim();
                brandName = txtBrand.getText().toString().trim();
                modelName = txtModel.getText().toString().trim();
                condition = txtCondition.getText().toString().trim();
                description = descriptionET.getText().toString().trim();
                priceText = priceET.getText().toString().trim();
                if(categoryName.equals(""))
                {
                    txtProductCategory.setError("Please select a product category");
                    return;
                }
                if(typeName.equals(""))
                {
                    txtProductType.setError("Please select a product type");
                    return;
                }
                if(locationName.equals(""))
                {
                    txtLocation.setError("Please select a location");
                    return;
                }
                if(brandName.equals(""))
                {
                    if(brandArray.size()>0) {
                        txtBrand.setError("Please select a brand");
                        return;
                    }
                }
                if(modelName.equals(""))
                {
                    txtModel.setError("Please enter model");
                    return;
                }
                if(condition.equals(""))
                {
                    txtCondition.setError("Please select a condition");
                    return;
                }
                if(priceText.equals("")||priceText.equals("0"))
                {
                    priceET.setError("Product price must be gather then 0");
                    return;
                }
                if(imageUri == null){
                    Toast.makeText(getApplicationContext(), "Please select or capture an image", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    saveProduct();
                }
            }
            else if(view.getId() == R.id.txt_product_category){
                txtProductCategory.showDropDown();
            }
            else if(view.getId() == R.id.txt_product_type){
                if(txtProductCategory.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please select a product category", Toast.LENGTH_LONG).show();
                }
                else
                {
                    loadProductTypeByCategory();
                    txtProductType.showDropDown();

                }
            }
            else if(view.getId() == R.id.txt_location){
                if(txtProductType.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please select a product type", Toast.LENGTH_LONG).show();
                }
                else
                {
                    loadLocation();
                    txtLocation.showDropDown();

                }
            }
            else if(view.getId() == R.id.txt_product_brand){
                if(txtProductType.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please select a product type", Toast.LENGTH_LONG).show();
                }
                else
                {
                    loadBrand();
                    txtBrand.showDropDown();

                }
            }
            else if(view.getId() == R.id.txt_condition){
                if(txtProductType.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please select a product type", Toast.LENGTH_LONG).show();
                }
                else
                {
                    loadCondition();
                    txtCondition.showDropDown();

                }
            }
        }
    };

    private void loadCondition() {
        conditionArray =new ArrayList<String>();
        conditionArray.add("Used");
        conditionArray.add("New");
        conditionAdapter =new ArrayAdapter<String>(getApplicationContext(), R.layout.autocompletetext, conditionArray);

        txtCondition.setAdapter(conditionAdapter);

    }

    private void loadBrand() {
        String typeName= txtProductType.getText().toString();
        brandArray =new ArrayList<String>();
        if(typeName.equals("Mobile Phones")){
            brandArray.add("Aamra");
            brandArray.add("Samsung");
            brandArray.add("Symphony");
            brandArray.add("Nokia");
            brandArray.add("HTC");
            brandArray.add("Huawei");
            brandArray.add("Lenovo");
            brandArray.add("Maximus");
            brandArray.add("Motorola");
            brandArray.add("OPPO");
            brandArray.add("Sony");
            brandArray.add("Xiaomi");
            brandArray.add("Other brand");
        }
        else if(typeName.equals("Computer and Tablets")){

            brandArray.add("Acer");
            brandArray.add("Apple MacBook");
            brandArray.add("Asus");
            brandArray.add("Blackberry");
            brandArray.add("Dell");
            brandArray.add("IBM");
            brandArray.add("Walton");
            brandArray.add("HP");
            brandArray.add("Other brand");
        }
        else if(typeName.equals("TVs")){

            brandArray.add("Haier");
            brandArray.add("Konka");
            brandArray.add("LG");
            brandArray.add("JVC");
            brandArray.add("Panasonic");
            brandArray.add("Philips");
            brandArray.add("Rangs");
            brandArray.add("Samsung");
            brandArray.add("Singer");
            brandArray.add("Sony");
            brandArray.add("TCL");
            brandArray.add("Toshiba");
            brandArray.add("Walton");
            brandArray.add("Other brand");
        }
        else if(typeName.equals("Cameras")){

            brandArray.add("Canon");
            brandArray.add("Casio");
            brandArray.add("Fujifilm");
            brandArray.add("Kodak");
            brandArray.add("Minolta");
            brandArray.add("Panasonic");
            brandArray.add("Sony");
            brandArray.add("Samsung");
            brandArray.add("Pentax");
            brandArray.add("Other brand");
        }

        else if(typeName.equals("Cars")){

            brandArray.add("Alfa Romeo");
            brandArray.add("Audi");
            brandArray.add("BMW");
            brandArray.add("Buick");
            brandArray.add("Cadillac");
            brandArray.add("Chery");
            brandArray.add("Chevrolet");
            brandArray.add("Daewoo");
            brandArray.add("Datsun");
            brandArray.add("Dodge");
            brandArray.add("Ferrari");
            brandArray.add("GMC");
            brandArray.add("Hino");
            brandArray.add("Honda");
            brandArray.add("Isuzu");
            brandArray.add("Jeep");
            brandArray.add("Land Rover");
            brandArray.add("Lexus");
            brandArray.add("Maruti Suzuki");
            brandArray.add("Mitsubishi");
            brandArray.add("Nissan");
            brandArray.add("Tata");
            brandArray.add("Toyota");
            brandArray.add("Volvo");
            brandArray.add("Other brand");
        }
        else if(typeName.equals("Motorbikes")){

            brandArray.add("Alliance");
            brandArray.add("Bajaj");
            brandArray.add("Bennett");
            brandArray.add("Butterfly");
            brandArray.add("Runner");
            brandArray.add("Emma");
            brandArray.add("Frantic");
            brandArray.add("Freedom");
            brandArray.add("H Power");
            brandArray.add("Haojin");
            brandArray.add("Hero");
            brandArray.add("Honda");
            brandArray.add("Intraco");
            brandArray.add("Jamuna");
            brandArray.add("Mahindra");
            brandArray.add("Sunsuki");
            brandArray.add("TVS");
            brandArray.add("Walton");
            brandArray.add("Other brand");
        }
        else if(typeName.equals("Trucks,Vans & Buses")){

            brandArray.add("Ashok Leyland");
            brandArray.add("Bedford");
            brandArray.add("Daihatsu");
            brandArray.add("Eicher");
            brandArray.add("Hino");
            brandArray.add("JAC");
            brandArray.add("Maruti");
            brandArray.add("Mazda");
            brandArray.add("Toyota");
            brandArray.add("Zonda");
            brandArray.add("Other brand");
        }

        brandAdapter =new ArrayAdapter<String>(getApplicationContext(), R.layout.autocompletetext, brandArray);

        txtBrand.setAdapter(brandAdapter);
    }

    private void loadLocation() {

        locationArray =new ArrayList<String>();

            locationArray.add("Mirpur");
            locationArray.add("Dhanmondi");
            locationArray.add("Gulshan");
            locationArray.add("Mohakhali");
            locationArray.add("Uttara");
            locationArray.add("Firmgate");

            locationAdapter =new ArrayAdapter<String>(getApplicationContext(), R.layout.autocompletetext, locationArray);
            txtLocation.setAdapter(locationAdapter);


    }

    private void loadProductTypeByCategory() {
        String categoryName= txtProductCategory.getText().toString();
        productTypeArray =new ArrayList<String>();

        if(categoryName.equals("Electronics")){

            productTypeArray.add("Mobile Phones");
            productTypeArray.add("Computer and Tablets");
            productTypeArray.add("TVs");
            productTypeArray.add("Cameras");
            productTypeArray.add("Video Games");

        }
        else if(categoryName.equals("Cars & Vehicles")){

            productTypeArray.add("Cars");
            productTypeArray.add("Motorbikes");
            productTypeArray.add("Trucks,Vans & Buses");
            productTypeArray.add("Tractors");
            productTypeArray.add("Boats");

        }
        else if(categoryName.equals("Clothing,Health & Beauty")){

            productTypeArray.add("Clothing");
            productTypeArray.add("Shoes");
            productTypeArray.add("Jewellery");
            productTypeArray.add("Watches");
            productTypeArray.add("Bags");
        }

        typeAdapter =new ArrayAdapter<String>(getApplicationContext(), R.layout.autocompletetext, productTypeArray);

        txtProductType.setAdapter(typeAdapter);

    }

    private void loadLoginActivity() {
        Intent loginIntent = new Intent(getApplicationContext(),LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
        finish();
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        imageUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        String storageDir = Environment.getExternalStorageDirectory() + "/picimage";
        File dir = new File(storageDir);
        if (!dir.exists())
            dir.mkdir();

        File image = new File(storageDir + "/" + imageFileName + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        imageUri = Uri.fromFile(image);
        photoName=timeStamp;
        Log.i(TAG, "photo path = " + mCurrentPhotoPath);
        return image;
    }

    private void saveProduct() {
        mProgress.setMessage("Posting product...");
        mProgress.show();
        userId  = firebaseAuth.getCurrentUser().getUid();
        final PostingProduct objProduct = new PostingProduct();
        objProduct.setCategoryName(categoryName);
        objProduct.setTypeName(typeName);
        objProduct.setBrandName(brandName);
        objProduct.setModelName(modelName);
        objProduct.setLocationName(locationName);
        objProduct.setCondition(condition);
        objProduct.setPrice(Double.parseDouble(priceText));
        objProduct.setDescription(description);
        objProduct.setDate(date);
        objProduct.setTime(time);
        objProduct.setUserId(userId);

        if(imageUri != null){

            StorageReference filePath = mStorage.child("Product_Images").child(imageUri.getLastPathSegment());
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    String productId = mDatabase.push().getKey();
                    objProduct.setProductId(productId);
                    objProduct.setImageUrl(downloadUri.toString());
                    mDatabase.child(productId).setValue(objProduct);
                    mProgress.dismiss();
                    finish();
                    startActivity(new Intent(PostingProductActivity.this,MainActivity.class));

                }
            });
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            setPic();

        }

    }

    private void setPic() {
        int targetW = 500;
        int targetH = 600;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor << 1;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        Matrix mtx = new Matrix();
        mtx.postRotate(0);
        // Rotating Bitmap
        rotatedBMP = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);

        if (rotatedBMP != bitmap)
            bitmap.recycle();

        imageView.setImageBitmap(rotatedBMP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:

                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
