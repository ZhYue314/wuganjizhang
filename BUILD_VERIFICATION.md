# Build Verification Checklist

Use this checklist to verify the project builds correctly after setup.

## ✅ Pre-Build Checks

- [ ] Android Studio is installed (latest version recommended)
- [ ] JDK 11+ is configured
- [ ] Android SDK with API 28+ is installed
- [ ] Gradle sync completed without errors
- [ ] All dependencies downloaded successfully

## ✅ Build Steps

### 1. Clean Project
```bash
gradlew.bat clean
```
- [ ] Clean completed successfully

### 2. Build Debug APK
```bash
gradlew.bat assembleDebug
```
- [ ] Build started
- [ ] No compilation errors
- [ ] APK generated at: `app/build/outputs/apk/debug/app-debug.apk`

### 3. Run Tests (Optional)
```bash
gradlew.bat test
```
- [ ] Tests executed
- [ ] All tests passed (currently no tests implemented)

## ✅ Installation & Launch

### Install on Device/Emulator
```bash
gradlew.bat installDebug
```
- [ ] App installed successfully
- [ ] App icon appears on device

### First Launch
- [ ] App launches without crashes
- [ ] Basic UI displays (3 tabs at bottom)
- [ ] No error messages in Logcat
- [ ] Database seeding completes (check logs)

## ✅ Feature Verification

### Database Seeding
Check Logcat for these logs:
- [ ] "Database seeded" or similar confirmation
- [ ] No SQL errors during seeding

### Verify Seed Data
Use Device File Explorer to pull database and check:
- [ ] Categories table has 17 rows (12 expense + 5 income)
- [ ] Accounts table has 4 rows
- [ ] Currencies table has 6 rows

### Services Registration
Check Settings → Apps → Special app access:
- [ ] "无感记账" appears in Notification access list
- [ ] "无感记账" appears in Accessibility services list

### Theme Support
- [ ] Light mode displays correctly
- [ ] Dark mode displays correctly (change system theme)
- [ ] Material3 components render properly

## ✅ Code Quality Checks

### Compilation
- [ ] No Kotlin compilation errors
- [ ] No Hilt/Dagger errors
- [ ] No Room database errors
- [ ] No Compose errors

### Warnings
- [ ] Minimal warnings (some deprecation warnings are OK)
- [ ] No critical warnings about missing permissions

### Dependencies
Run dependency check:
```bash
gradlew.bat dependencies
```
- [ ] All dependencies resolved
- [ ] No version conflicts
- [ ] Hilt dependencies present
- [ ] Room dependencies present
- [ ] Compose dependencies present

## ✅ Common Issues & Solutions

### Issue: Gradle Sync Failed
**Solutions:**
- [ ] Check internet connection
- [ ] Invalidate caches: File → Invalidate Caches / Restart
- [ ] Delete `.gradle` folder and resync
- [ ] Update Android Studio

### Issue: Hilt Errors
**Solutions:**
- [ ] Verify `@HiltAndroidApp` annotation on Application class
- [ ] Check `@AndroidEntryPoint` on Activity/Service
- [ ] Ensure all Hilt modules are correct
- [ ] Clean and rebuild project

### Issue: Room Compilation Errors
**Solutions:**
- [ ] Check all `@Entity` annotations
- [ ] Verify DAO method signatures
- [ ] Ensure TypeConverters are registered
- [ ] Check database version

### Issue: Compose Preview Not Working
**Solutions:**
- [ ] Rebuild project
- [ ] Invalidate caches
- [ ] Check Compose compiler version matches Kotlin version

### Issue: App Crashes on Launch
**Check Logcat for:**
- [ ] NullPointerException (check DI setup)
- [ ] Database errors (check schema)
- [ ] Missing activity declaration in manifest
- [ ] Permission denied errors

## ✅ Performance Checks

### Build Time
- [ ] Clean build completes in < 5 minutes
- [ ] Incremental build completes in < 1 minute

### APK Size
- [ ] Debug APK size < 50 MB
- [ ] Release APK size < 30 MB (after optimization)

### App Startup
- [ ] Cold start < 3 seconds
- [ ] No ANR (Application Not Responding) errors

## ✅ Documentation

Verify documentation files exist:
- [ ] README.md
- [ ] DEVELOPMENT_PROGRESS.md
- [ ] PROJECT_SUMMARY.md
- [ ] QUICK_START.md
- [ ] BUILD_VERIFICATION.md (this file)

## ✅ Version Control

If using Git:
- [ ] .gitignore is configured
- [ ] Initial commit made
- [ ] Build artifacts excluded from repo
- [ ] Documentation committed

## 🎉 Success Criteria

The build is successful if:
1. ✅ App compiles without errors
2. ✅ App installs on device/emulator
3. ✅ App launches and displays UI
4. ✅ Database seeds with initial data
5. ✅ Services are registered in system
6. ✅ No crashes or ANRs
7. ✅ Basic navigation works (tab switching)

## 📝 Notes

Document any issues encountered:

Date: _______________

Issues Found:
- 
- 
- 

Solutions Applied:
- 
- 
- 

Verified By: _______________

---

**Next Step**: If all checks pass, proceed to implement ViewModels and UI screens!
