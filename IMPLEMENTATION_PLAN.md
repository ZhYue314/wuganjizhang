# 无感记账 App 实现计划

## 项目概述
基于 UI 设计文档和技术架构文档实现的 Android 记账应用，支持自动识别交易、手动记账、统计分析等功能。

## 技术栈（已迁移完成）
- **语言**: ✅ Kotlin 2.0.0
- **UI**: ✅ Jetpack Compose (Material3)
- **架构**: MVVM + Clean Architecture (进行中)
- **数据库**: ✅ Room 2.6.1 (待添加 SQLCipher 加密)
- **依赖注入**: ✅ Hilt 2.51.1
- **异步处理**: ✅ Kotlin Coroutines 1.8.0 + Flow
- **导航**: ✅ Navigation Compose 2.7.7
- **偏好设置**: ✅ DataStore 1.0.0 (待实现)
- **后台任务**: ⏳ WorkManager (待配置)
- **注解处理**: ✅ KSP (Kotlin Symbol Processing)
- **图表**: ⏳ MPAndroidChart (待添加)
- **图片加载**: ⏳ Coil (待添加)
- **日志**: ⏳ Timber (待添加)

## 实现阶段（已根据技术文档和HTML原型更新）

### 阶段 1: 基础架构搭建 ✅ 已完成
- [x] 修复资源文件错误
- [x] 创建自定义属性 (attrs.xml)
- [x] 补充缺失的 drawable 资源
- [x] 添加必要的依赖库
- [x] 创建数据模型类 (Transaction, Category, Account) - Kotlin Data Classes
- [x] **迁移到 Kotlin 2.0.0** ✅
- [x] **配置 Hilt 依赖注入** ✅
- [x] **设置 Room Database** ✅ (待添加 SQLCipher 加密)
- [x] **配置 DataStore 依赖** ✅ (待实现业务逻辑)
- [x] **设置 Navigation Compose** ✅
- [x] **配置 KSP (替代 kapt)** ✅
- [x] **添加 Kotlin Coroutines** ✅
- [x] **配置 Compose Compiler Plugin** ✅
- [x] **清理旧的 Java/XML 文件** ✅

### 阶段 2: 核心数据层 ✅ 基础完成，待完善
- [x] 创建完整的 Room Entities:
  - [x] Transaction (Kotlin Data Class + @ColumnInfo)
  - [x] Category (Kotlin Data Class + @ColumnInfo)
  - [x] Account (Kotlin Data Class + @ColumnInfo)
  - [ ] TagEntity (标签)
  - [ ] PeriodicTemplateEntity (周期模板)
  - [ ] BackupRecordEntity (备份记录)
  - [ ] CurrencyRateEntity (汇率)
  - [ ] ArchiveRecordEntity (归档记录)
  - [ ] TransactionTagCrossRef (多对多关联)
  - [ ] SmartRuleEntity (智能学习规则)
  - [ ] PendingTransactionEntity (待确认交易)
- [x] 创建所有 DAO 接口 (TransactionDao, CategoryDao, AccountDao)
- [x] 实现 TypeConverters (DateConverter, ListConverter)
- [ ] 配置数据库加密 (SQLCipher)
- [ ] 创建 Repository 接口 (Domain Layer)
- [ ] 实现 RepositoryImpl (Data Layer)
- [ ] 创建 UseCase (Interactor)

### 阶段 3: 主要功能页面 (基于 HTML 原型) ⏳ 进行中
- [x] **首页 Screen** (`scr-home`) - Compose ✅
  - [x] 月度汇总卡片 (收入/支出/结余)
  - [x] 交易列表 (LazyColumn + 按日期分组)
  - [x] FAB 快速记账按钮
  - [x] 下拉刷新 (SwipeRefresh)
  - [ ] 分页加载 (Paging 3)
- [ ] **统计页 Screen** (`scr-stats`) - Compose
  - [ ] 时间维度切换 (日/周/月/年)
  - [ ] 收支趋势柱状图 (MPAndroidChart 或 Compose Charts)
  - [ ] 分类占比环形图
  - [ ] 分类排行榜
  - [ ] 导出报表入口
