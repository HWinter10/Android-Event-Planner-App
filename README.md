## Event Planner App

The Event Planner App is an Android application designed to help users organize and manage personal events in a simple and structured way. The project demonstrates the full application lifecycle, including testing, deployment preparation, and long-term maintenance planning.

The application was developed using Android development best practices and evaluated through multiple forms of testing to ensure stability, usability, and device compatibility.

### Technologies Used

- Kotlin
- Android Studio
- Android SDK
- SQLite / Local storage
- Android Jetpack libraries
- Android Emulator


### Testing and Verification

Several testing methods were used to verify the application's reliability and functionality.

### Unit Testing
Unit tests were used to validate core application logic, including event management and database operations.

### Instrumented Testing
Instrumented tests ensured the application behaved correctly on Android devices and emulators.

### UI Testing
User interface testing verified that navigation, event creation, and interaction elements worked as expected.


### Performance and Compatibility

Device compatibility was evaluated using multiple Android Studio emulator configurations, including small phone, medium phone, and tablet layouts. This testing confirmed that the interface adapts correctly across different screen sizes.

Performance profiling was conducted using Android Studio’s profiler tools to monitor CPU and memory activity during typical user interactions. The results indicated stable performance without significant memory issues.

Accessibility testing was also performed using Android's Accessibility Scanner to evaluate contrast, touch targets, and screen reader compatibility.


### Deployment Preparation

Preparing the application for deployment included several steps:

- Generating a secure **keystore** for signing release builds
- Switching build variants from **debug to release**
- Optimizing resources for production distribution

These steps ensure the application can be safely distributed through the Google Play Store.


### Maintenance Strategy

After release, the application can be maintained through regular updates and bug fixes. Updates would follow semantic versioning to track major changes, feature additions, and minor patches.

Ongoing maintenance may include:

- monitoring performance
- resolving reported issues
- maintaining accessibility compliance
- improving usability based on feedback
