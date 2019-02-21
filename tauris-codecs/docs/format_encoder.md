# Codec - encoder - format

## 1. 概述

作用于 **output** 插件，将event对象format为字符串

## 2. 属性

| 名称     | 类型     | 可选   | 说明                                    |
| ------ | ------ | ---- | ------------------------------------- |
| format | string | 否    | 格式化字符串。见[表达式](../../docs/eventfmt.md) |



## 3. 配置范例

```
stdout {
   codec => format {
     format => "%{name} is %{role}";
   }
}
```

输入event

```
{"name" : "jon snow", "role": "king of north"}
```

将会得到

```
jon snow is king of north
```