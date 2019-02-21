# Filter:json_decode

将json字符串decode为对象

## 一 配置

| 属性名    | 类型     | 可选   | 描述                                      |
| ------ | ------ | ---- | --------------------------------------- |
| source | string | 是    | json字符串的字段名，默认为@source                  |
| target | string | 是    | 保存json对象的字段名，如果未设置，则将json作为event的fields |


## 二 范例

**配置**
```
input {
    ...
}

filter {
    json_decode {
        source => '@source';
        target => 'json_obj';
    }
}


output {
    ...
}
```

**输入** 

str:

```
"{\"first_name\" : \"jon\", \"last_name\" :\"snow\"}"
```

**输出**

```
{
  "@source" : "{\"first_name\" : \"jon\", \"last_name\" :\"snow\"}";
  "json_obj" : {
      "first_name" : "jon",
      "last_name" : "snow"
  }
}
```