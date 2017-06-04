# BenefitSocial
    An IM social App.

1. 即时通讯类项目，主要代码来自缪传海
2. 使用v3.3.2版环信
3. 联系人表的操作类ContactTableDao中，“通过环信id获取用户联系人信息”、“保存联系人信息”时使用遍历集合，在循环中调用单个获取/保存的方法会导致多次操作数据库，应该优化
4. 对数据库操作层和业务层的封装简单、易懂，记账的封装难懂、优雅，记账源码：`https://github.com/phoenixshow/Readily`
5. InviteTableDao中的“将int类型状态转换为邀请的状态”方法，要根据枚举序数返回枚举常量对象，可采用在枚举类中自定义valueOf静态方法的方式返回需要的枚举常量对象，不必挨个匹配
6. 官方文档说新建群组时“默认是需要用户同意才能加群的”，实际在EMGroupOptions的构造方法中inviteNeedConfirm为false
7. 添加群成员被删除、群被解散、群成员加入、退出的广播
8. 集成v4.3.1版的百度地图SDK