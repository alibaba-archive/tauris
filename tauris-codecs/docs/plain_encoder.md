# Codec - encoder - plain

## 1. 概述

作用于 **output** 插件，将event对象encode为一个字符串

## 2. 属性

| 名称     | 类型     | 可选   | 说明               |
| ------ | ------ | ---- | ---------------- |
| source | string | 是    | 输出的字段，默认为@source |

## 3. 配置范例

```
stdout {
   codec => plain {
     source => "name";
   }
}
```

