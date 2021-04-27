# PhotoPicker
一个简单好用的照片选择器
![image](https://github.com/AchenBin/PhotoPicker/blob/master/photopickerlibrary/src/main/res/mipmap-hdpi/round_checked.png)


##### 已适配
	Android 10  android:requestLegacyExternalStorage="true"
	Android 7   fileProvider
##### 已自动动态获取存储权限
	<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />


### 导入依赖

Step 1. project.gradle中：Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. app.gradle中：Add the dependency

	dependencies {
	        implementation 'com.github.AchenBin:PhotoPicker:Tag'
	}

Step 3. app.gradle中：添加Java8语法

	buildTypes {
		release {
		 	...
		}
		compileOptions {
		    sourceCompatibility JavaVersion.VERSION_1_8
		    targetCompatibility JavaVersion.VERSION_1_8
		}
	}

### 使用
	new PhotoPickerBuilder(this)		//需要提供context
		.setMaxSelectNum(1)		//设置最大选择数量
		.setOnSelectConfirmListener(new OnSelectConfirmListener() {	//选择回调监听
			@Override
			public void onSelected(List<PictureItem> pictureItemList) {		//获取选择图片列表，PictureItem中的get方法可获取图片的各种信息
				//可以通过返回的列表进行操作了
				...
			}
		})
		.start();
			
### 方法说明
	.setMaxSelectNum(XX)		 	//设置最大选择数量
	.setColumnNum(XX)		 	//设置浏览页面图片列数
	.setTitleBarTextColor(R.color.XX)	//设置标题栏字体颜色
	.setTitleBarHeight(getResources().getDimension(R.dimen.XXX))//设置标题栏高度
	.setTitleBarColor(R.color.XX)		//设置标题栏背景颜色

### PictureItem说明
	public class PictureItem {
	    private String path;    //图片的绝对地址
	    private String type;    //图片的类型
	    private String name;    //图片名称
	    private float size;     //图片大小
	    private String width;   //图片宽度
	    private String height;  //图片高度
	    private long Date;      //图片生成日期
	    private int orientation;    //图片角度
	}
