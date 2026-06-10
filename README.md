# EA-Sniper 🎯

A comprehensive Android application suite featuring Expert Advisor (EA) code generation, advanced timezone management, and a feature-rich to-do list application with local storage.

## 📱 Applications Included

### 1. **EA Coder** - Expert Advisor Generator
Auto-generates professional MetaTrader trading Expert Advisors with advanced features.

#### Features:
- ✅ MT4 (.mq4) and MT5 (.mq5) code generation
- ✅ EMA Crossover + RSI trading strategy
- ✅ Dynamic risk management with lot sizing
- ✅ Customizable parameters:
  - Fast EMA period
  - Slow EMA period
  - RSI period
  - Risk percentage
  - Stop Loss (points)
  - Take Profit (points)
- ✅ Real-time signal detection
- ✅ Position management with signal reversal
- ✅ Spread checking and validation

#### Generated EA Includes:
- Complete MQL4/MQL5 code
- Proper error handling
- Risk management functions
- Trade logging
- Position tracking

---

### 2. **Digital Clock** - Advanced Timezone Manager
Real-time world clock with sophisticated timezone management and display options.

#### Features:
- 🌍 Multi-timezone support with real-time updates
- ⏱️ Dual time format: 24-hour and 12-hour (AM/PM)
- ⭐ Favorite timezones system
- 🔄 Three sorting modes:
  - By timezone name
  - By UTC offset
  - Alphabetically
- 📅 Date with day of week display
- 🌅 Sunrise/sunset times per timezone
- 🔍 Search dialog with 16 pre-loaded timezones
- 🕐 UTC offset display
- ⏰ Automatic 1-second updates

#### Pre-loaded Timezones:
- America/New_York (EST)
- Europe/London (GMT)
- Asia/Tokyo (JST)
- Australia/Sydney (AEDT)

#### Search Timezones Available:
- **Americas**: New York, Chicago, Denver, Los Angeles
- **Europe**: London, Paris, Berlin
- **Asia**: Tokyo, Shanghai, Hong Kong, Dubai, Bangkok
- **Pacific**: Sydney, Melbourne, Auckland
- **Africa**: Cairo

---

### 3. **To-Do List** - Local Storage App
Comprehensive task management application with persistent SQLite storage.

#### Features:
- ✅ Create, Read, Update, Delete (CRUD) operations
- 📊 Priority levels: LOW, MEDIUM, HIGH, URGENT
- 📁 Custom categories for organization
- 📅 Optional due dates and times
- 🔄 Real-time filtering:
  - All todos
  - Active (incomplete) todos
  - Completed todos
- 📝 Detailed descriptions for each todo
- 💾 Persistent storage with Room database
- 🗑️ Bulk delete completed items
- 📊 Active todo counter
- 🎨 Color-coded priorities
- ✓ Strikethrough for completed items

#### Priority Colors:
- 🟢 LOW - Green
- 🔵 MEDIUM - Blue
- 🟠 HIGH - Orange
- 🔴 URGENT - Red

---

## 🛠️ Tech Stack

### Core Technologies:
- **Language**: Kotlin
- **Min SDK**: 21 (Android 5.0)
- **Target SDK**: 33 (Android 13)
- **Architecture**: MVVM + Repository Pattern

### Dependencies:
- **Room** 2.5.2 - SQLite database abstraction
- **Kotlin Coroutines** 1.7.1 - Asynchronous operations
- **Lifecycle** 2.6.1 - ViewModel & LiveData
- **RecyclerView** 1.3.0 - Efficient list display
- **Material Design** 1.9.0 - Modern UI components
- **Retrofit** 2.x - HTTP client (for future integrations)
- **CardView** 1.0.0 - Material card layouts

---

## 📋 Requirements

### System Requirements:
- **Android SDK**: 21 or higher
- **JDK**: 11 or higher
- **Gradle**: 7.0 or higher
- **RAM**: Minimum 4GB (8GB recommended)

### Development Tools:
- Android Studio 2021.3.1 or higher
- Git (for cloning)
- Kotlin Plugin (included in Android Studio)

