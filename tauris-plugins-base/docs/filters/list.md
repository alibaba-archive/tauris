# 过滤器:List

将类型为array或list的字段中的item映射到字段

## 一 配置

| 属性名     | 类型       | 可选   | 描述                          |
| ------- | -------- | ---- | --------------------------- |
| source  | string   | 否    | 数组/列表字符串字段名                 |
| targets | string数组 | 否    | 保存映射后的结果的字段名， 若字段名为'_'， 则忽略 |


## 二 范例

**配置**
```
input {
    ...
}

filter {
    split {
       separator => ',';
       target => 'array';
    }
    list {
        source => 'array';
        target => ['f1', 'f2', 'f3', "_", "f4"]; # 第4个element将被忽略
    }
}


output {
    ...
}
```

**输入** 

```
v1,v2,v3,vv,v4
```

**输出**

ua:

```
{
	"f1" : "v1",
	"f2" : "v3",
	"f3" : "v3",
	"f4" : "v4"
}
```
## 三 高级用法

配置项'source'支持类似python的数组子集操作。如

```
list {
    source => 'array[-2:]'; #截取array最后两个element
    target => ["last1", "last2"];
}
```

此功能可以用于处理不定长数组尾部元素。