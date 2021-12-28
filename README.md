# Android transform插件demo
## 实现功能
 通对方法添加注解,完成日志的自动添加(打印方法参数).
## 运行环境
适配agp7.0.3
## 使用
### 源码依赖方式:
- 拷贝`buildSrc`参考app模块.
- 参考app模块.
```grovvy
import com.demo.android.plugin.MyPlugin
apply plugin: MyPlugin
```
- 项目中使用:

```kotlin
@LogInject(msg = "注解测试1", logType = LogInject.LOG_TYPE.ERROR)
fun test(arg1: Int) {
}
```
## 效果:
![image.png](https://flyisme.work/upload/2021/12/image-967b580013c94d599ef8270338535210.png)
## 参考
[DoraemonKit](https://github.com/didi/DoraemonKit)
