# Quick Start Guide - 无感记账

## 🚀 Running the App

### Option 1: Android Studio (Recommended)
1. Open Android Studio
2. Click "Open" and select the `wuganjizhang` folder
3. Wait for Gradle sync to complete
4. Click the "Run" button (green play icon) or press Shift+F10
5. Select an emulator or connected device

### Option 2: Command Line
```bash
# Navigate to project directory
cd D:\Project\Android_project\wuganjizhang

# Build debug APK
gradlew.bat assembleDebug

# Install on connected device
gradlew.bat installDebug
```

## 📱 First Launch

When you first launch the app:
1. The database will be automatically seeded with initial data
2. You'll see a basic UI with 3 tabs: 首页, 统计, 日历
3. Tabs currently show placeholder text

## 🔧 Enabling Auto-Detection

To test the payment detection features:

### Enable Notification Listener
1. Go to Settings → Apps → Special app access → Notification access
2. Find "无感记账" and enable it
3. The app can now read WeChat/Alipay notifications

### Enable Accessibility Service
1. Go to Settings → Accessibility → Installed services
2. Find "无感记账" and enable it
3. The app can now detect payment completion screens

## 🧪 Testing Payment Detection

### Test with Notifications
1. Make a small payment using WeChat or Alipay (or have someone send you money)
2. When the payment notification appears, the app should detect it
3. Check Logcat with tag "PaymentNotification" to see detection logs

### Test with Accessibility
1. Complete a payment in WeChat or Alipay
2. Stay on the payment success screen
3. The accessibility service should extract transaction details
4. Check Logcat with tag "PaymentAccessibility" for logs

## 📊 Viewing Logs

In Android Studio:
1. Open "Logcat" tab at the bottom
2. Filter by tag: `PaymentNotification` or `PaymentAccessibility`
3. You should see logs when payments are detected

## 🗄️ Database Inspection

To view the database:
1. Run the app on an emulator or rooted device
2. In Android Studio: View → Tool Windows → Device File Explorer
3. Navigate to: `/data/data/com.example.wuganjizhang/databases/`
4. Pull `wuganjizhang.db` to your computer
5. Open with DB Browser for SQLite or similar tool

## 🎨 Current UI Structure

The app has a basic structure:
- **Bottom Navigation**: 3 tabs (Home, Stats, Calendar)
- **Material3 Theme**: Follows system light/dark mode
- **Placeholder Screens**: Ready for implementation

## 🛠️ Development Tips

### Adding New Features
1. Create ViewModel in `ui/viewmodel/` package
2. Create Composable screen in `ui/screens/` package
3. Add navigation route in MainActivity
4. Connect ViewModel to repository

### Testing Changes
- Use Hot Reload in Compose (just save the file)
- No need to rebuild entire app for UI changes
- Database changes require app reinstall

### Common Issues

**Gradle Sync Failed**
- Check internet connection
- Invalidate caches: File → Invalidate Caches / Restart
- Delete `.gradle` folder and resync

**App Crashes on Launch**
- Check Logcat for error messages
- Ensure Hilt is properly configured
- Verify all dependencies are downloaded

**Database Not Seeding**
- Uninstall app completely
- Reinstall (seed only runs on first launch)
- Check Logcat for seeding errors

## 📝 Next Development Steps

See PROJECT_SUMMARY.md for detailed roadmap.

Quick wins to implement next:
1. **MainViewModel** - Expose transaction list to UI
2. **Home Screen** - Show actual transactions from database
3. **Manual Entry** - Form to add transactions manually
4. **Transaction Detail** - View full transaction info

## 🔗 Useful Resources

- **Requirements**: `C:\Users\zhangyue\Desktop\无感记账.md`
- **Technical Spec**: `C:\Users\zhangyue\Desktop\技术文档.md`
- **UI Demo**: `C:\Users\zhangyue\Desktop\index.html` (open in browser)
- **Progress**: `DEVELOPMENT_PROGRESS.md`
- **Summary**: `PROJECT_SUMMARY.md`

## 💡 Pro Tips

1. **Test Early**: Don't wait until everything is built - test each component
2. **Use Real Devices**: Emulators may not handle notifications/accessibility well
3. **Check Permissions**: Many features require user-granted permissions
4. **Monitor Performance**: Watch for memory leaks and battery drain
5. **Backup Often**: Commit code frequently to Git

## 🆘 Getting Help

If you encounter issues:
1. Check Logcat for error messages
2. Review DEVELOPMENT_PROGRESS.md for known limitations
3. Search Android documentation for specific APIs
4. Check Stack Overflow for common Android issues

---

**Ready to build?** Start with creating ViewModels to connect the UI to your data layer!
