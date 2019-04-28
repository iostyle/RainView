# RainView [![](https://jitpack.io/v/iostyle/RainView.svg)](https://jitpack.io/#iostyle/RainView)

## How to
To get a Git project into your build:

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Step 2. Add the dependency
```
	dependencies {
	        implementation 'com.github.iostyle:RainView:1.6.1'
	}
```

---

## 使用帮助

| Attrs | Format | description |
|:-:|:-:|:-:|
|auto_play|boolean|自动播放|
|max_num|integer|一屏最大数量|
|random_size|boolean|是否随机大小|
|item_width|dimension\|reference|每个单位宽度|
|item_height|dimension\|reference|每个单位高度|
|item_drawable|reference|单位图索引|
|speed|integer|下落速度|
|wind|integer|风速|
|random_speed|boolean|是否随机速度|
|random_wind|boolean|是否随机速度|
|change_wind|boolean|风向是否改变|
|once|boolean|是否只执行一次|
|trans|boolean|是否开启透明度动画|
|rotate|boolean|是否开启旋转|

```
//单位被点击回调
public interface ClickListener {
    void onClick();
}
//动画执行完毕回调 if（once == true）
public interface FinishListener {
    void onFinish();
}
```

```
 /**
  * 设置透明消失位置
  *
  * @param isTrans 是否开启透明度动画
  * @param f1 从屏幕的某个高度开始消失 如0.7f
  * @param f2 消失过程高度 如0.2f
  */
public Builder setTrans(boolean isTrans, float f1, float f2);
  
 /**
  * 设置物体大小随机
  *
  * @param isRandomSize 是否随机
  * @param minScale     最小倍率
  * @param maxScale     最大倍率
  */
public Builder setRandomSize(boolean isRandomSize, float minScale, float maxScale);
```

## demo
![](https://github.com/iostyle/ImageRepo/blob/master/RainViewDemo.gif) 
        
---

## You Can Do More
![](https://github.com/iostyle/ImageRepo/blob/master/AttireDemo.gif) 
![](https://github.com/iostyle/ImageRepo/blob/master/RedRainDemo.gif)
