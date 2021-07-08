package sparrow.com.touristfriend;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class NewPlaceActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private EditText placeEditText, addressEditText;
    private CardView chooseImageCardView, addCardView;
    private ProgressBar progressBar;
    private Uri imageUri;

    DatabaseReference databaseReference;
    StorageReference storageReference;
    StorageTask uploadTask;

    private static final int IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_place);

        databaseReference = FirebaseDatabase.getInstance().getReference("Upload");
        storageReference = FirebaseStorage.getInstance().getReference("Upload");

        imageView = findViewById(R.id.imageViewId);
        placeEditText = findViewById(R.id.placeEditTextId);
        addressEditText = findViewById(R.id.addressEditTextId);
        chooseImageCardView = findViewById(R.id.chooseImageCardViewId);
        addCardView = findViewById(R.id.addCardViewId);
        progressBar = findViewById(R.id.progressbarId);

        chooseImageCardView.setOnClickListener(this);
        addCardView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chooseImageCardViewId:
                openFileChooser();
                break;
            case R.id.addCardViewId:
                if (uploadTask!=null && uploadTask.isInProgress()){
                    Toast.makeText(getApplicationContext(),"Uploading in progress",Toast.LENGTH_LONG).show();
                } else{
                    addPlace();
                }
                break;
        }
    }

    public String getFileExtension(Uri imageUri){
        ContentResolver contentResolver =getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    private void addPlace() {
        String placeName = placeEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        if (placeName.isEmpty()){
            placeEditText.setError("Enter a place name");
            placeEditText.requestFocus();
            return;
        } else if (address.isEmpty()){
            addressEditText.setError("Enter a address");
            addressEditText.requestFocus();
            return;
        }

        StorageReference ref = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));

        ref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUrl = uriTask.getResult();

                        Upload upload = new Upload(placeName, address, downloadUrl.toString());
                        Intent intent = new Intent(getApplicationContext(),PlaceActivity.class);
                        startActivity(intent);
                        String uploadId = databaseReference.push().getKey();

                        databaseReference.child(uploadId).setValue(upload);
                    }
                })
                .addOnFailureListener(new  OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageView);
        }
    }
}