---

## 🚀 Installation & Setup

### Step 1: Clone the Repository

```bash
# Using HTTPS
git clone https://github.com/Makabaza97/EA-Sniper.git

# Or using SSH (if configured)
git clone git@github.com:Makabaza97/EA-Sniper.git

# Navigate to project directory
cd EA-Sniper
```

### Step 2: Open in Android Studio

1. Open Android Studio
2. Click **File** → **Open**
3. Select the cloned `EA-Sniper` folder
4. Wait for Gradle sync to complete

### Step 3: Install Dependencies

```bash
# Automatic via Android Studio
# Or manual via command line:
./gradlew sync
./gradlew build
```

### Step 4: Run the Application

**Option A: Using Android Studio**
1. Connect an Android device or start an emulator
2. Click the **Run** button (green play icon)
3. Select your target device
4. App will install and launch

**Option B: Using Command Line**

```bash
# Build and install on connected device/emulator
./gradlew installDebug

# Run tests
./gradlew test

# Build release APK
./gradlew assembleRelease
```

---

## 📁 Project Structure

```
EA-Sniper/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/com/example/
│   │   │   │   ├── eacoder/
│   │   │   │   │   └── MainActivity.kt
│   │   │   │   ├── digitalclock/
│   │   │   │   │   └── MainActivity.kt
│   │   │   │   └── todolist/
│   │   │   │       ├── MainActivity.kt
│   │   │   │       ├── adapter/
│   │   │   │       │   └── TodoAdapter.kt
│   │   │   │       ├── database/
│   │   │   │       │   ├── TodoDatabase.kt
│   │   │   │       │   ├── TodoDao.kt
│   │   │   │       │   └── Converters.kt
│   │   │   │       ├── model/
│   │   │   │       │   └── TodoItem.kt
│   │   │   │       └── repository/
│   │   │   │           └── TodoRepository.kt
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── advanced_timezone_item.xml
│   │   │   │   │   ├── timezone_item.xml
│   │   │   │   │   └── todo_item.xml
│   │   │   │   ├── drawable/
│   │   │   │   │   └── edit_text_background.xml
│   │   │   │   └── values/
│   │   │   │       └── strings.xml
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   │
│   ├── build.gradle
│   └── src/
│
├── build.gradle
├── settings.gradle
├── gradle.properties
├── .gitignore
└── README.md
```

---

## 🎮 Usage Guide

### EA Coder

1. **Launch the EA Coder app**
2. **Enter parameters**:
   - Fast EMA: 9-20 (recommended: 12)
   - Slow EMA: 20-50 (recommended: 26)
   - RSI Period: 10-30 (recommended: 14)
   - Base Risk: 0.5-5% (recommended: 1%)
   - Stop Loss: 20-100 points
   - Take Profit: 30-200 points
3. **Select MT4 or MT5**
4. **Click "Generate EA"**
5. **File saved** to app's storage

### Digital Clock

1. **View current UTC time** at top
2. **Add timezone**:
   - Type timezone name (e.g., `America/New_York`)
   - Click "Add" or use "Search" for common zones
3. **Manage timezones**:
   - Click ⭐ to favorite (shows at top)
   - Select sort option
   - Toggle time format (24/12-hour)
   - Click Remove to delete
4. **Real-time updates** every second

### To-Do List

1. **Create todo**:
   - Enter title (required)
   - Add description (optional)
   - Select priority
   - Enter category (optional)
   - Click "Add Todo"
2. **Manage todos**:
   - ✓ Check to mark complete
   - Delete to remove
   - Click "Clear Completed" to bulk delete
3. **Filter todos**:
   - All: Show all todos
   - Active: Show incomplete todos
   - Completed: Show finished todos
4. **Data persists** even after app restart

---

## 🔧 Configuration

### Gradle Properties

Edit `gradle.properties` for custom settings:

```properties
org.gradle.jvmargs=-Xmx2048m
org.gradle.parallel=true
android.useAndroidX=true
```

### Build Variants