- [ ] **日历页 Screen** (`scr-calendar`) - Compose
  - [ ] 月份导航 (上/下月)
  - [ ] 日历网格 (显示每日消费)
  - [ ] 月度汇总卡片
  - [ ] 消费热力图
  - [ ] 点击日期查看详情
- [x] **底部导航栏** ✅ (Navigation Compose + BottomNavigationBar)

### 阶段 4: 交易管理 (核心功能) ⏳ 部分完成
- [x] **手动记账底部抽屉** (`sub-entry`) - 基础版完成 ✅
  - [x] 金额输入 (TextField + 错误提示)
  - [x] 类型切换 (支出/收入/转账 + 彩色背景 + 侧滑动效)
  - [x] 分类选择网格 (Emoji 图标 + 中文名称)
  - [x] 账户选择 (Dialog 选择器)
  - [x] 商户名称输入
  - [x] 备注输入
  - [x] 保存交易到数据库
  - [x] 表单验证 (金额/分类/账户)
  - [x] 错误提示优化 (placeholder + 标题旁提示)
  - [x] BottomSheet 动画 (上滑打开/下滑关闭)
  - [ ] 日期时间选择 (使用默认当前时间)
  - [ ] 标签添加
  - [ ] 附件添加 (图片/语音)
  - [ ] 自定义数字键盘
- [ ] **交易详情页** (`sub-detail`)
  - [ ] 交易金额和类型展示
  - [ ] 详细信息列表 (分类/商户/账户/时间/标签/备注/来源)
  - [ ] 附件预览 (图片/语音播放)
  - [ ] 编辑/删除/合并操作
- [ ] **搜索页** (`sub-search`)
  - [ ] 搜索框 (商户/备注/标签)
  - [ ] 筛选 Chips (金额/日期/分类/标签)
  - [ ] 排序选项 (时间/金额)
  - [ ] 搜索结果列表
- [ ] **批量操作页** (`sub-batch`)
  - [ ] 多选交易
  - [ ] 批量修改分类
  - [ ] 批量添加标签
  - [ ] 批量删除

### 阶段 5: 分类和账户管理
- [ ] **分类管理页** (`sub-category`)
  - [ ] 支出分类列表 (带开关)
  - [ ] 收入分类列表
  - [ ] 自定义分类管理
  - [ ] 添加/编辑/删除分类
  - [ ] 分类图标和颜色选择
- [ ] **账户管理页** (`sub-account`)
  - [ ] 账户列表 (微信/支付宝/银行卡/现金)
  - [ ] 账户余额显示
  - [ ] 添加/编辑/删除账户
  - [ ] 账户图标和颜色选择
- [ ] **转账功能** (`sub-transfer`)
  - [ ] 转出账户选择
  - [ ] 转入账户选择
  - [ ] 转账金额输入
  - [ ] 转账备注
  - [ ] 账户间互转 vs 转给商户

### 阶段 6: 高级功能
- [ ] **数据备份与恢复** (`sub-backup`)
  - [ ] 立即备份
  - [ ] 备份历史列表
  - [ ] 恢复备份
  - [ ] 自动备份配置 (WorkManager)
- [ ] **数据导出** (`sub-export`)
  - [ ] PDF 报表导出 (含图表)
  - [ ] Excel 报表导出
  - [ ] CSV 数据导出
  - [ ] 图表图片导出
  - [ ] 时间范围选择
  - [ ] 包含内容选择
- [ ] **数据归档** (`sub-archive`)
  - [ ] 创建归档
  - [ ] 归档列表
  - [ ] 恢复归档
- [ ] **数据清理** (`sub-cleanup`)
  - [ ] 按时间清理
  - [ ] 按分类清理
  - [ ] 缓存清理
- [ ] **周期交易管理** (`sub-periodic`)
  - [ ] 周期模板列表
  - [ ] 创建/编辑模板
  - [ ] 暂停/启用模板
  - [ ] 自动生成周期交易 (WorkManager)
- [ ] **多币种管理** (`sub-currency`)
  - [ ] 币种列表 (CNY/USD/EUR/JPY/GBP/HKD)
  - [ ] 汇率显示
  - [ ] 手动更新汇率
  - [ ] 交易时币种转换

