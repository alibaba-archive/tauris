# 过滤器:Regex

正则解析，通过正则表达式解析指定的文本，提取匹配的内容，保存到event中。

## 一 配置

| 属性名          | 类型       | 可选   | 描述                                 |
| ------------ | -------- | ---- | ---------------------------------- |
| source       | string   | 是    | 需正则解析的字段名，默认为@source               |
| pattern_file | filepath | 是    | 正则表达式文件路径                          |
| separator    | regex    | 是    | 正则表达式分隔符，仅在设置pattern_file时有效，默认为\s |
| pattern      | string   | 是    | 正则表达式                              |
| discard      | boolean  | 是    | 如果为true，在匹配失败时丢弃event              |

pattern_file和pattern至少配置一项

如果正则表达式较长，可以将正则表达式拆成多行，放到一个配置文件里，在配置文件中用pattern_file指定。在初始化时，插件系统将会读取这个文件每一行，然后用separator连接成一个正则表达式。如

```
"(?<aclDefault>[^"]*)"
"(?<matchedHost>[^"]*)"
"(?<contentType>[^"]*)"
```

相当于正则表达式

```
"(?<aclDefault>[^"]*)"\s"(?<matchedHost>[^"]*)"\s"(?<contentType>[^"]*)"
```

在正则解析成功后，会将命名的匹配结果保存到到event中，如上例中的aclDefault/matchedHost/contentType



## 二 范例

**配置**
```
input {
    ...
}

filter {
    regex {
        source => '@source';
        pattern_file => 'config/pattern.txt';
        discard => true;
    }
}


output {
    ...
}
```
