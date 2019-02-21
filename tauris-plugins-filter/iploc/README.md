# IP地址位置过滤器

## 一 配置

| 属性名             | 类型      | 可选   | 描述                   |
| --------------- | ------- | ---- | -------------------- |
| source          | ipv4字符串 | 否    | IPv4属性名              |
| target          | 字符串     | 否    | 目标属性名                |
| locator         | 对象      | 否    | 定位器                  |
| update_interval | 整数      | 是    | 位置库更新间隔时间，默认3600秒    |
| cache_size      | 整数      | 是    | IP位置信息缓存大小， 默认0，即不缓存 |

读取event的source属性值, 查询其地理位置信息, 将地理位置信息对象保存到$target属性中.



## 二 地理位置信息属性

| 名称        | 描述               |
| --------- | ---------------- |
| country   | 国家, 如'中国', '美国'  |
| countryId | 国家编码, 如CN, US    |
| region    | 省份, 如浙江,广州       |
| regionId  | 省份编码,对国外的IP无效    |
| city      | 城市名称, 如 南京, 杭州   |
| cityId    | 城市编码, 中国的城市编码是邮编 |
| isp       | 运营商名称, 如中国联通     |



## 三 定位器

### 3.1 tbip

此过滤器插件依赖淘宝的IP地理位置库, 需要自行下载并解压到一个文件夹下. 

地理位置库分为国内(china)和国际(global)两种, 下载地址:

- 国际版压缩包: http://ip.taobao.org:9999/ipdata/ipdata_code_with_maxmind.txt.utf8.tar.gz
- 国际版语言文件: http://ip.taobao.org:9999/ipdata/language.txt
- 国内版压缩包: http://ip.taobao.org:9999/ipdata/ipdata_geo_isp_code.txt.gbk.tar.gz

将压缩包下载后解压缩到一个文件夹, 如果是国际版, 还需要将language.txt下载到此文件夹下.

或者用yum安装, 参见: http://ip.taobao.org:9999/ipdata_interface.html

#### 3.1.1 配置

| 属性名      | 类型   | 可选   | 描述                                       |
| -------- | ---- | ---- | ---------------------------------------- |
| data_dir | 字符串  | 否    | 地理位置库文件所在文件夹路径。文件夹下必须有ipdata_geo_isp_code.txt.<encode>和language.txt两个文件 |
| encode   | 字符串  | 是    | encode类型，默认utf-8                         |

#### 3.1.2 范例

```
iploc {
        on => '$remoteAddr is ip4';
        source => 'remoteAddr';
        target => 'remoteLoc';
        locator => dna {
          data_dir => 'config/iploc';
        }
}
```

### 3.2 dna

此IP库来自https://dna.alibaba-inc.com/。IP库需要自行下载。

#### 3.2.1 配置

| 属性名      | 类型   | 可选   | 描述                                       |
| -------- | ---- | ---- | ---------------------------------------- |
| data_dir | 字符串  | 否    | 地理位置库文件所在文件夹路径。文件夹下必须有geocache.bin文件     |
| language | 字符串  | 是    | 语言，cn或en，默认cn，如果为en，data_dir下必须有lang.bin文件 |

#### 3.2.2 范例

```
iploc {
        on => '$remoteAddr is ip4';
        source => 'remoteAddr';
        target => 'remoteLoc';
        locator => dna {
          data_dir => 'config/iploc';
        }
}
```

#### 3.2.3 位置库下载脚本

```
#!/bin/sh
prefix=$1
target=$2
cmd="osscmd -i <ak> -k <sk> -H cn-hangzhou.oss-pub.aliyun-inc.com"
vv=$($cmd ls  oss://dna-oss/$prefix | grep $prefix | grep -v md5 | tail -n 1 2>/dev/null)
filename=
for row in $vv; do
filename=$row
done
md5file=$(echo $filename | sed -e s/bin/bin.md5/)

if [ ! -d "$target" ]; then
	mkdir -p $target
fi

$cmd get $filename $target/$prefix.bin
$cmd get ${md5file} $target/$prefix.bin.md5
```

用法

```
./getdna.sh geocache config/iploc
./getdna.sh lang config/iploc
```

建议使用crontab定期更新。脚本第三行中的ak和sk请自行在dna提供的java二方库**com.alibaba.dna.dna-client.jar**中查找:) 并替换。



## 四 完整范例

```
input {
    ...
}

filter {
    regex {
        discard => true;
        pattern  => '(?<remoteAddr>\d+\.\d+\.\d+\.\d+) .*';
    }
    iploc {
        on => '$remoteAddr is ip4';
        source => 'remoteAddr';
        target => 'remoteLoc';
        locator => tbip {
            data_dir => 'config/iploc';
        }
    }
    mutate {
        on => "$remoteLoc is not null && $remoteLoc.countryId == 'CN'";
        add_field => {
            new_fields => {
                'client_region' : '%{remoteLoc.region}',
                'client_isp' : '%{remoteLoc.isp}'
            }
        }
    }
    mutate {
        on => "$remoteLoc is not null && $remoteLoc.countryId != 'CN'";
        add_field => {
            new_fields => {
                'client_region' : '%{remoteLoc.country}'
            }
        }
    }
    mutate {
        remove_field => {
            fields => ['remoteLoc'];
        }
    }
}


output {
    stdout {
        codec => json {
            pretty => true;
        }
    }
}
```