### 阶段 7: 自动化功能 (核心技术)
- [ ] **通知监听服务** (`NotificationListenerService`)
  - [ ] 服务声明和权限引导
  - [ ] 微信支付通知解析
  - [ ] 支付宝支付通知解析
  - [ ] 重复检测 (30秒窗口)
  - [ ] 发送到交易检测器
- [ ] **无障碍服务** (`AccessibilityService`)
  - [ ] 服务声明和配置
  - [ ] 支付完成页面识别
  - [ ] 微信交易数据提取
  - [ ] 支付宝交易数据提取
- [ ] **交易检测引擎** (`TransactionDetector`)
  - [ ] 智能分类匹配 (SmartRule)
  - [ ] 自动记录模式
  - [ ] 确认后记录模式
  - [ ] 待确认队列管理
  - [ ] 置信度计算
- [ ] **智能学习系统** (`SmartLearningEngine`)
  - [ ] 从用户修正中学习
  - [ ] 自动生成规则 (基于历史)
  - [ ] 规则置信度更新
  - [ ] 批量更新建议

### 阶段 8: 优化和完善
- [ ] **首次启动引导页** (`onboard`)
  - [ ] 欢迎页
  - [ ] 权限说明页
  - [ ] 常用分类选择页
  - [ ] 准备就绪页
- [ ] **主题系统**
  - [ ] 浅色主题
  - [ ] 深色主题
  - [ ] 跟随系统
  - [ ] 动态主题切换
- [ ] **性能优化**
  - [ ] 启动优化 (App Startup)
  - [ ] 列表性能 (Paging 3)
  - [ ] 图片优化 (Coil 缓存)
  - [ ] 数据库查询优化 (索引)
  - [ ] 内存优化 (Flow + StateFlow)
  - [ ] Compose 重组优化 (如迁移)
- [ ] **安全加固**
  - [ ] 数据库加密 (SQLCipher)
  - [ ] 密钥管理 (Android Keystore)
  - [ ] 应用锁 (生物识别)
  - [ ] 敏感数据处理
- [ ] **测试**
  - [ ] 单元测试 (UseCase, Repository)
  - [ ] UI 测试 (Compose Test / Espresso)
  - [ ] 性能测试 (Macrobenchmark)
  - [ ] 集成测试

## 当前项目状态 (2026-04-15 最新更新)

### ✅ 已完成的工作
1. **Kotlin + Jetpack Compose 迁移** - 100% 完成
   - 所有 Java 文件已转换为 Kotlin
   - 所有 XML Layout 已替换为 Composable
   - 配置了 Compose Compiler Plugin
   
2. **依赖配置** - 100% 完成
   - Kotlin 2.0.0
   - Jetpack Compose (Material3)
   - Hilt 2.51.1 (依赖注入)
   - Room 2.6.1 (数据库)
   - KSP (注解处理)
   - Navigation Compose
   - Coroutines + Flow
   - DataStore
   
3. **数据层** - 80% 完成
   - 3个 Entity (Transaction, Category, Account)
   - 3个 DAO (TransactionDao, CategoryDao, AccountDao)
   - AppDatabase (Room)
   - TypeConverters (Date, List)
   - DatabaseModule (Hilt)
   - ⚠️ 缺失 8 个表（标签、周期模板、备份记录、汇率、归档、智能规则、待确认交易等）
   - ⚠️ 未启用 SQLCipher 加密
   
4. **UI 层** - 85% 完成
   - MainActivity (Compose Activity)
   - AppNavigation (底部导航)
   - HomeScreen (完整实现 ✅)
     - 月度汇总卡片（紧凑设计 + 小数部分字号优化）✅
     - 交易列表（按日期分组 + 紧凑布局）✅
     - FAB 按钮
     - 删除交易功能
     - 转账金额颜色显示（蓝色）✅
     - 长按多选功能 ✅
     - 全选/取消全选 ✅
     - 批量删除 ✅
     - 顶部空白优化（条件性 padding）✅
   - StatsScreen (占位符) ⏳
   - CalendarScreen (占位符) ⏳
   - AddTransactionSheet (完整实现 ✅)
     - 金额输入框
     - 类型选择器（支出/收入/转账）
     - 分类网格（预加载所有分类）✅
     - 账户选择 Dialog
     - 商户和备注输入
     - 表单验证
     - BottomSheet 动画
     - 滑动切换效果（HorizontalPager）✅
     - 滑动背景渐变（红→绿→蓝）✅
     - 分类即时显示（无延迟）✅
     - UI 布局统一（无跳动）✅
     - 保存后自动关闭 ✅
     - 错误状态清除（取消时重置）✅
     - 类型同步修复（点击/滑动正确保存）✅
   - TransactionDetailSheet (完整实现 ✅)
     - 交易详情展示
     - 编辑/删除功能
   - Theme (浅色/深色主题)
   
