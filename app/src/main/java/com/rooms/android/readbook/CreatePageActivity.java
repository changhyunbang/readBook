package com.rooms.android.readbook;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.document.FirebaseVisionCloudDocumentRecognizerOptions;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;
import com.rooms.android.readbook.database.DBManager;
import com.rooms.android.readbook.model.PageData;
import com.rooms.android.readbook.tts.TTSManager;
import com.rooms.android.readbook.tts.android.AndroidTTS;
import com.rooms.android.readbook.utils.Constants;
import com.rooms.android.readbook.utils.GetWordTextView;
import com.rooms.android.readbook.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CreatePageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CreatePageActivity.class.getSimpleName();

    private static final int TEXT_TO_SPEECH_CODE = 0x100;
    private static final int LOAD_GALLERY_CODE = 0x101;
    private static final int LOAD_CAMERA_CODE = 0x102;

    private static final boolean ENABLE_CLOUD_OCR = false;

    ImageView srcImgVie;
    Button btnLoad;
    GetWordTextView tvResult;
    Button btnRead;
    Button btnOk;

    String bookId;
    String pageId;
    Bitmap srcBitmap;

    private String imageFilePath;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_createpage);

        srcImgVie = findViewById(R.id.IV_SRC);
        btnLoad = findViewById(R.id.BTN_LOAD);
        tvResult = findViewById(R.id.TV_RESULT);
        btnRead = findViewById(R.id.BTN_READ);
        btnOk = findViewById(R.id.BTN_OK);

        findViewById(R.id.BTN_ROTATE_L).setOnClickListener(this);
        findViewById(R.id.BTN_ROTATE_R).setOnClickListener(this);
        findViewById(R.id.BTN_LOAD).setOnClickListener(this);
        findViewById(R.id.BTN_READ).setOnClickListener(this);
        findViewById(R.id.BTN_OK).setOnClickListener(this);

        tvResult.setMovementMethod(new ScrollingMovementMethod());
        tvResult.setOnWordClickListener(new GetWordTextView.OnWordClickListener() {
            @Override
            public void onClick(String word) {
                Toast.makeText(getApplicationContext(), word, Toast.LENGTH_LONG).show();

                TTSManager.getInstance(getApplicationContext()).speak(word);
            }
        });

        final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

        final int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                ActivityCompat.requestPermissions(this, permissions, 1);
            } else {
                init();
            }
        }

        bookId = getIntent().getStringExtra(Constants.KEY_BOOK_ID);
        pageId = getIntent().getStringExtra(Constants.KEY_PAGE_ID);

        Log.d(TAG, "getBookId : " + bookId);
        Log.d(TAG, "getPageId : " + pageId);

        if (!TextUtils.isEmpty(pageId)) {
            // TODO : 페이지 정보 로드
            PageData pageData = DBManager.getInstance(this).selectPageData(bookId, pageId);
            if (pageData != null) {
                Log.d(TAG, "getPageIndex : " + pageData.getPageIndex());
                Log.d(TAG, "getImagePath : " + pageData.getImagePath());
                Log.d(TAG, "getText : " + pageData.getText());
                srcImgVie.setImageBitmap(Utils.getBitmap(pageData.getImagePath()));
                tvResult.setText(pageData.getText());
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.BTN_LOAD :
                showPictureDialog();
                break;
            case R.id.BTN_READ :

//                if (mSpinnerLanguage.getSelectedItem() == null ||
//                        mSpinnerStyle.getSelectedItem() == null) {
//                    Toast.makeText(this,
//                            "Loading Voice Error, please check network or API_KEY.",
//                            Toast.LENGTH_LONG).show();
//
//                    mTextToSpeechManger = loadAndroidTTS();
//                    if (mTextToSpeechManger != null) {
//                        mTextToSpeechManger.speak(tvResult.getText().toString());
//                    }
//
//                    return;
//                }

                TTSManager.getInstance(this).speak(tvResult.getText().toString());
                break;
            case R.id.BTN_OK :
                if (srcBitmap != null && !TextUtils.isEmpty(bookId) && !TextUtils.isEmpty(tvResult.getText())) {
                    DBManager.getInstance(this).insertPageData(bookId, "0", Utils.saveImage(this, srcBitmap, "REED_BOOK"), tvResult.getText().toString());
                    finish();
                } else {
                    Toast.makeText(this, "필수 데이터 누락", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.BTN_ROTATE_L :
                try {
//                    srcBitmap = Utils.getRotatedBitmap(srcBitmap, (currentDegree + 90) % 360);
                    srcBitmap = Utils.getRotatedBitmap(srcBitmap, 270);

                    srcImgVie.setImageBitmap(srcBitmap);
                    recognizeText(FirebaseVisionImage.fromBitmap(srcBitmap));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.BTN_ROTATE_R :
                try {
//                    currentDegree -= 90;
//                    if (currentDegree < 0) {
//                        currentDegree += 360;
//                    }
//                    srcBitmap = Utils.getRotatedBitmap(srcBitmap, currentDegree);

                    srcBitmap = Utils.getRotatedBitmap(srcBitmap, 90);

                    srcImgVie.setImageBitmap(srcBitmap);
                    recognizeText(FirebaseVisionImage.fromBitmap(srcBitmap));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void showPictureDialog(){

        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, LOAD_GALLERY_CODE);
    }

    private void takePhotoFromCamera() {
//        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, LOAD_CAMERA_CODE);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, LOAD_CAMERA_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".jpg",         /* suffix */
                storageDir          /* directory */
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void init() {
        Context context = getApplicationContext();
        Drawable drawable = getResources().getDrawable(R.drawable.sampledata1);

        // drawable 타입을 bitmap으로 변경
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        recognizeText(FirebaseVisionImage.fromBitmap(bitmap));

        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, TEXT_TO_SPEECH_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menus, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tts_setting :
                startActivity(new Intent(this, TtsSettingActivity.class));
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == this.RESULT_CANCELED) {
            return;
        }

        switch(requestCode) {
            case LOAD_GALLERY_CODE:
                if (data != null) {
                    Uri contentURI = data.getData();
                    try {
                        srcBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
//                        String path = saveImage(bitmap);

                        srcImgVie.setImageBitmap(srcBitmap);

                        recognizeText(FirebaseVisionImage.fromBitmap(srcBitmap));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case TEXT_TO_SPEECH_CODE:
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    TTSManager.getInstance(this);
                } else {
                    Toast.makeText(this,
                            "You do not have the text to speech file you have to install",
                            Toast.LENGTH_LONG).show();
                    Intent installIntent = new Intent();
                    installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installIntent);
                }
                break;
            case LOAD_CAMERA_CODE:
//                srcBitmap = (Bitmap) data.getExtras().get("data");
//                srcImgVie.setImageBitmap(srcBitmap);

                if (resultCode == RESULT_OK) {

                    try {
                        srcBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(photoUri));
                        srcImgVie.setImageBitmap(srcBitmap);
                        recognizeText(FirebaseVisionImage.fromBitmap(srcBitmap));
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                break;
        }
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();

                } else {
                    Toast.makeText(this, "Please give your permission.", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private void recognizeText(FirebaseVisionImage image) {

        tvResult.setText("");
        recognizeProcess(generateDetector(ENABLE_CLOUD_OCR), image);
    }

    private FirebaseVisionTextRecognizer generateDetector(boolean useCloud) {

        FirebaseVisionTextRecognizer detector = null;
        if (useCloud) {
            FirebaseVisionCloudTextRecognizerOptions options = new FirebaseVisionCloudTextRecognizerOptions.Builder()
                    .setLanguageHints(Arrays.asList("en", "hi"))
                    .build();

            detector = FirebaseVision.getInstance()
                    .getCloudTextRecognizer();
        } else {
            detector = FirebaseVision.getInstance()
                    .getOnDeviceTextRecognizer();
        }

        return detector;
    }

    private void recognizeProcess(FirebaseVisionTextRecognizer detector, FirebaseVisionImage image) {
        // [START run_detector]



        Task<FirebaseVisionText> result =
                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {

                                Log.d(TAG, "recognizeProcess onSuccess");

                                StringBuilder recognizeBuilder = new StringBuilder();

                                for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                                    Rect boundingBox = block.getBoundingBox();
                                    Point[] cornerPoints = block.getCornerPoints();
                                    String text = block.getText();

                                    for (FirebaseVisionText.Line line: block.getLines()) {

                                        Log.d(TAG, "recognizeProcess line : " + line.getText());
//                                        tvResult.append(line.getText() + "\n");
                                        recognizeBuilder.append(line.getText() + ".\n");

                                        // ...
                                        for (FirebaseVisionText.Element element: line.getElements()) {
                                            Log.d(TAG, "recognizeProcess element : " + element.getText());
                                        }
                                    }
                                }

                                tvResult.setText(recognizeBuilder.toString());
//                                setClickableText(tvResult.getText().toString(), tvResult);
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                        Log.d(TAG, "recognizeProcess onFailure : " + e.toString());
                                        tvResult.append(e.toString());
                                    }
                                });
        // [END run_detector]
    }

    private void processTextBlock(FirebaseVisionText result) {
        // [START mlkit_process_text_block]
        String resultText = result.getText();
        for (FirebaseVisionText.TextBlock block: result.getTextBlocks()) {
            String blockText = block.getText();
            Float blockConfidence = block.getConfidence();
            List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();
            for (FirebaseVisionText.Line line: block.getLines()) {
                String lineText = line.getText();
                Float lineConfidence = line.getConfidence();
                List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();
                for (FirebaseVisionText.Element element: line.getElements()) {
                    String elementText = element.getText();
                    Float elementConfidence = element.getConfidence();
                    List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();
                }
            }
        }
        // [END mlkit_process_text_block]
    }

    private FirebaseVisionDocumentTextRecognizer getLocalDocumentRecognizer() {
        // [START mlkit_local_doc_recognizer]
        FirebaseVisionDocumentTextRecognizer detector = FirebaseVision.getInstance()
                .getCloudDocumentTextRecognizer();
        // [END mlkit_local_doc_recognizer]

        return detector;
    }

    private FirebaseVisionDocumentTextRecognizer getCloudDocumentRecognizer() {
        // [START mlkit_cloud_doc_recognizer]
        // Or, to provide language hints to assist with language detection:
        // See https://cloud.google.com/vision/docs/languages for supported languages
        FirebaseVisionCloudDocumentRecognizerOptions options =
                new FirebaseVisionCloudDocumentRecognizerOptions.Builder()
                        .setLanguageHints(Arrays.asList("en", "hi"))
                        .build();
        FirebaseVisionDocumentTextRecognizer detector = FirebaseVision.getInstance()
                .getCloudDocumentTextRecognizer(options);
        // [END mlkit_cloud_doc_recognizer]

        return detector;
    }

    private void processDocumentImage() {
        // Dummy variables
        FirebaseVisionDocumentTextRecognizer detector = getLocalDocumentRecognizer();
        FirebaseVisionImage myImage = FirebaseVisionImage.fromByteArray(new byte[]{},
                new FirebaseVisionImageMetadata.Builder().build());

        // [START mlkit_process_doc_image]
        detector.processImage(myImage)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionDocumentText>() {
                    @Override
                    public void onSuccess(FirebaseVisionDocumentText result) {
                        // Task completed successfully
                        // ...
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                    }
                });
        // [END mlkit_process_doc_image]
    }

    private void processDocumentTextBlock(FirebaseVisionDocumentText result) {
        // [START mlkit_process_document_text_block]
        String resultText = result.getText();
        for (FirebaseVisionDocumentText.Block block: result.getBlocks()) {
            String blockText = block.getText();
            Float blockConfidence = block.getConfidence();
            List<RecognizedLanguage> blockRecognizedLanguages = block.getRecognizedLanguages();
            Rect blockFrame = block.getBoundingBox();
            for (FirebaseVisionDocumentText.Paragraph paragraph: block.getParagraphs()) {
                String paragraphText = paragraph.getText();
                Float paragraphConfidence = paragraph.getConfidence();
                List<RecognizedLanguage> paragraphRecognizedLanguages = paragraph.getRecognizedLanguages();
                Rect paragraphFrame = paragraph.getBoundingBox();
                for (FirebaseVisionDocumentText.Word word: paragraph.getWords()) {
                    String wordText = word.getText();
                    Float wordConfidence = word.getConfidence();
                    List<RecognizedLanguage> wordRecognizedLanguages = word.getRecognizedLanguages();
                    Rect wordFrame = word.getBoundingBox();
                    for (FirebaseVisionDocumentText.Symbol symbol: word.getSymbols()) {
                        String symbolText = symbol.getText();
                        Float symbolConfidence = symbol.getConfidence();
                        List<RecognizedLanguage> symbolRecognizedLanguages = symbol.getRecognizedLanguages();
                        Rect symbolFrame = symbol.getBoundingBox();
                    }
                }
            }
        }
        // [END mlkit_process_document_text_block]
    }
}
