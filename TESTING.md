# Testing Guide for Rick and Morty Android App

## Overview

This document provides a comprehensive guide to the testing strategy and implementation for the Rick and Morty Android application. The app follows a multi-layered testing approach to ensure reliability, maintainability, and quality.

## Testing Architecture

### Test Pyramid Structure

```
        🔺 UI Tests (Few)
       🔺🔺 Integration Tests (Some)  
      🔺🔺🔺 Unit Tests (Many)
```

### Test Types Implemented

1. **Unit Tests** - Fast, isolated tests for business logic
2. **Integration Tests** - Tests for API interactions and data flow
3. **UI Tests** - Tests for user interface behavior and interactions

## Dependencies

### Testing Libraries Added

```kotlin
// Unit Testing
testImplementation(libs.junit)
testImplementation(libs.kotlinx.coroutines.test)
testImplementation(libs.mockk)
testImplementation(libs.turbine)
testImplementation(libs.hilt.android.testing)

// Android Testing
androidTestImplementation(libs.androidx.junit)
androidTestImplementation(libs.androidx.espresso.core)
androidTestImplementation(platform(libs.androidx.compose.bom))
androidTestImplementation(libs.androidx.ui.test.junit4)
androidTestImplementation(libs.hilt.android.testing)

// Debug Testing
debugImplementation(libs.androidx.ui.tooling)
debugImplementation(libs.androidx.ui.test.manifest)
```

### Key Testing Libraries

- **JUnit 4** - Core testing framework
- **MockK** - Mocking library for Kotlin
- **Turbine** - Testing library for Kotlin Flow
- **Coroutines Test** - Testing utilities for coroutines
- **Hilt Testing** - Dependency injection testing
- **Compose Testing** - UI testing for Jetpack Compose

## Test Structure

### Unit Tests (`/src/test/`)

#### Repository Tests
- **File**: `RickAndMortyRepositoryTest.kt`
- **Purpose**: Tests data layer logic and error handling
- **Coverage**:
  - Successful API calls
  - Network error handling
  - Null response handling
  - Exception handling

#### ViewModel Tests
- **Files**: 
  - `CharacterListViewModelTest.kt`
  - `CharacterDetailViewModelTest.kt`
- **Purpose**: Tests business logic and state management
- **Coverage**:
  - State transitions (Loading → Success/Error)
  - Pagination logic
  - Error handling
  - Data flow

#### Model Tests
- **File**: `ExampleUnitTest.kt` (renamed from placeholder)
- **Purpose**: Tests data model validation
- **Coverage**:
  - UiState sealed class behavior
  - Character model properties
  - Data integrity

### Integration Tests (`/src/androidTest/`)

#### API Integration Tests
- **File**: `RickAndMortyApiIntegrationTest.kt`
- **Purpose**: Tests real API interactions
- **Coverage**:
  - Successful API responses
  - Error responses (404, etc.)
  - Data validation
  - Network behavior

### UI Tests (`/src/androidTest/`)

#### Compose Screen Tests
- **Files**:
  - `CharacterListScreenTest.kt`
  - `CharacterDetailScreenTest.kt`
- **Purpose**: Tests user interface behavior
- **Coverage**:
  - Loading states
  - Error states
  - Success states
  - User interactions
  - Navigation

## Test Utilities

### Test Data (`TestData.kt`)
- Provides mock data for consistent testing
- Includes sample characters, episodes, and API responses
- Supports various test scenarios

### Test Extensions (`TestExtensions.kt`)
- `MainDispatcherRule` - Manages coroutine dispatchers in tests
- Ensures deterministic test execution

## Running Tests

### Unit Tests
```bash
./gradlew test
```

### Integration Tests
```bash
./gradlew connectedAndroidTest
```

### All Tests
```bash
./gradlew check
```

### Specific Test Class
```bash
./gradlew test --tests "RickAndMortyRepositoryTest"
```

## Test Coverage

### Current Coverage Areas

#### ✅ Covered
- Repository error handling
- ViewModel state management
- API response handling
- UI state transitions
- Data model validation
- Pagination logic
- Navigation interactions

#### 🔄 Partially Covered
- UI component interactions (basic structure)
- Error message display
- Loading indicators

#### ❌ Not Yet Covered
- Complex UI interactions
- Accessibility testing
- Performance testing
- Memory leak testing

## Best Practices

### Test Naming
- Use descriptive test names: `functionName_condition_expectedResult()`
- Example: `getCharacters_returnsSuccess_whenApiCallSucceeds()`

### Test Structure
- Follow Given-When-Then pattern
- Use clear comments for test sections
- Mock external dependencies

### Test Data
- Use consistent test data from `TestData.kt`
- Create specific test scenarios as needed
- Avoid hardcoded values in tests

### Assertions
- Use meaningful assertion messages
- Test both positive and negative cases
- Verify all relevant state changes

## Continuous Integration

### Test Execution
- Unit tests run on every build
- Integration tests run on CI/CD pipeline
- UI tests run on device farm

### Quality Gates
- All unit tests must pass
- Code coverage threshold: 80%
- No flaky tests allowed

## Troubleshooting

### Common Issues

#### MockK Issues
- Ensure proper mocking setup
- Use `relaxed = true` for complex objects
- Verify mock interactions

#### Coroutine Testing
- Use `MainDispatcherRule` for main dispatcher
- Use `runTest` for coroutine testing
- Handle timing issues with proper delays

#### Hilt Testing
- Use `@HiltAndroidTest` for integration tests
- Set up `HiltAndroidRule` properly
- Mock dependencies as needed

### Debug Tips
- Use `println()` for debugging test flow
- Check test logs for detailed error information
- Use Android Studio test runner for interactive debugging

## Future Enhancements

### Planned Additions
1. **Accessibility Tests** - Screen reader compatibility
2. **Performance Tests** - Memory and CPU usage
3. **Snapshot Tests** - UI regression testing
4. **End-to-End Tests** - Complete user journeys
5. **Property-Based Testing** - Random input validation

### Testing Metrics
- Test execution time optimization
- Code coverage improvement
- Test maintainability enhancement
- Flaky test elimination

## Conclusion

The Rick and Morty Android app now has a comprehensive testing suite that covers:
- **Unit tests** for business logic and data handling
- **Integration tests** for API interactions
- **UI tests** for user interface behavior
- **Test utilities** for consistent and maintainable tests

This testing strategy ensures the app's reliability, maintainability, and quality while following Android development best practices.
