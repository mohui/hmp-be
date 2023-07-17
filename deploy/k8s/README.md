
## 参考文档

- [参考文档](https://kubectl.docs.kubernetes.io/zh/guides/bespoke/)
- [Kubernetes官网](https://kubernetes.io/zh-cn/docs/tasks/manage-kubernetes-objects/kustomization/)
- [github](https://github.com/kubernetes-sigs/kustomize)

## 使用命令

```bash
# 部署开发环境
kubectl apply -k demo/overlays/dev
```

```bash
# 删除开发环境
kubectl delete -k demo/overlays/dev
```