```bash
# Debug build (development)
./gradlew buildDebug

# Release build (production)
./gradlew buildRelease

# Custom build with specific variant
./gradlew assembleDebug
```

---

## 🐛 Troubleshooting

### Common Issues

**Issue**: Gradle sync fails
```bash
# Solution: Clear Gradle cache
./gradlew clean
./gradlew sync
```

**Issue**: App crashes on startup
```
- Ensure Android SDK is installed
- Check minimum API level (21+)
- Verify all dependencies are resolved
```

**Issue**: Database errors
```
- Clear app data: Settings → Apps → EA-Sniper → Storage → Clear Data
- Uninstall and reinstall the app
- Check Room database version compatibility
```

**Issue**: RecyclerView not showing items
```
- Verify adapter is properly initialized
- Check layout files for syntax errors
- Ensure ViewHolder IDs match layout resource IDs
```

---

## 📊 Database Schema

### TodoItem Table

```sql
CREATE TABLE todo_items (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  title TEXT NOT NULL,
  description TEXT,
  isCompleted BOOLEAN DEFAULT 0,
  priority TEXT DEFAULT 'MEDIUM',
  dueDate TEXT,
  category TEXT DEFAULT 'General',
  createdAt TEXT NOT NULL,
  updatedAt TEXT NOT NULL
)
```

---

## 📝 API Documentation

### TodoRepository Methods

```kotlin
// Get all todos
val allTodos: Flow<List<TodoItem>>

// Get only active todos
val activeTodos: Flow<List<TodoItem>>

// Get only completed todos
val completedTodos: Flow<List<TodoItem>>

// Insert new todo
suspend fun insertTodo(todo: TodoItem): Long

// Update existing todo
suspend fun updateTodo(todo: TodoItem)

// Delete todo
suspend fun deleteTodo(todo: TodoItem)

// Delete all completed todos
suspend fun deleteCompletedTodos()

// Get count of active todos
val activeCount: Flow<Int>
```

---

## 🚀 Performance Optimization

### Tips for Better Performance:

1. **Database Queries**:
   - Use proper indexes on frequently queried columns
   - Avoid N+1 query problems
   - Use Flow for reactive updates

2. **UI Rendering**:
   - RecyclerView DiffUtil for efficient updates
   - Proper ViewHolder pattern implementation
   - CardView shadow optimization

3. **Memory Management**:
   - Proper Coroutine scope handling
   - Flow lifecycle awareness
   - Database connection pooling

---

## 🔐 Security Considerations

- ✅ Input validation on all user inputs
- ✅ SQLite local storage (encrypted optional)
- ✅ No sensitive data transmitted
- ✅ Proper error handling without exposing stack traces

---

## 🤝 Contributing

Contributions are welcome! To contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is open source and available under the MIT License. See LICENSE file for details.

---

## 📞 Support & Contact

**Author**: Makabaza97  
**GitHub**: https://github.com/Makabaza97  
**Repository**: https://github.com/Makabaza97/EA-Sniper  
**Email**: motladiilekabelo97@gmail.com

---

## 📚 Resources & References

### Learning Resources:
- [Android Developers Documentation](https://developer.android.com/)
- [Kotlin Official Documentation](https://kotlinlang.org/docs/)
- [Room Database Tutorial](https://developer.android.com/training/data-storage/room)
- [Coroutines Guide](https://kotlinlang.org/docs/coroutines-overview.html)

### Official Links:
- [Android Studio Download](https://developer.android.com/studio)
- [GitHub Documentation](https://docs.github.com/)
- [Material Design](https://material.io/design/)

---

## 🎯 Future Enhancements

- [ ] Weather integration for timezone display
- [ ] Task reminders and notifications
- [ ] Cloud backup for todos
- [ ] Dark theme support
- [ ] Multi-language support
- [ ] Advanced EA indicators
- [ ] Import/Export functionality
- [ ] Analytics dashboard

---

**Last Updated**: June 10, 2026  
**Status**: ✅ Active Development  
**Maintainer**: Makabaza97
