# 过滤器:UA

解析浏览器字符串，输出操作系统，浏览器类型版本等信息

## 一 配置

| 属性名        | 类型      | 可选   | 描述                      |
| ---------- | ------- | ---- | ----------------------- |
| source     | string  | 否    | UA字符串字段名                |
| target     | string  | 是    | 保存解析后的结果的字段名，默认为ua      |
| cache_size | integer | 是    | 缓存大小，单位字节。为0时不缓存， 默认为0. |
| remove     | bool    | 是    | 解析完成后，删除原UA字段           |


## 二 范例

**配置**
```
input {
    ...
}

filter {
    ua {
        source => 'user_agent';
        target => 'ua';
        cache_size => 10000;
        remove => false;
    }
}


output {
    ...
}
```

**输入** 

user_agent:

```
Mozilla/5.0 (Linux; U; Android 4.0.3; zh-cn; M032 Build/IML74K) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30
```

**输出**

ua:

```
{
	"os":"android4",
	"os_family":"android",
	"browser":"mobile_safari",
	"browser_type":"mobile_browser",
	"device_type":"mobile",
	"browser_family":"safari",
	"browser_version":"4.0"
}
```