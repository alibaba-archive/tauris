# 过滤器:Mutate

数据修改插件。mutate是最常用的插件，支持多种数据修改功能。

## 一 配置

```
filter {
  mutate {
    on => '...'; #布尔表达式,可选
    <action> {
      ... #配置项
    }
  }
}
```



## 二 Actions

### remove_field

删除一个或多个field

| 属性名    | 类型       | 可选   | 描述        |
| ------ | -------- | ---- | --------- |
| fields | string数组 | 否    | 被删除字段名称数组 |

范例

```
mutate {
  remove_field {
    fields => ['field1', 'field2'];
  }
}
```



### add_field

添加一个或多个field

| 属性名        | 类型   | 可选   | 描述       |
| ---------- | ---- | ---- | -------- |
| new_fields | hash | 否    | 待添加字段的定义 |

范例

```
mutate {
  add_field {
    new_fields => {
      'field1' => 'hello';
      'field2' => '%{world}'
    }
  }
}
```

hash的value支持表达式。

### copy

field复制/改名

| 属性名    | 类型       | 可选   | 描述                                |
| ------ | -------- | ---- | --------------------------------- |
| source | string   | 是    | 原字段名，非空时需要设置target                |
| target | string   | 是    | 新字段名，非空时需要设置source                |
| fields | string数组 | 是    | 格式为'old_field:new_field'格式的字符串数组。 |
| rename | bool     | 是    | 若为true，则在copy之后删除source.缺省为false。 |

source&target和fields至少设置一个。

范例

```
mutate {
  copy {
    source => 'f1';
    target => 'nf1';
    fields => ['f2:nf2', 'f3:nf3'];
    rename => true;
  }
}
```

将f1改名为nf1， 将field f2改名为nf2, f3改名为nf3.

### convert

field类型转换

配置

```
mutate {
  convert {
    <type> {
      fields => ['fieldname', ...];
    }
  }
}
```

类型'type'支持：

* long
* boolean 
* date
* double
* float
* integer

#### boolean converter

字符串"on/true"，以及非0数值类型被理解true

#### date converter

date类型有特别的配置项

| 属性名    | 类型     | 可选   | 描述                                       |
| ------ | ------ | ---- | ---------------------------------------- |
| format | string | 是    | 日期格式                                     |
| type   | enum   | 是    | joda或standard。joda：将字段转换为joda的DateTime类型； standard：转换为java.util.Date类型。默认为joda。 |

原字段的类型可以是

* joda datetime
* Long(理解为毫秒)
* Integer(理解为秒)
* String(日期字符串，需要设置format配置项)

### 范例

```
mutate {
  convert {
    date {
      fields => ['t1', 't2'];
      format => 'yyyy-MM-dd HH:mm:ss';
    }
    long {
      fields => ['long_val1', 'long_val2'];
    }
  }
}
```

### remove

删除字段

### add_tag

添加tag

### caseformat

Key或Value大小写转换

| 属性名    | 类型       | 可选   | 描述                                       |
| ------ | -------- | ---- | ---------------------------------------- |
| from   | enum     | 否    | UPPER_CAMEL,LOWER_CAMEL,UPPER_UNDERSCORE,LOWER_UNDERSCORE |
| to     | enum     | 否    | 同上                                       |
| mode   | enum     | 否    | key：转换key的大小写；value：转换value的大小写          |
| fields | string数组 | 是    | 需要被大小写转换的字段名称数组，如果为空，则转换event的所有field    |

将字符串由*from*格式转换为*to*格式

范例

```
mutate {
  caseformat {
    from => 'UPPER_CAMEL';
    to => 'LOWER_CAMEL';
    mode => 'key';
    fields => ['field1', 'field2'];
  }
}
```



### reverse

翻转数组/列表/字符串

| 属性名    | 类型      | 可选   | 描述                          |
| ------ | ------- | ---- | --------------------------- |
| source | 源field  | 否    | 字段值的类型可以是string, array和list |
| target | 目标field | 是    | 如果为空，则等同于source             |

范例

```
mutate {
  reverse {
    source => 'array';
    to => 'rev_array';
  }
}
```

### truncate

截断字符串

### trim

消除字符串首尾空白

### split

字符串分割

### join

字符串连接

### replace

字符串替换

### dateformat

格式化日期为字符串

| 属性名    | 类型                | 可选   | 描述                                 |
| ------ | ----------------- | ---- | ---------------------------------- |
| source | jodatime/int/long | 否    | 日期或时间戳字段名. long类型被理解为毫秒，int类型被理解为秒 |
| target | string            | 否    | 保存格式化后的日期的字段名                      |
| format | string            | 否    | 日期格式，如yyyy-MM-dd HH:mm:ss          |

范例

```
mutate {
  dateformat {
    source => '@timestamp';
    target => 'datetime';
    format => 'yyyy-MM-dd';
  }
}
```



















