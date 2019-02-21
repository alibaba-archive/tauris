# Filter:KeyMap



## 一 配置

| 属性名             | 类型     | 可选   | 描述                |
| --------------- | ------ | ---- | ----------------- |
| source          | string | 否    | 源字段名              |
| target          | string | 否    | 目标字段名             |
| mapper          | mapper | 否    | mapper，json 或 csv |
| update_interval | int    | 是    | 更新周期，单位秒，默认3600   |

## 二 Mapper

### 2.1 json

从event中得到源字段的值，通过值在找到对应的json对象，将json对象保存到event的target字段中。

| 属性名       | 类型     | 可选   | 说明                                       |
| --------- | ------ | ---- | ---------------------------------------- |
| uri       | string | 否    | json文件的uri，目前只支持文件路径                     |
| key_name  | string | 否    | json对象的key名称                             |
| check_md5 | bool   | 是    | 在更新时检测json文件的md5，仅在md5文件有变化时更新。默认为false。如果为true，需要提供一个后缀为.md5sum的文件。例如 uri是"config/user.json"，就需要在config目录下放置一个"config/user.json.md5sum"，文件内容是user.json的md5sum。 |

### 配置范例

```
filter {
    keymap {
        source => 'name';
        target => 'user';
        mapper => json {
          uri => 'config/user.json';
          key_name => 'username';
        }
    }
}
```

json文件由1到多个jsonobject组成，每个jsonobject占一行，如

user.json

```
{"username":"jon snow", "actor": "Harington"}
{"username":"dragon mom", "actor": "Emilia Clarke"}
```
在json文件被读取后，将会得到下面的结构 

```
{
  'json snow' : {"username":"jon snow", "actor": "Harington"},
  'dragon mom' : {"username":"dragon mom", "actor": "Emilia Clarke"}
}
```

在上例中，如果输入event = { 'name' : 'jon snow', …}， 就会得到输出

```
{
  'name' : 'jon snow'
  'user' : {
    'username' : 'jon snow',
    'actor' : 'Harington'
  }
}
```

### 2.2 csv

从event中得到源字段的值，通过值在找到对应的行，将行以数组形式保存到event的target字段中。

| 属性名       | 类型     | 可选   | 说明                                       |
| --------- | ------ | ---- | ---------------------------------------- |
| uri       | string | 否    | csv文件的uri，目前只支持文件路径                      |
| separator | string | 是    | 分隔符， 默认为','                              |
| key_index | int    | 否    | key在csv列中的索引, 从1开始。                      |
| check_md5 | bool   | 是    | 在更新时检测文件的md5，仅在md5文件有变化时更新。默认为false。如果为true，需要提供一个后缀为.md5sum的文件。例如 uri是"config/user.csv"，就需要在config目录下放置一个"config/user.csv.md5sum"，文件内容是user.csv的md5sum。 |

### 配置范例

```
filter {
    keymap {
        source => 'name';
        target => 'user';
        mapper => csv {
          uri => 'config/user.csv';
          key_index => 2;
        }
    }
}
```

user.csv

```
1,jon snow,Harington,male
2,dragon mom,Emilia Clarke,female
```

在文件被读取后，将会得到下面的结构 

```
{
  'json snow' : ["1"，"jon snow", "Harington", "male"],
  'dragon mom' : ["2"，"dragon mom", "Emilia Clarke", "female"]
}
```

在上例中，如果输入event = { 'name' : 'jon snow', …}， 就会得到输出

```
{
  'name' : 'jon snow',
  'user' : ["1"，"jon snow", "Harington", "male"]
}
```

