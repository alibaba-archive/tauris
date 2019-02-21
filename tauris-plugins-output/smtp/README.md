# Input - http

Http 输入插件

## 一 配置

| 属性名              | 类型                  | 可选   | 描述                   |
| ---------------- | ------------------- | ---- | -------------------- |
| port             | int                 | 否    | 监听端口号                |
| host             | string              | 是    | 绑定IP， 默认为0.0.0.0     |
| path             | string              | 是    | url上下文路径, 默认为'/'     |
| threads          | int                 | 是    | http处理线程数， 默认为10.    |
| username         | string              | 是    | basic认证用户名           |
| password         | string              | 是    | basic认证密码            |
| success_code     | int                 | 是    | 消息接收成功后返回的响应码，默认为204 |
| response_headers | hash<string,string> | 是    | http响应头              |

## 二 说明

http input插件用于接收通过http传递来的消息。支持post、put、get、head四种method。

### 2.1 GET & HEAD

每个请求生成一个event。event包含下面meta

| 名称           | 说明                          | 范例                                |
| ------------ | --------------------------- | --------------------------------- |
| @source      | 无                           |                                   |
| @method      | request的method              | GET、HEAD                          |
| @timestamp   | datetime类型                  | 收到请求的时间                           |
| @path        | 上下文路径                       | /                                 |
| @querystring | 请求中querystring              | name=number&value=123             |
| @params      | hash类型，解析querystring得到的请求参数 | {"name":"number", "value": "123"} |

### 2.1 POST & PUT

请求的body**每行**生成一个event。每个event包含下面meta

| 名称           | 说明                          | 范例                                |
| ------------ | --------------------------- | --------------------------------- |
| @source      | body中一行的内                   | 无                                 |
| @method      | request的method              | PUT、POST                          |
| @timestamp   | datetime类型                  | 收到请求的时间                           |
| @path        | 上下文路径                       | /                                 |
| @querystring | 请求中querystring              | name=number&value=123             |
| @params      | hash类型，解析querystring得到的请求参数 | {"name":"number", "value": "123"} |



## 三 范例


```
input {
    http {
        port => 8080;
    }
}
```



```
input {
  http {
    port => 8080;
    host => '10.10.10.10';
    username => 'jon';
    password => 'snow';
    success_code => 200;
    codec => json {}
  }
}
```