# SONG TO WORDS (Android)

## Features in this starter version
- Select and play an audio file from the phone.
- Use Android speech recognition through the microphone.
- Display recognized words in an editable text box.
- Save the text as a `.txt` file.
- Offer a “Prefer offline” setting when the phone supports on-device speech recognition.

## Important transcription limitation
This version does **not** directly decode or transcribe the selected MP3/audio file. Android's built-in `SpeechRecognizer` listens to the microphone. To try it, play the song aloud through the phone speaker and tap **Start listening**. Singing, music, instruments, and background vocals can reduce accuracy. Offline recognition depends on the phone's installed speech engine and language packs; it is not guaranteed for Nepali, Hindi, or English songs.

For reliable transcription directly from an audio file, a dedicated audio transcription model (such as Whisper) must be integrated into the Android app. This workflow does not download or build a separate Whisper project.

## Build APK using GitHub Actions (phone-friendly)
1. Upload the **contents** of this ZIP into the root of your GitHub repository. The `.github/workflows/build-v2.yml` file must be included.
2. Commit/push the files to `main` or `master`.
3. Open the repository's **Actions** tab.
4. Select **Build SONG TO WORDS Android APK**.
5. Tap **Run workflow** (or wait for the build after pushing).
6. Open the completed run and find the **Artifacts** section.
7. Download `Song-To-Words-debug-apk`, extract the ZIP, then install `app-debug.apk` on your Android phone. Allow installation from your browser/files app if Android asks.

## Build configuration
- Android Gradle Plugin: 8.7.3
- Gradle: 8.9
- Java: 17
- Compile SDK: 35
- Minimum Android version: 6.0 (API 23)
