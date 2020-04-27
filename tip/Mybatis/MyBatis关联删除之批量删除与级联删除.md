# MyBatis关联删除之批量删除与级联删除

## 数据基础
两张表，头行结构。逻辑结构：一对多，一个老师有多个学生。为帮助理解，相当于是一张老师表一张学生表。以下代码中没有用这两张表的字段，以头表行表来替代老师表和学生表，通用性高一些。
[![1541582063.jpg](https://i.loli.net/2018/11/07/5be2ad1af403a.jpg)](https://i.loli.net/2018/11/07/5be2ad1af403a.jpg)
[![1541582063(1).jpg](https://i.loli.net/2018/11/07/5be2ad1af3a3d.jpg)](https://i.loli.net/2018/11/07/5be2ad1af3a3d.jpg)


## 问题
1. 批量删除行表信息，单表删除操作。
2. 批量删除头表，这里在每删除一条头表内容的时候，就需要相应的取删除它所对应的行表的信息。

## 解决方法
1. 在controller中接收到的，是需要删除的头表的id集合`ids`
```
@Permission(permissionLogin = true)
@ApiOperation(value = "[删除] 批量删除以及级联删除")
@RequestMapping(value = "/batchDelete",method = RequestMethod.DELETE)
    public ResponseEntity<Map<String,Object>> batchDelete(@RequestBody List<Long> ids) {
        return Optional.ofNullable(listService.batchDelete(ids))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new HapException("error.listService.batchDelete"));
    }
```
2.先进行行表的删除。 
- service实现类中，这里是级联删除的关键，
```
@Override
public Map<String, Object> batchDelete(List<Long> ids) {
        Map<String, Object> resultMap = new HashMap<>();
        int status = 0;
        //级联删除
        if (!ids.isEmpty()) {
            status = listMapper.batchDeleteCascadeList(ids);
        }
        resultMap.put("delete.one", status);
        //批量删除
        if (!ids.isEmpty()) {
            status = listMapper.batchDelete(ids);
        }
        resultMap.put("delete.list", status);
        return resultMap;
    }
```
- 行表删除的mapper接口
```
    /**
     * [删除] 级联删除
     *
     * @return
     */
    int batchDeleteCascadeList(@Param(value = "idList") List<Long> idList);
```

- 行表删除的xml，这里也是关键，**删除行表中 uuid 是接收到的id集合中的项目所对应的的值**，将每个头表项目所对应的行表内容删除。

```
 <delete id="batchDeleteCascadeList">
        DELETE FROM 
        one
        WHERE
        uuid IN
        (SELECT uuid FROM list WHERE id IN
        <foreach item="id" index="index" collection="idList" open="(" separator="," close=")">
            #{id}
        </foreach>
        )
    </delete>
```
3. 进行头表的数据删除

- mapper接口
```
    /**
     * [删除] 根据id批量删除头表信息
     *
     * @param idList
     * @return
     */
    int batchDelete(@Param(value = "idList") List<Long> idList);
```

- xml
```
    <delete id="batchDelete" parameterType="java.util.List">
        delete from 
        list
        where id in
        <foreach collection="idList" item="id" open="(" separator="," close=")">
            #{id}
        </foreach>
    </delete>
```



## 总结
以上代码中只是截取了部分关键信息，还有一些service接口代码没有贴出来。

这是使用java的方法拼接实现级联删除。如果有mybatis的拼接或者是其他更高效的方法，求指导！