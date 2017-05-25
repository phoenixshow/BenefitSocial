# BenefitSocial
    An IM social App.

1. 即时通讯类项目，主要代码来自缪传海
2. 使用v3.3.2版环信
3. 联系人表的操作类ContactTableDao中，“通过环信id获取用户联系人信息”、“保存联系人信息”时使用遍历集合，在循环中调用单个获取/保存的方法会导致多次操作数据库，应该优化
4. 对数据库操作层和业务层的封装简单、易懂，记账的封装难懂、优雅，记账源码：`https://github.com/phoenixshow/Readily`
5. InviteTableDao中的“将int类型状态转换为邀请的状态”方法，要根据枚举序数返回枚举常量对象，可采用在枚举类中自定义valueOf静态方法的方式返回需要的枚举常量对象，不必挨个匹配