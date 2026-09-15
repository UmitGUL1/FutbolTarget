<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Run and deploy your AI Studio app

This contains everything you need to run your app locally.

View your app in AI Studio: https://ai.studio/apps/9b4893f2-2bc1-4396-a391-4ebbe6ec606b

## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)


1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. Create a file named `.env` in the project directory and set `GEMINI_API_KEY` in that file to your Gemini API key (see `.env.example` for an example)
5. Remove this line from the app's `build.gradle.kts` file: `signingConfig = signingConfigs.getByName("debugConfig")`
6. Run the app on an emulator or physical device
7. If you have already published your app in AI Studio, please [request upload key reset](https://support.google.com/googleplay/android-developer/answer/9842756#zippy=%2Crequest-an-upload-key-reset) in Google Play Console.
## Automated Tests

Run the fast game rule, flow, and stress tests:

```powershell
.\gradlew.bat testGame --no-daemon
```

Build the debug APK and run the important automated tests together:

```powershell
.\gradlew.bat testAll --no-daemon
```

The core game rules live in `GameRuleEngine`, so local match behavior and tests use the same winner, reveal, duplicate-pick, timeout, ranked-score, and reconnect-timeout logic.

