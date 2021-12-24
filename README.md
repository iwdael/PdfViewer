# PdfViewer
![](https://img.shields.io/badge/platform-android-orange.svg)
![](https://img.shields.io/badge/language-java-yellow.svg)
![](https://jitpack.io/v/iwdael/pdfviewer.svg)
![](https://img.shields.io/badge/build-passing-brightgreen.svg)
![](https://img.shields.io/badge/license-apache--2.0-green.svg)
![](https://img.shields.io/badge/api-19+-green.svg)

用于在 Android 上显示 PDF 文档的库，具有动画、手势、缩放和双击支持。自定义解码源 解码PDF文件。适用于 API 19 (Android 4.4) 及更高版本。


## 如何配置
将本仓库引入你的项目:
### Step 1. 添加JitPack仓库到Build文件
合并以下代码到项目根目录下的build.gradle文件的repositories尾。

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

### Step 2. 添加依赖
合并以下代码到需要使用的application Module的dependencies尾。
```
	dependencies {
        implementation 'com.iwdael.pdfviewer:viewer:$version'
        implementation 'com.iwdael.pdfviewer:core-mupdf:$version' //mupdf加载方式
        implementation 'com.iwdael.pdfviewer:core-pdfium:$version' //pdfium加载方式
	}
```

## 示例
加载PDF
```
        val pdfView = findViewById(R.id.pdfView);
        val source = AssetSource("sample.pdf") //播放源类型，以及加载器可自定义
        pdfView.fromSource()
                .defaultPage(0)
                .enableAnnotationRendering(true)
                .swipeHorizontal(false)
                .enableSwipe(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(0) // in dp
                .pageFitPolicy(FitPolicy.WIDTH)
                .load();
```
自定义加载器
```
public interface CoreSource {
    void renderPageBitmap(Bitmap bitmap, int docPage, int left, int top, int width, int height, boolean annotationRendering);

    Meta getDocumentMeta();

    List<Bookmark> getTableOfContents();

    List<Link> getPageLinks(int docPage);

    RectF mapRectToDevice(int docPage, int startX, int startY, int sizeX, int sizeY, int i, RectF rect);

    void closeDocument();

    int getPageCount();

    Size getPageSize(int page);
}
```
```
public interface DocumentSource {
    CoreSource createCore(Context context, String password) throws IOException;
}
```
## 感谢
Power by [barteksc/AndroidPdfViewer](https://github.com/barteksc/AndroidPdfViewer)