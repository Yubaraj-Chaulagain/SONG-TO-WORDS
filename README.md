# SONG TO WORDS (Android)

## What this starter app does
- Selects an audio file from the phone.
- Plays and pauses the selected audio.
- Uses Android speech recognition through the microphone to recognize words while the song is playing aloud.
- Shows partial recognition status and appends final recognized text to an editable box.
- Saves the text as a `.txt` file.
- Offers a "Prefer offline" option. Actual offline availability depends on the phone's installed speech recognition engine and language packs.

## Important limitation
This starter app does **not** directly decode/transcribe the selected MP3 file. Android's built-in `SpeechRecognizer` listens to the microphone, so play the song through the phone speaker and tap **Start listening**. Music/singing recognition can be inaccurate, especially with instruments, background vocals, and fast lyrics. Offline mode is device/language dependent. This is not guaranteed to recognize every Nepali/Hindi/English song.

For reliable file-based transcription, a dedicated audio transcription model/service would need to be integrated.

## Build APK on GitHub (phone-friendly)
1. Create or open your GitHub repository.
2. Upload the **contents** of this ZIP into the repository root (not the outer folder).
3. Commit the files to the `main` branch.
4. Open the repository's **Actions** tab.
5. Select **Build Android APK**, then **Run workflow** (or wait for the automatic build after push).
6. Open the completed workflow run, scroll to **Artifacts**, and download `Song-To-Words-debug-apk`.
7. Extract the artifact ZIP and install `app-debug.apk` on your Android phone. You may need to allow installs from your browser/files app.

## Project files
- `settings.gradle`
- `build.gradle`
- `gradle.properties`
- `app/build.gradle`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/songtowords/app/MainActivity.java`
- `.github/workflows/build-apk.yml`

## Requirements
- Android 6.0+ (API 23+)
- Microphone permission for speech recognition
- A speech recognition service on the phone (Google app / supported speech service)
