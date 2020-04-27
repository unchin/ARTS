# MyBatis关联结构的数据批量插入方法详解与优化

## 数据基础
两张表，头行结构。逻辑结构：一对多，一个老师有多个学生。为帮助理解，相当于是一张老师表一张学生表。以下代码中没有用这两张表的字段，以头表行表来替代老师表和学生表，通用性高一些。
[![1541582063.jpg](https://i.loli.net/2018/11/07/5be2ad1af403a.jpg)](https://i.loli.net/2018/11/07/5be2ad1af403a.jpg)
[![1541582063(1).jpg](https://i.loli.net/2018/11/07/5be2ad1af3a3d.jpg)](https://i.loli.net/2018/11/07/5be2ad1af3a3d.jpg)

## 问题
新增的数据为头行结构数据。
例如，新增了一个老师和两个学生信息，两个学生都属于这个老师的java班中。
我们需要将这一条json数据分别插入到老师表和学生表这两个表中。
```
[
  {
    "className": "java",
    "id": 6,
    "name": "teacherTom",
    "students": [
      {
        "className": "java",
        "name": "steven",
        "teacherId": 6
      },
      {
        "className": "java",
        "name": "kite",
        "teacherId": 6
      }
    ]
  }
]
```

## 解决方法
1. 在头表的实体类中需要有一个暂时存放行表数据的字段。
2. 接收过来的也就是需要插入的一批数据可以从前端来，也可以自己经过处理后放在list容器里，我这里是后者。

```
List<addDTO> addDTOS = new ArrayList<>();
```
3. 然后是批量插入，这个就是普通的mybatis的批量新增。主要说一下这个优化的方法。
```
@Override
    public Map<String, Object> insert(AddDTO addDTO) {
        Map<String, Object> result = new HashMap<>();

        //查询到的待新增的项
        List<AddDTO> adds = addMapper.selectOne();

        //取出获取到的数据中的不可重复项的值
        String code = addDTO.getCode();
        String name = addDTO.getName();

        //遍历每个编码和名字
        for (AddDTO add : adds) {
            //如果从前台输入的值与数据库中的有重复，则新增失败，抛出异常
            if (code.equals(add.getCode()) || name.equals(add.getName())) {
                throw new Exception("error.insert");
            }
        }

        //批量插入到表中
        int status = 0;
        int part = (int) Math.ceil(adds.size() / 50);
        int beginIndex = 0;
        int endIndex = 0;
        for (int i = 0; i < part; i++) {
            beginIndex = part * i;
            if ((i + 1) == part) {
                endIndex = adds.size();
            } else {
                endIndex = part * (i + 1);
            }
            status = status + addMapper.insert(adds.subList(beginIndex, endIndex));

        }
        result.put("insert", status);
        return result;
    }
```
这里这种优化的方法主要是分批操作，我这边是一次新增2260条信息，mybatis一次新增耗时很久，然后本人试过很多种方案，刚开始是想着尽量减少与数据库的交互应该会快一些，但是经过试验，每批控制在50到100之间的耗时是最小的，当然这个原理我还是没有弄清楚。如果有知道这个原理的，求指导！

4. xml


```
   <insert id="insert" parameterType="java.util.List">
        INSERT INTO
        teacher(
            id,
            name,
            className
        )
        VALUES
        <foreach collection="addDTOS" item="teacher" index="index" separator=",">
            (
            default,
            #{teacher.name},
            #{teacher.className}
            )
        </foreach>;

        <if test="addDTO.student != null and addDTO.student.size != 0">
            INSERT INTO
            student(
            id,
            name,
            className,
            teacherId
            )
        VALUES
          <foreach collection="addDTOS" item="addDTO" index="index" separator=",">

              <foreach collection="addDTO.student" item="studentDTO" index="index" separator=",">
            (
            default,
            #{studentDTO.name},
            #{studentDTO.className},
            #{studentDTO.teacherId}
            )
              </foreach>
          </foreach>
        </if>
    </insert>

```
这里是两条语句拼接的头行关联结构新增语句。

## 总结

在做批量新增的时候，要尽可能的多想想该接口在面对数据量大的情况时应该怎么处理。
在做头行关联结构新增的时候，也可以在mybatis中拼接新增，免去java的拼接。