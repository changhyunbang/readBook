package com.rooms.android.readbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.rooms.android.readbook.tts.TTSManager;
import com.rooms.android.readbook.utils.Utils;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int TEXT_TO_SPEECH_CODE = 0x100;

    private static final boolean ENABLE_CLOUD_OCR = false;

    ImageView srcImgVie;
    Button btnLoad;
    TextView tvResult;
    Button btnRead;
    Bitmap srcBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate!!!!!!!");

        setContentView(R.layout.activity_main);

        srcImgVie = findViewById(R.id.IV_SRC);
        btnLoad = findViewById(R.id.BTN_LOAD);
        tvResult = findViewById(R.id.TV_RESULT);
        btnRead = findViewById(R.id.BTN_READ);

        findViewById(R.id.BTN_ROTATE_L).setOnClickListener(this);
        findViewById(R.id.BTN_ROTATE_R).setOnClickListener(this);
        findViewById(R.id.BTN_LOAD).setOnClickListener(this);
        findViewById(R.id.BTN_READ).setOnClickListener(this);

        tvResult.setMovementMethod(new ScrollingMovementMethod());

        Log.d(TAG, "sdk version : " + Build.VERSION.SDK_INT);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

            boolean checkPermission = checkIfAlreadyhavePermission();

            Log.d(TAG, "checkPermission : " + checkPermission);

            if (!checkPermission) {
                final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(this, permissions, 1);
            } else {
                init();
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.BTN_LOAD :
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
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

    private void init() {
//        Context context = getApplicationContext();
//        Drawable drawable = getResources().getDrawable(R.drawable.sampledata1);
//
//        // drawable 타입을 bitmap으로 변경
//        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
//
//        recognizeText(FirebaseVisionImage.fromBitmap(bitmap));
//
//        Intent checkIntent = new Intent();
//        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
//        startActivityForResult(checkIntent, TEXT_TO_SPEECH_CODE);
        startActivity(new Intent(this, BookListActivity.class));
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

        switch(requestCode) {
            case RESULT_LOAD_IMAGE:
                if (resultCode == RESULT_OK && null != data) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    srcBitmap = BitmapFactory.decodeFile(picturePath);

                    srcImgVie.setImageBitmap(srcBitmap);

                    recognizeText(FirebaseVisionImage.fromBitmap(srcBitmap));
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
        }
    }

    private boolean checkIfAlreadyhavePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
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

                                // Task completed successfully
                                // [START_EXCLUDE]
                                // [START get_text]
                                for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                                    Rect boundingBox = block.getBoundingBox();
                                    Point[] cornerPoints = block.getCornerPoints();
                                    String text = block.getText();

                                    for (FirebaseVisionText.Line line: block.getLines()) {

                                        Log.d(TAG, "recognizeProcess line : " + line.getText());
                                        tvResult.append(line.getText() + "\n");
                                        // ...
                                        for (FirebaseVisionText.Element element: line.getElements()) {
                                            Log.d(TAG, "recognizeProcess element : " + element.getText());
                                        }
                                    }
                                }
                                // [END get_text]
                                // [END_EXCLUDE]
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
