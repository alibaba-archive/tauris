# Input - Syslog

Syslog 输入插件

## 一 配置

| 属性名      | 类型     | 可选   | 描述               |
| -------- | ------ | ---- | ---------------- |
| host     | string | 是    | 绑定的IP，默认         |
| port     | int    | 否    | 监听端口             |
| protocol | enum   | 是    | 协议，tcp或udp。默认udp |

## 二 元数据

每条消息会带有下面的元数据，通过@名称方式读取

| name     | comment                         |
| -------- | ------------------------------- |
| host     | 源主机名                            |
| level    | 如 info, warn/warning,alert,crit |
| facility | 如 auth，kern，cron等               |



## 三 范例

```
input {
    sls {
        port => 1999;
    }
}
```
# Output - Syslog

Syslog 输出插件

## 一 配置

| 属性名      | 类型     | 可选   | 描述                  |
| -------- | ------ | ---- | ------------------- |
| protocol | enum   | 是    | udp或tcp。默认udp       |
| host     | string | 否    | 目标主机名/ip            |
| port     | int    | 否    | 目标端口                |
| level    | string | 是    | syslog的level，默认info |

## 二 范例

```
output {
    syslog {
        protocol => "udp";
        host => '127.0.0.1';
        port => '1999';
    }
}
```