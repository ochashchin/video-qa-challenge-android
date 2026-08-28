# Video QA Challenge (Android) — Test Automation & QA Report

Senior QA Engineer Test Assignment submission for **Video QA Challenge (Android)**.

---

## 1. Executive Summary & Deliverables

- **Repository**: [https://github.com/ochashchin/video-qa-challenge-android](https://github.com/ochashchin/video-qa-challenge-android)
- **Platform Tested**: Android (Native Jetpack Compose)
- **Core Stack**: Kotlin, Jetpack Compose UI Test (`androidx.compose.ui.test`), Robot Pattern, JUnit 4
- **Test Management & Coverage**:
  - **Jira / Xray Test Plan (Smoke Test)**: [VQA-74 Test Plan — Smoke Test](https://fiverrtesttracking.atlassian.net/browse/VQA-74)
  - **Live Xray Test Coverage Report**: [VQA Test Coverage Report](https://fiverrtesttracking.atlassian.net/plugins/servlet/ac/com.xpandit.plugins.xray/test-coverage-report-page?project.key=VQA&project.id=10034&ac.reportId=6a8a757a6f0691f8ca99769c)
- **Time Tracked**: **~16.5 hours total**
  - Test Planning, Risk Analysis & Xray Specification: ~3.0 hrs
  - Test Implementation (Robot Pattern, Page Objects, Image Matchers): ~8.5 hrs
  - Environment validation & State injection edge cases: ~3.0 hrs
  - Documentation, AI Usage Note & Final Report: ~2.0 hrs

---

## 2. Testing Levels & Strategy Motivation

Our testing strategy organizes test cases across standardized testing levels to guarantee thorough coverage, maintainability, and clear separation of concerns:

| Testing Level | Scope | Focus / Motivation |
|---|---|---|
| **System Integration** | Entire E2E Ecosystem | *How* all units and screens interact seamlessly across complete end-to-end user journeys (Consent → Overview → Detail → Video Playback). |
| **Integration System** | Environmental & System Boundary | *Where* the application functions under varying system constraints (Intent Extras, configuration changes, app restarts, persistent storage resets). |
| **System Testing** | All UI Components Combined | *What* functional behaviors occur across the whole screen and user flows (e.g. video state machine transitioning from Buffering to Playing/Completed). |
| **Integration Testing** | Grouped Components | *What* connection and data flow exists between adjacent units (e.g. Overview Card selection passing Content ID to Detail Page & Player). |
| **Component Testing** | Single Component / Unit Group | *What* action a single isolated UI element performs (e.g. Consent toggles, retry button click handler, loading spinner visibility). |
| **Unit Testing** | Individual classes / pure functions | Micro-level validation of business models, JSON parsing, and state reducers. |

### Strict WWW Naming Convention
All test cases in Jira/Xray and test methods in the codebase follow the **WWW** convention:

> **[Where]** on **[When / Circumstance]**, **[What]** shown / expected

*Examples from the automated test suite:*
- `verify_60`: *Consent screen within icon, "Your privacy choices", sub-header, "Accept all", "Reject optional", "Manage preferences" shown* ([`VQA-60`](https://fiverrtesttracking.atlassian.net/browse/VQA-60))
- `verify_58`: *Consent screen on "Manage preferences" click, consent manage preferences screen shown* ([`VQA-58`](https://fiverrtesttracking.atlassian.net/browse/VQA-58))
- `verify_146`: *Video screen within "Travel", "News", "News", "Technology", "Travel", "Interviews" shown* ([`VQA-146`](https://fiverrtesttracking.atlassian.net/browse/VQA-146))
- `verify_156`: *Video screen on video details player cta button, video player time progress resumed shown* ([`VQA-156`](https://fiverrtesttracking.atlassian.net/browse/VQA-156))
- `verify_158`: *Video screen on video details player cta button, video player time progress resumed left boundary shown* ([`VQA-158`](https://fiverrtesttracking.atlassian.net/browse/VQA-158))
- `verify_161`: *Video screen on video details player cta button, video player time progress resumed max boundary shown* ([`VQA-161`](https://fiverrtesttracking.atlassian.net/browse/VQA-161))
- `verify_163`: *Video details screen on player cta button click, video player slow buffering shown* ([`VQA-163`](https://fiverrtesttracking.atlassian.net/browse/VQA-163))
- `verify_131`: *Error screen on server error response, "Something went wrong" and "Try again" shown* ([`VQA-131`](https://fiverrtesttracking.atlassian.net/browse/VQA-131))

---

## 3. Solution Architecture & Technical Motivation

### Why Jetpack Compose UI Tests + Kotlin + Robot Pattern?

1. **Direct Integration with Compose Semantic Tree**:
   The app is built natively using Jetpack Compose. Native Compose testing (`composeTestRule`) interacts directly with semantic nodes via `testTags` without requiring accessibility bridge translations needed by external frameworks.
2. **Reliable Synchronization (No Arbitrary Sleeps)**:
   Compose test rules automatically synchronize with the Compose clock, coroutine dispatchers, and recomposition loops. Explicit waiting is handled via predicate-based polling (`WaitExtensions.kt`), eliminating flaky hardcoded sleeps (`Thread.sleep`).
3. **Deterministic State Injection**:
   Using `ActivityScenario` and Intent Extras (`resetAllState`, `consentState`, `contentMode`, `videoMode`, `contentDelayMs`, `videoBufferingMs`), each test isolates its execution and injects initial state instantly without multi-step manual setup.
4. **Robot / Page Object Pattern**:
   - `page/`: High-level user interaction methods (`ConsentPage`, `OverviewPage`, `VideoDetailsPage`, `VideoPage`, `ErrorPage`, `LoadingPage`).
   - `robot/`: Low-level element finding and Compose assertions (`BaseRobot`, `VideoDetailsRobot`, etc.).
   - `test/`: Clean, declarative test specifications.
5. **Visual Frame Verification**:
   Included `ImageMatcher.kt` and pre-rendered baseline frames in `assets/frames/` (`sample_video_00_00_frame_11.png`, `sample_video_00_27_frame_663.png`, `sample_video_29_00_frame_719.png`) to assert exact video playback progress.

---

## 4. Test Scenarios Covered

### Suggested Minimum Scope (Fulfilled):
1. **Consent Screen**: First-launch appearance, "Accept all", "Reject optional", and "Manage preferences" flow (`ConsentScreenTest.kt`, `ConsentManagePreferencesScreenTest.kt`).
2. **Content Overview & Detail**: Verified deterministic 6-item video list, scrolling below the fold, opening `Amsterdam from above`, and asserting title, category, description, and metadata (`VideoScreenTest.kt`, `VideoDetailsScreenTest.kt`).
3. **Video Playback Lifecycle**: Verified transition from `Buffering` to `Playing`, time progression, pause/resume, and reaching `Completed` state (`VideoDetailsScreenTest.kt`).

### Extended Risk-Based & Boundary Scenarios:
- **Playback Resume Boundary Analysis**:
  - `verify_158`: Left boundary resume.
  - `verify_159`: Min boundary (`00:00`) resume.
  - `verify_160`: Right boundary resume.
  - `verify_161`: Max boundary (`29:00`) completion resume.
- **Simulated Buffering Modes**:
  - `verify_163`: Slow buffering mode (~6s buffer duration verification).
  - `verify_164`: Fast buffering mode (~800ms buffer transition).
- **Negative & Error States**:
  - `verify_131`: Server error mode (`ContentMode.ERROR`) displays "Something went wrong" + retry CTA.
  - `verify_132`: Server empty response (`ContentMode.EMPTY`) displays "No videos are available".
  - `verify_133`: Error state retry click triggers loading spinner.
- **Loading & Latency**:
  - `verify_135` – `verify_138`: Artificial delay injection and loading spinner presentation under different consent configurations (`LoadingScreenTest.kt`).

---

## 5. Instructions for Running the Tests & Test Management

### Test Management & Tracking Links
- **Jira / Xray Test Plan (Smoke Test)**: [VQA-74 Test Plan — Smoke Test](https://fiverrtesttracking.atlassian.net/browse/VQA-74)
- **Live Xray Test Coverage Report**: [VQA Test Coverage Report](https://fiverrtesttracking.atlassian.net/plugins/servlet/ac/com.xpandit.plugins.xray/test-coverage-report-page?project.key=VQA&project.id=10034&ac.reportId=6a8a757a6f0691f8ca99769c)

### Prerequisites
- JDK 17–24
- Android SDK Platform 35
- Android Emulator running (Pixel 6, API 35 recommended)

### Execution via Command Line (Gradle)

```bash
# 1. Set ANDROID_HOME if not already in your path
export ANDROID_HOME="$HOME/Android/Sdk" # Linux/macOS
# Windows PowerShell: $env:ANDROID_HOME="C:\Users\<User>\AppData\Local\Android\Sdk"

# 2. Run all instrumented tests
./gradlew connectedAndroidTest

# 3. Run a specific test class
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.videoqa.challenge.test.VideoDetailsScreenTest

# 4. Run a single test method (e.g. VQA-156)
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.videoqa.challenge.test.VideoDetailsScreenTest#verify_156
```

### Execution via Android Studio
1. Open the project in Android Studio.
2. Select your connected target emulator/device in the top device menu.
3. Navigate to `app/src/androidTest/java/com/videoqa/challenge/test/`.
4. Right-click on the `test` package or any individual test file and select **Run 'Tests in 'test''**.

### Test Reports
HTML execution reports are automatically generated at:
```
app/build/reports/androidTests/connected/debug/index.html
```

---

## 6. Test Execution Report & Environment

| Parameter | Value |
|---|---|
| **Environment Type** | Android Virtual Device (Emulator) |
| **Device Model** | Google Pixel 6 |
| **Android Version / API** | Android 15.0 / API 35 (`x86_64` / `arm64-v8a`) |
| **Screen Resolution & Density** | 1080 x 2400 px, 420 dpi |
| **System Animations** | Disabled (`0.0x`) during test execution |
| **Overall Pass Rate** | 100% (All scenarios passing) |

### Why This Environment Was Chosen:
1. **Target Compatibility**: Matches the app's target and compile SDK (API 35).
2. **Deterministic & Isolated**: Virtual devices provide zero state leakage across test runs and eliminate flaky real-device interruptions (calls, battery warnings, background push notifications).
3. **CI/CD Parity**: Headless emulators can be executed identically in GitHub Actions or cloud device farms (Firebase Test Lab / BrowserStack).

---

## 7. Next Steps, Roadmap & Risk Analysis

### Next Steps / Remaining Work:
1. **CI/CD Integration**:
   - Add a GitHub Actions pipeline using `reactivecircus/android-emulator-runner` for automated PR gating and nightly regression runs.
2. **Transition from Isolated Mock Environment to Full Real E2E Testing**:
   - The current automated test suite was executed in an isolated environment with injected mock states (Intent Extras for `contentMode`, `videoMode`, fixed delays, and deterministic buffering). The next crucial step is to test the application in a full End-to-End (E2E) environment with live backend services and real media streaming (without synthetic mocks), verifying genuine network latency, CDN asset streaming, and end-to-end user journeys.
3. **Device Compatibility Matrix (Min, Mid & Max Target APIs)**:
   - Establish a 3-tier API testing strategy to guarantee coverage across average real-world consumer devices:
     - **Min API (API 26 / Android 8.0)**: Validates backward compatibility on legacy runtimes, older graphics rendering pipelines, and baseline Compose support.
     - **Mid API (API 31–33 / Android 12–13)**: Covers the highest market-share demographic for average active devices, ensuring standard OS lifecycle and permission behaviors.
     - **Max / Target API (API 35 / Android 15)**: Validates the latest platform features, modern background limits, and target SDK optimizations.
4. **Cross-Platform Parity Suite (Appium / XCUITest)**:
   - Set up an Appium / XCUITest test suite for end-to-end parity testing against both iOS (`.app`) and Android (`.apk`) builds, ensuring unified test coverage and identical feature behavior across platforms.
5. **Performance & Profiling**:
   - Implement Macrobenchmark tests (`androidx.benchmark`) to monitor frame drops during video rendering and cold start time.

### Risks & Open Questions:
- **Audio Focus & Call Interruptions**: Need to automate verification of playback behavior when phone calls or competing media playback occur.
- **Accessibility (ADA / a11y)**: Add automated semantics assertions for screen readers (TalkBack), focus traversal order, and minimum touch target sizes (48dp).
- **Undocumented Metadata Test Tag**: The publish date/duration metadata row on the detail screen lacks a dedicated test identifier; recommended adding `testTag("detail_metadata")`.

---

## 8. AI Usage Note

### Overview
AI tools were used as an assistive pair programmer across the QA lifecycle: generating structured Jira/Xray test cases, defining boundary conditions, accelerating Robot pattern scaffolding, and formulating JQL filters.

### Representative Prompt Templates & Use Cases:

#### Template 1: 🚀 Feature Test Cases & User Stories Generation
```text
Feature: Video Details & Player State Machine
Please generate:
1. Structured Test Cases with Steps to Reproduce (Action, Data, Expected Result).
2. User Stories with tags [Video Details][Story] and descriptions.
3. Apply strict WWW naming (Where on When/Circumstance, What shown).
4. Group player state transitions into System, Integration, and Component levels.
5. Format expected results with standard Jira markup {quote}.
```
*Evaluation & Action*: AI output provided the base structure for WWW test naming. Verified boundary timestamps against the application's `PlaybackProgress` model.

#### Template 2: 📁 Generate Import-Ready CSVs (Stories & Xray Tests)
```text
Please generate the CSV files for:
1. User Stories CSV (Project Key, Issue Type, Summary, Description)
2. Xray Test Cases CSV (Project Key, Test ID, Test Type, Summary, Description, Action, Data, Expected Result)
Ensure:
- Test Type is set to "Manual" on every row.
- Multi-step tests are grouped by Test ID with blank Summary on continuation rows.
- Exactly 8 columns on every row.
```
*Evaluation & Action*: Validated CSV delimiter and column mappings for direct import into Jira without manual reformatting.

#### Template 3: 🎯 Generate JQL for Test Plan Management
```text
I have imported new tests for Video Details (Keys: VQA-156 to VQA-171).
Please give me:
1. Exact JQL query to add these new tests to the Test Plan.
2. Complete Smoke Test Plan JQL containing all active project tests.
```
*Evaluation & Action*: Generated JQL queries for organizing [VQA-74 Smoke Test Plan](https://fiverrtesttracking.atlassian.net/browse/VQA-74).

#### Template 4: 🔗 Generate 1:1 Mapping Table (Stories ⟷ Tests)
```text
Here are the imported Stories: [e.g. VQA-172 to VQA-187]
Here are the imported Tests: [e.g. VQA-156 to VQA-171]
Please generate the 1:1 direct link mapping table (Story <-> Test Case) with clickable Jira links.
```
*Evaluation & Action*: Generated traceability matrix for the [VQA Test Coverage Report](https://fiverrtesttracking.atlassian.net/plugins/servlet/ac/com.xpandit.plugins.xray/test-coverage-report-page?project.key=VQA&project.id=10034&ac.reportId=6a8a757a6f0691f8ca99769c).

#### Template 5: 🤖 Print Automation-Ready Reference
```text
Please print the complete test reference for automation for [Video Details] including:
- Xray Test Case Key
- User Story Key & Title
- Test Summary
- Detailed Steps to Reproduce (Step #, Action, Data, Expected Result).
```
*Evaluation & Action*: Converted Jira test specifications into Kotlin Compose UI Robot methods (`VideoDetailsRobot.kt`, `VideoDetailsPage.kt`).

---

## 9. Application Under Test Reference

### Test Identifiers (resource-id)

| Screen | Element | Identifier |
|---|---|---|
| Consent | Screen container | `consent_screen` |
| Consent | Buttons | `consent_accept_button`, `consent_reject_button`, `consent_manage_preferences_button` |
| Preferences | Screen, toggles, save | `preferences_screen`, `analytics_toggle`, `personalisation_toggle`, `preferences_save_button` |
| Overview | Screen, list, loading | `content_overview_screen`, `content_list`, `content_loading_indicator` |
| Overview | Toolbar buttons | `content_refresh_button`, `debug_options_button` |
| Overview | Card / title per item | `content_item_<contentId>`, `content_title_<contentId>` (e.g. `content_item_amsterdam`) |
| Overview | Empty / Error states | `content_empty_state`, `content_error_state`, `content_error_message` |
| Detail | Fields | `detail_title`, `detail_category`, `detail_description`, `detail_back_button`, `content_detail_screen` |
| Player | Controls & States | `video_player`, `video_play_button`, `video_pause_button`, `video_buffering_indicator`, `video_progress`, `video_state_label`, `video_error_message` |
| Debug | Options & Controls | `debug_content_success`, `debug_content_empty`, `debug_content_error`, `debug_video_normal`, `debug_reset_all`, `debug_done_button` |

### Launch Configuration via Intent Extras

| Extra | Type | Effect |
|---|---|---|
| `resetAllState` | boolean (`--ez`) | Permanently clears **all** persisted state at launch. |
| `resetConsent` | boolean (`--ez`) | Permanently clears the persisted consent selection at launch. |
| `contentMode` | string (`--es`) | `success`, `empty`, `error`, `slow`. |
| `videoMode` | string (`--es`) | `normal`, `buffering`, `error`, `completeQuickly`. |
| `contentDelayMs` | int (`--ei`) | Fixed content loading delay in ms. |
| `videoBufferingMs` | int (`--ei`) | Fixed simulated buffering duration in ms. |
