package com.songtowords.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int PICK_AUDIO = 10;
    private static final int SAVE_TEXT = 11;
    private static final int REQ_AUDIO_PERMISSION = 12;

    private MediaPlayer player;
    private SpeechRecognizer recognizer;
    private EditText lyricsBox;
    private TextView status;
    private TextView songName;
    private Button playButton;
    private CheckBox offlineBox;
    private Uri selectedAudio;
    private boolean listening = false;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        buildScreen();
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            status.setText("Speech recognition service is not available on this device.");
        }
    }

    private void buildScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20, 22, 20, 16);
        root.setBackgroundColor(Color.rgb(14, 20, 38));

        TextView title = new TextView(this);
        title.setText("🎵 SONG TO WORDS");
        title.setTextColor(Color.WHITE);
        title.setTextSize(26);
        title.setGravity(Gravity.CENTER);
        title.setPadding(4, 12, 4, 8);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Turn a song into editable text");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(14);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(4, 0, 4, 18);
        root.addView(subtitle);

        songName = new TextView(this);
        songName.setText("No song selected");
        songName.setTextColor(Color.WHITE);
        songName.setTextSize(16);
        songName.setPadding(8, 12, 8, 12);
        root.addView(songName);

        Button choose = button("Choose MP3 / audio");
        root.addView(choose);
        choose.setOnClickListener(v -> chooseAudio());

        playButton = button("▶ Play song");
        root.addView(playButton);
        playButton.setEnabled(false);
        playButton.setOnClickListener(v -> togglePlayback());

        offlineBox = new CheckBox(this);
        offlineBox.setText("Prefer offline speech recognition");
        offlineBox.setTextColor(Color.WHITE);
        offlineBox.setButtonTintList(android.content.res.ColorStateList.valueOf(Color.WHITE));
        root.addView(offlineBox);

        TextView hint = new TextView(this);
        hint.setText("For transcription, play the song aloud on the phone speaker, then tap Start listening. The microphone listens to the speaker.");
        hint.setTextColor(Color.rgb(210, 215, 230));
        hint.setTextSize(13);
        hint.setPadding(4, 8, 4, 8);
        root.addView(hint);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        Button start = button("Start listening");
        Button stop = button("Stop");
        row.addView(start, new LinearLayout.LayoutParams(0, -2, 1));
        row.addView(stop, new LinearLayout.LayoutParams(0, -2, 1));
        root.addView(row);
        start.setOnClickListener(v -> startListening());
        stop.setOnClickListener(v -> stopListening());

        status = new TextView(this);
        status.setText("Ready. Choose an audio file to begin.");
        status.setTextColor(Color.rgb(120, 220, 170));
        status.setPadding(4, 8, 4, 8);
        root.addView(status);

        TextView lyricsTitle = new TextView(this);
        lyricsTitle.setText("Recognized words (editable)");
        lyricsTitle.setTextColor(Color.WHITE);
        lyricsTitle.setTextSize(16);
        lyricsTitle.setPadding(4, 10, 4, 6);
        root.addView(lyricsTitle);

        lyricsBox = new EditText(this);
        lyricsBox.setHint("Recognized lyrics will appear here...");
        lyricsBox.setHintTextColor(Color.GRAY);
        lyricsBox.setTextColor(Color.WHITE);
        lyricsBox.setTextSize(16);
        lyricsBox.setGravity(Gravity.TOP | Gravity.START);
        lyricsBox.setMinLines(7);
        lyricsBox.setPadding(12, 12, 12, 12);
        lyricsBox.setBackgroundColor(Color.rgb(34, 43, 65));
        LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(-1, 0, 1);
        root.addView(lyricsBox, editParams);

        LinearLayout saveRow = new LinearLayout(this);
        Button save = button("Save TXT");
        Button clear = button("Clear");
        saveRow.addView(save, new LinearLayout.LayoutParams(0, -2, 1));
        saveRow.addView(clear, new LinearLayout.LayoutParams(0, -2, 1));
        root.addView(saveRow);
        save.setOnClickListener(v -> saveText());
        clear.setOnClickListener(v -> lyricsBox.setText(""));

        setContentView(root);
    }

    private Button button(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        return b;
    }

    private void chooseAudio() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");
        startActivityForResult(intent, PICK_AUDIO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;
        if (requestCode == PICK_AUDIO) {
            selectedAudio = data.getData();
            if (selectedAudio != null) {
                try {
                    if (player != null) player.release();
                    player = MediaPlayer.create(this, selectedAudio);
                    if (player == null) throw new IllegalStateException("Unsupported audio");
                    playButton.setEnabled(true);
                    songName.setText("Selected audio: " + selectedAudio.getLastPathSegment());
                    player.setOnCompletionListener(mp -> {
                        playButton.setText("▶ Play song");
                        status.setText("Song finished.");
                    });
                    status.setText("Audio ready. Play it aloud, then start listening.");
                } catch (Exception e) {
                    status.setText("Could not open this audio file.");
                    Toast.makeText(this, "Audio could not be opened", Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == SAVE_TEXT) {
            Uri uri = data.getData();
            if (uri != null) {
                try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                    out.write(lyricsBox.getText().toString().getBytes("UTF-8"));
                    Toast.makeText(this, "Lyrics saved", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Could not save file", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void togglePlayback() {
        if (player == null) return;
        if (player.isPlaying()) {
            player.pause();
            playButton.setText("▶ Play song");
        } else {
            player.start();
            playButton.setText("❚❚ Pause song");
        }
    }

    private void startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            status.setText("No speech recognition service found. Install/update Google app or speech services.");
            return;
        }
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQ_AUDIO_PERMISSION);
            return;
        }
        if (recognizer != null) recognizer.destroy();
        if (Build.VERSION.SDK_INT >= 31 && offlineBox.isChecked()
                && SpeechRecognizer.isOnDeviceRecognitionAvailable(this)) {
            recognizer = SpeechRecognizer.createOnDeviceSpeechRecognizer(this);
        } else {
            recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        }
        recognizer.setRecognitionListener(new RecognitionListener() {
            @Override public void onReadyForSpeech(Bundle params) { status.setText("Listening... keep the song playing aloud."); }
            @Override public void onBeginningOfSpeech() { }
            @Override public void onRmsChanged(float rmsdB) { }
            @Override public void onBufferReceived(byte[] buffer) { }
            @Override public void onEndOfSpeech() { status.setText("Processing speech..."); }
            @Override public void onError(int error) {
                status.setText("Listening stopped (code " + error + "). Tap Start listening to continue.");
                listening = false;
            }
            @Override public void onResults(Bundle results) { appendResults(results); }
            @Override public void onPartialResults(Bundle partialResults) { showPartial(partialResults); }
            @Override public void onEvent(int eventType, Bundle params) { }
        });
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, offlineBox.isChecked());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toLanguageTag());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        try {
            recognizer.startListening(intent);
            listening = true;
        } catch (Exception e) {
            status.setText("Could not start listening: " + e.getMessage());
        }
    }

    private void showPartial(Bundle bundle) {
        ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches != null && !matches.isEmpty()) status.setText("Hearing: " + matches.get(0));
    }

    private void appendResults(Bundle bundle) {
        ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches != null && !matches.isEmpty()) {
            String old = lyricsBox.getText().toString().trim();
            String next = matches.get(0).trim();
            if (!next.isEmpty()) lyricsBox.setText(old.isEmpty() ? next : old + "\n" + next);
            lyricsBox.setSelection(lyricsBox.length());
            status.setText("Words added. Tap Start listening again for the next part.");
        }
        listening = false;
    }

    private void stopListening() {
        if (recognizer != null) {
            try { recognizer.stopListening(); } catch (Exception ignored) { }
            recognizer.cancel();
        }
        listening = false;
        status.setText("Listening stopped.");
    }

    private void saveText() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "song-lyrics.txt");
        startActivityForResult(intent, SAVE_TEXT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_AUDIO_PERMISSION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        } else if (requestCode == REQ_AUDIO_PERMISSION) {
            status.setText("Microphone permission is required for speech recognition.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recognizer != null) recognizer.destroy();
        if (player != null) player.release();
    }
}
