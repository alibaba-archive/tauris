# Filter:json_encode

将field encode为json字符串

## 一 配置

| 属性名    | 类型     | 可选   | 描述            |
| ------ | ------ | ---- | ------------- |
| source | string | 否    | 字段名           |
| target | string | 否    | 保存json字符串的字段名 |

## 二 范例

**配置**

```
input {
    ...
}

filter {
    json_encode {
        source => 'actor';
        target => 'json_text';
    }
}


output {
    ...
}
```

**输入** 

event:

```
{
  "actor" : {
      "first_name" : "jon",
      "last_name" : "snow"
  }
}
```

**输出**

```
{
  "json_text" : "{\"first_name\" : \"jon\", \"last_name\" :\"snow\"}";
  "actor" : {
      "first_name" : "jon",
      "last_name" : "snow"
  }
}
```