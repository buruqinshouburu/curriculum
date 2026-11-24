# 公库课程导入：办事大厅配置

`POST /csys/curriculum/course/import` 中，只要填写了课程负责人就查询大厅，不受 `templateType` 限制；空白负责人不请求大厅。

以下仅为后端 Spring 外部配置示例，所有值均为占位引用。不要将真实值写入仓库或前端配置。

```yaml
course-import:
  portal:
    token-url: "${PORTAL_TOKEN_ENDPOINT:}"
    user-url: "${PORTAL_USER_ENDPOINT:}"
    client-id: "${PORTAL_CLIENT_ID:}"
    secret: "${PORTAL_CLIENT_SECRET:}"
```

- 两个端点分别填写从后台可达、运维确认的完整 HTTPS 地址，可能位于不同主机。令牌路径为 `/portal-token/getToken`，人员路径为 `/portal-core/user/getPersonalInfoByEmailOrUserName.do`；不含查询串，不使用浏览器代理前缀 `/cas-token`、`/cas-core`。
- 使用具备人员精确查询权限的服务凭据，由部署环境的密钥管理系统注入。不要把密钥放入 URL、命令行、工单或日志。应用、代理及 HTTP 调试日志均应屏蔽 `secretKey` 和 `access-token` 请求头。
- 配置缺失时不会阻断应用启动；导入时鉴权或查询异常不阻断整批导入，发生异常的行保留原负责人字段。负责人全部空白则不需要凭据。
- 联调时用获准的测试身份和不存在的测试关键字验证响应。仅成功码字符串 `00000` 且显式 `data: null` 表示查无；查询成功时写入匹配的账号和姓名，查无的负责人不保留；任一负责人查询异常时，该行原负责人字段保持不变。
