# PhotoPicker
照片选择器

导入依赖
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
