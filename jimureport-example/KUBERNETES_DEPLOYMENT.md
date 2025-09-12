# JimuReport Kubernetes 部署指南

## 部署说明

本指南将帮助您在 Kubernetes 集群中部署 JimuReport 应用，使用指定的 namespace `ksec-edgeai` 并通过 host port 暴露服务。

## 预先准备

1. 确保你有一个正在运行的 Kubernetes 集群
2. 确保你已经安装了 `kubectl` 并配置好访问集群
3. 准备好 JimuReport 应用镜像

## 部署步骤

### 1. 准备数据库初始化脚本

首先需要将数据库初始化脚本复制到 Kubernetes 节点的 `/opt/jimureport/init_sql` 目录下：

```bash
# 在 Kubernetes 节点上执行
sudo mkdir -p /opt/jimureport/init_sql
sudo cp db/jimureport.mysql5.7.create.sql /opt/jimureport/init_sql/
```

### 2. 准备数据存储目录

创建用于持久化存储的目录：

```bash
# 在 Kubernetes 节点上执行
sudo mkdir -p /opt/jimureport/mysql
sudo mkdir -p /opt/jimureport/upload
```

### 3. 构建应用镜像

由于我们使用 `hostNetwork: true`，需要确保应用镜像在所有 Kubernetes 节点上都可用：

```bash
# 构建应用镜像
docker build -t jimureport:latest .

# 如果是多节点集群，需要在所有节点上都有该镜像
```

### 4. 部署到 Kubernetes

使用以下命令部署应用：

```bash
# 应用部署配置
kubectl apply -f k8s-deployment.yml
```

### 5. 检查部署状态

```bash
# 检查 Pod 状态
kubectl get pods -n ksec-edgeai

# 检查服务状态
kubectl get svc -n ksec-edgeai
```

## 访问应用

由于我们使用了 `hostNetwork: true`，应用将直接使用主机网络，可以直接通过 Kubernetes 节点的 IP 和端口 8085 访问：

```
http://<node-ip>:8085
```

默认登录账号密码为：
- 用户名：admin
- 密码：123456

## 配置说明

### Namespace

所有资源都部署在 `ksec-edgeai` namespace 中。

### MySQL 数据库

- 使用单独的 MySQL 容器
- 数据库 root 密码为 `root`
- 数据库配置通过 ConfigMap 管理
- 初始化脚本通过 hostPath 挂载
- 数据存储使用 hostPath 卷持久化在 `/opt/jimureport/mysql`

### JimuReport 应用

- 通过 `hostNetwork: true` 使用主机网络
- 通过环境变量 `MYSQL-HOST=jimureport-mysql` 连接到数据库
- 文件上传存储使用 hostPath 卷持久化在 `/opt/jimureport/upload`

## 注意事项

1. 由于使用了 hostNetwork，确保 8085 端口在节点上没有被占用
2. hostPath 卷需要在节点上具有适当的目录权限
3. 应用镜像需要在所有节点上都可用，因为使用了 IfNotPresent 拉取策略
4. 在生产环境中，建议使用 PersistentVolume 替代 hostPath 以获得更好的可移植性和管理性