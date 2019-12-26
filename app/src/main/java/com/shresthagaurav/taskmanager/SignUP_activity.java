package com.shresthagaurav.taskmanager;

        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AlertDialog;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.app.ActivityCompat;
        import androidx.core.content.ContextCompat;
        import androidx.core.content.res.ResourcesCompat;
        import androidx.loader.content.CursorLoader;

        import android.Manifest;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.content.res.Resources;
        import android.database.Cursor;
        import android.graphics.Bitmap;
        import android.graphics.drawable.TransitionDrawable;
        import android.net.Uri;
        import android.os.Bundle;
        import android.os.Environment;
        import android.provider.MediaStore;
        import android.text.TextUtils;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.shresthagaurav.taskmanager.api.ApiClass;
        import com.shresthagaurav.taskmanager.model.ImageModel;
        import com.shresthagaurav.taskmanager.model.User_model;

        import java.io.File;
        import java.text.SimpleDateFormat;
        import java.util.Date;
        import java.util.List;

        import okhttp3.MediaType;
        import okhttp3.MultipartBody;
        import okhttp3.RequestBody;
        import okhttp3.ResponseBody;
        import retrofit2.Call;
        import retrofit2.Callback;
        import retrofit2.Response;

public class SignUP_activity extends AppCompatActivity {
    EditText ed_fn, ed_ln, ed_un, ed_p, ed_rp;
    TextView imagename;
    Button btn_register;
    String selected, imagePath;
    Uri muri;

    ImageView image_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_activity);
        ed_fn = findViewById(R.id.sn_fn);
        ed_ln = findViewById(R.id.sn_ln);
        ed_un = findViewById(R.id.sn_un);
        ed_p = findViewById(R.id.sn_p);
        ed_rp = findViewById(R.id.sn_rp);
        imagename = findViewById(R.id.imagename);
        btn_register = findViewById(R.id.btnsn);
        image_profile = findViewById(R.id.profile_image);
        final ApiClass apiClass = new ApiClass();
        image_profile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Choose();
                    }
                }
        );
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEmpty(ed_fn, ed_ln, ed_un, ed_p, ed_rp, imagename)) {
                    String fname, lname, username, password, repassword, imagesname;
                    fname = ed_fn.getText().toString();
                    lname = ed_ln.getText().toString();
                    username = ed_un.getText().toString();
                    password = ed_p.getText().toString();
                    imagesname = imagename.getText().toString();
                    repassword = ed_rp.getText().toString();
                    if (password.equals(repassword)) {
                        User_model user_model = new User_model(fname, lname, username, password, imagesname);
                        final Call<Void> listcall = apiClass.calls().register(user_model);
                        listcall.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (!response.isSuccessful()) {
                                    Toast.makeText(SignUP_activity.this, "error" + response.code(), Toast.LENGTH_SHORT).show();
                                    Log.d("error", "error" + response.code());
                                    return;
                                }
                                Toast.makeText(SignUP_activity.this, "user created", Toast.LENGTH_SHORT).show();
                                Clear();
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(SignUP_activity.this, "error" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("error", "error   " + t.getLocalizedMessage());
                            }
                        });

                    }
                } else {
                    Toast.makeText(SignUP_activity.this, "check password", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void Choose() {
        checkPermission();
        final CharSequence[] options_list = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUP_activity.this);
        builder.setTitle("Choose Image From");
        builder.setItems(options_list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int count) {
                if (options_list[count].equals("Camera")) {
                    Toast.makeText(SignUP_activity.this, "Camera", Toast.LENGTH_SHORT).show();
                    dispatchTakePictureIntent();
                    selected = "camera";
                } else if (options_list[count].equals("Gallery")) {
                    Toast.makeText(SignUP_activity.this, "Gallery", Toast.LENGTH_SHORT).show();
                    BrowseImage();
                    selected = "Gallery";

                }
            }
        });
        builder.show();
    }

    void BrowseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {


            startActivityForResult(takePictureIntent, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "no image selected", Toast.LENGTH_SHORT).show();
            }
        }

        if (selected.equals("camera")) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image_profile.setImageBitmap(imageBitmap);
            muri = getImageUri();
        } else if (selected.equals("Gallery")) {
            Uri uri = data.getData();
            image_profile.setImageURI(uri);
            muri = data.getData();

        }

        imagePath = ServerImage(muri);
        File file = new File(imagePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("imageFile", file.getName(), requestBody);
        ApiClass apiClass = new ApiClass();
        Call<ImageModel> call = apiClass.calls().uploadImage(body);
        call.enqueue(new Callback<ImageModel>() {
            @Override
            public void onResponse(Call<ImageModel> call, Response<ImageModel> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(SignUP_activity.this, "error" + response.code(), Toast.LENGTH_SHORT).show();
                    Log.d("error", "error" + response.code());
                    return;
                }
                ImageModel imageModel = response.body();
                Toast.makeText(SignUP_activity.this, "upload", Toast.LENGTH_SHORT).show();
                imagename.setVisibility(View.VISIBLE);
                imagename.setText(imageModel.getFilename());


            }

            @Override
            public void onFailure(Call<ImageModel> call, Throwable t) {
                Toast.makeText(SignUP_activity.this, "error" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.d("error", "error   " + t.getLocalizedMessage());
            }
        });

    }

    void checkPermission() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    boolean checkEmpty(EditText sn_first, EditText sn_last, EditText sn_username, EditText sn_pass, EditText sn_repass, TextView imagename) {

        if (!TextUtils.isEmpty(sn_first.getText().toString())) {
            if (!TextUtils.isEmpty(sn_last.getText().toString())) {
                if (!TextUtils.isEmpty(sn_username.getText().toString())) {
                    if (!TextUtils.isEmpty(sn_pass.getText().toString())) {
                        if (!TextUtils.isEmpty(sn_repass.getText().toString())) {
                            if (!TextUtils.isEmpty(imagename.getText().toString())) {
                                return true;
                            } else {
                                Toast.makeText(this, "Select Images", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        } else {
                            sn_repass.setError("type repassword");
                            return false;
                        }
                    } else {
                        sn_pass.setError("type password");
                        return false;
                    }
                } else {
                    sn_username.setError("set date of birth");
                    return false;
                }
            } else {
                sn_last.setError("type last name");
                return false;
            }
        } else {
            sn_first.setError("type first name");
            return false;
        }


    }

    private Uri getImageUri() {
        Uri m_imgUri = null;
        File m_file;
        try {
            SimpleDateFormat m_sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String m_curentDateandTime = m_sdf.format(new Date());
            String m_imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + m_curentDateandTime + ".jpg";
            m_file = new File(m_imagePath);
            m_imgUri = Uri.fromFile(m_file);
        } catch (Exception p_e) {
        }
        return m_imgUri;
    }

    String ServerImage(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_ind = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_ind);
        cursor.close();
        return result;
    }

    void Clear() {
        ed_fn.setText("");
        ed_ln.setText("");
        ed_un.setText("");
        ed_p.setText("");
        ed_rp.setText("");
        imagename.setText("");
        imagename.setVisibility(View.INVISIBLE);
        image_profile.setImageDrawable(null);
        Intent intent = new Intent(SignUP_activity.this,MainActivity.class);
        startActivity(intent);

    }

}
