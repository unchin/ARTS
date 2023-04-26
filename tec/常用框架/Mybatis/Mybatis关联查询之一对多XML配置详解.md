Mybatis关联查询之一对多XML配置详解

最初看的这篇教程https://blog.csdn.net/qq_33561055/article/details/78861131
但是有些地方没有解释详细，自己研究了一会，决定将之详细化

- 首先是有两张表，头行结构。相当于是一张老师表一张学生表。

![](/Mybatis关联查询之一对多XML配置详解/20181102032513291.png)

![](/Mybatis关联查询之一对多XML配置详解/20181102033056030.png)
逻辑结构就是一个老师有多个学生，我们在查询的时候想要的结果是查出所有的老师的数据以及每一个老师所对应的学生的数据。

- 创建实体bean
头表和行表都需要创建相应的实体类。在头表中需要新增一个字段（表中不存在的），用来存放行表中查出的对应的数据。

```
   @VersionAudit
@ModifyAudit
@Table(name = "teacher")
public class TeacherDTO {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "class_name")
    private String className;

    @Transient
    private List<StudentDTO> students;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<StudentDTO> getStudents() {
        return students;
    }

    public void setStudents(List<StudentDTO> students) {
        this.students = students;
    }
}

```

我这里用students来存放 行表 中查询的出的数据。`@Transient`这个注解表示该实体中定义数据库中没有的字段。

-  配置Mapper.xml文件
在查询了网上资料以及翻阅相关书籍后，总结出mybatis中一对多的方式，其实这个方法的效率是比较高的，所以优先用这个方法，前文中的链接中也有提到这种方法，就是链接中的第二种方法。
**代码的详解放在代码的注释中**

```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="test.mapper.TeacherMapper">

    <!-- type 头表的返回格式，也就是头表的实体类-->
    <resultMap id="teacherMap" type="test.domain.TeacherDTO">
        <id property="id" column="id"/>
        <result column="name" property="name"/>
        <result column="class_name" property="className"/>
        <!-- ofType 行表的返回格式-->
        <!-- select 行表的sql查询语句的id-->
        <!--column 头表与行表的连接字段，注意这里用的是下划线，下面用的是驼峰-->
        <collection property="students" ofType="test.domain.StudentDTO" select="findStudents" column="id">
        </collection>
    </resultMap>

    <!--头表查询语句-->
    <select id="findTeacherAndStudent" resultMap="teacherMap">

        SELECT
            id,
            NAME,
            class_name
        FROM
            `teacher`
    </select>

    <!--行表查询语句-->
    <select id="findStudents" resultType="test.domain.StudentDTO">

        SELECT
            id,
            teacher_id,
            name,
            class_name
        FROM
            `student`
        WHERE
            1=1
--         这里递进来的的参数是驼峰格式
        AND id = #{id}

    </select>

</mapper>
```

- 这是mybatis的拼接方式，有时候其实也可以用java的拼接方式，独立出两个查询接口，然后在java中拼接

这是service实现类中的拼接方式

```
public List<TeacherDTO> findTeacherAndStudentByJava(String id) {
        List<TeacherDTO> result = teacherMapper.findTeacher(id);
        //遍历头表中查询到的每一条数据
        for (TeacherDTO teacherDTO : result) {
            //将行表查询到的数据放在头表中的students字段中
            teacherDTO.setStudents(teacherMapper.findStudents(teacherDTO.getId()));
        }
        return result;
    }
```
