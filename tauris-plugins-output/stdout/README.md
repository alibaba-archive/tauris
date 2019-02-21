# Output - 标准输出

控制台标准输出插件

## 一 配置

| 属性名   | 类型      | 可选   | 描述                                       |
| ----- | ------- | ---- | ---------------------------------------- |
| codec | encoder | 是    | 默认[plain](../tauris-codecs/docs/plain_encoder.md) |

## 二 范例

```
output {
    stdout {
        codec => json{}
    }
}
```