5. **业务逻辑层** - 70% 完成
   - HomeViewModel (月度统计 + 交易列表 + 格式化函数) ✅
   - AddTransactionViewModel (表单状态管理 + 验证 + 分类预加载 + 类型同步) ✅
   - DataInitializer (预设数据初始化)
   - ⚠️ 缺少 Domain Layer（UseCase、Repository 接口）
   - ⚠️ ViewModel 直接调用 DAO，未使用 Repository 模式

### 🎯 下一步优先级任务

#### P0 - 已完成 ✅
1. ✅ **创建 HomeViewModel**
   - ✅ 使用 Hilt 注入 DAO
   - ✅ 使用 StateFlow 管理数据
   - ✅ 加载月度统计数据
   - ✅ 观察交易列表变化
   
2. ✅ **实现完整的 HomeScreen**
   - ✅ 月度汇总卡片 (从数据库读取)
   - ✅ 交易列表 (LazyColumn + 日期分组)
   - ✅ FAB 按钮响应
   - ✅ 空状态提示
   - ✅ 删除交易功能
   - ✅ 大额数字布局优化 (防溢出)
   - ✅ 转账金额颜色显示（蓝色）
   
3. ✅ **添加初始化数据**
   - ✅ 首次启动时插入预设分类 (8个支出 + 5个收入)
   - ✅ 插入默认账户 (微信/支付宝/银行卡/现金)
   - ✅ DataInitializer 自动执行

4. ✅ **实现手动记账 BottomSheet** (新增)
   - ✅ 金额输入框 (动态 placeholder + 错误提示)
   - ✅ 类型选择器 (支出/收入/转账 + 彩色背景)
   - ✅ 分类网格 (Emoji 图标 + 中文名称)
   - ✅ 账户选择 Dialog
   - ✅ 商户和备注输入
   - ✅ 表单验证 (金额/分类/账户)
   - ✅ 错误提示优化 (金额placeholder + 分类标题旁)
   - ✅ BottomSheet 动画 (上滑/下滑)
   - ✅ 类型切换动效 (HorizontalPager 滑动)
   - ✅ 滑动背景渐变 (红→绿→蓝)
   - ✅ 分类预加载（无延迟显示）
   - ✅ UI 布局统一（无跳动）
   - ✅ 点击切换防卡顿（isUserClick 标志）
   - ✅ 保存后自动关闭（带动画）
   - ✅ 表单重置保留分类数据

#### P1 - 短期任务 (1-2周)
5. **完善交易列表** ⭐ 推荐下一个
   - 添加下拉刷新 (SwipeRefresh)
   - 优化性能 (Paging 3)
   - 添加滑动删除手势
   
6. **交易详情页**
   - 显示完整交易信息
   - 编辑功能
   - 删除确认对话框优化
   
7. **完善统计页**
   - 添加图表库 (MPAndroidChart 或 Compose Charts)
   - 实现数据统计查询
   - 时间维度切换
   
8. **完善日历页**
   - 实现日历组件
   - 显示每日消费
   - 点击日期查看详情

9. **分类和账户管理**
    - 分类管理页面
    - 账户管理页面
    - 自定义分类/账户

#### P2 - 中期任务 (2-4周)
10. **搜索功能**
11. **数据备份/恢复**
12. **数据导入/导出**
13. **批量操作**

#### P3 - 长期任务 (1-2月)
14. **通知监听服务** (自动化记账)
15. **无障碍服务**
16. **智能学习和自动分类**
17. **安全加固 (SQLCipher)**
18. **性能优化和测试**
19. **多币种支持**

