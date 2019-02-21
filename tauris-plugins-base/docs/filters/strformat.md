# 过滤器:Split

将字符串根据分隔符拆分成数组, 通常配合filter插件[list](list.md)使用

## 一 配置

| 属性名       | 类型     | 可选   | 描述                   |
| --------- | ------ | ---- | -------------------- |
| source    | string | 是    | 被拆分字符串字段名，缺省为@source |
| target    | string | 否    | 保存拆分后的结果的字段名         |
| separator | string | 否    | 分隔符.                 |


## 二 范例

**配置**
```
input {
    ...
}

filter {
    split {
        source => 'str';
        target => 'array';
        separator => ',';
    }
}


output {
    ...
}
```

**输入** 

str:

```
a,b,c,d
```

**输出**

array:

```
["a", "b", "c", "d"]
```