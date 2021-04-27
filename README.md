# PhotoPicker
照片选择器

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
	  new PhotoPickerBuilder()
			.with(this)	//设置context
			.setMaxSelectNum(1)	//设置最大选择数量
			.setOnSelectConfirmListener(new OnSelectConfirmListener() {	//选择回调监听
			    @Override
			    public void onSelected(List<PictureItem> pictureItemList) {		//获取选择图片列表，PictureItem中的get方法可获取图片的各种信息
				//可以通过返回的列表进行操作了
				...
			    }
			})
			.start();
			

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