### 📊 完成度统计
- **基础架构**: 100% ✅
- **数据层**: 27% ⏳ (3/11 表，缺少加密)
- **UI 层**: 85% ⏳ (首页 + 记账 + 详情页完成，统计/日历待开发)
- **业务逻辑**: 70% ⏳ (2个 ViewModel 完成，缺少 Domain Layer)
- **自动化功能**: 0% ❌ (通知监听、无障碍服务、交易检测引擎)
- **第三方库**: 0% ❌ (SQLCipher, MPAndroidChart, POI 等均未添加)
- **总体进度**: ~15% (核心 UI 完成，但距离完整产品还有很大差距)

### 💡 重要说明
- 项目已成功从 Java + View 迁移到 Kotlin + Compose
- 所有核心依赖已配置完成
- 可以开始实现具体业务功能
- 建议先完成 P0 任务，让应用具备基本可用性
- ⚠️ **当前进度评估**：虽然 UI 层完成度较高（85%），但整体项目仅完成约 15%
- ⚠️ **架构简化**：当前使用 ViewModel + DAO 直接交互，未采用 Clean Architecture
- ⚠️ **数据库不完整**：仅实现 3/11 个表，缺少标签、周期模板、备份等核心表
- ⚠️ **自动化功能缺失**：通知监听、无障碍服务等核心技术尚未开始
- ⚠️ **第三方库未添加**：SQLCipher、MPAndroidChart、POI 等均未集成

## 注意事项
1. 本项目规模较大，建议逐步实现
2. 优先保证核心功能可用
3. 每个阶段完成后进行测试
4. 保持代码结构清晰
5. 遵循 Material Design 规范

## 下一步行动
✅ **核心功能已完成！**

现在应该：
1. ✅ 创建 HomeViewModel 并连接数据库
2. ✅ 实现完整的 HomeScreen UI
3. ✅ 添加初始化数据逻辑
4. ✅ 实现手动记账 BottomSheet
5. ✅ 优化 UI/UX (滑动动画 + 错误提示 + 布局统一)
6. ✅ 修复分类丢失问题（预加载 + 保留数据）
7. ✅ 修复点击切换卡顿问题（isUserClick 标志）
8. ✅ 添加转账金额颜色显示（蓝色）
9. ✅ 实现交易详情页
10. ✅ 首页交互优化（多选 + 全选 + 批量删除）
11. ✅ UI 紧凑化设计（卡片缩小 + 顶部空白优化）
12. ✅ 金额显示优化（小数部分字号 + 分类加粗）
13. 🎯 下一步：完善交易列表 (下拉刷新 + 分页)
14. 🎯 然后：统计页和日历页
15. 🎯 继续：分类和账户管理

### ⚠️ 与技术文档的主要差距

**数据层缺失：**
- ❌ 8个数据表未实现（标签、周期模板、备份记录、汇率、归档、智能规则、待确认交易等）
- ❌ SQLCipher 加密未启用
- ❌ Repository 模式未采用
- ❌ Domain Layer（UseCase）缺失

**UI 页面缺失：**
- ❌ 搜索页
- ❌ 批量操作页（完整功能）
- ❌ 分类管理页
- ❌ 账户管理页
- ❌ 转账页
- ❌ 数据备份/导出/归档页
- ❌ 周期交易管理页
- ❌ 多币种管理页
- ❌ 首次启动引导页

**自动化功能完全缺失：**
- ❌ NotificationListenerService（通知监听）
- ❌ AccessibilityService（无障碍服务）
- ❌ TransactionDetector（交易检测引擎）
- ❌ WeChatParser / AlipayParser（解析器）
- ❌ SmartLearningEngine（智能学习）

**第三方库未添加：**
- ❌ SQLCipher（数据库加密）
- ❌ MPAndroidChart（图表）
- ❌ Apache POI（Excel）
- ❌ OpenCSV（CSV）
- ❌ iText / PdfDocument（PDF）
- ❌ Coil（图片加载）
- ❌ ExoPlayer（语音播放）
- ❌ Timber（日志）
