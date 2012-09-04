Android-OCRSample using tesseract
====
This is an Android OCR sample using tesseract. You need to do the followings:

1. build http://code.google.com/p/tesseract-android-tools/ as Android library project
2. checkout http://code.google.com/p/tesseract-ocr/ and copy traineddata file(s) into "/mnt/sdcard/ocrsample/tessdata/" on your Android like this:

    adb push ~/src/tesseract-ocr-read-only/tessdata/eng.traineddata /mnt/sdcard/ocrsample/tessdata/

