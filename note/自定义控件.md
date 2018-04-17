# 自定义控件 CustomView

1. 完成指定效果
2. 封装、方便

## 流程
 1. 准备阶段-加载阶段
 2. 测量阶段-规划大小
 3. 布局阶段-绘制位置
 4. 绘制阶段-画
 
 
 
## 加载阶段
自定义view构造函数:用于初始化加载数据，属于自定义view第一个阶段。

``` 
    //用于创建自定义控件实例
    public TextView(Context context) {
         this(context, null);
     }
    //用于将当前自定义控件声明在布局文件中
    public TextView(Context context, @Nullable AttributeSet attrs) {
     this(context, attrs, com.android.internal.R.attr.textViewStyle);
    }
    //用于将当前自定义控件声明在布局文件中，并且加入样式style
    public TextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
     this(context, attrs, defStyleAttr, 0);
    }
    public TextView( Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(); 
    }
    
    init(){
    //初始化数据
    }
```

## 绘制阶段


```
     /*
    画圆
    cx,cy圆心的坐标
    radius圆的半径
    paint 画笔
     */
    canvas?.drawCircle(facePoint.x, facePoint.y, faceRadius, redPaint)

    /*
    画线
    startX,startY
    stopX,stopY
    paint
     */
    canvas?.drawLine(pointLeftEyeStart.x, pointLeftEyeStart.y, pointLeftEyeStop.x, pointLeftEyeStop.y, redPaint)

    /*
    RectF oval :　用于确定圆弧形状与尺寸的椭圆边界（即椭圆外切矩形）
   float startAngle  开始角度（以时钟3点的方向为0°，顺时针为正方向）
   float sweepAngle 扫过角度（以时钟3点的方向为0°，顺时针为正方向）
   boolean useCenter 是否包含圆心
   Paint paint绘制圆弧的画笔
     */
    val rectF = RectF(440f, 1060f, 640f, 1160f)
    canvas?.drawRect(rectF, redPaint)
    canvas?.drawArc(rectF, 30f, 120f, true, bluePaint)
```

Paint Style
       Paint.Style.FILL_AND_STROKE 描边并且填充
       Paint.Style.FILL 填充，实心
       Paint.Style.STROKE 描边，空心

## onMeasure
用于测量控件，如果控件还有子控件，则会递归测量子控件。
如果是ViewGroup,默认值只会测量自身控件。
     
     protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                    getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
        }
MeasureSpec：32位，由两部分组成，由父布局和子控件共同决定，是一个组合值，用来控制宽高。
前两位模式
* 未指定:0<<30 UNSPECIFIED 较少使用，未限定实际宽高，比如：ScrollView对于子视图的高度的限定
* 确切的:1<<30 EXACTLY 明确的惠存
* 最大的:2<<30 AT_MOST Wrap_content

后三十位 控件尺寸
   

## 其它问题
1. 自定义控件中的onMeasure和onLayout执行两次？

```
    03-21 01:15:49.693 23928-23928/com.liuhe.customviewpractice D/liuhe->d: onResume()执行完毕
    03-21 01:15:49.737 23928-23928/com.liuhe.customviewpractice D/liuhe->d: onMeasure
    03-21 01:15:49.771 23928-23928/com.liuhe.customviewpractice D/liuhe->d: onLayout
    03-21 01:15:49.832 23928-23928/com.liuhe.customviewpractice D/liuhe->d: onMeasure
    03-21 01:15:49.833 23928-23928/com.liuhe.customviewpractice D/liuhe->d: onLayout
    03-21 01:15:49.836 23928-23928/com.liuhe.customviewpractice D/liuhe->d: onDraw
```
测量的时候父控件的onMeasure方法会遍历他所有的子控件，挨个调用子控件的measure方法，measure方法会调用onMeasure，
然后会调用setMeasureDimension方法保存测量的大小，一次遍历下来，第一个子控件以及这个子控件中的所有子控件都会完成
测量工作；然后开始测量第二个子控件…；最后父控件所有的子控件都完成测量以后会调用setMeasureDimension方法保存自己的
测量大小。值得注意的是，这个过程不只执行一次，也就是说有可能重复执行，因为有的时候，一轮测量下来，父控件发现某一个子控
件的尺寸不符合要求，就会重新测量一遍。

2. getX()与getRawX()

getX() 获取当前视图x位置

```
override fun onTouchEvent(event: MotionEvent?): Boolean {
    return super.onTouchEvent(event)
    "event?.x=${event?.x}-- event.rawX=${event?.rawX}".log()
}
```

3. Canvas的save和restore

在创建新的控件或修改现有的控件时，我们都会涉及到重写控件或View的onDraw方法。
onDraw方法会传入一个Canvas对象，它是你用来绘制控件视觉界面的画布。
在onDraw方法里，我们经常会看到调用save和restore方法，它们到底是干什么用的呢？
* save：用来保存Canvas的状态。save之后，可以调用Canvas的平移、放缩、旋转、错切、裁剪等操作。
* restore：用来恢复Canvas之前保存的状态。防止save后对Canvas执行的操作对后续的绘制有影响。

save和restore要配对使用（restore可以比save少，但不能多），如果restore调用次数比save多，会引发Error。

4. Canvas的scale()
    
   // x,y缩放比例
   public void scale (float sx, float sy) ;

   // 指定缩放的基准点
   public final void scale (float sx,float sy, float px, float py);

5. 类的加载过程
    
        java类的加载过程 静态代码块及静态成员->成员变量->构造函数
        kotlin 成员变量->主构造函数和init
      

6. 绘制罗盘
    https://github.com/ChaosOctopus/ChaosCompass
    
7. kotlin自定义控件，要在主构造上使用多参数的，否则在xml中使用，findViewById会报NPE,比如：  
class PieView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {

8. requestLayout() 与 invalidate（）

    invalidate()方法会执行draw过程，重绘View树。
    当View的，隐显改变,背景改变,焦点状态和一些其他状态改变，会引起invalidate操作。
    View（非容器类）调用invalidate方法只会重绘自身，ViewGroup调用则会重绘整个View树。
    
    当View的宽高发生了变化，现在所在区域发生变化，调用requestLayout()方法重新对View布局。
    View执行requestLayout()方法，会向上递归到顶级父View中，再执行这个顶级父View的requestLayout，所以其他View的onMeasure，onLayout也可能会被调用。
    
    进行View更新时，若仅View的显示内容发生改变且新的内容不影响View的大小、位置，则只需调用invalidate方法；若View的宽高、位置发生改变且显示内容不变，只需调用requestLayout方法；若两者均发生改变，则需调用两者，按照View的绘制流程，推荐先调用requestLayout方法再调用invalidate方法。
    
    
9. 弧度角度
    长度为半径长的弧，所对的圆心角是 1 弧度（Radian），用符号rad表示。
    
    
    360度= 2π rad
    180度= π rad
    1度 =（π / 180）rad ≈ 0.01745 rad
    1弧度 =（180 /π）o ≈ 57.30 o
    
    α 度的角 =  α ·（π / 180）rad