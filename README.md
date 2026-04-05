# 无感记账 (Seamless Expense Tracker)

一款基于Android平台的移动支付无感记账应用，利用通知监听和无障碍服务实现交易的自动识别和记录，所有数据完全本地存储，不联网。

## 📱 Features

### Core Features (Implemented)
- ✅ **Automatic Transaction Detection**
  - Notification Listener Service for WeChat & Alipay
  - Accessibility Service for payment screen recognition
  - Smart parsing of payment notifications
  
- ✅ **Local Data Storage**
  - Room database with encrypted storage capability
  - Complete offline functionality
  - No data leaves the device
  
- ✅ **Multi-Account Support**
  - WeChat Wallet, Alipay, Bank Cards, Cash
  - Account balance tracking
  - Transfer between accounts
  
- ✅ **Category Management**
  - 12 preset expense categories
  - 5 preset income categories
  - Custom category creation
  
- ✅ **Multi-Currency Support**
  - CNY, USD, EUR, JPY, GBP, HKD
  - Manual exchange rate management
  - Automatic conversion to CNY

### Planned Features
- 🚧 Manual transaction entry UI
- 🚧 Statistics and charts
- 🚧 Calendar view with heat map
- 🚧 Search and filtering
- 🚧 Data backup and restore
- 🚧 Export to PDF/Excel/CSV
- 🚧 Periodic transactions
- 🚧 Smart learning and auto-categorization
- 🚧 Onboarding flow

## 🏗️ Architecture

### Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose (Material3)
- **Architecture**: MVVM + Repository Pattern
- **Database**: Room with Kotlin Coroutines
- **Dependency Injection**: Hilt
- **Async Operations**: Kotlin Coroutines + Flow
- **Minimum SDK**: Android 8.0 (API 28)
- **Target SDK**: Android 14 (API 36)

### Project Structure
```
com.example.wuganjizhang/
├── data/
│   ├── model/          # Data entities
│   │   ├── Transaction.kt
│   │   ├── Category.kt
│   │   ├── Account.kt
│   │   ├── Tag.kt
│   │   ├── PeriodicTemplate.kt
│   │   └── Currency.kt
│   ├── dao/            # Data Access Objects
│   │   ├── TransactionDao.kt
│   │   ├── CategoryDao.kt
│   │   ├── AccountDao.kt
│   │   ├── TagDao.kt
│   │   ├── PeriodicTemplateDao.kt
│   │   └── CurrencyDao.kt
│   ├── local/          # Local data source
│   │   ├── AppDatabase.kt
│   │   └── Converters.kt
│   ├── repository/     # Repository layer
│   │   ├── TransactionRepository.kt
│   │   ├── CategoryRepository.kt
│   │   └── AccountRepository.kt
│   └── DatabaseSeeder.kt
├── di/                 # Dependency Injection
│   └── DatabaseModule.kt
├── service/            # Background services
│   ├── PaymentNotificationListener.kt
│   └── PaymentAccessibilityService.kt
├── ui/
│   └── theme/          # Compose theme
│       ├── Theme.kt
│       ├── Color.kt
│       └── Type.kt
├── MainActivity.kt
└── WuganJizhangApp.kt
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 11 or later
- Android SDK with API 28+ support

### Build & Run
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on emulator or physical device

```bash
# Or use command line
./gradlew assembleDebug
./gradlew installDebug
```

## 🔐 Permissions

The app requires the following permissions:

- **Notification Access**: To read payment notifications from WeChat/Alipay
- **Accessibility Service**: To detect payment completion screens
- **Storage** (optional): For data backup and restore
- **Microphone** (optional): For voice notes
- **Camera** (optional): For receipt photos

All permissions are explained during first launch, and users can grant/revoke them at any time.

## 💾 Data Model

### Key Entities

#### Transaction
- Amount, currency, exchange rate
- Type (expense/income/transfer)
- Category, account, merchant
- Timestamp, tags, notes
- Source (auto-detected/manual/periodic)
- Confidence score

#### Category
- Name, icon, color
- Type (expense/income)
- Preset or custom
- Enabled/disabled status

#### Account
- Name, type (WeChat/Alipay/Bank/Cash)
- Initial and current balance
- Default account flag

## 🎨 UI Design

The UI follows the design from the provided HTML demo (`index.html`), featuring:

- Clean, modern Material Design 3
- Bottom navigation: Home, Statistics, Calendar
- Side drawer for secondary features
- Light/Dark theme support (follows system)
- Chinese language interface

## 📊 Database Schema

The app uses Room database with the following tables:
- `transactions` - All transaction records
- `categories` - Expense and income categories
- `accounts` - User accounts (WeChat, Alipay, etc.)
- `tags` - Transaction tags
- `periodic_templates` - Recurring transaction templates
- `currencies` - Currency exchange rates

Initial seed data includes:
- 17 preset categories (12 expense + 5 income)
- 4 default accounts
- 6 currencies with exchange rates

## 🔧 Development Status

See [DEVELOPMENT_PROGRESS.md](DEVELOPMENT_PROGRESS.md) for detailed progress tracking.

### Completed
- ✅ Project setup and dependencies
- ✅ Complete data layer (models, DAOs, database)
- ✅ Repository pattern implementation
- ✅ Hilt dependency injection
- ✅ Notification listener service (basic)
- ✅ Accessibility service (basic)
- ✅ Database seeding with initial data
- ✅ Basic Compose UI structure

### In Progress
- 🚧 Full UI implementation
- 🚧 ViewModels
- 🚧 Advanced notification parsing
- 🚧 Smart categorization

### TODO
- Backup/restore functionality
- Export/import features
- Statistics and charts
- Calendar view
- Search functionality
- Onboarding flow

## 🧪 Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## 📝 License

This project is for educational purposes.

## 👥 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📞 Support

For issues and questions, please open an issue on GitHub.

## 🙏 Acknowledgments

- Based on requirements from the product specification document
- UI design inspired by the provided HTML prototype
- Built with modern Android development best practices

---

**Version**: 1.0.0 (Development)  
**Last Updated**: 2026-04-05
