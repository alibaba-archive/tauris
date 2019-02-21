# Codec - encoder - json

## 1. 概述

作用于 **output** 插件，将event对象encode为一个json字符串

## 2. 属性

| 名称       | 类型       | 可选   | 说明                |
| -------- | -------- | ---- | ----------------- |
| includes | string[] | 是    | 输出的字段名            |
| excludes | string[] | 是    | 不输出的字段名           |
| pretty   | bool     | 是    | 格式化输出json，默认false |

includes和excludes只能配置一个，如果都不配置，则输出event的所有字段。

## 3. 配置范例

```
stdout {
   codec => json {
     includes => ['name', 'value'];
   }
}
```

