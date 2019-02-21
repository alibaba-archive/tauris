# Codec - decoder - plain

## 1. 概述

作用于 **input** 插件，将消息字符串decode为一个event

## 2. 属性

无

## 3. 配置范例

```
stdin {
   codec => plain { }
}
```

输入的消息字符串保存在event的@source中。