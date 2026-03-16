# Batter Code (Android)

Приложение с вводом 6-значного кода и анимацией в стиле «Матрицы».

## Сборка

Откройте корень проекта **batter_app** в **Android Studio**. При первом открытии Studio предложит синхронизировать проект и скачать Gradle — подтвердите. Затем **Build → Make Project** или запуск на устройстве/эмуляторе.

Из командной строки (после первой успешной синхронизации в Studio, чтобы появился `gradle/wrapper/gradle-wrapper.jar`):

```bash
./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`.

## Коды доступа (4 штуки)

- `123456` → фраза 1  
- `654321` → фраза 2  
- `111222` → фраза 3  
- `999888` → фраза 4  

Шаблоны фраз задаются в `app/src/main/res/values/strings.xml` (`matrix_phrase_1` … `matrix_phrase_4`).
