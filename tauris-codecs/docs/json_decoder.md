# Codec - decoder - json

## 1. 概述

作用于 **input** 插件，用于将消息字符串decode为一个event对象

## 2. 属性

| 名称       | 类型       | 可选   | 说明      |
| -------- | -------- | ---- | ------- |
| includes | string[] | 是    | 输出的字段名  |
| excludes | string[] | 是    | 不输出的字段名 |

includes和excludes只能配置一个，如果都不配置，则输出event的所有字段。

## 3. 配置范例

```
stdin {
   codec => json {
     includes => ['name', 'value'];
   }
}
```

消息字符串将保存到event的@source中，decoder后的json内容会作为event的field。

如json

```
{"name": "jon snow", "gender" : "male"}
```

会得到event

```
{
  "@source" : "{\"name\": \"jon snow\", \"gender\" : \"male\"}",
  "name": "jon snow", 
  "gender" : "male"
}
```

